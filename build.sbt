import sbt._
import Process._
import Keys._
import complete.DefaultParsers._

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val hello = taskKey[Unit]("Prints welcome message")
hello := println("""
  ======= WELCOME to the sbt build of lunduniversity/introprog =========

    type 'build' for a complete pdf build including dependent gen tasks
      DON'T PANIC: a full build can take >200sec on a 2.8GHz machine...

    type 'pdf' generate all pdf files

    type 'pdf<TAB>' to see individual pdf build commands

    type 'gen' to generate plan files (headings land in target/, nothing else is touched)

    type 'syncMuntabot' to copy the generated Swedish headings into a local
      muntabot clone -- deliberately NOT part of 'build', because the page numbers
      come from THIS box's compendium.pdf; rebuild that first if unsure

    type 'genquiz' to generate quiz files

    type 'gengloss' to generate glossary files

    type 'projects' to see all sub-projects

    type 'project workspace' to change to sub-project workspace

    type 'scalaVersion' to see all versions in (sub)projects

    type 'hello' to see this message

    --- English mirror (autotranslate sub-project) ---

      NOTE: a MODEL BACKEND (local ollama, or codeberg.org/bjornregnell/modly on LAN)
            is needed ONLY for --all with new Swedish prose, and for --clean.
            The default path below is offline: it reads the committed cache.

    the English side is GENERATED from the Swedish side and is not git-tracked,
      so it must be regenerated after every change to the Swedish source

    type 'autotranslate' to (re)build the English mirror compendium-en/ and slides-en/
      DEFAULT = translate from the COMMITTED CACHE with the backend OFF:
      offline, deterministic, a few seconds, no ollama needed.
      It FAILS LOUDLY if Swedish prose is not in the cache yet, so it can never
      emit a silently untranslated (all-Swedish) English side.

    when you have UPDATED SWEDISH PROSE the cache does not know it yet, so:
      1. 'autotranslateProject/run --all'   translate the new units with the model
           (needs a backend: modly on LAN, else local ollama; with NO backend the
            new units just stay Swedish) and write autotranslate/translate-cache.tsv
      2. commit translate-cache.tsv, so everyone else can rebuild offline
      3. if the fallback count moved, update autotranslate/cache-only-baseline.txt

    usual order after editing the Swedish side:
      gen  ->  autotranslate  ->  pdfCompendiumEn  ->  gen  ->  syncMuntabot
      ('gen' first only when plan/quiz/glossary changed: the mirror copies what is there)
      the SECOND 'gen' is not a typo: the sv->en heading map muntabot displays is read
      from compendium-en.pdf, which autotranslate clears and pdfCompendiumEn rebuilds,
      so a map generated before that step describes the PREVIOUS English edition.
      Without compendium-en.pdf no map is written at all and the old one stands,
      which is deliberate: an empty map would silently revert every label to Swedish.

    other 'autotranslateProject/run' flags:
      --all          cache + model; the one to use after writing new Swedish prose
      --only w01     restrict to files matching a substring
      --cache-only   the default, stated explicitly (what CI should call)
      --mirror-only  copy WITHOUT translating: the English side stays SWEDISH.
                       scaffold/plumbing tests only; it warns when it does this
      --clean        drop the cache (a following --all then needs the model for everything)
      --swedish-left / --prose-swedish / --pdf-swedish <pdf>   how much Swedish is left
      --selftest / --latextest / --dryrun / --retry-fallbacks

    translation backend + model is set in autotranslate/Translate.scala (SelectedModel);
      uses the modly GPU server if reachable, else local Ollama, else keeps Swedish

    type 'pdfCompendiumEn' to build the English compendium-en/compendium-en.pdf
      (it reports how much Swedish is left in the pdf it just built)

    type 'pdfSlidesEn w01' to build an English lecture, e.g. slides-en/lect-w01-en.pdf

  =====================================================================

""")

lazy val myStartupTransition: State => State = { s: State =>
  "hello" :: s
}

