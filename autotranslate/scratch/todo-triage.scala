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

final case class Entry(todoLine: Int, section: String, task: String, text: String)
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
  norm(t)

// A section header needs at least one DOT: a bare leading digit is far more likely to be a REPL error
// gutter ("1 |val djurbur: Bur[Djur] = ...") than a heading, and treating those as headers silently
// re-labels every following fragment with the wrong chapter. Learned by doing exactly that.
val HeaderRx = raw"^\s*(-?\d+(?:\.\d+)+)\s*(.*)$$".r
val ChapterRx = raw"^\s*Chapter\s+(\d+)\b.*$$".r
val TaskRx = raw"^\s*[Tt]ask\s+(\d+)\b.*$$".r
// `footnote 1` / `bullet 3` are LOCATION markers, not commentary: they say which footnote or bullet of
// the current section the following fragment sits in. Keeping them is what makes the entry findable.
val NoteRx = raw"^\s*((?:footnote|bullet)\s*\d*)\s*$$".r

/** A dotted number is only a header if what follows does not look like code. */
def looksLikeHeader(rest: String): Boolean =
  val r = rest.trim
  !r.startsWith("|") && !r.contains("scala>") && !r.startsWith("=") && !r.startsWith("(")

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
)

/** Candidate work inside a code line: identifier tokens that could need renaming, and the contents of
  * quoted literals, which are a different mechanism (codeStr, not id). Deliberately NOT a Swedishness
  * judgement -- SwedishScore's lists are FUNCTION words, which cannot see `dubblera` or `gurka`, and
  * every name needs BR's ratification regardless. So: surface candidates, exclude what is certainly
  * not one, and mark what the glossary already covers. */
def candidates(line: String): (Vector[String], Vector[String], Vector[String]) =
  val idRx = raw"[A-Za-zÅÄÖåäö_][A-Za-z0-9ÅÄÖåäö_]*".r
  val strRx = raw""""([^"]*)"""".r
  val strings = strRx.findAllMatchIn(line).map(_.group(1)).toVector.filter(_.trim.nonEmpty).distinct
  val inStrings = strings.mkString(" ")
  val idents = idRx
    .findAllIn(line)
    .toVector
    .distinct
    .filter(t => t.length > 2 && !scalaNoise.contains(t) && !raw"res\d+".r.matches(t))
    .filterNot(inStrings.contains)
  val (covered, fresh) = idents.partition(t => CodeGlossary.id.contains(t))
  (fresh, strings.filterNot(CodeGlossary.codeStr.contains), covered)

def norm(s: String): String = s.trim.replaceAll("\\s+", " ")

/** Mark each line as inside-code or not by tracking \begin/\end of the environments the mirror masks
  * verbatim. Reuses Latex.verbatimEnvs so this cannot drift from what the pipeline actually masks. */
def codeFlags(lines: Vector[String]): Vector[Boolean] =
  val beginRx = raw"\\begin\{([A-Za-z*]+)\}".r
  val endRx = raw"\\end\{([A-Za-z*]+)\}".r
  var depth = 0
  lines.map: ln =>
    val opened = beginRx.findAllMatchIn(ln).count(m => Latex.verbatimEnvs.contains(m.group(1)))
    val closed = endRx.findAllMatchIn(ln).count(m => Latex.verbatimEnvs.contains(m.group(1)))
    val wasIn = depth > 0
    depth = math.max(0, depth + opened - closed)
    // a line carrying \begin{Code} is chrome, not content; a line inside the env is content
    wasIn || (opened > 0 && closed == 0 && false)

def readSrc(root: Path, rel: String): Src =
  val lines = String(Files.readAllBytes(root.resolve(rel)), "UTF-8").linesIterator.toVector
  Src(rel, lines, codeFlags(lines), lines.map(deMarkup))

