//> using scala 3.8.4
//> using jvm 21
//> using file ../Latex.scala
//> using file ../CodeGlossary.scala
//> using file SwedishScore.scala

// TRIAGE for the hand-written `compendium-todo` inventory: for every fragment listed there, find where
// it lives in the SWEDISH source, decide what KIND of defect it is, and name the mechanism that can fix
// it WITHOUT a model. Transforms nothing, writes nothing except an optional report: it is a worklist
// generator, so it can be re-run after every batch to see what is left.
//
//   scala-cli run autotranslate/scratch/todo-triage.scala -- [--root <dir>] [--todo <file>]
//                                                           [--chapter <prefix>] [--kind <NAME>]
//                                                           [--report <file>] [--hits]
//
// Why the search does most of the classifying: a fragment that is NOT in the Swedish source but IS in
// the generated mirror can only be an English-side defect -- a missing space, a misspelling, a
// mistranslation -- because there is no Swedish left to rename or clamp. One lookup separates "translate
// this" from "fix the English", which are different jobs with different owners.
//
// The mechanism column follows the ladder established by the merged w06/w09/w10/w11 work:
//   identifier in code        -> CodeGlossary.id (global) or .perFileId (scoped)  -- never touches source
//   whole quoted literal      -> CodeGlossary.codeStr                             -- never touches source
//   code comment / prose unit -> Overrides, key from scratch/unit-probe.scala     -- never touches source
//   term already \Eng-glossed -> the issue-981 transform                          -- no per-term ratifying
//   genuinely divergent text  -> whole-unit \ifswedish in the source              -- touches source
// Source-touching is last on purpose: it changes a unit's text, which invalidates its cache row and
// invites a re-translation nobody can review without a model (measured by BR on PR #976).

import java.nio.file.{Files, Path}
import scala.jdk.CollectionConverters.*

/** One line of the log. `issue` groups ADJACENT lines that belong together -- most often a single REPL
  * session quoted over several lines. That grouping is not cosmetic: a transcript has to be fixed as a
  * unit, because the identifiers in the input and in the echoed output must agree and the whole thing
  * must still compile (the phase 1-3 gates). Counting its lines separately both inflates the work and
  * invites fixing half of one. */
final case class Entry(todoLine: Int, section: String, task: String, text: String,
    expect: Option[String] = None, issue: Int = 0, expectStatus: Option[String] = None)
final case class Hit(rel: String, line: Int, inCode: Boolean)

/** A source file plus, per line, whether that line sits inside a verbatim/code environment, and the
  * line with LaTeX markup flattened. `flat` is precomputed because the todo quotes the RENDERED pdf
  * while the source carries markup, so every match has to be made against a flattened form -- and
  * flattening per needle per line would be O(needles x corpus). */
final case class Src(rel: String, lines: Vector[String], inCode: Vector[Boolean], flat: Vector[String])

/** Flatten LaTeX so rendered text can be matched against source: `\code{ls}` -> `ls`, `''x''` -> `"x"`,
  * and leftover commands dropped. Deliberately crude -- it only has to make substring search land. */
def deMarkup(s: String): String =
  val cmdBrace = raw"\\[A-Za-z]+\*?\{([^{}]*)\}".r
  val cmdPipe = raw"\\[A-Za-z]+\*?\|([^|]*)\|".r
  var t = s
  var prev = ""
  while t != prev do
    prev = t
    t = cmdBrace.replaceAllIn(t, m => java.util.regex.Matcher.quoteReplacement(m.group(1)))
  t = cmdPipe.replaceAllIn(t, m => java.util.regex.Matcher.quoteReplacement(m.group(1)))
  t = t.replace("\\\\", " ").replace("~", " ")
  t = raw"\\[A-Za-z]+\*?".r.replaceAllIn(t, " ")
  t = t.replace("``", "\"").replace("''", "\"").replace("\u201d", "\"").replace("\u201c", "\"")
  t = t.replace("\u2019", "'").replace("{", "").replace("}", "")
  // `$` goes too: the inventory quotes rendered output ("gurka nr i") while the source interpolates
  // ("gurka nr $i"), and dropping it on both sides makes those meet.
  t = t.replace("$", "")
  // LaTeX writes thousands with a space inside math ($12 750$) and the pdf pastes as 12750, so close
  // gaps BETWEEN digits only -- narrow enough that it cannot join two separate numbers in prose.
  t = raw"(?<=\d) (?=\d)".r.replaceAllIn(norm(t), "")
  t

// A section header needs at least one DOT: a bare leading digit is far more likely to be a REPL error
// gutter ("1 |val djurbur: Bur[Djur] = ...") than a heading, and treating those as headers silently
// re-labels every following fragment with the wrong chapter. Learned by doing exactly that.
val HeaderRx = raw"^\s*(-?\d+(?:\.\d+)+)\s*(.*)$$".r
val ChapterRx = raw"^\s*Chapter\s+(\d+)\b.*$$".r
// `Task 6` and `Task6` are both written in practice, so the space is optional. Requiring it turned
// eight location markers per chapter into unlocatable "fragments".
val TaskRx = raw"^\s*[Tt]ask\s*(\d+)\b.*$$".r
// `footnote 1` / `bullet 3` are LOCATION markers, not commentary: they say which footnote or bullet of
// the current section the following fragment sits in. Keeping them is what makes the entry findable.
val NoteRx = raw"^\s*((?:footnote|bullet)\s*\d*)\s*$$".r

/** Does this fragment look like Scala? Used in two places that previously disagreed: the guard that
  * keeps code out of the English-evidence test, and the --tokens aggregation. Both were too narrow --
  * they wanted `scala>` or a def/val/var opener -- so `for i <- 1 to 10 do ...` and `if x then y`
  * scored as confident English and landed in the remarks bucket, and lone identifiers were dropped
  * from the candidate table. Scala keywords ARE English function words, so this has to be generous. */
def looksLikeScala(text: String): Boolean =
  val t = text.trim
  val markers = Vector("scala>", "<-", "=>", "::", "def ", "val ", "var ", " then ", " do ", " yield ",
    " match", "case ", "println", "(", "=")
  val opener = raw"^(def|val|var|class|object|trait|enum|given|case|for|if|while|import)\b".r
  val isProse = t.startsWith("#") || t.startsWith("//") || t.contains("←")
  !isProse && (markers.exists(t.contains) || opener.findFirstIn(t).isDefined)

/** A dotted number is only a header if what follows does not look like code. `0.5 * bredd` is an
  * expression, not section 0.5. */
def looksLikeHeader(rest: String): Boolean =
  val r = rest.trim
  val operatorStart = raw"^[*+\-/=<>%&|^!]".r.findFirstIn(r).isDefined
  !r.startsWith("|") && !r.contains("scala>") && !r.startsWith("(") && !operatorStart
    && !Vector("=", "*", "<-", "=>").exists(r.contains)

/** Scala keywords, stdlib names and REPL noise: tokens that must never be proposed as renames. Not a
  * Swedishness test -- just the floor of things that are certainly not example names. */
