//> using scala 3.8.4
//> using dep com.lihaoyi::os-lib:0.11.8

// Reusable build+verify driver for the English compendium. Run from the introprog/ root:
//   scala-cli run verify.scala -- [build]
// With "build" it runs the no-halt pdflatex sweep first (via os-lib); otherwise it just analyses the
// existing compendium-en.log + .pdf. Prints clean === sections. No bash brace/quote obfuscation.

@main def run(args: String*): Unit =
  val root = os.pwd
  val dir  = root / "compendium-en"
  val log  = dir / "compendium-en.log"
  val pdf  = dir / "compendium-en.pdf"

  if args.contains("build") then
    println("=== no-halt pdflatex sweep ===")
    os.proc("pdflatex", "-interaction=nonstopmode", "compendium-en.tex")
      .call(cwd = dir, check = false, stdout = os.Pipe, stderr = os.Pipe)
    println("  sweep done")

  // 1. build errors (the relaxation/safety gate — must be 0). LaTeX logs aren't valid UTF-8, so read
  // the bytes as Latin-1 (every byte maps to a char, never throws); "! " detection is ASCII anyway.
  val logText = new String(os.read.bytes(log), "ISO-8859-1")
  val errs = logText.linesIterator.filter(_.startsWith("! ")).toVector.distinct
  println(s"=== build errors: ${errs.size} ===")
  errs.take(12).foreach(e => println("  " + e))

  // 2. Swedish density + 3. heading terms, from pdftotext
  val txt = os.proc("pdftotext", pdf.toString, "-").call(check = false).out.text()
  val lines = txt.linesIterator.toVector
  val svRe = "[åäöÅÄÖ]".r
  val svLines = lines.count(l => svRe.findFirstIn(l).isDefined)
  val pct = if lines.nonEmpty then 100 * svLines / lines.size else 0
  println(s"=== Swedish: $svLines / ${lines.size} lines ($pct%) ===")

  def c(w: String): Int = (raw"\b" + java.util.regex.Pattern.quote(w) + raw"\b").r.findAllIn(txt).size
  println("=== heading terms (Swedish should be ~0) ===")
  for (sv, en) <- List("Övning" -> "Exercise", "Lösning" -> "Solution", "Kapitel" -> "Chapter") do
    println(f"  $sv%-8s = ${c(sv)}%4d    $en%-9s = ${c(en)}%4d")
