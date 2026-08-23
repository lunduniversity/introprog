//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8

// Build the SIMPLE INTRO deck (slides-en/simple/intro-en.tex, or the SV twin) with pdflatex,
// 3 passes for TOC/refs. This deck has NO sbt task (pdfSlides* cover lect-* only), which is
// how a stale intro-en.pdf survived a release build (found in BR's v2026.5 rendered review).
//   scala-cli run autotranslate/scratch/build-intro-deck.scala -- /abs/introprog en
//   scala-cli run autotranslate/scratch/build-intro-deck.scala -- /abs/introprog sv
@main def buildIntro(rootStr: String, edition: String): Unit =
  val root = os.Path(rootStr)
  val (dir, tex) = edition match
    case "en" => (root / "slides-en" / "simple", "intro-en.tex")
    case "sv" => (root / "slides" / "simple", "intro.tex")
    case other => println(s"unknown edition: $other (use en or sv)"); return
  for p <- 1 to 3 do
    println(s"=== [intro-$edition p$p] pdflatex $tex ===")
    val r = os.proc("pdflatex", "-interaction=nonstopmode", tex)
      .call(cwd = dir, check = false, stdout = os.Pipe, stderr = os.Pipe, mergeErrIntoOut = true)
    println(s"=== [intro-$edition p$p] exit=${r.exitCode} ===")
  val pdf = dir / tex.replace(".tex", ".pdf")
  val log = dir / tex.replace(".tex", ".log")
  val logLines =
    if os.exists(log) then new String(os.read.bytes(log), "ISO-8859-1").linesIterator.toVector
    else Vector.empty
  val errs = logLines.count(_.startsWith("! "))
  println(s"deck=intro-$edition pdf=${os.exists(pdf)} errors=$errs")
  if errs > 0 then logLines.filter(_.startsWith("! ")).take(10).foreach(l => println(s"  $l"))