lazy val commonSettings = Seq(
  organization := "se.lth.cs",
  version := "2024.0.2",
  scalaVersion := "3.8.3",
  scalacOptions ++= Seq("-deprecation", "-feature")
)

lazy val plan = (project in file("plan")).settings(commonSettings: _*).
  dependsOn(glossary). // plan reads glossary.explain to generate translations for muntabot
  settings(
    name := "plan",
    libraryDependencies += "com.lihaoyi" %% "os-lib" % "0.10.2",
  )

lazy val quiz = (project in file("quiz")).settings(commonSettings: _*).
  settings(
    name := "quiz",
  )

lazy val workspace = (project in file("workspace")).settings(commonSettings: _*).
  settings(
    name := "workspace",
  )

lazy val glossary = (project in file("glossary")).settings(commonSettings: _*).
  settings(
    name := "glossary",
  )

lazy val autotranslateProject = (project in file("autotranslate")).settings(commonSettings: _*).
  dependsOn(glossary). // typed access to glossary.explain.allConcepts (authoritative sv<->en)
  settings(
    name := "autotranslate",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "os-lib"   % "0.11.8",
      "com.lihaoyi" %% "requests" % "0.9.3",
      "com.lihaoyi" %% "ujson"    % "4.4.3",
    ),
  )


// `build` is a COMMAND alias (not a task): each step runs as its own command,
// so the final `gen` re-runs AFTER `pdf`. (As a task, `gen` is one node in the
// graph and sbt evaluates it once — the second `gen` would be skipped.) This
// makes headings-GENERATED.scala reflect the freshly built compendium pages.
//
// The English side is built too, from the CACHE ONLY (autotranslate's default: no model
// backend, about 11s + one pdflatex run). That is not decoration: FindHeadings reads
// whichever compendium-en.pdf happens to be on disk, so a stale or untranslated one
// silently becomes muntabot's ENGLISH heading table -- on 2026-08-21 that is exactly how
// Swedish titles ended up in headings-En-GENERATED.scala. Regenerating it right before the
// final `gen` makes the English headings as trustworthy as the Swedish ones, and makes a
// full `build` cover both languages instead of only one.
addCommandAlias("build", "gen; genquiz; gengloss; pdf; autotranslate; pdfCompendiumEn; gen")

lazy val gen = taskKey[Unit]("alias for plan/run")
gen := (plan/Compile/run).toTask("").value

// Distribution of the generated headings is DELIBERATE, never a build side effect. `gen` writes
// target/ only; this task is the one act that reaches into another repository. It used to happen
// automatically inside FindHeadings, guarded by nothing but "does the directory exist", so a plain
// `sbt gen` silently dirtied muntabot on any box that had it cloned -- and since the page numbers
// come from THIS box's compendium.pdf, a stale local build could hand muntabot wrong numbers with
// nobody deciding to. Keep it opt-in, and keep it out of the `build` alias.
lazy val syncMuntabot = taskKey[Unit]("copy generated headings from target/ into a local muntabot clone")
syncMuntabot := {
  val hub = baseDirectory.value.getParentFile.getParentFile
  val dest = hub / "bjornregnell" / "muntabot" / "src" / "main" / "scala"
  if (!dest.isDirectory)
    sys.error(s"no muntabot clone at $dest -- clone bjornregnell/muntabot beside lunduniversity/")
  // TWO files: the Swedish headings table (page geometry for the links) and the sv -> en display
  // map muntabot shows in English mode. The map used to be generated inside muntabot by a local
  // ollama run, which meant the same heading was translated twice by two systems that could
  // silently disagree -- at the switchover 85 of 157 shared headings DID disagree. Now introprog
  // is the single writer, and the text is what the English compendium actually prints.
  // headings-En-GENERATED.scala (headingsEn) still stays in target/: nothing in muntabot reads it.
  val targetDir = baseDirectory.value / "target"
  val required = targetDir / "headings-GENERATED.scala"
  if (!required.isFile) sys.error(s"no $required -- run `sbt gen` first")
  // The sv->en label map only exists once compendium-en.pdf has been built, and on a box without
  // it there is no authoritative English text to ship. Sync the Swedish headings anyway and SAY so,
  // rather than refusing to sync at all -- muntabot then keeps the map it already has committed.
  val optional = targetDir / "heading-translate-GENERATED.scala"
  // translations-GENERATED.scala (the glossary concept map) used to be written straight into the
  // muntabot clone by FindTranslations, as a side effect of `gen`. It ships here instead now, so
  // that `gen` truly never touches another repository.
  val concepts = targetDir / "translations-GENERATED.scala"
  if (!concepts.isFile) sys.error(s"no $concepts -- run `sbt gen` first")
  val srcs = Seq(required, concepts) ++ (if (optional.isFile) Seq(optional) else Seq.empty)
  srcs.foreach { src =>
    IO.copyFile(src, dest / src.getName)
    println(s"syncMuntabot: ${src.getName} -> $dest")
  }
  if (!optional.isFile)
    println(s"syncMuntabot: NO ${optional.getName} in target/ -- English labels keep muntabot's " +
      "committed map. Build compendium-en.pdf and re-run `sbt gen` to refresh them.")
  println("Next: rebuild and deploy with muntabot/publish.sh")
}