def texUnder(root: Path, dirs: Seq[String]): Vector[Src] =
  dirs.toVector.flatMap: d =>
    val base = root.resolve(d)
    if !Files.isDirectory(base) then Vector.empty
    else
      Files
        .walk(base)
        .iterator
        .asScala
        .toVector
        .filter(p => Files.isRegularFile(p) && p.toString.endsWith(".tex"))
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
  Vector(t, noEnum, noHyphen).distinct.filter(_.length >= 5)

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
  case CodeIdent, CodeString, CodeComment, CodeLine, CodeOther, Glossed, ProseSv, Pseudocode, Url,
    EnInSv, EnSide, Unclear, TodoNote, NotFound

def mechanism(k: Kind): String = k match
  case Kind.CodeIdent   => "CodeGlossary.id / .perFileId"
  case Kind.CodeString  => "CodeGlossary.codeStr"
  case Kind.CodeComment => "Overrides (unit-probe key)"
  case Kind.CodeLine    => "CodeGlossary.id / .perFileId (tokens listed)"
  case Kind.CodeOther   => "inspect: code region, no Swedish token found"
  case Kind.TodoNote    => "none: a note in the inventory, not a fragment"
  case Kind.Glossed     => "issue-981 transform (source already names the English)"
  case Kind.ProseSv     => "Overrides (unit-probe key)"
  case Kind.Pseudocode  => "ASK BR: algorithm block, no mechanism reaches it"
  case Kind.Url         => "ASK BR: Swedish URL, source change"
  case Kind.EnInSv      => "source fix (English already, merely wrong)"
  case Kind.EnSide      => "source fix or Overrides: defect exists only in the English"
  case Kind.Unclear     => "manual: Swedish verdict weak"
  case Kind.NotFound    => "manual: not located in source or mirror"

/** Classify one fragment given where it was found. */
def classify(text: String, svHits: Vector[Hit], enHits: Vector[Hit], srcLine: Option[String],
    lists: SwedishScore.Lists): Kind =
  val t = norm(text)
  val verdict = SwedishScore.verdict(SwedishScore.score(t, lists))
  val line = srcLine.getOrElse("")
  // a URL is a URL whether or not the search located it: \url{...} with accent escapes often defeats
  // the flattened match, and the verdict does not depend on finding it.
  if t.contains("http") || t.contains("wikipedia.org") then Kind.Url
  else if svHits.isEmpty then
    if enHits.nonEmpty then Kind.EnSide
    // not in either tree and not Swedish: almost always the inventory's own prose -- a question, a
    // heading like "Chapter 7", or a stray "|" from a pasted REPL gutter. Not work, so not a defect.
    else if verdict != SwedishScore.Verdict.Swedish then Kind.TodoNote
    else Kind.NotFound
  else if t.contains("Indata") || t.contains("Utdata") || t.contains("\u2190") then Kind.Pseudocode
  else if t.contains("http") || t.contains("wikipedia.org") then Kind.Url
  else if svHits.exists(_.inCode) then
    // a comment marker before the fragment: `//` in Scala, `#` in the shell/terminal listings that
    // make up most of the w01 lab. Both are prose inside a code env, so both are Overrides work.
    val markerAt = Vector(line.indexOf("//"), line.indexOf("#")).filter(_ >= 0)
    val afterComment = markerAt.nonEmpty && markerAt.min < math.max(0, line.indexOf(t.take(12)))
      || t.startsWith("#") || t.startsWith("//")
    val quoted = raw""""[^"]*"""".r.findAllIn(line).exists(q => norm(q).contains(t))
    val (idents, strings, _) = candidates(t)
    if afterComment then Kind.CodeComment
    else if quoted then Kind.CodeString
    else if !t.contains(" ") then Kind.CodeIdent
    else if idents.nonEmpty || strings.nonEmpty then Kind.CodeLine
    else Kind.CodeOther
  else if line.contains("\\Eng{") then Kind.Glossed
  else
    verdict match
      case SwedishScore.Verdict.Swedish    => Kind.ProseSv
      case SwedishScore.Verdict.NotSwedish => Kind.EnInSv
      case SwedishScore.Verdict.Weak       => Kind.Unclear

/** For a hit in the generated mirror, the Swedish line it was generated from. The mirror preserves line
  * structure, so the same line number in the corresponding source file is the counterpart -- which is
  * what an EnSide row needs, because an override has to be keyed on the SWEDISH text, and the todo
  * quotes the mangled English. */
