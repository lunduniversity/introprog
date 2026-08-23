//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8

// Build ONE English slide deck via pdfSlidesEn (no cd; cwd=root via os.proc), report errors.
//   scala-cli run autotranslate/scratch/build-deck.scala -- /abs/introprog w01
@main def run(rootStr: String, deck: String): Unit =
  val root = os.Path(rootStr)
  os.proc("sbt", "--client", s"pdfSlidesEn $deck")
    .call(cwd = root, check = false, stdout = os.Pipe, stderr = os.Pipe, mergeErrIntoOut = true)
  val pdf = root / "slides-en" / s"lect-$deck-en.pdf"
  val log = root / "slides-en" / s"lect-$deck-en.log"
  val logLines = if os.exists(log) then new String(os.read.bytes(log), "ISO-8859-1").linesIterator.toVector else Vector.empty
  val errs = logLines.count(_.startsWith("! "))
  println(s"deck=$deck pdf=${os.exists(pdf)} errors=$errs")
  if errs > 0 then logLines.filter(_.startsWith("! ")).take(10).foreach(l => println(s"  $l"))
