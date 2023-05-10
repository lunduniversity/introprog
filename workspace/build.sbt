import sbt._
import Process._
import Keys._

lazy val commonSettings = Seq(
  organization := "se.lth.cs",
  version := "2021.0.2",
  scalaVersion := "3.2.2",  
  scalacOptions := Seq("-unchecked", "-deprecation")
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

val IntroprogLibVersion = "1.3.1"
val IntroprogLibName    = "introprog"
lazy val introprog_scalalib = (project in file("introprog")).
  settings(commonSettings: _*).
  settings(
    name := IntroprogLibName,
    version := IntroprogLibVersion,
    scalaVersion := "3.0.2", // stick to latest 3.0 for backward and forward compatibility
      // when sbt 1.7 is released start using scalaOutputVersion
      //     https://scala-lang.org/blog/2022/04/12/scala-3.2.2-released.html
    scalacOptions ++= Seq("-encoding", "UTF-8"),
    Compile/doc/scalacOptions ++= Seq( // TODO why docs here? scala3?
      "-implicits",
      "-groups",
      "-doc-title", IntroprogLibName,
      "-doc-footer", "Dep. of Computer Science, Lund University, Faculty of Engineering LTH",
      "-sourcepath", (ThisBuild/baseDirectory).value.toString,
      "-doc-version", IntroprogLibVersion,
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
).dependsOn(introprog_scalalib) 


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
 ).aggregate(cslib, introprog_scalalib, w03_irritext, w04_blockmole, w06_blockbattle, w07_shuffle, w08_life, w09_words, w10_snake, w11_javatext, w13_bank_proj, w13_img_proj, w13_music_proj, w13_tabular)
