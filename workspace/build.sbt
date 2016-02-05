import sbt._
import Process._
import Keys._


lazy val commonSettings = Seq(
  organization := "se.lth.cs",
  version := "2016.0.1",
  scalaVersion := "2.11.7"
)

lazy val cs_pt = (project in file("cs_pt")).
  settings(commonSettings: _*).
  settings(
    name := "cs_pt"
  )

lazy val week01 = (project in file("week01")).
  settings(commonSettings: _*).
  settings(
    name := "week01"
  ).dependsOn(cs_pt)
  
lazy val week03 =(project in file("week03")).
  settings(commonSettings: _*).
  settings(
    name := "week03"
  ).dependsOn(cs_pt)
  
lazy val root = (project in file(".")). 
  settings(commonSettings: _*).
  settings(
    name := "workspace" ,
    EclipseKeys.skipProject := true,
    EclipseKeys.skipParents in ThisBuild := true  // ??? https://github.com/typesafehub/sbteclipse/wiki/Using-sbteclipse
 )


