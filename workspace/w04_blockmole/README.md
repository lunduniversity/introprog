# Laboration blockmole

Laboration `blockmole` utvecklar du helt från början med en kodeditor och kompilering i terminalen. Du hittar laborationsuppgifterna i kompendiet:
https://fileadmin.cs.lth.se/pgk/compendium.pdf

Om du försöker köra filen `hello-window.scala` med `scala-cli run .` så får du följande felmeddelande:
```
Compiling project (Scala 3.5.0, JVM)
[error] ./hello-window.scala:4:8
[error] Not found: introprog
[error] import introprog.PixelWindow
[error]        ^^^^^^^^^
[error] ./hello-window.scala:11:3
[error] Not found: introprog
[error]   introprog.examples.TestPixelWindow.main(Array())
[error]   ^^^^^^^^^
Error compiling project (Scala 3.5.0, JVM)
Compilation failed
```

Det beror på att paketet `introprog` saknas på classpath. Följ instruktionerna nedan för att göra `introprog` tillgänglig, först som en nedladdad jar-fil och sedan som ett externt beroende med automatisk nedladdning.

## Ladda ner jar-fil själv

Använd `hello-window.scala` och prova kompilering tillsammans med en egen-händigt nedladdad jar-fil på classpath med optionen `--jar` enligt nedan:

* Ladda ner introprog-biblioteket härifrån: https://fileadmin.cs.lth.se/pgk/introprog_3-1.4.0.jar och kalla filen för `introprog.jar` du kan klicka på länken ovan, eller om du kör Linux skriva följande i terminalen:

  wget -O introprog.jar https://fileadmin.cs.lth.se/pgk/introprog_3-1.4.0.jar

* Kompilera filen `hello-window.scala` med detta kommando:

    scala-cli compile . --jar introprog.jar 

Kör med detta kommando:

    scala-cli run . --jar introprog.jar

Se dokumentationen för `introprog` här: http://fileadmin.cs.lth.se/pgk/api/

## Använda externt beroende med automatisk nedladdning

I stället för att själv ladda ner en jar-fil kan du, om den finns publicerad som öppen källkod på Maven Central, använda `//> using dep` för att lägga till jar-filen som ett externt beroende (eng. dependency, förkortat "dep") enligt nedan - notera dubbla kolon på två ställen. 

``` 
//> using dep se.lth.cs::introprog::1.4.0
```

Då kommer scala-cli att automatiskt att (om det inte redan är gjort) ladda ned och sparar undan jar-filen på ett speciellt ställe i din hemkatalog. När du kompilerar och kör så lägger scala-cli automatiskt till jar-filen på classpath.

    scala-cli run . 


Du kan också, i stället för en magisk kommentar inne i din kodfil, ange beroendet som en option direkt i terminalen: 

    scala-cli run . --dep se.lth.cs::introprog::1.4.0

## Ange optioner till Scala-compilatorn

I filen `hello-window-scala` finns även denna magiska kommentar:

    //> using option -unchecked -deprecation -Wunused:all -Wvalue-discard

Med hjälp av `//> using option` kan du ge optioner till Scala-kompilatorn. Här är några användbara optioner:

* `-unchecked` Extra varningar för flera fall av osäker kod. 
* `-deprecation` Förklaring vid användning av utgående funktioner.
* `-Wunused:all` Varning om deklarationer ej används. 
* `-Wvalue-discard` Varning vid förlorat värde.
* `-Ysafe-init` Varna vid användning av ännu ej initialiserade variabler. 