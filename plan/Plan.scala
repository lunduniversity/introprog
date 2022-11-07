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
      | definiera och anropa funktion, funktionshuvud, funktionskropp, procedur,
      | inbyggda grundtyper, 
      | println, typen Unit, enhetsvärdet (), stränginterpolatorn s,
      | aritmetik, slumptal, logiska uttryck, de Morgans lagar, if, true, false, while, for,
      """.stripTrim),

    Module("Program och kontrollstrukturer",
      id = "programs", exercise = "programs", lab = "", contents = """
      | huvudprogram, program-argument, indata, scala.io.StdIn.readLine,
      | kontrollstruktur,
      | iterera över element i samling, for-uttryck, yield, map, foreach,
      | samling, sekvens, indexering, Array, Vector,
      | intervall, Range,
      | algoritm, implementation, pseudokod,
      | algoritmexempel: SWAP, SUM, MIN-MAX, MIN-INDEX,
      """.stripTrim),

    Module("Funktioner och abstraktion",
      id = "functions", exercise = "functions", lab = "irritext", contents = """
      | abstraktion, funktion,
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

    Module("Objekt och inkapsling",
      id = "objects", exercise = "objects", lab = "blockmole", contents = """
      | modul, singelobjekt, punktnotation, tillstånd, medlem, attribut, metod,
      | paket, filstruktur, jar, dokumentation, JDK,
      | import, selektiv import, namnbyte vid import, export,
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

    Module("Klasser och datamodellering",
      id = "classes", exercise = "classes", lab = "", contents = """
      | applikationsdomän, datamodell, objektorientering, klass, instans, 
      | Any, isInstanceOf, toString,
      | new, null, this,
      | accessregler, private, private[this],
      | klassparameter, primär konstruktor, fabriksmetod, alternativ konstruktor,
      | förändringsbar, oföränderlig,
      | case-klass, kompanjonsobjekt,
      | referenslikhet, innehållslikhet, eq, ==,
      """.stripTrim),

    Module("Mönster och felhantering",
      id = "patterns", exercise = "patterns", lab = "blockbattle", contents = """
      | mönstermatchning, match, Option, throw, try, catch, Try, unapply, sealed,
      | flatten, flatMap, partiella funktioner, collect,
      | wildcard-mönster, variabelbindning i mönster, sekvens-wildcard, bokstavliga mönster,
      | implementera equals, hashcode 
      """.stripTrim), // equals -> sortering???


    Module("Sekvenser och enumerationer",
      id = "sequences", exercise = "sequences", lab = "shuffle", contents = """
      | översikt av Scalas samlingsbibliotek och samlingsmetoder,
      | klasshierarkin i scala.collection, Iterable,
      | Seq, List, ListBuffer, ArrayBuffer, WrappedArray,
      | sekvensalgoritm,  algoritm: SEQ-COPY,
      | in-place vs copy, algoritm: SEQ-REVERSE,
      | registrering, algoritm: SEQ-REGISTER,
      | linjärsökning, algoritm: LINEAR-SEARCH,
      | tidskomplexitet, minneskomplexitet,
      | översikt strängmetoder, StringBuilder,
      | ordning, inbyggda sökmetoder, find, indexOf, indexWhere,
      | inbyggda sorteringsmetoder, sorted, sortWith, sortBy,
      | repeterade parametrar,
      """.stripTrim),

    Module("KONTROLLSKRIVN.", id = "", exercise = "", lab = "", contents = "".stripTrim),

    Module("Nästlade och generiska strukturer",
      id = "matrices", exercise = "matrices", lab = "life", contents = """
      | matris, nästlad samling, nästlad for-sats,
      | typparameter, generisk funktion, generisk klass, fri och bunden typparameter,
      | generiska datastrukturer, generiska samlingar i Scala,
      """.stripTrim),

    Module("Mängder och tabeller",
    id = "setmap", exercise = "lookup", lab = "words", contents = """
    | innehållstest, mängd, Set, mutable.Set,
    | nyckel-värde-tabell, Map, mutable.Map,
    | hash code, java.util.HashMap, java.util.HashSet,
    | persistens, serialisering, textfiler, Source.fromFile, java.nio.file,
    """.stripTrim),

    Module("Arv och komposition", 
      id = "inheritance", exercise = "inheritance", lab = "snake0", contents = """
      | arv, komposition, polymorfism, trait, extends, asInstanceOf, with, inmixning
      | supertyp, subtyp, bastyp, override,
      | Scalas typhierarki, Any, AnyRef, Object, AnyVal, Null, Nothing,
      | topptyp, bottentyp, referenstyper, värdetyper,
      | accessregler vid arv, protected, final,
      | trait, abstrakt klass, 
      """.stripTrim),

    Module("Kontextuella abstraktioner och varians",
      id = "context", exercise = "context", lab = "snake1", contents = """
      | övre- och undre typgräns, varians, kontravarians, kovarians, typjoker, egentyp,
      | givet värde (given), kontextparameter (using), generiska extensionsmetoder, ad hoc polymorfism, kontextgräns, typklass,
      | api, kodläsbarhet, granskningar
      """.stripTrim),

    Module("Valfri fördjupning, Projekt",
        id = "extra", exercise = "extra", lab = "project0", contents = """
        | välj valfritt fördjupningsområde, påbörja projekt,
        """.stripTrim),
        //http://techie-notebook.blogspot.se/2014/07/difference-between-sorted-sortwith-and.html

    Module("Repetition", id = "examprep", exercise = "examprep", lab = "project1", 
      contents = "träna på extentor, redovisa projekt, träna inför muntligt prov"),

    Module("MUNTLIGT PROV",
      id = "munta", exercise = "Munta", lab = "Munta", contents = """
      """.stripTrim),

    Module(name = "VALFRI TENTAMEN", id = "", exercise = "", lab = "", contents = "")
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
    ( "Övn01,Övn02,Övn03,Övn04,Övn05,Övn06,Övn07,--," +
      "Övn08,Övn09,Övn10,Övn11,Övn12,Övn13,Övn14,--,--").split(',').toVector

  lazy val exerciseOfWeek = for (w <- 0 until exerciseNumOfWeek.size) yield {
    if (exerciseNumOfWeek(w).startsWith("Ö") && modules(w).exercise != "")
      modules(w).exercise
    else exerciseNumOfWeek(w)
  }

  lazy val labNumOfWeek =
    "Lab01,--,Lab02,Lab03,--,Lab04,Lab05,--,Lab06,Lab07,Lab08,Lab09,Projekt0,Projekt1,Munta,--,--"
      .split(',').toVector

  lazy val labOfWeek = for (w <- 0 until labNumOfWeek.size) yield {
    if (labNumOfWeek(w).startsWith("L") && modules(w).lab != "")
      modules(w).lab
    else labNumOfWeek(w)
  }

  lazy val startLp1 = Date(2022, 8, 29)

  lazy val startLp2 = Date(2022, 10, 31)

  lazy val ksdatum = Date(2022, 10, 26)

  lazy val tentadatum = Date(2023, 1, 7)

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
