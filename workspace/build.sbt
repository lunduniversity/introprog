import sbt._
import Process._
import Keys._


lazy val commonSettings = Seq(
  organization := "se.lth.cs",
  version := "16.1",
  scalaVersion := "2.11.7"
)

lazy val cslib = (project in file("cslib")).
  settings(commonSettings: _*).
  settings(
    name := "cslib"
  )

lazy val week01 = (project in file("week01")).
  settings(commonSettings: _*).
  settings(
    name := "week01"
  ).dependsOn(cslib)
  
lazy val week03 =(project in file("week03")).
  settings(commonSettings: _*).
  settings(
    name := "week03"
  ).dependsOn(cslib)
  
lazy val workspace = (project in file(".")). 
  settings(commonSettings: _*).
  settings(
    name := "workspace" ,
    EclipseKeys.withSource := true,
    EclipseKeys.skipProject := true,
    EclipseKeys.skipParents in ThisBuild := true  // ??? https://github.com/typesafehub/sbteclipse/wiki/Using-sbteclipse
 )


