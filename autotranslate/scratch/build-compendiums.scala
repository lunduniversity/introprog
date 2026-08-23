//> using scala 3.8.4
//> using dep com.lihaoyi::os-lib:0.11.8

// Build ALL compendium PDFs (SV + EN), 2 passes each for TOC/cross-refs, with the print pair interleaved so the
// compendium1<->2 external xref resolves (mirrors build-release.scala's compendium section; NO slides). Regens
// the EN mirror first (--all, mostly cache hits) so the EN compendium reflects current source. Reports per-PDF
// status + pdflatex "! " error counts. Run bare with the introprog root:
//   scala-cli run autotranslate/scratch/build-compendiums.scala -- /home/bjornr/git/hub/lunduniversity/introprog
@main def buildCompendiums(rootStr: String): Unit =
  val root = os.Path(rootStr)

  def sbtRun(label: String, cmd: String): Unit =
    println(s"\n=== [$label] sbt --client $cmd ===")
    val r = os.proc("sbt", "--client", cmd)
      .call(cwd = root, check = false, stdout = os.Inherit, stderr = os.Inherit)
    println(s"=== [$label] exit=${r.exitCode} ===")

  // 1. regen EN mirror (SV source -> compendium-en/*, slides-en/*); SV compendium is invariant under this.
  sbtRun("regen-mirrors", "autotranslateProject/run --all")

  // 2. SV compendium (screen): 2 passes for TOC
  sbtRun("sv-compendium p1", "pdfCompendium")
  sbtRun("sv-compendium p2", "pdfCompendium")
  // 3. SV print compendium1/2: interleaved 2 rounds so the cross-doc xref resolves
  for r <- 1 to 2 do
    sbtRun(s"sv-comp1 r$r", "pdfCompendium1")
    sbtRun(s"sv-comp2 r$r", "pdfCompendium2")
  // 4. EN compendium (screen): 2 passes
  sbtRun("en-compendium p1", "pdfCompendiumEn")
  sbtRun("en-compendium p2", "pdfCompendiumEn")
  // 5. EN print compendium1-en/2-en: interleaved 2 rounds
  for r <- 1 to 2 do
    sbtRun(s"en-comp1 r$r", "pdfCompendium1En")
    sbtRun(s"en-comp2 r$r", "pdfCompendium2En")

  // ---------------- report ----------------
  println("\n======== COMPENDIUM BUILD REPORT ========")
  def errCount(log: os.Path): Int =
    if !os.exists(log) then -1
    else new String(os.read.bytes(log), "ISO-8859-1").linesIterator.count(_.startsWith("! "))
  def report(pdf: os.Path, log: os.Path): Unit =
    val ok = os.exists(pdf)
    val sz = if ok then f"${os.size(pdf) / 1024}%,d KB" else "MISSING"
    val e  = errCount(log)
    val es = if e < 0 then "no-log" else s"$e err"
    println(f"  ${if ok then "OK  " else "FAIL"}  ${pdf.last}%-30s $sz%12s  $es")
  val comp = root / "compendium"; val compEn = root / "compendium-en"
  report(comp / "compendium.pdf",       comp / "compendium.log")
  report(comp / "compendium1.pdf",      comp / "compendium1.log")
  report(comp / "compendium2.pdf",      comp / "compendium2.log")
  report(compEn / "compendium-en.pdf",  compEn / "compendium-en.log")
  report(compEn / "compendium1-en.pdf", compEn / "compendium1-en.log")
  report(compEn / "compendium2-en.pdf", compEn / "compendium2-en.log")
  println("=========================================")
