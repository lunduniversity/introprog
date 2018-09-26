import sbt._
import Process._
import Keys._


lazy val commonSettings = Seq(
  organization := "se.lth.cs",
  version := "2018.1",
  scalaVersion := "2.12.3"  // ScalaIDE is not ready for 2.12.6 yet
  // check versions here: http://scala-ide.org/download/sdk.html
)

lazy val cslib = (project in file("cslib")).  // still used by
  settings(commonSettings: _*).
  settings(
    name := "cslib",
    version := "2017",
    javacOptions in (Compile,doc) ++= Seq(
      "-encoding", "UTF-8", "-charset", "UTF-8", "-docencoding", "UTF-8")
  )

lazy val w03_irritext =(project in file("w03_irritext")).
    settings(commonSettings: _*).
    settings(
      name := "w03_irritext",
      EclipseKeys.skipProject := true
    )

lazy val w04_blockmole =(project in file("w04_blockmole")).
  settings(commonSettings: _*).
  settings(
    name := "w04_blockmole",
    EclipseKeys.skipProject := true
  )

lazy val w05_blockbattle =(project in file("w05_blockbattle")).
  settings(commonSettings: _*).
  settings(
    name := "w05_blockbattle",
    EclipseKeys.skipProject := true
  )

lazy val w06_shuffle =(project in file("w06_shuffle")).
  settings(commonSettings: _*).
  settings(
    name := "w06_shuffle",
    EclipseKeys.skipProject := true
  )

lazy val w07_words =(project in file("w07_words")).
  settings(commonSettings: _*).
  settings(
    name := "w07_words",
    EclipseKeys.skipProject := true
  )

lazy val w08_life =(project in file("w08_life")).
  settings(commonSettings: _*).
  settings(
    name := "w08_life"
  )

lazy val w09_snake =(project in file("w09_snake")).
  settings(commonSettings: _*).
  settings(
    name := "w09_snake"
  )

lazy val w10_tabular =(project in file("w10_tabular")).
  settings(commonSettings: _*).
  settings(
    name := "w10_tabular"
  ).dependsOn(cslib)

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

lazy val w13_img_proj =(project in file("w13_img_proj")).
  settings(commonSettings: _*).
  settings(
    name := "w13_img_proj"
  ).dependsOn(cslib)

lazy val w13_music_proj =(project in file("w13_music_proj")).
  settings(commonSettings: _*).
  settings(
    name := "w13_music_proj"
  )


lazy val workspace = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "workspace",
    EclipseKeys.withSource := true,
    EclipseKeys.skipProject := true,
    EclipseKeys.skipParents in ThisBuild := true,
    //EclipseKeys.relativizeLibs := true,
    // https://github.com/typesafehub/sbteclipse/wiki/Using-sbteclipse
 )
