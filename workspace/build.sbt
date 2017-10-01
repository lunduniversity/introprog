import sbt._
import Process._
import Keys._


lazy val commonSettings = Seq(
  organization := "se.lth.cs",
  version := "2017.2",
  scalaVersion := "2.12.2"  // ScalaIDE is not ready for 2.12.3 yet
)

lazy val cslib = (project in file("cslib")).
  settings(commonSettings: _*).
  settings(
    name := "cslib",
    javacOptions in (Compile,doc) ++= Seq(
      "-encoding", "UTF-8", "-charset", "UTF-8", "-docencoding", "UTF-8")
  )

lazy val w03_irritext =(project in file("w03_irritext")).
    settings(commonSettings: _*).
    settings(
      name := "w03_irritext",
      EclipseKeys.skipProject := true
    ).dependsOn(cslib)

lazy val w04_blockmole =(project in file("w04_blockmole")).
  settings(commonSettings: _*).
  settings(
    name := "w04_blockmole",
    EclipseKeys.skipProject := true
  ).dependsOn(cslib)

lazy val w05_turtle =(project in file("w05_turtle")).
  settings(commonSettings: _*).
  settings(
    name := "w05_turtle",
    EclipseKeys.skipProject := true
  ).dependsOn(cslib)

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

lazy val w08_maze =(project in file("w08_maze")).
  settings(commonSettings: _*).
  settings(
    name := "w08_maze"
  ).dependsOn(cslib)


lazy val w09_turtlerace_team =(project in file("w09_turtlerace_team")).
  settings(commonSettings: _*).
  settings(
    name := "w09_turtlerace_team"
  ).dependsOn(cslib)

lazy val w10_chords_team =(project in file("w10_chords_team")).
  settings(commonSettings: _*).
  settings(
    name := "w10_chords_team"
  ).dependsOn(cslib)

lazy val w11_lthopoly_team =(project in file("w11_lthopoly_team")).
  settings(commonSettings: _*).
  settings(
    name := "w11_lthopoly_team"
  )

lazy val w12_survey =(project in file("w12_survey")).
  settings(commonSettings: _*).
  settings(
    name := "w12_survey"
  ).dependsOn(cslib)

lazy val w13_life =(project in file("w13_life")).
  settings(commonSettings: _*).
  settings(
    name := "w13_life"
  ).dependsOn(cslib)

lazy val w13_bank_proj =(project in file("w13_bank_proj")).
  settings(commonSettings: _*).
  settings(
    name := "w13_bank_proj"
  ).dependsOn(cslib)

lazy val w13_img_proj =(project in file("w13_img_proj")).
  settings(commonSettings: _*).
  settings(
    name := "w13_img_proj"
  ).dependsOn(cslib)

lazy val w13_tictactoe_proj =(project in file("w13_tictactoe_proj")).
  settings(commonSettings: _*).
  settings(
    name := "w13_tictactoe_proj"
  ).dependsOn(cslib)

lazy val workspace = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "workspace",
    EclipseKeys.withSource := true,
    EclipseKeys.skipProject := true,
    EclipseKeys.skipParents in ThisBuild := true
    // https://github.com/typesafehub/sbteclipse/wiki/Using-sbteclipse
 )
