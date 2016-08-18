import sbt._
import Process._
import Keys._

libraryDependencies += "jline" % "jline" % "2.14.2"

lazy val commonSettings = Seq(
  organization := "se.lth.cs",
  version := "16.1",
  scalaVersion := "2.11.8"
)

lazy val util = (project in file(".")). 
  settings(commonSettings: _*).
  settings(
    name := "util" 
 )