val scalaNoise: Set[String] = Set(
  "def", "val", "var", "if", "then", "else", "while", "do", "for", "yield", "case", "class", "object",
  "trait", "enum", "given", "using", "import", "new", "this", "true", "false", "null", "extends",
  "match", "try", "catch", "throw", "type", "return", "lazy", "override", "private", "sealed", "end",
  "Int", "String", "Double", "Boolean", "Char", "Unit", "Any", "Nothing", "Long", "Float", "Byte",
  "Vector", "Array", "List", "Seq", "Set", "Map", "Option", "Some", "None", "Try", "Success",
  "Failure", "Either", "Left", "Right", "Range", "println", "print", "math", "random", "scala",
  "map", "filter", "flatMap", "foreach", "fill", "length", "size", "toInt", "toString", "mkString",
  "apply", "head", "tail", "isEmpty", "nonEmpty", "get", "getOrElse", "sum", "sorted", "reverse",
  "indices", "indexOf", "StdIn", "readLine", "io", "reflect", "ClassTag", "compare", "equals",
  // java/awt API names reached by the graphics examples, and Exception/Try machinery
  "java", "awt", "swing", "util", "Color", "Graphics", "Exception", "Throwable", "Error", "Thread",
  "IllegalArgumentException", "Random", "PixelWindow", "atan2", "sqrt", "abs", "toUpperCase",
  "toLowerCase", "MaxValue", "MinValue", "until", "orElse", "recover", "extension", "add", "min",
  "max", "str", "pos", "find", "empty", "block", "argument", "collection", "content", "Values", "TAB",
  // English words that leak in from translated comments and compiler messages inside code envs
  "called", "cannot", "compared", "equality", "required", "found", "expected", "declared",
)

/** REPL artefacts: object hashes and synthetic lambda names, e.g. `$Lambda7356/0x0000000841ed2840`.
  * They are neither Swedish nor identifiers anyone wrote, and they differ on every run. */
val replArtefact: scala.util.matching.Regex = raw"(?:Lambda\d+|x?[0-9a-f]{6,}|\w*\d{3,}\w*)".r

/** Example names that are ALREADY English, so never rename candidates. Curated by hand as the table
  * surfaces them, because no detector can tell `monster` -- the same word in both languages -- or the
  * English `alien`/`predator` in the Rymdvarelse example from a Swedish coinage. Grows over time; that
  * is the point, since every entry here is one fewer false candidate in every future run. */
val englishExampleNames: Set[String] = Set("monster", "alien", "predator")

/** Candidate work inside a code line: identifier tokens that could need renaming, and the contents of
  * quoted literals, which are a different mechanism (codeStr, not id). Deliberately NOT a Swedishness
  * judgement -- SwedishScore's lists are FUNCTION words, which cannot see `dubblera` or `gurka`, and
  * every name needs BR's ratification regardless. So: surface candidates, exclude what is certainly
  * not one, and mark what the glossary already covers. */
def candidates(line: String, rel: String = ""): Cands =
  val idRx = raw"[A-Za-zÅÄÖåäö_][A-Za-z0-9ÅÄÖåäö_]*".r
  val strRx = raw""""([^"]*)"""".r
  val strings = strRx.findAllMatchIn(line).map(_.group(1)).toVector.filter(_.trim.nonEmpty).distinct
  val inStrings = strings.mkString(" ")
  val worth = idRx
    .findAllIn(line)
    .toVector
    .distinct
    .filter(t => t.length > 2 && !scalaNoise.contains(t) && !raw"res\d+".r.matches(t)
      && !replArtefact.matches(t))
    .filterNot(inStrings.contains)
  // Being on the exclusion list is a DECISION -- "already English, no rename" -- and dropping those
  // tokens silently made the decision invisible: a line whose only candidate was `monster` reported
  // as "no Swedish token found", which reads as unexamined rather than settled. Keep them, labelled.
  val (english, idents) = worth.partition(t => englishExampleNames.contains(t.toLowerCase))
  // perFileId counts as covered too. Ignoring it listed every already-scoped name -- Djur, Katt,
  // Bur, the whole w10 and w11 clusters -- as still needing ratification. overridesFor does the
  // lookup the mirror itself does, so a name scoped to THIS file reads as done and a name scoped to
  // another file still reads as open, which is the distinction that matters.
  val scoped = if rel.isEmpty then Map.empty[String, String] else CodeGlossary.overridesFor(rel)
  val (covered, fresh) = idents.partition(t => CodeGlossary.id.contains(t) || scoped.contains(t))
  Cands(fresh, strings.filterNot(CodeGlossary.codeStr.contains), covered, english)

/** The three states a name can be in, which the report has to keep apart: `open` still needs a
  * ratified English name; `covered` already has one in the glossary; `english` was ruled already-English
  * and needs none. Conflating the last two with "nothing found here" is what hid settled decisions. */
final case class Cands(open: Vector[String], strings: Vector[String], covered: Vector[String],
    english: Vector[String]):
  def settled: Vector[String] = covered ++ english
  def nothingOpen: Boolean = open.isEmpty && strings.isEmpty

def norm(s: String): String = s.trim.replaceAll("\\s+", " ")

/** The built English pdf, as `pdftotext` extracted it, flattened into one searchable string.
  *
  * This is the only honest oracle for "is this still broken". The source always still contains the
  * Swedish, so source-based checking reports every fixed item as open work; and the mirror .tex is no
  * better, because a clamped item keeps BOTH branches there while the reader sees only one. What a
  * reader actually meets is the pdf. De-hyphenation first: pdftotext breaks words across lines, so
  * `sökru-\ntan` has to become `sökrutan` before anything can be found. */
def loadPdfText(path: Path): String =
  val raw = String(Files.readAllBytes(path), "UTF-8")
  norm(raw.replaceAll("-\\n", "").replace('­', ' '))

/** Letters and digits only, lowercased. The log quotes the pdf and the pdf renders the source, and the
  * three disagree about everything EXCEPT the letters: the source writes `s"Dubbla längden:
  * ${namn.size * 2}"` where deMarkup leaves `s"Dubbla längden: namn.size * 2"`, and the pdf still has
  * its `$` and braces; the log glues an enumerator onto its item (`a)poäng > 100`); the pdf wraps and
  * hyphenates where the log does not. Comparing de-markup'd text against a RAW extract made every one
  * of those read as SOLVED -- `Dubbla längden`, `poäng > 100`, `är för stort` and `desarmerad bomb` are
  * all still in the English pdf, and all four were reported fixed. Squashing both sides is blunt and
  * can join across a line or page break, but it errs toward "still there", which is the safe direction:
  * a fragment wrongly left in the queue costs a second look, a fragment wrongly called SOLVED is work
  * silently dropped.
  *
  * Both sides also lose `\cmd`, because deMarkup already took it off the needle and a Scala escape is
  * not a LaTeX command: `println(s"\n--- FREKVENSANALYS AV:\n\$s")` stands verbatim in the English pdf,
  * but deMarkup ate the two `\n` while the extract kept their `n`, and the row read SOLVED. */
