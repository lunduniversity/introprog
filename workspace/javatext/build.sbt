ThisBuild/scalaVersion := "3.8.3"
scalacOptions := Seq("-unchecked", "-deprecation")
Compile/doc/javacOptions ++= Seq(
  "-encoding",    "UTF-8", 
  "-charset",     "UTF-8", 
  "-docencoding", "UTF-8"
)

