//> using scala 3.8.4
//> using dep com.lihaoyi::os-lib:0.11.8

// Step-2 leak/appendix verifier. Run from introprog root:
//   scala-cli run autotranslate/scratch/verify-step2.scala -- <introprog-root>
// For compendium1-en.pdf + compendium2-en.pdf reports: page count, cover (page 1) text,
// appendix/Part-III headings, Swedish density, and a code-vs-prose split of Swedish lines.

@main def run(rootStr: String): Unit =
  val root = os.Path(rootStr)
  val dir = root / "compendium-en"
  val svRe = "[åäöÅÄÖ]".r
  // heuristics: a "code-ish" Swedish line is one that looks like a listing/REPL/identifier, not prose.
  def looksCode(l: String): Boolean =
    val s = l.trim
    s.startsWith("scala>") || s.startsWith("val ") || s.startsWith("def ") || s.startsWith("//") ||
      s.contains("java.lang") || s.contains("()") || s.contains("=>") || s.matches(""".*\b\w+\(\w*\).*""")

  for name <- List("compendium1-en.pdf", "compendium2-en.pdf") do
    val pdf = dir / name
    println(s"\n########## $name ##########")
    if !os.exists(pdf) then println("  MISSING")
    else
      val txt = os.proc("pdftotext", pdf.toString, "-").call(check = false).out.text()
      val pages = txt.count(_ == '\f') + 1
      println(s"=== pages: $pages ===")

      val firstPage = txt.split('\f').headOption.getOrElse("")
      println("=== cover (page 1, non-empty lines) ===")
      firstPage.linesIterator.map(_.trim).filter(_.nonEmpty).take(14).foreach(l => println("  | " + l))

      println("=== appendix / Part-III headings (Appendix|Bilaga|Kojo|Java|Part) ===")
      val headRe = "(?i)\\b(appendix|bilaga|kojo|part iii|del iii)\\b".r
      val heads = txt.linesIterator.map(_.trim).filter(_.nonEmpty)
        .filter(l => headRe.findFirstIn(l).isDefined && l.length < 80).toVector.distinct
      if heads.isEmpty then println("  (none found)")
      else heads.take(30).foreach(h => println("  > " + h))

      val lines = txt.linesIterator.toVector
      val svLines = lines.filter(l => svRe.findFirstIn(l).isDefined)
      val pct = if lines.nonEmpty then 100 * svLines.size / lines.size else 0
      val (code, prose) = svLines.partition(looksCode)
      println(s"=== Swedish: ${svLines.size}/${lines.size} ($pct%)  |  code-ish=${code.size}  prose-ish=${prose.size} ===")
      println("--- sample Swedish PROSE lines (leaks to grind) ---")
      prose.map(_.trim).filter(_.length > 12).distinct.take(12).foreach(l => println("  ~ " + l.take(100)))