def squash(s: String): String =
  raw"\\[A-Za-z]+".r.replaceAllIn(s, " ").toLowerCase.filter(_.isLetterOrDigit)

/** The squashed extract, plus the map back from each squashed character to where it came from in the
  * original. Squashing is what makes the match robust and also what makes a match unreadable: the only
  * way to judge an "open" verdict is to see the text it matched, in the pdf's own words. */
final case class Pdf(raw: String, squashed: String, origin: Array[Int])

def preparePdf(text: String): Pdf =
  val stripped = raw"\\[A-Za-z]+".r.replaceAllIn(text, " ")
  val sb = StringBuilder()
  val idx = Array.newBuilder[Int]
  // `stripped` and `text` differ in length, so positions are taken in `stripped` and the context is
  // read from `stripped` too -- close enough to the pdf's words, and always self-consistent.
  for (c, i) <- stripped.zipWithIndex do
    val lower = c.toLower
    if lower.isLetterOrDigit then { sb += lower; idx += i }
  Pdf(stripped, sb.result(), idx.result())

/** Is the fragment still in the pdf, and if so, what did it match? The context is the point: a short
  * fragment can match inside a longer word -- the heading `Bakgrund` matches `bakgrund(svart)`, a Kojo
  * command that is out of scope -- and without seeing the hit there is no way to tell a real leftover
  * from a coincidence. */
def stillInPdf(pdf: Option[Pdf], text: String): Option[(Boolean, String)] =
  pdf.map: p =>
    val n = squash(deMarkup(text))
    val at = p.squashed.indexOf(n)
    if at < 0 then (false, "")
    else
      val from = p.origin(at)
      val to = p.origin(math.min(at + n.length, p.origin.length - 1))
      val ctx = p.raw.slice(math.max(0, from - 25), math.min(p.raw.length, to + 25))
      (true, raw"\s+".r.replaceAllIn(ctx, " ").trim)

/** The tree a located fragment lives in. `--pdf` takes ONE pdf, so absence only means something for
  * fragments that pdf could have contained: a slides-only line is missing from the compendium pdf
  * whatever anyone does to it. */
def inCompendium(hits: Vector[Hit]): Boolean =
  hits.exists(h => h.rel.startsWith("compendium/") || h.rel.startsWith("compendium-en/"))

/** Replica of Translate.scala's private `scalaCodeEnvs` (Translate.scala:682) -- keep in sync, the way
  * unit-probe.scala replicates leadLen/trailStart. This is the set the RENAME PASS runs in, which is
  * the question this tool asks. Latex.verbatimEnvs is a wider set: it also masks tikz, algorithm and
  * the like, where a glossary entry would never reach, so using it suggested CodeGlossary for lines no
  * rename can touch and inflated the model-free count. */
val scalaCodeEnvs: Set[String] =
  Set("Code", "CodeSmall", "REPL", "REPLnonum", "REPLsmall", "lstlisting", "Trace", "Output")

/** Mark each line as inside a Scala code environment. */
def codeFlags(lines: Vector[String]): Vector[Boolean] =
  val beginRx = raw"\\begin\{([A-Za-z*]+)\}".r
  val endRx = raw"\\end\{([A-Za-z*]+)\}".r
  var depth = 0
  lines.map: ln =>
    val opened = beginRx.findAllMatchIn(ln).count(m => scalaCodeEnvs.contains(m.group(1)))
    val closed = endRx.findAllMatchIn(ln).count(m => scalaCodeEnvs.contains(m.group(1)))
    val wasIn = depth > 0
    depth = math.max(0, depth + opened - closed)
    // a line carrying \begin{Code} is chrome, not content; a line inside the env is content
    wasIn

/** The contents of inline code spans on a line: \code{..}, \jcode{..}, \lstinline{..} and their
  * pipe-delimited forms. The rename pass reaches these too (Translate.scala's inlineCodeRe), so a
  * fragment sitting inside one is code even though it stands in running prose -- which the
  * environment tracking alone cannot see. */
def inlineCode(line: String): Vector[String] =
  val brace = raw"\\(?:code|jcode|lstinline)\*?\{([^{}]*)\}".r
  val pipe = raw"\\(?:code|jcode|lstinline|verb)\*?\|([^|]*)\|".r
  (brace.findAllMatchIn(line) ++ pipe.findAllMatchIn(line)).map(_.group(1)).toVector

def readSrc(root: Path, rel: String): Src =
  val lines = String(Files.readAllBytes(root.resolve(rel)), "UTF-8").linesIterator.toVector
  Src(rel, lines, codeFlags(lines), lines.map(deMarkup))

/** `.cls` as well as `.tex`, and that is not a detail. `compendium/compendium.cls` is copied BYTE-FOR-BYTE
  * into `compendium-en/` -- the two are identical files -- so no translation pass touches it. It gets away
  * with that today because every user-visible string in it is already wrapped in `\ifswedish ... \else ...
  * \fi` and the English document injects `\swedishfalse`; the 32 remaining Swedish hits are all in trailing
  * `%` comments, which never render. But a NEW string added there without an `\else` branch would print
  * Swedish in the English pdf, and nothing would catch it: the mirror copies the file verbatim, the
  * glossary and Overrides never see it, and this tool used to report the fragment as "not located in
  * source or mirror". Indexing `.cls` turns that silent case into a located one with its own mechanism. */
def texUnder(root: Path, dirs: Seq[String]): Vector[Src] =
  dirs.toVector.flatMap: d =>
    val base = root.resolve(d)
    if !Files.isDirectory(base) then Vector.empty
    else
      scala.util.Using.resource(Files.walk(base))(_.iterator.asScala.toVector)
        // `old-*.tex` are superseded DUPLICATES of live exercise content that nothing \inputs -- but the
        // same examples still reach the pdf through the live file, e.g. the Rymdvarelse/huvud1 example is
        // in both old-classes-exercise and w05-classes-exercise. Since `old-` sorts first, keeping them
        // would attribute every such hit to a file nobody builds and send the fix to the wrong place.
        .filter(p => Files.isRegularFile(p) &&
          (p.toString.endsWith(".tex") || p.toString.endsWith(".cls") || p.toString.endsWith(".sty")))
        .filterNot(p => p.getFileName.toString.startsWith("old-"))
        .map(p => readSrc(root, root.relativize(p).toString))
        .sortBy(_.rel)

def findIn(idx: Vector[Src], needle: String): Vector[Hit] =
  val n = deMarkup(needle)
  if n.length < 5 then Vector.empty
  else
    for
      s <- idx
      (ln, i) <- s.flat.zipWithIndex
      if ln.contains(n)
    yield Hit(s.rel, i + 1, s.inCode(i))

/** The todo quotes the RENDERED pdf, so some fragments carry artefacts that exist in no source file:
  * a LaTeX-generated enumerator ("a)poäng > 100"), and a word hyphenated across a pdf line break
  * ("skriver math i sökru-"). Try the fragment stripped of each before giving up. */
