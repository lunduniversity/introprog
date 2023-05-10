scalaVersion := "3.2.2"
Compile/scalaSource := baseDirectory.value / "src"
//unmanagedBase := baseDirectory.value / "../../../../lib/"  //old cslib
libraryDependencies += "se.lth.cs" %% "introprog" % "1.2.0"
