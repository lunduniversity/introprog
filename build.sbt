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

  =====================================================================

""")

lazy val myStartupTransition: State => State = { s: State =>
  "hello" :: s
}

lazy val commonSettings = Seq(
  organization := "se.lth.cs",
  version := "2024.0.1",
  scalaVersion := "3.3.3",
  scalacOptions ++= Seq("-deprecation", "-feature")
)

lazy val plan = (project in file("plan")).settings(commonSettings: _*).
  settings(
    name := "plan",
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


lazy val build = taskKey[Unit]("complete build including plan/run before pdf")
build := Def.sequential(
  gen,
  genquiz,
  gengloss,
  pdf
).value

lazy val gen = taskKey[Unit]("alias for plan/run")
gen := (plan/Compile/run).toTask("").value

lazy val genquiz = taskKey[Unit]("alias for quiz/run")
genquiz := (quiz/Compile/run).toTask("").value


lazy val gengloss = taskKey[Unit]("alias for glossary/run")
gengloss := (glossary/Compile/run).toTask("").value

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

lazy val root = (project in file(".")).
  aggregate(workspace, plan, quiz, glossary).
  settings(commonSettings: _*).
  settings(
    name := "introprog root",
    Global/onLoad := {
      // https://www.scala-sbt.org/1.0/docs/offline/Howto-Startup.html
      val old = (Global/onLoad).value
      myStartupTransition.compose(old)
    }
  )