def variants(text: String): Vector[String] =
  val t = text.trim
  val noEnum = raw"^[a-zA-Z]\)\s*".r.replaceFirstIn(t, "")
  val noHyphen = if t.endsWith("-") then t.dropRight(1).reverse.dropWhile(_ != ' ').reverse.trim else t
  // A pdf TABLE row pastes as its cells joined by runs of spaces ("cp orig kopia  Copy the file orig
  // to kopia."), and no single source line contains that whole string. Try the cells separately.
  val cells = if raw"\s{2,}".r.findFirstIn(t).isDefined then raw"\s{2,}".r.split(t).toVector else Vector.empty
  // A resolved cross-reference ends the fragment with a number LaTeX supplied ("...i kapitel 1"),
  // which is in no source file. Drop that last token -- but ONLY when it is a number, and only when
  // something substantial survives. Dropping it unconditionally turned `scala> singlaSlant` into
  // `scala>`, which matches the first REPL line in the corpus and reported a confidently wrong
  // file:line.
  val toks = t.split("\\s+").toVector
  val noLast =
    if toks.length >= 3 && toks.last.forall(c => c.isDigit || c == '.') then toks.init.mkString(" ")
    else t
  (Vector(t, noEnum, noHyphen) ++ cells :+ noLast).map(_.trim).distinct.filter(_.length >= 5)

/** Second pass for fragments the todo quotes as one rendered sentence while the source hard-wraps it
  * across two lines. Only used when the single-line search finds nothing, so it cannot add noise. */
def findInPairs(idx: Vector[Src], needle: String): Vector[Hit] =
  val n = deMarkup(needle)
  if n.length < 5 then Vector.empty
  else
    for
      s <- idx
      i <- 0 until math.max(0, s.flat.size - 1)
      if norm(s.flat(i) + " " + s.flat(i + 1)).contains(n)
    yield Hit(s.rel, i + 1, s.inCode(i))

enum Kind:
  case CodeIdent, CodeString, CodeComment, CodeLine, CodeCovered, CodeOther, Glossed, ProseSv,
    Pseudocode, Url, EnInSv, EnSide, Unclear, TodoNote, Noise, ClassFile, NotFound

def mechanism(k: Kind): String = k match
  case Kind.CodeIdent   => "CodeGlossary.id / .perFileId"
  case Kind.CodeString  => "CodeGlossary.codeStr"
  case Kind.CodeComment => "Overrides (unit-probe key)"
  case Kind.CodeLine    => "CodeGlossary.id / .perFileId (tokens listed)"
  case Kind.CodeCovered => "none: every name here is settled (in the glossary, or ruled English)"
  case Kind.CodeOther   => "inspect: code region, no Swedish token found"
  case Kind.TodoNote    => "none: a note in the inventory, not a fragment"
  case Kind.Noise       => "none: punctuation or a stray line from a pasted transcript"
  case Kind.Glossed     => "issue-981 transform (source already names the English)"
  case Kind.ProseSv     => "Overrides (unit-probe key)"
  case Kind.Pseudocode  => "ASK BR: algorithm block, no mechanism reaches it"
  case Kind.Url         => "ASK BR: Swedish URL, source change"
  // Both of these used to name a CAUSE the test does not establish, which sent the reader of the report
  // looking for the wrong thing. They now report the EVIDENCE, with the populations that actually turn
  // up in it. The distinction matters because each bucket is two different jobs wearing one name.
  //
  // EnInSv was "source fix (English already, merely wrong)". The test is only: found in the Swedish
  // source, in prose, and SwedishScore says NOT Swedish. In this corpus the larger population is the
  // second kind -- `programmeringsparadigm`, `datatyp`, `Hemkatalog`, `antalet heltal` and the
  // placeholder templates (`paketnamn.objektnamn.metodnamn`) are Swedish compounds with no function
  // words for the scorer to catch, not English that went wrong.
  case Kind.EnInSv      =>
    "in Swedish source, scored NOT Swedish: English that is wrong, OR Swedish the scorer missed"
  // EnSide was "defect exists only in the English". The test is: NOT found in the Swedish source, but
  // found in the mirror. Usually that is English-side damage (`for-satement`, `Simpleidentifier`), but
  // it is also exactly what a STALE log entry looks like once the Swedish has moved -- and the #993
  // identifier batches moved a great deal of it. Across one consolidated build EnSide fell 158 -> 96
  // while NotFound rose 66 -> 117 and CodeCovered 12 -> 227; those rows were neither fixed nor broken,
  // they stopped matching. Anyone working this bucket has to expect stale rows in it.
  case Kind.EnSide      =>
    "in mirror, not in Swedish source: English-side damage, OR the Swedish moved since the log"
  case Kind.Unclear     => "manual: Swedish verdict weak"
  // No glossary entry and no Overrides key can reach a .cls: the mirror copies it verbatim, so the
  // only repair is in the class itself, and it has to be the bilingual form rather than a translation --
  // the SAME file is used by the Swedish build.
  case Kind.ClassFile   => "source fix in the .cls: wrap it in \\ifswedish ... \\else ... \\fi"
  case Kind.NotFound    => "manual: not located in source or mirror"

