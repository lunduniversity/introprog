trait Plan {
  import StringExtras._

  case class Module(
    name:     String,
    id:       String,
    exercise: String,
    lab:      String,
    contents: String
  )

  lazy val modules: Seq[Module] = Vector(
  /* Ändring 2018->2019: sänk tempot på labbarna i lp1 - en labb mindre
    * flytta labb blockbattle en vecka senare, så att man får två veckor på sig
    * tidigarelägg modul Mönster, Undantag till efter modul Klasser, för att hålla tempot i teorin
    * gör om labb tabular till ett projekt - totalt en labb mindre i kursen
    * senarelägg modul Mängder, tabeller till efter modul Matriser, typparametrar eftersom det är svårt med nästlade strukturer och det är bra med en lätt labb (life är lättare än words?) efter kontrollskrivningen
  */
    Module("Introduktion",
      id = "intro", exercise = "expressions", lab = "kojo", contents = """
      | sekvens, alternativ, repetition, abstraktion, editera, kompilera, exekvera, datorns delar,
      | virtuell maskin,
      | litteral, värde, uttryck, identifierare, variabel, typ, tilldelning, namn, val, var, def,
      | definera och anropa funktion, funktionshuvud, funktionskropp, procedur,
      | inbyggda grundtyper, Int, Long, Short, Double, Float, Byte, Char, String,
      | println, typen Unit, enhetsvärdet (), stränginterpolatorn s,
      | if, else, true, false, MinValue, MaxValue, aritmetik, slumptal, math.random,
      | logiska uttryck, de Morgans lagar, while-sats, for-sats,
      """.stripTrim),

    Module("Program",
      id = "programs", exercise = "programs", lab = "", contents = """
      | kompilerad app, skript, main i Scala, scalac,
      | utdata, println, indata, scala.io.StdIn.readLine,
      | programargument, args i main,
      | main i Java, javac, java.lang.System.out.println,
      | iterera över element i samling, for-uttryck, yield, map, foreach,
      | samling, sekvens, indexering, Array, Vector,
      | intervall, Range,
      | algoritm vs implementation, pseudokod,
      | algoritm: SWAP, algoritm: SUM, algoritm: MIN/MAX, algoritm: MININDEX,
      """.stripTrim),

    Module("Funktioner",
      id = "functions", exercise = "functions", lab = "irritext", contents = """
      | parameter, argument, returtyp, default-argument,
      | namngivna argument, parameterlista, funktionshuvud, funktionskropp,
      | applicera funktion på alla element i en samling,
      | uppdelad parameterlista, skapa egen kontrollstruktur,
      | funktionsvärde, funktionstyp, äkta funktion, stegad funktion, apply,
      | anonyma funktioner, lambda, predikat,
      | aktiveringspost, anropsstacken, objektheapen, stack trace,
      | rekursion,
      | scala.util.Random, slumptalsfrö,
      """.stripTrim),

    Module("Objekt, inkapsling",
      id = "objects", exercise = "objects", lab = "blockmole", contents = """
      | modul, singelobjekt, paket, punktnotation, tillstånd, medlem, attribut, metod,
      | paket, import, filstruktur, jar, dokumentation, programlayout, JDK,
      | import, selektiv import, namnbyte vid import,
      | tupel, multipla returvärden,
      | block, lokal variabel, skuggning,
      | lokal funktion,
      | funktioner är objekt med apply-metod,
      | namnrymd, synlighet, privat medlem, inkapsling,
      | getter och setter, principen om enhetlig access,
      | överlagring av metoder,
      | introprog.PixelWindow,
      | initialisering, lazy val,
      | värdeandrop, namnanrop,
      | typalias,
      """.stripTrim),
//      | enkelt bash-skript för kompilering ???här eller i vecka 2???,
//      | sbt tilde run ???här eller i vecka2???,

    Module("Klasser",
      id = "classes", exercise = "classes", lab = "", contents = """
      | objektorientering, klass, instans, Point, Square, Complex,
      | Any, isInstanceOf, toString,
      | new, null, this,
      | accessregler, private, private[this],
      | klassparameter, primär konstruktor, fabriksmetod, alternativ konstruktor,
      | förändringsbar, oföränderlig,
      | case-klass, kompanjonsobjekt,
      | referenslikhet, innehållslikhet, eq, ==,
      """.stripTrim),

    Module("Mönster, undantag",
      id = "patterns", exercise = "patterns", lab = "blockbattle", contents = """
      | mönstermatchning, match, Option, throw, try, catch, Try, unapply, sealed,
      | flatten, flatMap, partiella funktioner, collect,
      | wildcard-mönster, variabelbindning i mönster, sekvens-wildcard, bokstavliga mönster,
      | implementera equals, hashcode, exempel: equals för klassen Complex, 
      | switch-sats i Java,
      """.stripTrim), // equals -> sortering???


    Module("Sekvenser, enumerationer",
      id = "sequences", exercise = "sequences", lab = "shuffle", contents = """
      | översikt av Scalas samlingsbibliotek och samlingsmetoder,
      | klasshierarkin i scala.collection, Iterable,
      | Seq, List, ListBuffer, ArrayBuffer, WrappedArray,
      | sekvensalgoritm,  algoritm: SEQ-COPY,
      | in-place vs copy, algoritm: SEQ-REVERSE,
      | registrering, algoritm: SEQ-REGISTER,
      | linjärsökning, algoritm: LINEAR-SEARCH,
      | tidskomplexitet, minneskomplexitet,
      | sekvenser i Java vs Scala, for-sats i Java,
      | java.util.Scanner,
      | översikt strängmetoder, StringBuilder,
      | ordning, inbyggda sökmetoder, find, indexOf, indexWhere,
      | inbyggda sorteringsmetoder, sorted, sortWith, sortBy,
      | repeterade parametrar,
      """.stripTrim),

    Module("KONTROLLSKRIVN.", id = "", exercise = "", lab = "", contents = "".stripTrim),

/*    Module("Repetition, trösklar, luckor",
      id = "rebootcamp", exercise = "reboot-init", lab = "reboot-check", contents = "REBOOT CAMP: identifiera dina egna lärandetrösklar och kunskapsluckor, kom-i-kapp med övningar och labbar, repetera, fördjupning för de som är redo, specialträning för behövande"), */

    Module("Matriser, typparametrar",
      id = "matrices", exercise = "matrices", lab = "life", contents = """
      | matris, nästlad samling, nästlad for-sats,
      | typparameter, generisk funktion, generisk klass, fri vs bunden typparameter,
      | generisk samling som typparameter,
      | matriser i Java vs Scala, allokering av nästlade arrayer i Scala och Java,
      """.stripTrim),

    Module("Mängder, tabeller",
    id = "setmap", exercise = "lookup", lab = "words", contents = """
    | innehållstest, mängd, Set, mutable.Set,
    | nyckel-värde-tabell, Map, mutable.Map,
    | hash code, java.util.HashMap, java.util.HashSet,
    | persistens, serialisering, textfiler, Source.fromFile, java.nio.file,
    | repetition inför kontrollskrivning,
    """.stripTrim),


   Module("Arv", 
      id = "inheritance", exercise = "inheritance", lab = "snake", contents = """
      | arv, polymorfism, trait, extends, asInstanceOf, with, inmixning,
      | supertyp, subtyp, bastyp, override,
      | Scalas typhierarki, Any, AnyRef, Object, AnyVal, Null, Nothing,
      | topptyp, bottentyp, referenstyper, värdetyper,
      | Shape som bastyp till Rectangle och Circle,
      | accessregler vid arv, protected, final,
      | case-object, typer med uppräknade värden,
      | trait, abstrakt klass, inmixning,
      | gränssnitt, interface i Java, programmeringsgränssnitt (api),
      """.stripTrim),

    Module("TODO: Flytta Java till appendix",
      id = "java", exercise = "java", lab = "javatext", contents = """
      | syntaxskillnader mellan Scala och Java,
      | klasser i Scala och Java,
      | referensvariabler i Java, enkla värden i Java, primitiva typer i Java,
      | referenstilldelning och värdetilldelning i Java,
      | alternativ konstruktor i Scala och Java,
      | for-sats i Java, for-each-sats i Java,
      | java.util.ArrayList,
      | autoboxing i Java, wrapperklasser i Java,
      | samlingar i Java, scala.jdk.CollectionConverters,
      | namnkonventioner för konstanter i Scala och Java,
      | kodläsbarhet, idiom, kodningsstandard,
      """.stripTrim),

    Module("TODO: Ändra från Sortering till annat",
        id = "sort", exercise = "sort", lab = "", contents = """
        | strängjämförelse, compareTo, implicit ordning,
        | binärsökning, algoritm: BINARY-SEARCH,
        | sortering till ny vektor, sortering på plats,
        | insättningssortering, urvalssortering,
        | algoritm: INSERTION-SORT, algoritm: SELECTION-SORT,
        | Ordering[T], Ordered[T], Comparator[T], Comparable[T],
        | riktlinjer för projektredovisning,
        """.stripTrim),
        //http://techie-notebook.blogspot.se/2014/07/difference-between-sorted-sortwith-and.html

    Module("Repetition, tentaträning, projekt", id = "examprep", exercise = "examprep", lab = "", contents = "göra extenta, förbereda projektredovisning, skapa dokumentation med scaladoc och javadoc"),

    Module("Extra",
      id = "extra", exercise = "extra", lab = "", contents = """
      | tråd, jämlöpande exekvering,
      | icke-blockerande anrop, callback,
      | java.lang.Thread,
      | java.util.concurrent.atomic.AtomicInteger,
      | scala.concurrent.Future,
      | kort om html+css+javascript+scala.js och webbprogrammering,
      """.stripTrim),

    Module(name = "TENTAMEN", id = "", exercise = "", lab = "", contents = "")
  )

  lazy val contentsOfModule: Map[String, String] =
    modules.map(m => (m.name, m.contents)).toMap

  lazy val moduleOfWeek: Vector[String] = modules.map(_.name).toVector

  lazy val nameOfWeek: Vector[String] =
    "W01 W02 W03 W04 W05 W06 W07 KS W08 W09 W10 W11 W12 W13 W14 T".split(' ').toVector

  def studyPeriodOfWeek(week: Int) = {
    val (lp, v) = (1 + week / 8, 1 + week % 8)
    if (v < 8) "Lp" + lp + "V" + v else "TP" + lp
  }

  lazy val lecturesOfWeek =
    Vector("F01 F02","F03 F04","F05 F06","F07 F08","F09 F10", "F11 F12", "F13 F14", "--") ++
    Vector("F15 F16","F17 F18","F19 F20","F21 F22","F23 F24", "F25 F26", "F27 F28", "--")

  lazy val exerciseNumOfWeek =
    ("Övn01,Övn02,Övn03,Övn04,Övn05,Övn06,Övn07,--," +
     "Övn08,Övn09,Övn10,Övn11,Övn12,Övn13,Övn14,--,--").split(',').toVector

  lazy val exerciseOfWeek = for (w <- 0 until exerciseNumOfWeek.size) yield {
    if (exerciseNumOfWeek(w).startsWith("Ö") && modules(w).exercise != "")
      modules(w).exercise
    else exerciseNumOfWeek(w)
  }

  lazy val labNumOfWeek =
    "Lab01,--,Lab02,Lab03,--,Lab04,Lab05,--,Lab06,Lab07,Lab08,Lab09,--,Projekt,--,--,--".
       split(',').toVector

  lazy val labOfWeek = for (w <- 0 until labNumOfWeek.size) yield {
    if (labNumOfWeek(w).startsWith("L") && modules(w).lab != "")
      modules(w).lab
    else labNumOfWeek(w)
  }

  lazy val startLp1 = Date(2020, 8, 31)

  lazy val startLp2 = Date(2020, 11, 2)

  lazy val ksdatum = Date(2020, 10, 28)

  lazy val tentadatum = Date(2020, 1, 11)

  def weeksOf(date: Date, n: Int): Seq[String] =
    for (week <- 0 until n) yield date.addDays(week*7).workWeek

  lazy val weekDates: Vector[String] = (
    weeksOf(startLp1, 7) ++ Seq(ksdatum.shortDate) ++
    weeksOf(startLp2, 7) ++ Seq(tentadatum.shortDate)
  ).toVector

  def bodyItem(w: Int): Map[String, String] = Map(
    "W"        -> nameOfWeek(w),
    "Datum"    -> weekDates(w),
    "Lp V"     -> studyPeriodOfWeek(w),
    "Modul"    -> moduleOfWeek(w),
    "Förel"    -> lecturesOfWeek(w),
    "Övn"      -> exerciseOfWeek(w),
    "Lab"      -> labOfWeek(w),
    "Innehåll" -> contentsOfModule(moduleOfWeek(w))
  )

  lazy val body = (0 until 16).map(bodyItem)
}
