//> using scala 3.9.0
//> using dep com.lihaoyi::os-lib:0.11.8

/** Does the English mirror USE the `\Eng{}` gloss the Swedish source already supplies?
  *
  * WHY THIS EXISTS: the Swedish source names the English term itself, e.g.
  * `\Emph{gard} \Eng{guard}`, and the pipeline DISCARDS that gloss and translates the term
  * blind. When the term sits alone in an isolated child unit it has no sentence for context,
  * so the model guesses. Measured 2026-09-18: `lect-w06-matching.tex:247` renders as
  * "a so-called \Emph{garden}", while lines 246 and 267 of the SAME mirror file say "guard"
  * correctly. The authoritative English word was four characters to the right and unused.
  *
  * This probe is the MEASUREMENT half of issue #981. It answers "how often does the mirror
  * contradict the source's own stated terminology", which nobody currently knows, and it is
  * the natural basis for a gate: it transforms nothing, so it cannot corrupt output.
  *
  * HOW IT COMPARES: source and mirror line numbers align for the great majority of files, so
  * each `\Eng{Y}` is checked against the SAME line number in the `-en` mirror. Both sides are
  * stripped of LaTeX markup before comparing, so `\Emph {partially applied} functions` does
  * match the gloss "partially applied functions" -- an earlier version of this probe did not
  * strip, and reported that correct translation as a defect.
  *
  * VERDICTS, ordered from strongest evidence to weakest:
  *   ok             the mirror line carries the term
  *   UNTRANSLATED   the mirror line is byte-identical to its Swedish source (a real leak)
  *   SUSPECT-SV     the mirror line carries Swedish-only letters outside code -- WEAKER, see below
  *   TERM-MISSING   the mirror is English but lacks the term
  *   UNALIGNED      the mirror line looks structurally unrelated; line numbers have drifted
  *   EMPTY-MIRROR   nothing at that line number
  * `%`-commented source lines are SKIPPED entirely: an untranslated comment is never typeset.
  *
  * ⚠ WHAT THIS PROBE IS NOT: a defect count. TERM-MISSING includes acceptable rewordings
  * (`traversera \Eng{traverse}` rendering as "traversing") and inflection differences. On a
  * hand-checked slice of 20 rows on 2026-09-18, roughly two thirds were real. SUSPECT-SV is
  * weaker still: a line whose English prose is perfectly good can carry `\code{Färg}` where an
  * identifier is deliberately unclamped. Treat the numbers as an upper bound and READ the rows.
  *
  * Baselines, both measured rather than carried over, because this number gets quoted:
  *   2026-09-18, before the `%`-comment skip existed: 328 glosses, 147 delimited, 181 bare.
  *   2026-09-23, current: 307 glosses, 140 delimited, 167 bare. The drop of 21 is the
  *     `%`-commented lines now skipped; the delimited/bare shift of 2 is the `stripTrailing`
  *     fix below, which had been misreading a delimited gloss as bare whenever more than one
  *     space or a tab sat before `\Eng{`. TERM-MISSING is 108 (40 delimited) on both counts,
  *     so the figure issue #981 sequences on was never affected by that bug.
  *
  * Usage:
  *   scala-cli run autotranslate/scratch/eng-gloss-probe.scala -- . [out.tsv]
  */

/** Everything a `\Eng{}` occurrence needs to be judged. */
final case class Gloss(
    file: os.Path, line: Int, eng: String, before: String,
    delimited: Boolean, srcLine: String, mirrorLine: Option[String]
)

/** The argument of a macro whose `{` is at `open`, brace-balanced so nested macros survive. */
def balanced(s: String, open: Int): Option[(String, Int)] =
  if open >= s.length || s.charAt(open) != '{' then None
  else
    var depth = 0
    var i = open
    var out: Option[(String, Int)] = None
    while out.isEmpty && i < s.length do
      if s.charAt(i) == '{' then depth += 1
      else if s.charAt(i) == '}' then
        depth -= 1
        if depth == 0 then out = Some((s.substring(open + 1, i), i))
      i += 1
    out

/** Strip LaTeX so a term split by markup still compares equal: drop macro NAMES, drop braces,
  * collapse whitespace, lowercase. `\Emph {partially applied} functions` -> `partially applied functions`. */
def flatten(s: String): String =
  s.replaceAll("""\\[a-zA-Z]+""", " ")
    .replaceAll("""[{}]""", " ")
    .replaceAll("""\s+""", " ")
    .trim.toLowerCase

val SwedishOnly = "åäöÅÄÖ"

/** Swedish letters OUTSIDE code regions. Identifiers inside \code{}/\jcode{}/verbatim are often
  * deliberately left Swedish, so counting them as "untranslated prose" is how this over-reports. */
def swedishInProse(s: String): Boolean =
  val withoutCode = s.replaceAll("""\\(code|jcode|lstinline)\{[^}]*\}""", " ")
    .replaceAll("""\\(code|jcode|lstinline)\|[^|]*\|""", " ")
  withoutCode.exists(SwedishOnly.contains)

/** A mirror line that is a bare structural delimiter where the source is real prose means the
  * line numbers have drifted apart, not that anything is wrong with the translation. */
