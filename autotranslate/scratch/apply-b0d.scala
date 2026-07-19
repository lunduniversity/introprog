//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8
//> using file ../Latex.scala
//> using file ../Code.scala
//> using file ../CodeGlossary.scala

// Fused B0+D applier (case (a): inline \code + verbatim code ENVS in .tex). For each code unit, compute the
// English via a ratified cluster glossary (identifier rename + string/comment replace), and clamp it as
// `\ifswedish<sv>\else<en>\fi` IFF the English is fully non-Swedish (auto-gate: a unit only partially covered
// by the glossary stays unclamped/Swedish — so no Swedish ever leaks into an \else branch). The clamp makes
// the SWEDISH build byte-identical; only the en branch changes. Reuses Latex.mask (locate) + the span-
// transform/restore round-trip + Code.swedishish (gate). DRY by default; --write applies.
//   scala-cli run autotranslate/scratch/apply-b0d.scala -- <file.tex> [--write]

import scala.util.matching.Regex

// glossary promoted to the production module autotranslate/CodeGlossary.scala (shared with the mirror).
val Glossary = CodeGlossary

// ALLOWLIST leak gate (BR-approved 2026-07-01). SOUND by construction: a clamped unit's English must contain
// NO token that isn't recognized English / Scala / glossary. Anything unknown is treated as SUSPECT SWEDISH and
// the unit stays Swedish (logged as a fall-through), so ALL diacritic-free Swedish is caught -- namn, smak, Fyle,
// KanSkalas -- INCLUDING invented pun-words that a blocklist or even a full Swedish dictionary can't. Cost: it
// over-skips genuinely-English-but-unlisted tokens (SAFE -- they just stay Swedish and surface in the fall-through
// report, which drives what to allowlist/glossary next). Contrast a blocklist (`namn` still leaked). English =
// /usr/share/dict/{american,british}-english; the report's fall-through vocabulary is the key review artefact.
object Allow:
  private def loadDict(p: String): Set[String] =
    val f = os.Path(p)
    if os.exists(f) then os.read.lines(f).iterator.map(_.trim.toLowerCase).filter(_.nonEmpty).toSet else Set.empty
  lazy val english: Set[String] =
    loadDict("/usr/share/dict/american-english") ++ loadDict("/usr/share/dict/british-english")
  // Scala keywords + common stdlib/abbreviations NOT in an English dictionary (English words are covered above):
  val scalaProg: Set[String] = Set(
    "def", "val", "var", "class", "trait", "object", "case", "match", "if", "then", "else", "for", "while", "do",
    "yield", "extends", "with", "given", "using", "import", "export", "package", "new", "this", "super", "sealed",
    "enum", "type", "lazy", "implicit", "override", "final", "private", "protected", "abstract", "null", "true",
    "false", "return", "throw", "try", "catch", "finally", "end", "derives", "opaque", "transparent", "inline",
    "erased", "infix", "as", "scala", "repl", "int", "str", "seq", "nil", "res", "args", "arg", "obj", "ctx",
    "idx", "tmp", "impl", "init", "len", "expr", "stmt", "env", "cfg", "config", "param", "params", "ptr", "ref",
    "num", "io", "os", "jvm", "sbt", "api", "cli", "url", "uri", "http", "https", "json", "html", "css", "xml",
    "ok", "todo", "fixme", "uuid", "foo", "bar", "baz", "qux", "std", "stdin", "stdout", "stderr", "util", "utils",
    "lib", "src", "dir", "msg", "err", "fn", "cb", "req", "resp", "elem", "elems", "acc", "iter", "prev", "curr",
    "vararg", "varargs", "lhs", "rhs", "xs", "ys", "gs", "kv", "toint", "tostring", "hashcode", "eq", "ne",
    // java/lib package parts + math abbreviations (false positives seen in the fall-through report):
    "java", "awt", "swing", "lang", "nio", "sql", "rgb", "nom", "denom", "div", "mul", "rem", "mod", "gcd",
    "lcm", "abs", "sqrt", "pow", "sin", "cos", "tan", "arr", "vec", "buf", "idx", "pos", "dim", "px",
    // Scala stdlib + English words the dict misses, and all-lowercase LaTeX listing/font option keys
    // captured from code-env optional args (\begin{Code}[basicstyle=...\ttfamily\selectfont]) — never Swedish:
    "println", "instantiated", "basicstyle", "ttfamily", "selectfont", "numberstyle", "unapply",
    // proper nouns kept verbatim in examples (BR): a personal-name demo, not a translatable identifier.
    "björn",
  )
  private def subwords(s: String): Iterator[String] =
    "[A-Za-zÅÄÖåäö_]+".r.findAllIn(s).iterator
      .flatMap(_.split("(?<=[a-zåäö])(?=[A-ZÅÄÖ])"))       // split camelCase so KanSkalas -> kan, skalas
      .map(_.toLowerCase.filter(_.isLetter)).filter(_.nonEmpty)
  lazy val glossaryEn: Set[String] =
    (Glossary.id.values.iterator.flatMap(subwords) ++ Glossary.str.iterator.map(_._2).flatMap(subwords)).toSet
  def allowed(w: String): Boolean = w.length <= 2 || scalaProg(w) || glossaryEn(w) || english(w)
  /** distinct lowercased subwords in `en` that are NOT recognized = suspect Swedish / fall-through. */
  def suspects(en: String): Seq[String] = subwords(en).filterNot(allowed).toSeq.distinct
  def isClean(en: String): Boolean = !subwords(en).exists(w => !allowed(w))

