//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8
//> using file ../Code.scala

// Dump a PDF's text and list every DISTINCT line the TRANSLATOR would consider Swedish (reuses
// Code.swedishish — the exact same åäö + full swedishWords detector), so the PDF scan matches what the
// autotranslator translates. Writes the full list to scratch/pdf-swedish-out.txt; prints count + sample.
//   scala-cli run autotranslate/scratch/pdf-swedish.scala -- /abs/slides-en/lect-w01-en.pdf
@main def run(pdfStr: String): Unit =
  val pdf = os.Path(pdfStr)
  val txt = os.proc("pdftotext", "-layout", pdf.toString, "-").call(check = false).out.text()
  val lines = txt.linesIterator.map(_.trim).filter(_.nonEmpty).filter(Code.swedishish).toVector.distinct
  val out = pdf / os.up / s"swedish-${pdf.baseName}.txt"
  os.write.over(out, lines.mkString("\n") + "\n")
  println(s"=== ${lines.size} distinct Swedish lines in ${pdf.last} (full list -> $out) ===")
  lines.take(25).foreach(println)