/** Classify one fragment given where it was found. */
def classify(text: String, svHits: Vector[Hit], enHits: Vector[Hit], srcLine: Option[String],
    lists: SwedishScore.Lists): Kind =
  val t = norm(text)
  val sc = SwedishScore.score(t, lists)
  val verdict = SwedishScore.verdict(sc)
  val line = srcLine.getOrElse("")
  // a lone `}` or `|` is a stray line from a pasted transcript, not a defect. Separated from TodoNote
  // so that category means "the inventory's own words" and stays small enough to read.
  if t.count(_.isLetter) < 2 then Kind.Noise
  // a URL is a URL whether or not the search located it: \url{...} with accent escapes often defeats
  // the flattened match, and the verdict does not depend on finding it.
  else if t.contains("http") || t.contains("wikipedia.org") then Kind.Url
  else if svHits.isEmpty then
    if enHits.nonEmpty then Kind.EnSide
    // Not in either tree. Call it the inventory's own voice only on POSITIVE English evidence --
    // "Column Swedish name can be completely removed in English studyguide" scores en >> sv. Merely
    // failing to look Swedish is not enough: "Studera begreppen i kapitel 1" is Swedish that happens
    // to carry no function words the detector knows, and calling that commentary hides real work.
    // ...and never on a code fragment. Scala keywords ARE English function words -- `if`, `then`,
    // `else`, `def` -- so any transcript line scores as confident English and would be written off as
    // commentary. A `scala>` line that is in neither tree means the mirror is stale or built on
    // another branch, which is work to redo, not a note to ignore.
    else if looksLikeScala(t) then Kind.NotFound
    else if sc.en > sc.strong then Kind.TodoNote
    else Kind.NotFound
  // ClassFile only when the class file is the ONLY place it was found. A `.cls` hit must not outrank a
  // `.tex` one: `while` occurs once in compendium.cls and 218 times in the .tex files, and letting the
  // class win sent a plain code fragment to the wrong mechanism. If it is in a .tex, fix it there.
  else if svHits.forall(h => h.rel.endsWith(".cls") || h.rel.endsWith(".sty")) then Kind.ClassFile
  else if t.contains("Indata") || t.contains("Utdata") || t.contains("\u2190") then Kind.Pseudocode
  else if t.contains("http") || t.contains("wikipedia.org") then Kind.Url
  // inline \code{..} in running prose counts as code here: the rename pass reaches it (Translate's
  // inlineCodeRe), so a fragment quoted from inside one wants a glossary entry, not an override.
  // Environment tracking alone never sees these, because the line itself is prose.
  else if svHits.exists(_.inCode) || inlineCode(line).exists(sp => norm(sp).contains(t)) then
    // a comment marker before the fragment: `//` in Scala, `#` in the shell/terminal listings that
    // make up most of the w01 lab. Both are prose inside a code env, so both are Overrides work.
    val markerAt = Vector(line.indexOf("//"), line.indexOf("#")).filter(_ >= 0)
    val afterComment = markerAt.nonEmpty && markerAt.min < math.max(0, line.indexOf(t.take(12)))
      || t.startsWith("#") || t.startsWith("//")
    val quoted = raw""""[^"]*"""".r.findAllIn(line).exists(q => norm(q).contains(t))
    val c = candidates(t, svHits.headOption.map(_.rel).getOrElse(""))
    if afterComment then Kind.CodeComment
    else if quoted then Kind.CodeString
    else if !t.contains(" ") then Kind.CodeIdent
    else if !c.nothingOpen then Kind.CodeLine
    // every name on the line is settled -- in the glossary, or ruled already-English. That is "done",
    // a different thing from "nothing Swedish found here", and it was reported as the latter.
    else if c.settled.nonEmpty then Kind.CodeCovered
    else Kind.CodeOther
  else if line.contains("\\Eng{") then Kind.Glossed
  else
    verdict match
      case SwedishScore.Verdict.Swedish    => Kind.ProseSv
      case SwedishScore.Verdict.NotSwedish => Kind.EnInSv
      case SwedishScore.Verdict.Weak       => Kind.Unclear

/** For a hit in the generated mirror, the Swedish line it was generated from. The mirror preserves line
  * structure, so the same line number in the corresponding source file is the counterpart -- which is
  * what an EnSide row needs, because an override has to be keyed on the SWEDISH text, and the log
  * quotes the mangled English.
  *
  * The mirror does NOT preserve line numbers: w01-intro-exercise sv:1607 is en:1602, sv:902 is en:897,
  * intro.tex:569 is intro-en.tex:570 -- it drifts both ways as clamps and masked blocks change length.
  * Taking the same number printed an unrelated line, or an empty one, and a wrong line makes a wrong
  * override key. So search a window for the line sharing the most distinctive tokens, and return
  * nothing when the best candidate shares too little: silence beats a plausible wrong key. */
def svCounterpart(sv: Vector[Src], enRel: String, line: Int, enText: String): Option[(Int, String)] =
  val rel = enRel.replace("compendium-en/", "compendium/").replace("slides-en/", "slides/")
    .replaceAll("-en\\.tex$", ".tex")
  val distinctive = raw"[A-Za-zÅÄÖåäö_][A-Za-z0-9ÅÄÖåäö_]{3,}|\d+".r
  def keys(s: String): Set[String] = distinctive.findAllIn(s).map(_.toLowerCase).toSet
  val want = keys(enText)
  sv.find(_.rel == rel).flatMap: s =>
    val lo = math.max(0, line - 1 - 25)
    val hi = math.min(s.lines.size - 1, line - 1 + 25)
    if lo > hi || want.isEmpty then None
    else
      val (bestIdx, bestScore) = (lo to hi).map(i => (i, (keys(s.lines(i)) & want).size)).maxBy((_, n) => n)
      // two shared distinctive tokens is the floor; below that it is a guess dressed as evidence
      if bestScore >= 2 then Some((bestIdx + 1, s.lines(bestIdx).trim)) else None

def parseTodo(path: Path): Vector[Entry] =
  val lines = String(Files.readAllBytes(path), "UTF-8").linesIterator.toVector
  var section = ""
  var task = ""
  var expect: Option[String] = None
  var expectStatus: Option[String] = None
  // What groups lines into one issue is a TRANSCRIPT, not mere adjacency. A `scala>` line opens one
  // and the following lines join it -- the other prompts, the echoed values, the error gutter -- until
  // a marker, a blank line, or a shell comment, which is a different medium. Grouping on adjacency
  // alone swallowed fifteen independent `# ...` lab fragments into a single "issue", because the log
  // writes them under one header with no blank lines.
  var issue = 0
  var inTranscript = false
  val out = Vector.newBuilder[Entry]
  for (raw, i) <- lines.zipWithIndex do
    val ln = raw.stripTrailing
    ln match
      case _ if ln.trim.isEmpty      => issue += 1; inTranscript = false
      // `##` starts a comment, so a fixture can say what each line tests. NOT a single `#`: the w01
      // lab's fragments are shell comments (`# gå till katalogen`), and treating those as comments
      // silently deleted ten real rows from the log and took CodeComment from 16 to 7.
      // `##expect KIND` binds to the next fragment; --selftest checks it. Everything else after ## is
      // a comment, which is what lets the fixture explain each case next to it.
      // `##expect-status` must be tested BEFORE `##expect`, or it parses as a kind called "-status ..."
      case _ if ln.trim.startsWith("##expect-status") =>
        expectStatus = Some(ln.trim.stripPrefix("##expect-status").trim)
      case _ if ln.trim.startsWith("##expect") =>
        expect = Some(ln.trim.stripPrefix("##expect").trim)
      case _ if ln.trim.startsWith("##") => ()
      case ChapterRx(n)                 => section = n; task = ""; issue += 1; inTranscript = false
      case TaskRx(n)                    => task = s"Task $n"; issue += 1; inTranscript = false
      case HeaderRx(sec, rest) if looksLikeHeader(rest) =>
        section = sec; task = ""; issue += 1; inTranscript = false
        // a header may carry a note on the same line ("1.1.16 Column Swedish name can be removed")
        if rest.trim.length > 3 then out += Entry(i + 1, sec, "note", rest.trim, None, issue)
      case NoteRx(marker) => task = marker.trim; issue += 1; inTranscript = false
      case _ =>
        val t = ln.trim
        val opensTranscript = t.contains("scala>")
        val joins = inTranscript && !t.startsWith("#")
        if opensTranscript && !inTranscript then issue += 1
        else if !joins then issue += 1
        inTranscript = opensTranscript || joins
        out += Entry(i + 1, section, task, t, expect, issue, expectStatus)
        expect = None
        expectStatus = None
  out.result()