object Apply:
  val inlineRe: Regex = raw"(?s)^\\(code|jcode|lstinline|verb)\*?(.)".r
  val envRe: Regex = raw"(?s)^\\begin\{(Code|CodeSmall|REPL|REPLnonum|REPLsmall|lstlisting|verbatim|Verbatim)\}(.*)\\end\{".r
  def inlineInner(span: String): String =
    val k = span.indexWhere(c => !c.isLetter && c != '\\', 1)
    if k < 0 || k + 1 >= span.length then "" else span.substring(k + 1, span.length - 1)
  def kind(span: String): String =
    if inlineRe.findPrefixMatchOf(span).isDefined then "inline"
    else if envRe.findPrefixMatchOf(span).isDefined then "env" else ""
  def content(span: String, k: String): String = k match
    case "inline" => inlineInner(span)
    case "env"    => envRe.findPrefixMatchOf(span).map(_.group(2)).getOrElse("")
    case _        => ""
  // NB: `\fi` is a control WORD, so TeX swallows the space that follows it -- `...\fi och` renders "Xoch".
  // Append `{}` after `\fi` so an existing following space is preserved (and none is added before
  // punctuation). Verified: `\fi and` -> "Band", `\fi{} and` -> "B and". Applies to BOTH builds.
  def clamp(sv: String, en: String, k: String): String = k match
    case "inline" => s"\\ifswedish$sv\\else$en\\fi{}"
    case "env"    => s"\\ifswedish\n$sv\n\\else\n$en\n\\fi{}"
    case _        => sv

  // Strip //-comments and /* */ comments (only) for the leak GATE, but KEEP string/char literals. Comment
  // Swedish is translated DOWNSTREAM by translateCodeEnvBodies inside the \else branch (see Translate.scala:
  // "composes with apply-b0d clamps: it also translates the comments inside an \else"), so it must NOT block
  // an identifier clamp -- this fixes the w10-extends false-skip where a Swedish comment
  // `// implementation saknas, inget =` skipped a fully-glossaried trait/case-class block.
  // Strings are KEPT deliberately: render() renames identifier tokens even INSIDE a string literal, so a
  // Swedish word used as string DATA (e.g. the n-gram demo Vector("fem","gurkor","aer",...)) gets half-
  // renamed to "cucumbers" while "fem"/"aer" stay Swedish. Keeping strings in the gate lets that residual
  // Swedish block the clamp (a skipped unit outputs the ORIGINAL, discarding the partial render). String-
  // aware so a `//` inside a string literal is not mistaken for a comment.
  def stripComments(s: String): String =
    val b = new StringBuilder; var i = 0; val n = s.length
    def copyDelimited(q: Char): Unit = // append a "..." or '...' literal verbatim (handles \-escapes)
      b += s(i); i += 1
      while i < n && s(i) != q do { if s(i)=='\\' && i+1 < n then { b += s(i); i += 1 }; if i < n then { b += s(i); i += 1 } }
      if i < n then { b += s(i); i += 1 }
    // idiomatic dispatch on a 3-char lookahead (JohanbcEkberg's review suggestion, PR #940): Option via
    // `lift` removes the manual i+1/i+2 bounds checks. Case order matters: // before /*, """ before ".
    while i < n do
      (s.lift(i), s.lift(i + 1), s.lift(i + 2)) match
        case (Some('/'), Some('/'), _) =>
          while i < n && s(i) != '\n' do i += 1
        case (Some('/'), Some('*'), _) =>
          i += 2
          while i + 1 < n && !(s(i) == '*' && s(i + 1) == '/') do i += 1
          i += 2
        case (Some('"'), Some('"'), Some('"')) =>
          b ++= "\"\"\""; i += 3
          while i + 2 < n && !(s(i) == '"' && s(i + 1) == '"' && s(i + 2) == '"') do { b += s(i); i += 1 }
          if i + 2 < n then { b ++= "\"\"\""; i += 3 } else while i < n do { b += s(i); i += 1 }
        case (Some('"'), _, _)  => copyDelimited('"')
        case (Some('\''), _, _) => copyDelimited('\'')
        case (Some(c), _, _)    => b += c; i += 1
        case _                  => i += 1 // unreachable (loop guard ensures s.lift(i) is Some)
    b.toString

  private val slideBegin = raw"(?s)^\\begin\{(Slide|SlideExtra|SlideSimple)\}".r
  private val slideEnd   = raw"(?s)^\\end\{(Slide|SlideExtra|SlideSimple)\}".r
  // clampable = has a real rename AND no residual suspect Swedish; susp = un-clampable Swedish identifiers/output
  private case class Info(k: String, sv: String, en: String, clampable: Boolean, susp: Seq[String], group: Int)

  /** returns (out, clamped[sv,en], skipped[preview,suspects], fallthroughVocab[token,count], suppressed[preview]).
    * SLIDE-LEVEL gating: a candidate unit is clamped only if it is clampable AND no other unit on the SAME
    * \begin{Slide}...\end{Slide} has residual (un-clampable) Swedish. This prevents mixed-language slides
    * (define-in-English / use-in-Swedish) under partial glossary coverage -- a slide clamps all-or-nothing.
    * Units OUTSIDE any slide (e.g. compendium continuous text) fall back to per-unit. A clampable unit held
    * back because a slide-mate is dirty is reported as `suppressed` (the coupling cost / worklist).
    * A unit with NOTHING to translate (no glossary id, only a downstream-handled comment) is neither clamped
    * nor dirtying -- it is benign and left as-is. */
  def apply(src: String): (String, Seq[(String, String)], Seq[(String, Seq[String])], Seq[(String, Int)], Seq[String]) =
    val (masked, spans, _) = Latex.mask(src, stripEng = false)
    // ---- pass 1: analyse each span (candidate?/clampable?/suspects) and assign a slide-group id (-1 = outside) ----
    val infos = new Array[Info](spans.size)
    var gid = -1; var cur = -1
    for idx <- spans.indices do
      val s = spans(idx)
      if slideBegin.findPrefixMatchOf(s).isDefined then { gid += 1; cur = gid }
      val k = kind(s)
      infos(idx) =
        if k.isEmpty then Info("", s, s, false, Nil, cur)
        else
          val c = content(s, k)
          if !(Code.swedishish(c) || Glossary.touches(s)) then Info("", s, s, false, Nil, cur)
          else
            val en = Glossary.render(s)
            // gate ignores comments (downstream) but keeps strings; also drop REPL object-identity hashcodes
            // (`Gurka@15f11bfb`, `A@5faeada1`) whose hex letter-runs (bfb/faeada/eee) are nondeterministic junk,
            // not Swedish. `@tailrec`/`@main` survive (a non-hex letter stops the match).
            val gateBody = stripComments(Glossary.render(c)).replaceAll("@[0-9a-fA-F]+", "")
            val susp = Allow.suspects(gateBody)
            Info(k, s, en, en != s && susp.isEmpty, susp, cur)
      if slideEnd.findPrefixMatchOf(s).isDefined then cur = -1
    // Swedish glossary identifier sitting in a code-display context we CANNOT clamp: a \code/\texttt/\lstinline
    // inside a masked-whole span (tikzpicture is a verbatimEnv, \texttt is maskWhole) — the tool never sees it
    // as a candidate, so it stays Swedish in BOTH builds. If such a span shares a slide with a clamped unit the
    // slide goes mixed (define-English / diagram-Swedish). Treat it as dirtying the slide. NOT triggered by
    // bare prose Swedish (no code-display wrapper) -- that is translated downstream by the prose pass.
    def codeDisplaySwedish(s: String): Boolean =
      Glossary.touches(s) &&
        Seq("\\code", "\\jcode", "\\lstinline", "\\verb", "\\texttt").exists(s.contains)
    // a slide-group is dirty (un-clampable) if ANY candidate has residual suspects OR any non-candidate span
    // hides code-Swedish. A benign candidate (nothing to translate, no suspects) does NOT dirty the slide.
    val dirtyGroups = infos.iterator.filter { in =>
      in.group >= 0 && (if in.k.nonEmpty then in.susp.nonEmpty else codeDisplaySwedish(in.sv))
    }.map(_.group).toSet
    // ---- pass 2: build output + logs ----
    val clamped = collection.mutable.ArrayBuffer[(String, String)]()
    val skipped = collection.mutable.ArrayBuffer[(String, Seq[String])]()
    val suppressed = collection.mutable.ArrayBuffer[String]()
    val fall = collection.mutable.Map[String, Int]().withDefaultValue(0)
    val spans2 = infos.map { in =>
      if in.k.isEmpty then in.sv
      else
        val doClamp = in.clampable && (in.group < 0 || !dirtyGroups.contains(in.group))
        val c = content(in.sv, in.k)
        if doClamp then
          clamped += ((c.replace("\n", "⏎").take(70), content(in.en, in.k).replace("\n", "⏎").take(70)))
          clamp(in.sv, in.en, in.k)
        else
          if in.clampable then suppressed += c.replace("\n", "⏎").take(70) // clampable, but a slide-mate is dirty
          else if in.susp.nonEmpty then
            skipped += ((c.replace("\n", "⏎").take(70), in.susp)); in.susp.foreach(w => fall(w) += 1)
          in.sv // benign (nothing to translate) or dirty -> keep original
    }
    val fallVocab = fall.toSeq.sortBy { case (w, n) => (-n, w) }
    (Latex.restore(masked, spans2.toVector), clamped.toSeq, skipped.toSeq, fallVocab, suppressed.toSeq)

