# Irritext

I filen `irritext.scala` finns en början till ett lagom irriterande textspel.

Ladda ner `irritext.scala` och kör igång med:

    scala-cli run irritext.scala

Du kan också samkompilera och köra alla .scala-filer i aktuell katalog med kommandot:

    scala-cli run .

Notera punkt efter blanktecken. Punkten representerar aktuell katalog.

Det finns en "magisk kommentar" i `irritext.scala` som börjar med `//>` och ser ut så här:

    //> using scala 3.5

Den anger vilken version av Scala-kompilatorn som ska användas. 
Du kan också ange Scala-version direkt i terminalen så här:

    scala-cli run . --scala 3.5
