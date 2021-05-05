scalaVersion := "2.13.5"
scalacOptions := Seq("-unchecked", "-deprecation")
javacOptions in (Compile,doc) ++= Seq(
  "-encoding",    "UTF-8", 
  "-charset",     "UTF-8", 
  "-docencoding", "UTF-8"
)

