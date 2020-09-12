scalaVersion        := "2.13.3"  // scalaide 4.7.0 is using 2.12.3
fork                := true // https://stackoverflow.com/questions/18676712
connectInput        := true // http://www.scala-sbt.org/1.x/docs/Forking.html
outputStrategy      := Some(StdoutOutput)
scalacOptions       := Seq("-unchecked", "-deprecation")
libraryDependencies += "se.lth.cs" %% "introprog" % "1.1.4"
//libraryDependencies += "jline" % "jline" % "2.14.6"
// https://repo1.maven.org/maven2/jline/jline/2.14.6/jline-2.14.6.jar
