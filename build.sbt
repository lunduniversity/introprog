import org.bouncycastle.pqc.jcajce.provider.lms.LMSSignatureSpi.generic
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

    type 'gen' to generate plan files

    type 'genquiz' to generate quiz files

    type 'gengloss' to generate glossary files

    type 'projects' to see all sub-projects

    type 'project workspace' to change to sub-project workspace

    type 'scalaVersion' to see all versions in (sub)projects

    type 'hello' to see this message

    --- English mirror (autotranslate sub-project) ---

      NOTE: depends on ollama to be run locally or
            using codeberg.org/bjornregnell/modly on LAN 
            if cache is dropped with --clean

    type 'autotranslate' to (re)build the English mirror compendium-en/ and slides-en/
      (copies .tex as X-en.tex, rewrites \input + assets; content NOT translated by this task)

    for translation run e.g. 'autotranslateProject/run --only w01' (also --all),
      '... --clean' (drop cache), '--selftest', '--dryrun', '--latextest'

    translation backend + model is set in autotranslate/Translate.scala (SelectedModel);
      uses the modly GPU server if reachable, else local Ollama, else keeps Swedish

    type 'pdfCompendiumEn' to build the English compendium-en/compendium-en.pdf

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
addCommandAlias("build", "gen; genquiz; gengloss; pdf; gen")

lazy val gen = taskKey[Unit]("alias for plan/run")
gen := (plan/Compile/run).toTask("").value

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

def runPdfLatexCmd(texFile: File, workDir: File, stdOutSuffix: String = "-console.log"): Unit = {
  println(s" ******* Compiling $texFile to pdf *******")
  val cmd = scala.sys.process.Process(
    Seq("pdflatex","-halt-on-error", texFile.getName),
    workDir
  )
  val cmdOutputFile =  workDir / texFile.getName.replace(".tex", stdOutSuffix)
  // val bibtexCmd = Process(Seq("bibtex", texFile.getName.replace(".tex", ".aux")), workDir)

  // run pdflatex command TWICE in sequence to generate toc from .aux etc:
  //val exitValue = cmd.#>(cmdOutputFile).#&&(cmd).#>(cmdOutputFile).run.exitValue
  val exitValue = cmd.#>(cmdOutputFile).run.exitValue
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
  runPdfLatexCmd(texFile = file("compendium-en.tex"), workDir = file("compendium-en"))
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
    runPdfLatexCmd(texFile, workDir)
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
