import sbt._
import Process._
import Keys._


lazy val hello = taskKey[Unit]("Prints welcome message")

hello := {
  println("===== WELCOME TO lunduniversity/introprog =====")
}

lazy val commonSettings = Seq(
  organization := "se.lth.cs",
  version := "16.1",
  scalaVersion := "2.11.8"
)


lazy val plan = (project in file("plan")).
  settings(commonSettings: _*).
  settings(
    name := "plan",
    EclipseKeys.skipProject := true
  )

lazy val workspace = (project in file("workspace")).
  settings(commonSettings: _*).
  settings(
    name := "workspace",
    EclipseKeys.withSource := true
  )

lazy val build = taskKey[Unit]("plan/run before pdf")

lazy val gen = taskKey[Unit]("alias for plan/run")

gen := (run in Compile in plan).toTask("").value

// ************** cmd util functions

def runPdfLatexCmd(texFile: File, workDir: File, stdOutSuffix: String = "-console.log"): Unit = {
  val cmd = Process(
    Seq("pdflatex","-halt-on-error", texFile.getName),
    workDir
  )
  val cmdOutputFile =  workDir / texFile.getName.replace(".tex", stdOutSuffix)
  // val bibtexCmd = Process(Seq("bibtex", texFile.getName.replace(".tex", ".aux")), workDir)
  val exitValue = cmd.#>(cmdOutputFile).#&&(cmd).#>(cmdOutputFile).run.exitValue
  if (exitValue != 0) {
    println("*** ############ ERROR LOG STARTS HERE ############### ***")
    Process(Seq("cat", cmdOutputFile.getName), workDir).run
    error(s"\n*** ERROR: pdflatex exit code: $exitValue\nSee pdflatex output in: $cmdOutputFile")
  } else println(s"     Log file: $cmdOutputFile")
}

// **************

lazy val pdf = taskKey[Unit]("Compile slides and compendium using pdflatex")

pdf := {
  println(" ******* compiling slides to pdf *******")
  val workDir = file("slides")
  val texFiles = (workDir * "*.tex").get
  for (texFile <- texFiles) {
    println(s" *** pdflatex $texFile")
    runPdfLatexCmd(texFile, workDir)
  }
  println(" ******* compiling compendium1 to pdf *******")
  runPdfLatexCmd(texFile = file("compendium1.tex"), workDir = file("compendium"))

  println(" ******* compiling compendium2 to pdf *******")
  runPdfLatexCmd(texFile = file("compendium2.tex"), workDir = file("compendium"))

  println(" ******* compiling compendium to pdf *******")
  runPdfLatexCmd(texFile = file("compendium.tex"), workDir = file("compendium"))

//  println(" ******* compiling exercises to pdf *******")
//  runPdfLatexCmd(texFile = file("exercises.tex"), workDir = file("compendium"))

//  println(" ******* compiling solutions to pdf *******")
//  runPdfLatexCmd(texFile = file("solutions.tex"), workDir = file("compendium"))
}

lazy val root = (project in file(".")).
  aggregate(workspace, plan).
  settings(commonSettings: _*).
  settings(
    name := "introprog",
    build := Def.sequential(
      hello,
      (run in Compile in plan).toTask(""),
      pdf
    ).value
  )

// ***********************************************************
