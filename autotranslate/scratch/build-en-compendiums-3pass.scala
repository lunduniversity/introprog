//> using scala 3.8.4
//> using dep com.lihaoyi::os-lib:0.11.8

// Build the THREE English compendium PDFs — screen `compendium-en` + print pair `compendium1-en`/`compendium2-en` —
// with THREE passes each so the TOC and cross-refs (incl. the compendium1<->2 external xref) fully resolve.
// Regens the EN mirror first (--all, mostly cache hits). EN-only: the SV compendium is untouched. Run bare w/ root:
//   scala-cli run autotranslate/scratch/build-en-compendiums-3pass.scala -- /home/bjornr/git/hub/lunduniversity/introprog
@main def buildEn3(rootStr: String): Unit =
  val root = os.Path(rootStr)

  def sbtRun(label: String, cmd: String): Unit =
    println(s"\n=== [$label] sbt --client $cmd ===")
    val r = os.proc("sbt", "--client", cmd)
      .call(cwd = root, check = false, stdout = os.Inherit, stderr = os.Inherit)
    println(s"=== [$label] exit=${r.exitCode} ===")

  // 1. regen EN mirror (SV source -> compendium-en/*); SV compendium is invariant under this
  sbtRun("regen-mirrors", "autotranslateProject/run --all")
  // 2. EN screen compendium: 3 passes for TOC/refs
  for p <- 1 to 3 do sbtRun(s"en-compendium p$p", "pdfCompendiumEn")
  // 3. EN print pair: 3 interleaved rounds so compendium1-en <-> compendium2-en external xref resolves
  for r <- 1 to 3 do
    sbtRun(s"en-comp1 r$r", "pdfCompendium1En")
    sbtRun(s"en-comp2 r$r", "pdfCompendium2En")

  // ---------------- report ----------------
  println("\n======== EN COMPENDIUM BUILD REPORT (3-pass) ========")
  def errCount(log: os.Path): Int =
    if !os.exists(log) then -1
    else new String(os.read.bytes(log), "ISO-8859-1").linesIterator.count(_.startsWith("! "))
  def report(pdf: os.Path, log: os.Path): Unit =
    val ok = os.exists(pdf)
    val sz = if ok then f"${os.size(pdf) / 1024}%,d KB" else "MISSING"
    val e  = errCount(log)
    println(f"  ${if ok then "OK  " else "FAIL"}  ${pdf.last}%-24s $sz%12s  ${if e < 0 then "no-log" else s"$e err"}")
  val d = root / "compendium-en"
  report(d / "compendium-en.pdf",  d / "compendium-en.log")
  report(d / "compendium1-en.pdf", d / "compendium1-en.log")
  report(d / "compendium2-en.pdf", d / "compendium2-en.log")
  println("=====================================================")
