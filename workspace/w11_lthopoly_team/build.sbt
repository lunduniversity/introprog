lazy val root = (project in file(".")).
  settings(
    name := "hello",
    version := "1.0",
    scalaVersion := "2.11.8",
    scalacOptions += "-deprecation",
    scalacOptions += "-unchecked"
  )