/** COMPILE GATE (--verify-examples <file.scala>...): render each given .scala through the SAME Glossary and
  * compile them together (one scala-cli invocation). "Only compilable code should pass": a glossary rename is
  * accepted only if the English programs still compile. Pass the self-contained, glossary-relevant files that
  * form one compilation unit (e.g. vego1.scala + vego1Test.scala, which imports exempelVego1.*) so cross-file
  * renames are verified consistent. Prints the compiler diagnostics and a PASS/FAIL verdict. */
def verifyExamples(files: Seq[os.Path]): Unit =
  val tmp = os.temp.dir(prefix = "b0d-verify")
  for f <- files do os.write.over(tmp / f.last, Glossary.render(os.read(f)))
  println(s"rendered ${files.size} .scala files -> $tmp; compiling with scala-cli...")
  val r = os.proc("scala-cli", "compile", "--server=false", tmp.toString)
    .call(check = false, mergeErrIntoOut = true, stdout = os.Pipe)
  r.out.text().linesIterator
    .filterNot(l => Seq("Checking", "Checked", "Downloading", "Downloaded", "Compiling", "project root").exists(l.contains))
    .filter(_.trim.nonEmpty).take(40).foreach(println)
  println(if r.exitCode == 0 then s"COMPILE OK — ${files.size} rendered example files compile"
          else s"COMPILE FAILED (exit ${r.exitCode}) — glossary rename breaks the English project")