// The coupling that had NO detector: muntabot deep-links into compendium.pdf by physical page, so
// republishing the compendium re-paginates it and every muntabot link quietly lands on the wrong
// page. The links still RESOLVE, so nothing fails and nobody notices. This makes that state
// observable, as a plain file-vs-file stamp comparison needing no pdf and no pdftk.
lazy val checkMuntabot = taskKey[Unit]("report whether a local muntabot clone carries stale heading pagination")
checkMuntabot := {
  val base = baseDirectory.value
  val here = base / "target" / "headings-GENERATED.scala"
  val pdf = base / "compendium" / "compendium.pdf"
  val dest = base.getParentFile.getParentFile / "bjornregnell" / "muntabot" / "src" / "main" / "scala" / "headings-GENERATED.scala"
  def stampOf(f: File): Option[String] =
    if (!f.isFile) None
    else IO.read(f).linesIterator.find(_.contains("headingsStamp")).map(_.trim)
  if (!here.isFile)
    println("checkMuntabot: UNKNOWN -- no target/headings-GENERATED.scala. Run: sbt gen")
  else if (pdf.isFile && pdf.lastModified > here.lastModified)
    println("checkMuntabot: STALE at step 1 -- compendium.pdf is NEWER than the generated table.\n" +
            "  Run: sbt gen  ->  sbt syncMuntabot  ->  muntabot/publish.sh")
  else if (!dest.isFile)
    println(s"checkMuntabot: no muntabot clone at $dest (nothing to check)")
  else (stampOf(here), stampOf(dest)) match {
    case (Some(a), Some(b)) if a == b =>
      println("checkMuntabot: FRESH -- the muntabot clone links into THIS pagination")
    case (Some(_), Some(_)) =>
      println("checkMuntabot: STALE at step 2 -- the muntabot clone carries a DIFFERENT pagination.\n" +
              "  Its links still resolve, they just land on the wrong pages.\n" +
              "  Run: sbt syncMuntabot  ->  muntabot/publish.sh")
    case (Some(_), None) =>
      println("checkMuntabot: STALE -- the muntabot clone predates pagination stamping, so what\n" +
              "  pagination it carries cannot be known. Run: sbt syncMuntabot  ->  muntabot/publish.sh")
    case _ =>
      println("checkMuntabot: UNKNOWN -- no headingsStamp in target/. Run: sbt gen")
  }
}

lazy val genquiz = taskKey[Unit]("alias for quiz/run")
genquiz := (quiz/Compile/run).toTask("").value


lazy val gengloss = taskKey[Unit]("alias for glossary/run")
gengloss := (glossary/Compile/run).toTask("").value

lazy val autotranslate = taskKey[Unit]("alias for autotranslate/run")
autotranslate := (autotranslateProject/Compile/run).toTask("").value