def svCounterpart(sv: Vector[Src], enRel: String, line: Int): Option[String] =
  val rel = enRel.replace("compendium-en/", "compendium/").replace("slides-en/", "slides/")
    .replaceAll("-en\\.tex$", ".tex")
  sv.find(_.rel == rel).flatMap(s => if line <= s.lines.size then Some(s.lines(line - 1).trim) else None)

def parseTodo(path: Path): Vector[Entry] =
  val lines = String(Files.readAllBytes(path), "UTF-8").linesIterator.toVector
  var section = ""
  var task = ""
  val out = Vector.newBuilder[Entry]
  for (raw, i) <- lines.zipWithIndex do
    val ln = raw.stripTrailing
    ln match
      case _ if ln.trim.isEmpty => ()
      case ChapterRx(n)         => section = n; task = ""
      case TaskRx(n)            => task = s"Task $n"
      case HeaderRx(sec, rest) if looksLikeHeader(rest) =>
        section = sec; task = ""
        // a header may carry a note on the same line ("1.1.16 Column Swedish name can be removed")
        if rest.trim.length > 3 then out += Entry(i + 1, sec, "note", rest.trim)
      case NoteRx(marker) => task = marker.trim
      case _              => out += Entry(i + 1, section, task, ln.trim)
  out.result()

@main def todoTriage(args: String*): Unit =
  def opt(name: String, default: String): String =
    val i = args.indexOf(s"--$name")
    if i >= 0 && i + 1 < args.length then args(i + 1) else default
  val root = Path.of(opt("root", ".")).toAbsolutePath.normalize
  val todo = Path.of(opt("todo", root.resolve("compendium-todo").toString))
  val chapterFilter = opt("chapter", "")
  val kindFilter = opt("kind", "")
  val report = opt("report", "")
  val showHits = args.contains("--hits")

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
  emit("")
  emit("section\ttask\tkind\twhere\tmechanism\ttokens\tfragment")
  for (e, k, hits) <- all do
    val where = hits.headOption.map(h => s"${h.rel}:${h.line}").getOrElse("-")
    val extra = if hits.size > 1 then s" (+${hits.size - 1})" else ""
    val toks =
      if Set(Kind.CodeLine, Kind.CodeIdent, Kind.CodeString, Kind.CodeOther).contains(k) then
        val (idents, strings, covered) = candidates(e.text)
        Vector(
          if idents.nonEmpty then s"id:${idents.mkString(",")}" else "",
          if strings.nonEmpty then s"str:${strings.mkString(",")}" else "",
          if covered.nonEmpty then s"done:${covered.mkString(",")}" else "",
        ).filter(_.nonEmpty).mkString(" ")
      else ""
    // an EnSide fragment is mangled English, so show the Swedish it came from: that is the override key
    val svText =
      if k == Kind.EnSide then
        hits.headOption.flatMap(h => svCounterpart(sv, h.rel, h.line)).map(t => s"  SV: ${t.take(80)}")
      else None
    emit(s"${e.section}\t${e.task}\t$k\t$where$extra\t${mechanism(k)}\t$toks\t${e.text.take(90)}")
    for s <- svText do emit(s"\t\t\t\t\t\t$s")
    if showHits then for h <- hits.drop(1) do emit(s"\t\t\t${h.rel}:${h.line}\t\t")

  emit("")
  emit("# counts by kind")
  for (k, n) <- all.groupBy((_, k, _) => k).view.mapValues(_.size).toVector.sortBy(-_._2) do
    emit(f"#   $k%-12s $n%4d   ${mechanism(k)}")
  val modelFree = all.count((_, k, _) =>
    Set(Kind.CodeIdent, Kind.CodeString, Kind.CodeComment, Kind.CodeLine, Kind.ProseSv, Kind.Glossed)
      .contains(k))
  emit(s"# model-free with an established mechanism: $modelFree of ${all.size}")

  if report.nonEmpty then
    Files.write(Path.of(report), sb.toString.getBytes("UTF-8"))
    println(s"(report written to $report)")
