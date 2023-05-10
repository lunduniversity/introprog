ThisBuild/scalaVersion := "3.2.2"
scalacOptions := Seq("-unchecked", "-deprecation")
Compile/doc/javacOptions ++= Seq(
  "-encoding",    "UTF-8", 
  "-charset",     "UTF-8", 
  "-docencoding", "UTF-8"
)