// ************** cmd util functions

def showTail(fileName: String, n: Int = 40): Unit = { 
  // this method was created as tail does not work on windows
  println(s"--- Last $n lines of $fileName: ")
  val lines = sbt.io.IO.readLines(new java.io.File(fileName))
  println(lines.takeRight(n).mkString("\n"))
}

def runPdfLatexCmd(texFile: File, workDir: File, stdOutSuffix: String = "-console.log", maxPasses: Int = 1): Unit = {
  val cmd = scala.sys.process.Process(
    Seq("pdflatex","-halt-on-error", texFile.getName),
    workDir
  )
  val cmdOutputFile =  workDir / texFile.getName.replace(".tex", stdOutSuffix)
  // val bibtexCmd = Process(Seq("bibtex", texFile.getName.replace(".tex", ".aux")), workDir)

  // Run pdflatex until the .toc / cross-refs converge, i.e. until LaTeX stops asking to rerun — capped at
  // maxPasses (like a tiny latexmk; avoids guessing 2 vs 3). The Swedish tasks default to 1 because their working
  // dir (compendium/, slides/) keeps .aux/.toc across builds; the English mirror dirs are regenerated fresh every
  // run, so pass 1 writes the .toc but never reads it (no ToC), pass 2 typesets it (which shifts pages), and a
  // 3rd pass may be needed for the ToC/\pageref page numbers to settle. En tasks pass a cap of 4.
  println(s" ******* Compiling $texFile to pdf (up to $maxPasses pass(es)) *******")
  var exitValue = 0; var pass = 0; var rerun = true
  while (pass < math.max(1, maxPasses) && exitValue == 0 && rerun) {
    exitValue = cmd.#>(cmdOutputFile).run.exitValue
    pass += 1
    rerun = exitValue == 0 && {
      val log = scala.util.Try(IO.read(cmdOutputFile)).getOrElse("")
      log.contains("Rerun to get") || log.contains("Label(s) may have changed")
    }
  }
  println(s"         ($pass pdflatex pass(es) run)")
  if (exitValue != 0) {
    println("*** ############ ERROR LOG STARTS HERE ############### ***")
    //Process(Seq("cat", cmdOutputFile.getName), workDir).run
    //scala.sys.process.Process(Seq("tail", "-40", cmdOutputFile.getName), workDir).run
    showTail(s"$cmdOutputFile")
    sys.error(s"\n*** ERROR: pdflatex exit code: $exitValue\nSee COMPLETE pdflatex output in: $cmdOutputFile")
  } else println(s"         Log file: $cmdOutputFile")
}

// **************

lazy val pdf = taskKey[Unit]("Latex all pdfs several times for xrefs & tocs to work)")

pdf := {
  println("\n====== Compiling pdf documents -- this may take several minutes!")

  println("\n=== Compiling slides:")
  val workDir = file("slides")
  val texFiles = (workDir * "*.tex").get
  for (texFile <- texFiles) {
    runPdfLatexCmd(texFile, workDir)
  }

  println("\n=== The main doc with all stuff in one pdf optimized for screen:")
  runPdfLatexCmd(texFile = file("compendium.tex"), workDir = file("compendium"))

  println("\n=== Docs optimized for print, two times for external xref to work:")
  runPdfLatexCmd(texFile = file("compendium1.tex"), workDir = file("compendium"))
  runPdfLatexCmd(texFile = file("compendium2.tex"), workDir = file("compendium"))

  runPdfLatexCmd(texFile = file("compendium1.tex"), workDir = file("compendium"))
  runPdfLatexCmd(texFile = file("compendium2.tex"), workDir = file("compendium"))

  runPdfLatexCmd(texFile = file("lectures.tex"),  workDir = file("compendium"))
  runPdfLatexCmd(texFile = file("exercises.tex"), workDir = file("compendium"))
  runPdfLatexCmd(texFile = file("labs.tex"),      workDir = file("compendium"))
  runPdfLatexCmd(texFile = file("assignments.tex"), workDir = file("compendium"))
  runPdfLatexCmd(texFile = file("solutions.tex"), workDir = file("compendium"))
}

