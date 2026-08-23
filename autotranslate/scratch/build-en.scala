//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8

// Regenerate the English mirrors and build the canonical compendium-en.pdf, then report build errors +
// residual-Swedish density. Run bare with the introprog root:
//   scala-cli run autotranslate/scratch/build-en.scala -- /home/bjornr/git/hub/lunduniversity/introprog
@main def run(rootStr: String): Unit =
  val root = os.Path(rootStr)
  val dir = root / "compendium-en"
  println("=== sbt: autotranslateProject/run --all (regenerate mirrors) ===")
  os.proc("sbt", "--client", "autotranslateProject/run --all").call(cwd = root, check = false)
  println("=== sbt: pdfCompendiumEn (canonical multi-pass build) ===")
  os.proc("sbt", "--client", "pdfCompendiumEn").call(cwd = root, check = false)

  val log = dir / "compendium-en.log"
  if !os.exists(log) then { println(s"!! no log at $log — build did not produce one"); sys.exit(1) }
  val logText = new String(os.read.bytes(log), "ISO-8859-1")
  val errs = logText.linesIterator.filter(_.startsWith("! ")).toVector.distinct
  println(s"=== build errors: ${errs.size} ===")
  errs.take(15).foreach(e => println("  " + e))

  val pdf = dir / "compendium-en.pdf"
  if os.exists(pdf) then
    val txt = os.proc("pdftotext", pdf.toString, "-").call(check = false).out.text()
    val lines = txt.linesIterator.toVector
    val sv = lines.count(l => "[åäöÅÄÖ]".r.findFirstIn(l).isDefined)
    val pages = "Kompileringsdatum|Compilation date".r // best-effort
    println(s"=== compendium-en.pdf OK: ${lines.size} text lines, $sv with åäö (${if lines.nonEmpty then 100 * sv / lines.size else 0}%) ===")
  else println("!! compendium-en.pdf NOT produced")
