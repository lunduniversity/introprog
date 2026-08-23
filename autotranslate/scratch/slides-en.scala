//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8

// Build every English slide deck (slides-en/lect-<id>-en.pdf) via the canonical pdfSlidesEn task and
// report per-deck PASS/FAIL. Assumes the mirrors were already regenerated (build-en.scala's --all).
//   scala-cli run autotranslate/scratch/slides-en.scala -- /home/bjornr/git/hub/lunduniversity/introprog
@main def run(rootStr: String): Unit =
  val root = os.Path(rootStr)
  val decks = os.list(root / "slides")
    .filter(f => f.ext == "tex" && f.last.startsWith("lect-"))
    .map(_.last.stripPrefix("lect-").stripSuffix(".tex")).toSeq.sorted
  println(s"building ${decks.size} decks: ${decks.mkString(" ")}")
  var ok = 0; var bad = 0
  for d <- decks do
    os.proc("sbt", "--client", s"pdfSlidesEn $d").call(cwd = root, check = false, stdout = os.Pipe, stderr = os.Pipe, mergeErrIntoOut = true)
    val pdf = root / "slides-en" / s"lect-$d-en.pdf"
    val log = root / "slides-en" / s"lect-$d-en.log"
    val errs = if os.exists(log) then new String(os.read.bytes(log), "ISO-8859-1").linesIterator.count(_.startsWith("! ")) else -1
    if os.exists(pdf) && errs == 0 then { ok += 1; println(s"  PASS  $d") }
    else { bad += 1; println(s"  FAIL  $d (errors=$errs, pdf=${os.exists(pdf)})") }
  println(s"=== slides-en: $ok PASS, $bad FAIL of ${decks.size} ===")