//http://www.scala-sbt.org/0.13/docs/Howto-Triggered.html
// This does not seem to work on sbt 1.1 :( as pdf task are not triggered on change anymore
//watchSources ++= ((baseDirectory.value / "compendium") * "*.tex").get
//Found workaround here thanks to eatkins:
//https://github.com/sbt/sbt/issues/4272
def ws(base: sbt.io.syntax.File, includeFilter: sbt.io.FileFilter): sbt.internal.io.Source =
  WatchSource(base, includeFilter, excludeFilter=NothingFilter)
// expands to e.g.:
//watchSources += WatchSource(baseDirectory.value / "compendium","*.tex", NothingFilter)
watchSources += ws(baseDirectory.value / "compendium",                  "*.tex")
watchSources += ws(baseDirectory.value / "compendium",                  "*.cls")
watchSources += ws(baseDirectory.value / "compendium" / "modules",      "*.tex")
watchSources += ws(baseDirectory.value / "compendium" / "prechapters",  "*.tex")
watchSources += ws(baseDirectory.value / "compendium" / "postchapters", "*.tex")
watchSources += ws(baseDirectory.value / "slides" / "body",             "*.tex")
watchSources += ws(baseDirectory.value / "slides",                      "*.tex")

lazy val pdfExercises = taskKey[Unit]("Compile exercises.tex")
pdfExercises := {
  runPdfLatexCmd(texFile = file("exercises.tex"), workDir = file("compendium"))
}

lazy val pdfSolutions = taskKey[Unit]("Compile solutions.tex")
pdfSolutions := {
  runPdfLatexCmd(texFile = file("solutions.tex"), workDir = file("compendium"))
}

lazy val pdfLabs = taskKey[Unit]("Compile labs.tex")
pdfLabs := {
  runPdfLatexCmd(texFile = file("labs.tex"), workDir = file("compendium"))
}

lazy val pdfCompendium = taskKey[Unit]("Compile compendium.tex")
pdfCompendium := {
  runPdfLatexCmd(texFile = file("compendium.tex"), workDir = file("compendium"))
}

// Best-effort Swedish-% report via the autotranslate scanner (single source of truth: Code.swedishish),
// run AFTER an English PDF is built so the *En tasks ALWAYS show how close to 0% Swedish we are. Wrapped
// so a missing pdftotext / scanner error never fails the build.
def reportSwedishPct(autotranslateCp: String, pdf: File): Unit =
  try { import scala.sys.process._; Seq("java", "-cp", autotranslateCp, "Main", "--pdf-swedish", pdf.getPath).!; () }
  catch { case _: Throwable => println(s"  (swedish-% report skipped: ${pdf.getName})") }

lazy val pdfCompendiumEn = taskKey[Unit]("Compile the generated English mirror compendium-en/compendium-en.tex")
pdfCompendiumEn := {
  val cp = (autotranslateProject / Compile / fullClasspath).value.files.map(_.getPath).mkString(java.io.File.pathSeparator)
  runPdfLatexCmd(texFile = file("compendium-en.tex"), workDir = file("compendium-en"), maxPasses = 4)
  reportSwedishPct(cp, file("compendium-en/compendium-en.pdf"))
}

lazy val pdfCompendiumPrint = taskKey[Unit]("Compile compendium-print.tex")
pdfCompendiumPrint := {
  runPdfLatexCmd(texFile = file("compendium-print.tex"), workDir = file("compendium"))
}

lazy val pdfCompendium1 = taskKey[Unit]("Compile compendium1.tex")
pdfCompendium1 := {
  runPdfLatexCmd(texFile = file("compendium1.tex"), workDir = file("compendium"))
}

lazy val pdfCompendium2 = taskKey[Unit]("Compile compendium2.tex")
pdfCompendium2 := {
  runPdfLatexCmd(texFile = file("compendium2.tex"), workDir = file("compendium"))
}