@main def todoTriage(args: String*): Unit =
  def opt(name: String, default: String): String =
    val i = args.indexOf(s"--$name")
    if i >= 0 && i + 1 < args.length then args(i + 1) else default

  if args.contains("--help") || args.contains("-h") then
    println("""todo-triage -- turn a reading log of Swedish in the built English pdf into a worklist
               |
               |  --todo <file>     the reading log (default: <root>/compendium-todo)
               |  --root <dir>      repo root (default: nearest ancestor of the cwd holding autotranslate/)
               |  --chapter <n>     only this chapter, matched on the chapter component
               |  --kind <NAME>     only this kind, e.g. EnSide, ProseSv, CodeLine, NotFound
               |  --pdf <file>      pdftotext output of the built COMPENDIUM English pdf; adds a status
               |                    column: open, SOLVED, or "-" when this pdf cannot say (a slides-only
               |                    fragment is missing from it however the mirror reads)
               |  --open            hide rows the pdf says are already solved
               |  --issues          one block per issue instead of per line; marks REPL transcripts
               |  --selftest        check every ##expect (and ##expect-status, with --pdf) in the log;
               |                    exit 1 on a mismatch. See scratch/todo-sample.txt
               |  --tokens          print the distinct-candidate table instead of per-fragment rows
               |  --hits            list every location, not just the first
               |  --report <file>   also write the output here
               |
               |Run with --main-class todoTriage: SwedishScore.scala carries its own main.""".stripMargin)
    sys.exit(0)

  // Default the root by walking up for the directory that holds autotranslate/, so the tool works
  // from anywhere in the tree rather than only from the repo root.
  def findRoot(start: Path): Path =
    Iterator
      .iterate(start)(_.getParent)
      .takeWhile(_ != null)
      .find(p => Files.isDirectory(p.resolve("autotranslate")))
      .getOrElse(start)
  val root =
    val given_ = opt("root", "")
    if given_.nonEmpty then Path.of(given_).toAbsolutePath.normalize
    else findRoot(Path.of(".").toAbsolutePath.normalize)
  val todo = Path.of(opt("todo", root.resolve("compendium-todo").toString))
  val chapterFilter = opt("chapter", "")
  val kindFilter = opt("kind", "")
  val report = opt("report", "")
  val showHits = args.contains("--hits")
  val openOnly = args.contains("--open")
  val pdfText =
    val p = opt("pdf", "")
    if p.isEmpty then None
    else if !Files.isRegularFile(Path.of(p)) then
      println(s"no pdf text at $p -- extract it first: pdftotext compendium-en/compendium-en.pdf out.txt")
      sys.exit(2)
    else Some(loadPdfText(Path.of(p)))

  if !Files.isRegularFile(todo) then
    println(s"no inventory at $todo")
    println("The inventory is a reading log of Swedish found in the BUILT English pdf, one")
    println("chapter.section[.subsection] header per location followed by the offending")
    println("fragments; `Task n` and `footnote n` narrow the location further. It is not")
    println("tracked in this repo -- pass your own with --todo <file>.")
    sys.exit(2)

  val lists = SwedishScore.load(root.resolve("autotranslate"))
  val sv = texUnder(root, Seq("compendium", "slides"))
  val en = texUnder(root, Seq("compendium-en", "slides-en"))
  // compare the chapter component, not a prefix: --chapter 1 must not also match 11.x through 14.x
  def chapterOf(sec: String): String = sec.takeWhile(_ != '.')
  val entries = parseTodo(todo).filter(e => chapterFilter.isEmpty || chapterOf(e.section) == chapterFilter)

  val rows = Vector.newBuilder[(Entry, Kind, Vector[Hit])]
  for e <- entries do
    def locate(idx: Vector[Src]): Vector[Hit] =
      variants(e.text).iterator
        .map(v => { val d = findIn(idx, v); if d.nonEmpty then d else findInPairs(idx, v) })
        .find(_.nonEmpty)
        .getOrElse(Vector.empty)
    val svHits = locate(sv)
    val enHits = if svHits.nonEmpty then Vector.empty else locate(en)
    val srcLine = svHits.headOption.flatMap: h =>
      sv.find(_.rel == h.rel).map(_.lines(h.line - 1))
    rows += ((e, classify(e.text, svHits, enHits, srcLine, lists), if svHits.nonEmpty then svHits else enHits))
  val all = rows.result().filter((_, k, _) => kindFilter.isEmpty || k.toString == kindFilter)

  val sb = StringBuilder()
  def emit(s: String): Unit = { println(s); sb ++= s; sb += '\n' }

  emit(s"# todo-triage: ${entries.size} fragments from ${todo.getFileName}" +
    (if chapterFilter.nonEmpty then s", chapter $chapterFilter" else ""))
  emit(s"# ${sv.size} Swedish .tex indexed, ${en.size} mirror .tex indexed")
  // EnSide depends entirely on the generated mirror matching the checked-out branch. A mirror built on
  // another branch silently moves rows to NotFound -- `singlaSlant` vanished from both trees that way,
  // because the mirror on disk had been generated where it is already `flipCoin`.
  // .tex only: the trees also hold pdf, aux and log files whose timestamps have nothing to do with
  // whether the mirror was regenerated, so comparing everything both warned falsely and, worse,
  // hid a stale mirror behind a freshly written .log.
  def newest(dirs: Seq[String]): Long =
    dirs.flatMap: d =>
      val base = root.resolve(d)
      if !Files.isDirectory(base) then Seq(0L)
      else
        scala.util.Using.resource(Files.walk(base)): stream =>
          stream.iterator.asScala
            // same set texUnder indexes: a .cls edit can leak Swedish too, so it counts as source
            .filter(p => Seq(".tex", ".cls", ".sty").exists(p.toString.endsWith))
            .map(p => Files.getLastModifiedTime(p).toMillis)
            .toSeq
    .maxOption.getOrElse(0L)
  val (svAge, enAge) = (newest(Seq("compendium", "slides")), newest(Seq("compendium-en", "slides-en")))
  if enAge < svAge then
    emit(s"# WARNING: the mirror is OLDER than the source (${(svAge - enAge) / 60000} min). Regenerate")
    emit("#   before trusting EnSide, or a mirror built on another branch will read as NotFound.")
  emit("")
  // Kinds that should leave a visible trace in the built pdf, and so can be asked whether they are
  // still there. A remark, a stray brace or a pdf artefact never appears as such, so asking is
  // meaningless and the status stays "-".
  val checkable = Set(Kind.CodeIdent, Kind.CodeString, Kind.CodeComment, Kind.CodeLine,
    Kind.CodeCovered, Kind.CodeOther, Kind.Glossed, Kind.ProseSv, Kind.Pseudocode, Kind.EnInSv,
    Kind.EnSide, Kind.Unclear, Kind.ClassFile)
  val pdf = pdfText.map(preparePdf)
  // "not in the compendium pdf" is evidence of a fix only for a fragment the compendium pdf could have
  // held. 49 of the first 81 SOLVED rows were located only under slides/, where absence means nothing,
  // so they now read "-" (unknown to this oracle) rather than claiming work that was never done.
  def statusOf(e: Entry, k: Kind, hits: Vector[Hit]): String =
    if !checkable.contains(k) then "-"
    else
      stillInPdf(pdf, e.text) match
        case None => "-"
        case Some((true, _)) => "open"
        case Some((false, _)) => if inCompendium(hits) then "SOLVED" else "-"

  /** The pdf text behind an `open` verdict, shown when the match is weak enough to doubt: a needle of
    * only a few characters can land inside a longer word. The heading `Bakgrund` matched
    * `bakgrund(svart)`, a Kojo command, and read as unfixed work after it had in fact been fixed.
    * Kojo's Swedish command names are deliberately out of scope for this log -- they are a real API,
    * renaming them would break them, and the maintainer owns that question -- so without the evidence
    * column that row looked like a defect twice over. */
  def evidence(e: Entry, k: Kind, hits: Vector[Hit]): String =
    if statusOf(e, k, hits) != "open" then ""
    else
      val n = squash(deMarkup(e.text))
      if n.length >= 16 then ""
      else stillInPdf(pdf, e.text).filter(_._1).map((_, ctx) => s" pdf:\"$ctx\"").getOrElse("")

  val shown = if openOnly then all.filter((e, k, h) => statusOf(e, k, h) != "SOLVED") else all

  emit("section\ttask\tkind\tstatus\twhere\tmechanism\ttokens\tfragment")
  for (e, k, hits) <- shown do
    // `where` must point at the file the mechanism applies to. compendium.cls sorts before
    // compendium/modules/, so a fragment that merely also occurs in the class would otherwise show the
    // class as its location while being bucketed for a .tex fix.
    val primary =
      if k == Kind.ClassFile then hits.headOption
      else hits.find(h => !(h.rel.endsWith(".cls") || h.rel.endsWith(".sty"))).orElse(hits.headOption)
    val where = primary.map(h => s"${h.rel}:${h.line}").getOrElse("-")
    // A fragment found in BOTH a .tex and the class file keeps its .tex bucket -- that is where a
    // glossary or Overrides fix belongs -- but the class hit still has to be visible, because the
    // rendered text may be coming from the macro rather than from the .tex. `Bakgrund` is exactly that:
    // the `Background` environment in compendium.cls types `\emph{Bakgrund:}` with no \else branch.
    val clsAlso =
      if k == Kind.ClassFile then ""
      else hits.find(h => h.rel.endsWith(".cls") || h.rel.endsWith(".sty"))
        .map(h => s" +cls:${h.rel.split('/').last}:${h.line}").getOrElse("")
    val extra = (if hits.size > 1 then s" (+${hits.size - 1})" else "") + clsAlso
    val toks =
      // CodeCovered belongs here too: a settled row has to say WHY it is settled, or it reads as an
      // unexplained verdict.
      if Set(Kind.CodeLine, Kind.CodeIdent, Kind.CodeString, Kind.CodeOther, Kind.CodeCovered)
          .contains(k)
      then
        val c = candidates(e.text, hits.headOption.map(_.rel).getOrElse(""))
        Vector(
          if c.open.nonEmpty then s"id:${c.open.mkString(",")}" else "",
          if c.strings.nonEmpty then s"str:${c.strings.mkString(",")}" else "",
          if c.covered.nonEmpty then s"done:${c.covered.mkString(",")}" else "",
          // a settled decision, shown rather than silently filtered away
          if c.english.nonEmpty then s"english:${c.english.mkString(",")}" else "",
        ).filter(_.nonEmpty).mkString(" ")
      else ""
    // an EnSide fragment is mangled English, so show the Swedish it came from: that is the override key
    val svText =
      if k == Kind.EnSide then
        hits.headOption
          .flatMap(h => svCounterpart(sv, h.rel, h.line, e.text))
          .map((ln, t) => s"  SV $ln: ${t.take(80)}")
      else None
    emit(s"${e.section}\t${e.task}\t$k\t${statusOf(e, k, hits)}\t$where$extra\t${mechanism(k)}" +
      s"\t$toks${evidence(e, k, hits)}\t" + e.text.take(90))
    for s <- svText do emit(s"\t\t\t\t\t\t\t$s")
    if showHits then for h <- hits.drop(1) do emit(s"\t\t\t\t${h.rel}:${h.line}\t\t")

  emit("")
  emit("# counts by kind")
  for (k, n) <- all.groupBy((_, k, _) => k).view.mapValues(_.size).toVector.sortBy(-_._2) do
    emit(f"#   $k%-12s $n%4d   ${mechanism(k)}")
  val modelFree = all.count((_, k, _) =>
    Set(Kind.CodeIdent, Kind.CodeString, Kind.CodeComment, Kind.CodeLine, Kind.ProseSv, Kind.Glossed)
      .contains(k))
  emit(s"# model-free with an established mechanism: $modelFree of ${all.size}")
  // Issues, not lines, are the unit of work: a REPL session written over six lines is one fix, and
  // has to be made as one, because the names in the input and in the echoed output must agree and the
  // result must still compile. Counting lines overstates the job and invites fixing half a transcript.
  val issues = all.groupBy((e, _, _) => e.issue)
  val multiLine = issues.count((_, rows) => rows.size > 1)
  emit(s"# ${issues.size} issues across ${all.size} fragment lines" +
    s" ($multiLine span several lines, most often one REPL session)")
  // What is left to do, as opposed to what was once written down. Without the pdf the tool can only
  // report settled-by-name, which is a small fraction: a prose fix lands in the cache or an override
  // where no name-based check can see it, so those rows read as open work forever.
  for _ <- pdfText do
    val checkableRows = all.filter((_, k, _) => checkable.contains(k))
    val judged = checkableRows.map((e, k, h) => statusOf(e, k, h))
    val solved = judged.count(_ == "SOLVED")
    val open = judged.count(_ == "open")
    emit(s"# against the built pdf: $solved SOLVED, $open still visible to a reader" +
      s" (${checkableRows.size - solved - open} of ${checkableRows.size} checkable fragments are" +
      " outside this pdf -- slides-only, so it cannot say)")
  if pdfText.isEmpty then
    emit("# no --pdf given: status is unknown, so every row reads as open. Extract the built pdf with")
    emit("#   pdftotext compendium-en/compendium-en.pdf /tmp/en.txt   and pass --pdf /tmp/en.txt")

  // --issues: one block per issue rather than one row per line, which is how the work is actually
  // taken on. A block that holds several code lines is a transcript: fix it as a unit, keep the
  // identifiers in the input and the echo in step, and run the phase 1-3 compile gates afterwards.
  if args.contains("--issues") then
    emit("")
    // --open applies here too, or the work queue still lists what is already done. An issue counts as
    // done only when the pdf says so for at least one of its lines and contradicts none: a row whose
    // status is "-" is unknown, not finished, so an issue made only of those stays in the queue. A
    // part-done transcript is kept WHOLE, with its finished lines marked, because the rest has to be
    // fixed against what is already there.
    def issueOpen(rows: Vector[(Entry, Kind, Vector[Hit])]): Boolean =
      val st = rows.map((e, k, h) => statusOf(e, k, h))
      st.contains("open") || !st.contains("SOLVED")
    val issueList = issues.toVector.sortBy((id, _) => id)
      .filter((_, rows) => !openOnly || issueOpen(rows))
    emit(s"# ${issueList.size} issues, in log order" +
      (if openOnly then s" (--open: ${issues.size - issueList.size} fully solved ones hidden)" else ""))
    for (id, rows) <- issueList do
      val (e0, _, _) = rows.head
      val kinds = rows.map((_, k, _) => k.toString).distinct.sorted.mkString(",")
      val where = rows.flatMap((_, _, hs) => hs.headOption).headOption
        .map(h => s"${h.rel}:${h.line}").getOrElse("-")
      val status = rows.map((e, k, h) => statusOf(e, k, h)).distinct
      val stat = if status.contains("open") then "open" else status.mkString(",")
      val repl = rows.count((e, _, _) => looksLikeScala(e.text)) > 1
      emit(s"")
      emit(s"# issue $id  ${e0.section} ${e0.task}  $stat  $kinds  $where" +
        (if repl then "   [transcript: fix as one unit, then run the phase 1-3 gates]" else ""))
      for (e, k, hs) <- rows do
        val mark = if statusOf(e, k, hs) == "SOLVED" then "  done" else "      "
        emit(s"  $mark ${e.text.take(100)}")

  // --selftest: check every `##expect KIND` in the log against what this run actually classified, and
  // exit non-zero on any mismatch. Without it the sample log is an example rather than a regression
  // fixture -- a change could silently re-break one of the reviewed bugs and the output would still
  // look reasonable to anyone who did not already know what each line should produce.
  if args.contains("--selftest") then
    val checked = all.collect { case (e, k, _) if e.expect.isDefined => (e, k, e.expect.get) }
    val failed = checked.filter((_, k, want) => k.toString != want)
    emit("")
    emit(s"# selftest: ${checked.size} expectations, ${failed.size} failed")
    for (e, k, want) <- failed do
      emit(s"#   FAIL line ${e.todoLine}: expected $want, got $k   ${e.text.take(70)}")
    if checked.isEmpty then
      emit("#   (no ##expect lines in this log -- selftest is for the sample fixture)")
    // `##expect-status` covers the OTHER question, and it is the one that was answered wrongly 74 times
    // out of 81: not "which mechanism fixes this" but "is it still in front of a reader". It needs a
    // pdf to check against, so the fixture ships a pretend extract rather than requiring a build.
    val statusChecked =
      all.collect { case (e, k, h) if e.expectStatus.isDefined => (e, statusOf(e, k, h), e.expectStatus.get) }
    if statusChecked.nonEmpty then
      if pdfText.isEmpty then
        emit(s"# selftest: ${statusChecked.size} status expectations SKIPPED -- pass --pdf to check them")
        emit("#   --pdf autotranslate/scratch/todo-sample-pdf.txt")
      else
        val statusFailed = statusChecked.filter((_, got, want) => got != want)
        emit(s"# selftest: ${statusChecked.size} status expectations, ${statusFailed.size} failed")
        for (e, got, want) <- statusFailed do
          emit(s"#   FAIL line ${e.todoLine}: expected status $want, got $got   ${e.text.take(60)}")
        if statusFailed.nonEmpty then sys.exit(1)
    if failed.nonEmpty then sys.exit(1)

  // --tokens: the ratification table. The fragment count overstates the work badly, because the same
  // demo name recurs across lines and weeks -- one `öka` entry answers dozens of rows. What BR has to
  // ratify is this list, not the fragments, and seeing every section a name appears in is what decides
  // global versus perFileId.
  if args.contains("--tokens") then
    final case class Agg(n: Int, secs: Set[String], files: Set[String])
    def add(m: Map[String, Agg], k: String, sec: String, file: String): Map[String, Agg] =
      val a = m.getOrElse(k, Agg(0, Set.empty, Set.empty))
      m.updated(k, Agg(a.n + 1, a.secs + sec, a.files + file))
    var ids = Map.empty[String, Agg]
    var strs = Map.empty[String, Agg]
    var done = Map.empty[String, Agg]
    var ruled = Map.empty[String, Agg]
    val codeKinds = Set(Kind.CodeLine, Kind.CodeIdent, Kind.CodeString)
    // EnSide rows are English prose from the mirror, so every English word in them would read as a
    // rename candidate -- `the` and `weight` topped the list before this. Comment and pseudocode lines
    // contribute their prose words the same way. So aggregate only over fragments that actually look
    // like Scala, and drop English function words, which no Swedish demo name is.
    // A lone identifier IS a candidate -- the old local test demanded `=`, `(`, `scala>` or a
    // def/val/var opener, so a fragment that is just `värdeSaknas` was dropped from the table it
    // most belongs in. CodeIdent rows are therefore always included, and everything else goes
    // through the shared looksLikeScala.
    for (e, k, hits) <- all if codeKinds.contains(k) && (k == Kind.CodeIdent || looksLikeScala(e.text))
    do
      val file = hits.headOption.map(_.rel.split("/").last).getOrElse("-")
      val c = candidates(e.text, hits.headOption.map(_.rel).getOrElse(""))
      // A glossary VALUE is an English target, never a rename candidate. They turn up because a clamped
      // file carries English in its \else branch, so the source itself contains `weight`, `Cucumber`...
      val englishTargets = CodeGlossary.id.values.toSet ++ CodeGlossary.codeStr.values.toSet
      val i = c.open.filterNot(t => lists.english.contains(t.toLowerCase) || englishTargets.contains(t))
      for t <- i do ids = add(ids, t, e.section, file)
      for t <- c.strings do strs = add(strs, t, e.section, file)
      for t <- c.covered do done = add(done, t, e.section, file)
      for t <- c.english do ruled = add(ruled, t, e.section, file)
    def dump(title: String, m: Map[String, Agg]): Unit =
      emit("")
      emit(s"# $title: ${m.size} distinct")
      for (t, a) <- m.toVector.sortBy((t, a) => (-a.n, t)) do
        val where = a.files.toVector.sorted.take(2).mkString(",")
        val secs = a.secs.toVector.sorted.take(5).mkString(" ")
        emit(f"#   ${a.n}%4d  $t%-30s $where%-46s $secs")
    dump("identifier candidates (need ratifying)", ids)
    dump("string-literal candidates (codeStr; blocked on #990)", strs)
    dump("already covered by the glossary", done)
    dump("ruled already-English, no rename needed", ruled)

  if report.nonEmpty then
    Files.write(Path.of(report), sb.toString.getBytes("UTF-8"))
    println(s"(report written to $report)")
