//> using scala 3.8.4
// Reusable Swedish-density scanner for the English PDFs. For each PDF: run pdftotext, then report
// total lines, lines containing Swedish letters (åäö), and counts of strong Swedish FUNCTION words
// (which distinguish untranslated PROSE from intentional Swedish code identifiers), vs English "the".

import scala.sys.process.*

@main def run(paths: String*): Unit =
  val svLetter = "[åäöÅÄÖ]".r
  val svWords  = List("och", "är", "att", "som", "med", "för", "inte", "denna", "detta", "ett")
  def wc(txt: String, w: String): Int = (raw"(?i)\b" + java.util.regex.Pattern.quote(w) + raw"\b").r.findAllIn(txt).size

  println(f"${"pdf"}%-34s ${"lines"}%6s ${"svÅÄÖ"}%6s ${"sv%%"}%4s ${"svProse"}%7s ${"the"}%5s")
  for p <- paths do
    val txt =
      try Seq("pdftotext", p, "-").!!
      catch case _: Throwable => ""
    if txt.isEmpty then println(f"${p.split("/").last}%-34s  (no text / missing)")
    else
      val lines = txt.linesIterator.toVector
      val svL   = lines.count(l => svLetter.findFirstIn(l).isDefined)
      val prose = svWords.map(w => wc(txt, w)).sum   // strong Swedish prose signal
      val the   = wc(txt, "the")
      val pct   = if lines.nonEmpty then 100 * svL / lines.size else 0
      println(f"${p.split("/").last}%-34s ${lines.size}%6d ${svL}%6d ${pct}%3d%% ${prose}%7d ${the}%5d")