lazy val pdfCompendium1En = taskKey[Unit]("Compile the generated English mirror compendium-en/compendium1-en.tex")
pdfCompendium1En := {
  val cp = (autotranslateProject / Compile / fullClasspath).value.files.map(_.getPath).mkString(java.io.File.pathSeparator)
  runPdfLatexCmd(texFile = file("compendium1-en.tex"), workDir = file("compendium-en"), maxPasses = 4)
  reportSwedishPct(cp, file("compendium-en/compendium1-en.pdf"))
}

lazy val pdfCompendium2En = taskKey[Unit]("Compile the generated English mirror compendium-en/compendium2-en.tex")
pdfCompendium2En := {
  val cp = (autotranslateProject / Compile / fullClasspath).value.files.map(_.getPath).mkString(java.io.File.pathSeparator)
  runPdfLatexCmd(texFile = file("compendium2-en.tex"), workDir = file("compendium-en"), maxPasses = 4)
  reportSwedishPct(cp, file("compendium-en/compendium2-en.pdf"))
}

lazy val pdfSlides = inputKey[Unit]("run pdflatex slides/lect-w<weeknumber>.tex")
pdfSlides := {
  // http://www.scala-sbt.org/1.0/docs/Input-Tasks.html#Basic+Input+Task+Definition
  val args: Seq[String] = spaceDelimited("<arg>").parsed
  val workDir = file("slides")
  val weeks = if (args.isEmpty) {
    val default = Seq.tabulate(7)(i => s"w0${i+1}")
    println(s"""<args> is empty, using ${default.mkString(" ")}""")
    default
  } else args
  for (w <- weeks) {
    val f: String = if (w startsWith "w") "lect-" + w else w // hack to make it possible to give both just w01 as arg but also info-week00 as arg
    val texFile = if (f.takeRight(4) != ".tex") file(f + ".tex") else file(f)
    println(s"runPdfLatexCmd($texFile, $workDir)")
    runPdfLatexCmd(texFile, workDir)
  }
  if (args.isEmpty) runPdfLatexCmd(file("all-lectures.tex"), workDir)
}

lazy val pdfSlidesEn = inputKey[Unit]("run pdflatex on the English mirror slides-en/lect-w<weeknumber>-en.tex")
pdfSlidesEn := {
  val args: Seq[String] = spaceDelimited("<arg>").parsed
  val cp = (autotranslateProject / Compile / fullClasspath).value.files.map(_.getPath).mkString(java.io.File.pathSeparator)
  val workDir = file("slides-en")
  val weeks = if (args.isEmpty) {
    // empty args = build ALL decks (like pdfSlides), derived from the Swedish source set slides/lect-*.tex
    val decks = Option(file("slides").listFiles).getOrElse(Array.empty)
      .filter(f => f.getName.startsWith("lect-") && f.getName.endsWith(".tex"))
      .map(_.getName.stripPrefix("lect-").stripSuffix(".tex"))
      .sorted.toSeq
    println(s"""<args> is empty, building all ${decks.size} decks: ${decks.mkString(" ")}""")
    decks
  } else args
  for (w <- weeks) {
    val f: String = if (w startsWith "w") "lect-" + w else w // allow both w01 and lect-w01 / file names
    val name = if (f.takeRight(4) == ".tex") f.dropRight(4) + "-en.tex" else f + "-en.tex"
    val texFile = file(name)
    println(s"runPdfLatexCmd($texFile, $workDir)")
    runPdfLatexCmd(texFile, workDir, maxPasses = 4)
    reportSwedishPct(cp, new File(workDir, name.dropRight(4) + ".pdf"))
  }
}

lazy val root = (project in file(".")).
  aggregate(workspace, plan, quiz, glossary, autotranslateProject).
  settings(commonSettings: _*).
  settings(
    name := "introprog root",
    Global/onLoad := {
      // https://www.scala-sbt.org/1.0/docs/offline/Howto-Startup.html
      val old = (Global/onLoad).value
      myStartupTransition.compose(old)
    }
  )
