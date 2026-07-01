//> using scala 3.8.4
//> using dep com.lihaoyi::os-lib:0.11.8

// Full RELEASE build: regenerate the EN mirrors, then build every SV + EN artifact with the pdflatex
// passes needed for TOC / cross-refs. NB build.sbt's runPdfLatexCmd runs ONE pass per task (line 141 —
// the "twice" chain on line 140 is commented out), so we invoke each task the right number of times HERE,
// mirroring the proven all-in-one `pdf` task (compendium 2x; the print pair c1->c2->c1->c2 interleaved so
// the external xref between compendium1/2 resolves; beamer decks 2x for nav/toc). Reports per-PDF status +
// pdflatex "! " error counts. Run bare with the introprog root:
//   scala-cli run autotranslate/scratch/build-release.scala -- /home/bjornr/git/hub/lunduniversity/introprog
@main def buildReleaseArtifacts(rootStr: String): Unit =
  val root  = os.Path(rootStr)
  val decks = "w01 w01-dod w02 w02-dod w03 w04 w05 w06 w07 w08 w08-dod w09 w09-dod w10 w11 w12 w13 w14 wjava"

  def sbtRun(label: String, cmd: String): Unit =
    println(s"\n=== [$label] sbt --client $cmd ===")
    val r = os.proc("sbt", "--client", cmd)
      .call(cwd = root, check = false, stdout = os.Inherit, stderr = os.Inherit)
    println(s"=== [$label] exit=${r.exitCode} ===")

  // 1. regenerate EN mirrors (SV source -> compendium-en/*, slides-en/*); writes ONLY the mirror.
  sbtRun("regen-mirrors", "autotranslateProject/run --all")

  // 2. SV compendium (screen, all-in-one): 2 passes for TOC
  sbtRun("sv-compendium p1", "pdfCompendium")
  sbtRun("sv-compendium p2", "pdfCompendium")

  // 3. SV print compendium1/2: interleaved 2 rounds so the cross-doc xref resolves (cf build.sbt `pdf`)
  for r <- 1 to 2 do
    sbtRun(s"sv-comp1 r$r", "pdfCompendium1")
    sbtRun(s"sv-comp2 r$r", "pdfCompendium2")

  // 4. SV slides (all 19 decks): 2 passes each for beamer TOC/nav
  sbtRun("sv-slides p1", s"pdfSlides $decks")
  sbtRun("sv-slides p2", s"pdfSlides $decks")

  // 5. EN compendium (screen): 2 passes
  sbtRun("en-compendium p1", "pdfCompendiumEn")
  sbtRun("en-compendium p2", "pdfCompendiumEn")

  // 6. EN print compendium1-en/2-en: interleaved 2 rounds
  for r <- 1 to 2 do
    sbtRun(s"en-comp1 r$r", "pdfCompendium1En")
    sbtRun(s"en-comp2 r$r", "pdfCompendium2En")

  // 7. EN slides (empty args = all decks): 2 passes
  sbtRun("en-slides p1", "pdfSlidesEn")
  sbtRun("en-slides p2", "pdfSlidesEn")

  // ---------------- report ----------------
  println("\n======== RELEASE BUILD REPORT ========")
  def errCount(log: os.Path): Int =
    if !os.exists(log) then -1
    else new String(os.read.bytes(log), "ISO-8859-1").linesIterator.count(_.startsWith("! "))

  def report(pdf: os.Path, log: os.Path): Unit =
    val ok  = os.exists(pdf)
    val sz  = if ok then f"${os.size(pdf) / 1024}%,d KB" else "MISSING"
    val e   = errCount(log)
    val es  = if e < 0 then "no-log" else s"$e err"
    println(f"  ${if ok then "OK  " else "FAIL"}  ${pdf.last}%-30s $sz%12s  $es")

  val comp = root / "compendium"; val compEn = root / "compendium-en"
  val sl   = root / "slides";     val slEn   = root / "slides-en"
  report(comp / "compendium.pdf",      comp / "compendium.log")
  report(comp / "compendium1.pdf",     comp / "compendium1.log")
  report(comp / "compendium2.pdf",     comp / "compendium2.log")
  report(compEn / "compendium-en.pdf", compEn / "compendium-en.log")
  report(compEn / "compendium1-en.pdf",compEn / "compendium1-en.log")
  report(compEn / "compendium2-en.pdf",compEn / "compendium2-en.log")
  println("  --- SV slides ---")
  for d <- decks.split(" ") do report(sl / s"lect-$d.pdf", sl / s"lect-$d.log")
  println("  --- EN slides ---")
  for d <- decks.split(" ") do report(slEn / s"lect-$d-en.pdf", slEn / s"lect-$d-en.log")
  println("======================================")
