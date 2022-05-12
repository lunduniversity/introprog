# Laboration blockmole

Laboration `blockmole` utvecklar du helt från början med en kodeditor och kompilering i terminalen. Du hittar laborationsuppgifterna i kompendiet:
http://cs.lth.se/pgk/kompendium

Filen `hello-window.scala` kan du använda för prova kompilering med en jar-fil på classpath.
Ladda ner introprog-biblioteket härifrån: http://cs.lth.se/pgk/lib och kalla filen för `introprog.jar`

  wget -O introprog.jar http://cs.lth.se/pgk/lib

Kompilera filen `hello-window.scala` i linuxterminal med detta kommando:

    scalac -cp "introprog.jar:." hello-window.scala

Kör i linuxterminal med detta kommando:

    scala -cp "introprog.jar:." Main

Om du kör i Windows Powershell eller Cmd byt kolon mot semikolon i kommandona ovan.

Du kan också använda `sbt` som beskrivs i Appendix "Byggverktyg" i kompendiet. Lägg då jar-filen i en underkatalog kallad `lib` *eller* låt `sbt` automatiskt ladda ner biblioteket från Sonatype Maven Central genom att använda följande text i en fil `build.sbt`:

    scalaVersion := "3.1.2"
    libraryDependencies += "se.lth.cs" %% "introprog" % "1.3.1"

Se vidare dokumentationen för `introprog` här: http://fileadmin.cs.lth.se/pgk/api/
