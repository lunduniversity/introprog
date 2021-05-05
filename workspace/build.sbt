import sbt._
import Process._
import Keys._

scalaVersion := "2.13.5"

lazy val commonSettings = Seq(
  organization := "se.lth.cs",
  version := "2021.0",
  scalaVersion := "2.13.5"  
)

// TODO: deprecate this lib:
lazy val cslib = (project in file("cslib")).  // still used by w13_img_proj
  settings(commonSettings: _*).
  settings(
    name := "cslib",
    version := "2017",
    Compile/doc/javacOptions ++= Seq(
      "-encoding", "UTF-8", "-charset", "UTF-8", "-docencoding", "UTF-8")
  )

val Version = "1.1.4"
val Name    = "introprog"
lazy val introprog_scalalib = (project in file("introprog")).
  settings(commonSettings: _*).
  settings(
    name := Name,
    version := Version,
    Compile/console/fork := true,
    scalacOptions ++= Seq("-encoding", "UTF-8"),
    Compile/doc/scalacOptions ++= Seq(
      "-implicits",
      "-groups",
      "-doc-title", Name,
      "-doc-footer", "Dep. of Computer Science, Lund University, Faculty of Engineering LTH",
      "-sourcepath", (ThisBuild/baseDirectory).value.toString,
      "-doc-version", Version,
      "-doc-root-content", (ThisBuild/baseDirectory).value.toString + "/src/rootdoc.txt",
      "-doc-source-url", s"https://github.com/lunduniversity/introprog-scalalib/tree/master€{FILE_PATH}.scala"
    )
  )

lazy val w03_irritext =(project in file("w03_irritext")).
    settings(commonSettings: _*).
    settings(
      name := "w03_irritext",
    )

lazy val w04_blockmole =(project in file("w04_blockmole")).
  settings(commonSettings: _*).
  settings(
    name := "w04_blockmole",
  ).dependsOn(introprog_scalalib)

lazy val w06_blockbattle =(project in file("w06_blockbattle")).
  settings(commonSettings: _*).
  settings(
    name := "w06_blockbattle",
  ).dependsOn(introprog_scalalib)

lazy val w07_shuffle =(project in file("w07_shuffle")).
  settings(commonSettings: _*).
  settings(
    name := "w07_shuffle",
  )

lazy val w08_life =(project in file("w08_life")).
  settings(commonSettings: _*).
  settings(
    name := "w08_life"
  ).dependsOn(introprog_scalalib)

lazy val w09_words =(project in file("w09_words")).
  settings(commonSettings: _*).
  settings(
    name := "w09_words",
  )

lazy val w10_snake =(project in file("w10_snake")).
  settings(commonSettings: _*).
  settings(
    name := "w10_snake"
  ).dependsOn(introprog_scalalib)


lazy val w11_javatext =(project in file("w11_javatext")).
  settings(commonSettings: _*).
  settings(
    name := "w11_javatext"
  )

lazy val w13_bank_proj =(project in file("w13_bank_proj")).
  settings(commonSettings: _*).
  settings(
    name := "w13_bank_proj"
  )

lazy val w13_tabular =(project in file("w13_tabular")).
settings(commonSettings: _*).
settings(
  name := "w13_tabular"
).dependsOn(introprog_scalalib) // TODO: remove this dependency when updated


lazy val w13_music_proj =(project in file("w13_music_proj")).
  settings(commonSettings: _*).
  settings(
    name := "w13_music_proj"
  )

// TODO: decouple dependency to cslib, extend introprog if needed
lazy val w13_img_proj =(project in file("w13_img_proj")).
  settings(commonSettings: _*).
  settings(
    name := "w13_img_proj"
  ).dependsOn(cslib)


lazy val workspace = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "workspace",
 )
