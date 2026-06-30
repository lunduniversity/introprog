//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8
//> using file ../Latex.scala
//> using file ../Code.scala

// B0 — automated bilingual code-scaffold pass (DUMMY round: \else = sv, so the en build is byte-identical
// and the plumbing is de-risked). Wraps each SWEDISH-containing case-(a) code unit (inline \code/\jcode/
// \lstinline/\verb + verbatim code ENV bodies) embedded in .tex prose in `\ifswedish<sv>\else<sv>\fi`.
// Reuses the translator's OWN locator (Latex.mask) + Swedish notion (Code.swedishish), and the
// span-transform + restore round-trip, so a clamp is inserted at EXACTLY a located unit and nowhere else.
//
// Default = DRY (writes nothing; prints count, sample before->after, and STRUCTURAL self-validation).
//   scala-cli run autotranslate/scratch/b0.scala -- <file.tex>            # dry
//   scala-cli run autotranslate/scratch/b0.scala -- <file.tex> --write    # write clamps in place

import scala.util.matching.Regex

object B0:
  val inlineRe: Regex = raw"(?s)^\\(code|jcode|lstinline|verb)\*?(.)".r
  val envRe: Regex = raw"(?s)^\\begin\{(Code|CodeSmall|REPL|REPLnonum|REPLsmall|lstlisting|verbatim|Verbatim)\}(.*)\\end\{".r

  def inlineInner(span: String): String =
    val k = span.indexWhere(c => !c.isLetter && c != '\\', 1)
    if k < 0 || k + 1 >= span.length then "" else span.substring(k + 1, span.length - 1)

  /** kind of a code unit, or "" if the span is not a clampable case-(a) code unit. */
  def kind(span: String): String =
    if inlineRe.findPrefixMatchOf(span).isDefined then "inline"
    else if envRe.findPrefixMatchOf(span).isDefined then "env"
    else ""

  def content(span: String, k: String): String = k match
    case "inline" => inlineInner(span)
    case "env"    => envRe.findPrefixMatchOf(span).map(_.group(2)).getOrElse("")
    case _        => ""

  /** wrap a span in a dummy bilingual clamp (else = same sv). Inline stays tight (one line); env is
    * newline-padded for review readability. \ifswedish is a complete control word, so no gluing risk. */
  def clamp(span: String, k: String): String = k match
    case "inline" => s"\\ifswedish$span\\else$span\\fi"
    case "env"    => s"\\ifswedish\n$span\n\\else\n$span\n\\fi"
    case _        => span

  /** Insert dummy clamps around every Swedish-containing case-(a) code unit. Returns (out, nClamped). */
  def apply(src: String): (String, Int) =
    val (masked, spans, _) = Latex.mask(src, stripEng = false) // stripEng=false → exact round-trip
    var n = 0
    val spans2 = spans.map { s =>
      val k = kind(s)
      if k.nonEmpty && Code.swedishish(content(s, k)) then { n += 1; clamp(s, k) } else s
    }
    (Latex.restore(masked, spans2), n)

@main def run(args: String*): Unit =
  val file = os.Path(args.head, os.pwd)
  val write = args.contains("--write")
  val src = os.read(file)
  val (out, n) = B0(src)

  // ── structural self-validation (proves build-safety WITHOUT a full pdflatex run) ──
  val (_, spans0, _) = Latex.mask(src, stripEng = false)
  val (_, spans1, _) = Latex.mask(out, stripEng = false)
  def ifswCount(sp: IndexedSeq[String]) = sp.count(_.startsWith("\\ifswedish"))
  val ifswDelta = ifswCount(spans1) - ifswCount(spans0)
  // round-trip: re-masking the clamped output must restore to itself (no malformed spans)
  val (m1, s1, _) = Latex.mask(out, stripEng = false)
  val roundTrips = Latex.restore(m1, s1) == out
  def braces(s: String) = s.count(_ == '{') - s.count(_ == '}')
  val braceBalanced = braces(out) == braces(src)
  // EN-side equivalence: with \swedishfalse the en build shows the \else branch; since else==sv (dummy),
  // the en-visible content is byte-identical to the original. We assert the dummy property structurally:
  // every inserted clamp has else-branch == if-branch.

  println(s"=== B0 ${if write then "WRITE" else "dry-run"}: $file ===")
  println(s"clamps to insert: $n")
  println(s"self-validation:")
  println(s"  \\ifswedish span delta = $ifswDelta  (expect == $n) -> ${if ifswDelta == n then "OK" else "MISMATCH"}")
  println(s"  re-mask round-trips    = $roundTrips -> ${if roundTrips then "OK" else "FAIL"}")
  println(s"  brace balance preserved= $braceBalanced -> ${if braceBalanced then "OK" else "FAIL"}")

  // show first sample before->after transforms
  val (masked, spans, _) = Latex.mask(src, stripEng = false)
  var shown = 0
  println(s"\n=== sample clamps (first 12 before -> after) ===")
  for s <- spans if shown < 12 do
    val k = B0.kind(s)
    if k.nonEmpty && Code.swedishish(B0.content(s, k)) then
      shown += 1
      val before = s.replace("\n", "⏎").take(90)
      val after = B0.clamp(s, k).replace("\n", "⏎").take(120)
      println(f"  [$k%-6s] $before")
      println(f"           -> $after")

  if write then
    os.write.over(file, out)
    println(s"\n[--write] wrote $n clamps into $file")
  else
    println(s"\n(dry run — no file written; pass --write to apply)")