def unaligned(src: String, mirror: String): Boolean =
  val m = mirror.trim
  m.length < 30 && src.trim.length > 60 &&
    (m.startsWith("\\begin") || m.startsWith("\\end") || m == "\\pause" || m.startsWith("\\item"))

def mirrorOf(p: os.Path, root: os.Path): Option[os.Path] =
  val rel = p.relativeTo(root).toString
  val swapped =
    if rel.startsWith("slides/") then rel.replaceFirst("^slides/", "slides-en/")
    else if rel.startsWith("compendium/") then rel.replaceFirst("^compendium/", "compendium-en/")
    else return None
  val m = root / os.RelPath(swapped.stripSuffix(".tex") + "-en.tex")
  if os.exists(m) then Some(m) else None

@main def run(args: String*): Unit =
  val root = os.Path(args.headOption.getOrElse("."), os.pwd)
  val outTsv = args.lift(1).map(os.Path(_, os.pwd))

  val sources =
    for
      dir <- Seq(root / "slides", root / "compendium") if os.exists(dir)
      f <- os.walk(dir) if os.isFile(f) && f.ext == "tex"
    yield f

  val glosses =
    for
      f <- sources.sortBy(_.toString)
      mirrorLines = mirrorOf(f, root).map(os.read.lines(_).toVector).getOrElse(Vector.empty)
      (line, idx) <- os.read.lines(f).toVector.zipWithIndex
      if !line.trim.startsWith("%")            // LaTeX comments are never typeset
      g <- glossesIn(line, f, idx + 1, mirrorLines.lift(idx))
    yield g

  def verdict(g: Gloss): String =
    g.mirrorLine match
      case None                                             => "EMPTY-MIRROR"
      case Some(m) if m.trim.isEmpty                        => "EMPTY-MIRROR"
      case Some(m) if m.trim == g.srcLine.trim              => "UNTRANSLATED"
      case Some(m) if flatten(m).contains(flatten(g.eng))   => "ok"
      case Some(m) if unaligned(g.srcLine, m)               => "UNALIGNED"
      case Some(m) if swedishInProse(m)                     => "SUSPECT-SV"
      case Some(_)                                          => "TERM-MISSING"

  val byVerdict = glosses.groupBy(verdict)
  val order = Seq("ok", "TERM-MISSING", "SUSPECT-SV", "UNTRANSLATED", "UNALIGNED", "EMPTY-MIRROR")

  println(s"\\Eng glosses found: ${glosses.size}   (delimited ${glosses.count(_.delimited)}, bare ${glosses.count(!_.delimited)})")
  println("  (%-commented source lines are skipped)\n")
  for v <- order; hs = byVerdict.getOrElse(v, Seq.empty) if hs.nonEmpty do
    println(f"  $v%-14s ${hs.size}%4d   (delimited ${hs.count(_.delimited)}%3d, bare ${hs.count(!_.delimited)}%3d)")

  val missing = byVerdict.getOrElse("TERM-MISSING", Seq.empty)
  val leaks = byVerdict.getOrElse("UNTRANSLATED", Seq.empty)
  println()
  if leaks.nonEmpty then
    println(s"  ${leaks.size} mirror line(s) are byte-identical to the Swedish source -- a real leak:")
    for g <- leaks.take(5) do println(s"      ${g.file.relativeTo(root)}:${g.line}  \\Eng{${g.eng}}")
    if leaks.size > 5 then println(s"      ... and ${leaks.size - 5} more")
  if missing.nonEmpty then
    println(s"\n  ${missing.size} gloss(es) whose term the mirror does not carry.")
    println("  ⚠ That is an UPPER BOUND on defects: it also catches acceptable rewordings and")
    println("    inflections. Read the rows before quoting the number. Of these,")
    println(s"    ${missing.count(_.delimited)} are ALREADY delimited by a macro -- those need no source")
    println("    edit at all, so they are the cheapest thing for issue #981 to fix first.")

  outTsv.foreach: out =>
    val sb = StringBuilder()
    sb ++= "verdict\tdelimited\tfile\tline\teng\tbefore\tmirror\n"
    for g <- glosses do
      sb ++= s"${verdict(g)}\t${if g.delimited then "delimited" else "bare"}\t" +
        s"${g.file.relativeTo(root)}\t${g.line}\t${g.eng}\t${g.before}\t${g.mirrorLine.getOrElse("").take(110)}\n"
    os.write.over(out, sb.toString)
    println(s"\n  rows written to $out")

/** Every `\Eng{...}` on one line, with whether the term before it is brace-delimited. */
def glossesIn(line: String, f: os.Path, lineNo: Int, mirror: Option[String]): Seq[Gloss] =
  val out = scala.collection.mutable.ArrayBuffer.empty[Gloss]
  var from = 0
  var k = line.indexOf("\\Eng{", from)
  while k >= 0 do
    balanced(line, k + 4) match
      case Some((eng, close)) =>
        val pre = line.substring(0, k).stripTrailing()
        val before = if pre.length > 60 then "..." + pre.takeRight(60) else pre
        out += Gloss(f, lineNo, eng, before, pre.endsWith("}"), line, mirror)
        from = close + 1
      case None => from = k + 5
    k = line.indexOf("\\Eng{", from)
  out.toSeq
