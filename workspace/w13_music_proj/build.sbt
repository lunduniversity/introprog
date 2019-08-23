scalaVersion        := "2.12.9"
fork                := true // https://stackoverflow.com/questions/18676712
connectInput        := true // http://www.scala-sbt.org/1.x/docs/Forking.html
outputStrategy      := Some(StdoutOutput)
scalacOptions       := Seq("-unchecked", "-deprecation")
//libraryDependencies += "jline" % "jline" % "2.14.6"
// https://repo1.maven.org/maven2/jline/jline/2.14.6/jline-2.14.6.jar
