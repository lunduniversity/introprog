scalaVersion := "3.8.3"
Compile/scalaSource := baseDirectory.value / "src"
//unmanagedBase := baseDirectory.value / "../../../../lib/"  //old cslib
libraryDependencies += "se.lth.cs" %% "introprog" % "1.4.0"
