ThisBuild/scalaVersion := "3.1.0"

scalacOptions := Seq("-unchecked", "-deprecation")

libraryDependencies += "se.lth.cs" %% "introprog" % "1.3.1"

Compile/doc/javacOptions ++= Seq(
  "-encoding",    "UTF-8", 
  "-charset",     "UTF-8", 
  "-docencoding", "UTF-8"
)