@main def run(args: String*): Unit =
  if args.headOption.contains("--verify-examples") then { verifyExamples(args.tail.map(a => os.Path(a, os.pwd))); return }
  val file = os.Path(args.head, os.pwd)
  val write = args.contains("--write")
  val src = os.read(file)
  val (out, clamped, skipped, fallVocab, suppressed) = Apply(src)

  // structural self-validation (same as b0.scala)
  val (m1, s1, _) = Latex.mask(out, stripEng = false)
  val roundTrips = Latex.restore(m1, s1) == out
  def braces(s: String) = s.count(_ == '{') - s.count(_ == '}')
  val braceOk = braces(out) == braces(src)
  def ifsw(s: String) = Latex.mask(s, stripEng = false)._2.count(_.startsWith("\\ifswedish"))
  val ifswDelta = ifsw(out) - ifsw(src)

  val valOk = roundTrips && braceOk && ifswDelta == clamped.size
  // instrumentation-by-default: write the full detail to a report file (Read it) so the tool is never
  // wrapped in shell (no `> file`, no `2>/dev/null`, no `echo`). stdout stays a short summary.
  val report = new StringBuilder
  report ++= s"=== fused B0+D ${if write then "WRITE" else "dry-run"}: $file ===\n"
  report ++= s"clamped (translated) units: ${clamped.size}   |   skipped (not fully covered): ${skipped.size}   |   suppressed (clean, but a slide-mate is dirty): ${suppressed.size}\n"
  report ++= s"self-validation: round-trip=$roundTrips brace=$braceOk ifswedishDelta=$ifswDelta (expect=${clamped.size}) -> ${if valOk then "OK" else "CHECK"}\n"
  report ++= s"fall-through vocabulary (distinct suspect Swedish tokens left untranslated): ${fallVocab.size}\n\n"
  // The KEY review artefact (BR): every suspect token the allowlist gate left Swedish, most-frequent first.
  // Real Swedish (namn, smak, fyle...) -> add to glossary; missed English/prog token -> add to Allow.scalaProg.
  report ++= "=== FALL-THROUGH VOCABULARY (token × count — what is still Swedish) ===\n"
  for (w, n) <- fallVocab do report ++= f"  ${n}%3d  $w\n"
  report ++= "\n--- clamped units (sv -> en) ---\n"
  for (sv, en) <- clamped do { report ++= s"  SV: $sv\n"; report ++= s"  EN: $en\n" }
  report ++= "\n--- skipped candidate units (blocking suspect tokens shown) ---\n"
  for (c, susp) <- skipped do { report ++= s"  UNIT: $c\n"; report ++= s"    suspects: ${susp.mkString(", ")}\n" }
  // SLIDE-GATING coupling cost: units that ARE clean but were held back because a slide-mate is dirty.
  // Clamping only these would create a mixed-language slide; fixing their slide's blockers (above) unlocks them.
  report ++= "\n--- suppressed by slide-gating (clean units on a slide that has a dirty unit) ---\n"
  for c <- suppressed do report ++= s"  UNIT: $c\n"
  val reportPath = file / os.up / os.up / os.up / "autotranslate" / "scratch" / "apply-b0d-report.txt"
  os.write.over(reportPath, report.toString)
  if write then { os.write.over(file, out); println(s"[--write] applied ${clamped.size} clamps to $file") }
  println(s"B0+D ${if write then "WRITE" else "dry-run"}: clamped=${clamped.size} skipped=${skipped.size} suppressed=${suppressed.size} fallthrough=${fallVocab.size} validation=${if valOk then "OK" else "CHECK"} -> report: ${reportPath.relativeTo(os.pwd)}")
