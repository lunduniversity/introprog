import sbt._
import Process._
import Keys._

lazy val commonSettings = Seq(
  organization := "se.lth.cs",
  version := "16.1",
  scalaVersion := "2.11.7"
)

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
    error(s"\n*** ERROR: pdflatex exit code: $exitValue\nSee pdflatex output in: $cmdOutputFile")
  }
}

// ************** 

lazy val pdfslides = taskKey[Unit]("Compile slides in slides/latex using pdflatex")
lazy val copyslides = taskKey[Unit]("Copy *.pdf from slides/latex to slides/pdf")

lazy val slides = (project in file("slides")).
  settings(commonSettings: _*).
  settings(
    name := "slides",
    EclipseKeys.skipProject := true,
    pdfslides := { 
      println(" ******* compiling slides to pdf*******") 
      val workDir = file("slides")
      val texFiles = (workDir * "*.tex").get
      for (texFile <- texFiles) {
        println(s" *** pdflatex $texFile")
        runPdfLatexCmd(texFile, workDir)         
      } 
    },
    copyslides := {
      println(" ******* copying slides to pdf *******") 
      val fromDir = file("slides")
      val toDir = file("slides/pdf")
      IO.createDirectory(toDir)
      val pdfFiles = (fromDir * "*.pdf").get
      for (pdfFile <- pdfFiles) {
        println(s" *** copy $pdfFile")
        IO.copyFile(pdfFile, toDir / pdfFile.getName)         
      } 
    }
)


lazy val workspace = (project in file("workspace")).
  settings(commonSettings: _*).
  settings(
    name := "workspace",
    EclipseKeys.withSource := true
  )  

// ***********************************************************

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "introprog"
  )
