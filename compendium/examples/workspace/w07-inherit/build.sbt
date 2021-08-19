scalaVersion := "3.0.1"
Compile/scalaSource := baseDirectory.value / "src"
//unmanagedBase := baseDirectory.value / "../../../../lib/"  //old cslib
libraryDependencies += "se.lth.cs" %% "introprog" % "1.1.5"
