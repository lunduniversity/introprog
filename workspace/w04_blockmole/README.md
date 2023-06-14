# Laboration blockmole

Laboration `blockmole` utvecklar du helt från början med en kodeditor och kompilering i terminalen. Du hittar laborationsuppgifterna i kompendiet:
http://cs.lth.se/pgk/kompendium

Om du försöker köra filen `hello-window.scala` med `scala-cli run .` så får du följande felmeddelande:

Det beror på att paketet `introprog` saknas på classpath. Följ instruktionerna nedan för att göra `introprog` tillgänglig, först som en nedladdad jar-fil och sedan som ett externt beroende med automatisk nedladdning.

## Ladda ner jar-fil själv

Använd `hello-window.scala` och prova kompilering tillsammans med en egen-händigt nedladdad jar-fil på classpath med optionen `--jar` enligt nedan:

* Ladda ner introprog-biblioteket härifrån: http://cs.lth.se/pgk/lib och kalla filen för `introprog.jar` du kan klicka på länken ovan, eller om du kör Linux skriva följande i terminalen:

  wget -O introprog.jar http://cs.lth.se/pgk/lib

* Kompilera filen `hello-window.scala` i linuxterminal med detta kommando:

    scala-cli compile . --jar introprog.jar 

Kör i linuxterminal med detta kommando:

    scala-cli run . --jar introprog.jar

Se vidare dokumentationen för `introprog` här: http://fileadmin.cs.lth.se/pgk/api/

## Använda externt beroende med automatisk nedladdning

I stället för att själv ladda ner en jar-fil kan du, om den finns publicerad som öppen källkod på Maven Central, använda `//> using dep` för att lägga till jar-filen som ett externt beroende (eng. dependency, förkortat "dep"). Då kommer scala-cli att automatiskt att första gången ladda ned och lägga till jar-filen på classpath om du kör med `scala-cli run .` 

``` 
//> using dep se.lth.cs::introprog::1.3.1
```

Du kan också, i stället för en magisk kommentar inne i din kodfil, ange beroendet som en option i terminalen: 

    scala-cli run . --dep se.lth.cs::introprog::1.3.1

