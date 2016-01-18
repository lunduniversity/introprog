import sbt._
import Process._
import Keys._

lazy val commonSettings = Seq(
  organization := "se.lth.cs",
  version := "2016.0.1",
  scalaVersion := "2.11.7"
)

// ************** cmd util functions

def runPdfLatexCmd(texFile: File, workDir: File, stdOutSuffix: String = "-cmdout.txt"): Unit = {
  val cmd = Process(
    Seq("pdflatex","-halt-on-error", texFile.getName), 
    workDir
  )
  val cmdOutputFile =  workDir / texFile.getName.replace(".tex", stdOutSuffix) 
  // val bibtexCmd = Process(Seq("bibtex", texFile.getName.replace(".tex", ".aux")), workDir)
  val exitValue = cmd.#>(cmdOutputFile).run.exitValue
  if (exitValue != 0) 
    error(s"\n*** ERROR: pdflatex exit code: $exitValue\nSee pdflatex output in: $cmdOutputFile")
}

// ************** 

lazy val pdflectures = taskKey[Unit]("Compile lectures in latex into pdf")
lazy val copylectures = taskKey[Unit]("Copy lectures in pdf from src to top project level")

lazy val lectures = (project in file("lectures")).
  settings(commonSettings: _*).
  settings(
    name := "lectures",
    EclipseKeys.skipProject := true,
    pdflectures := { 
      println(" ******* building pdflectures *******") 
      val workDir = file("lectures/src/latex")
      val texFiles = (workDir * "*.tex").get
      for (texFile <- texFiles) {
        println(s" *** pdflatex $texFile")
        runPdfLatexCmd(texFile, workDir)         
      } 
    },
    copylectures := {
      println(" ******* copying lectures pdf files *******") 
      val fromDir = file("lectures/src/latex")
      val toDir = file("lectures")
      val pdfFiles = (fromDir * "*.pdf").get
      for (pdfFile <- pdfFiles) {
        println(s" *** copy $pdfFile")
        IO.copyFile(pdfFile, toDir / pdfFile.getName)         
      } 
    }
)


// *************** 

lazy val util = (project in file("util")).
  settings(commonSettings: _*).
  settings(
    name := "util"
  )

// *************** 

lazy val labs = (project in file("labs")).
  settings(commonSettings: _*).
  settings(
    name := "labs",
    EclipseKeys.withSource := true
  )  
  

// ***********************************************************

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "introprog"
  )
