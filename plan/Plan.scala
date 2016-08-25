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
    
    Module("Introduktion", 
      id = "intro", exercise = "expressions", lab = "kojo", contents = """
      | sekvens, alternativ, repetition, abstraktion, programmeringsspråk,
      | programmeringsparadigmer, editera-kompilera-exekvera, datorns delar, 
      | virtuell maskin, REPL,  
      | literal, värde, uttryck, identifierare, variabel, typ, tilldelning, namn, val, var, def,  
      | inbyggda typer, Int, Long, Short, Double, Float, Byte, Char, String,
      | println, typen Unit, enhetsvärdet (), stränginterpolatorn s,
      | if, else, true, false, MinValue, MaxValue, aritmetik, slumptal, math.random,
      | logiska uttryck, de Morgans lagar, while-sats, for-sats, 
      """.stripTrim),
      
    Module("Kodstrukturer", 
      id = "codestruct", exercise = "programs", lab = "", contents = """
      | iterering, for-uttryck, map, foreach, Range, Array, Vector, 
      | algoritm vs implementation, pseudokod, 
      | algoritm: SWAP, algoritm: SUM, algoritm: MIN/MAX, algoritm: MININDEX,
      | block, namnsynlighet, namnöverskuggning, lokala variabler,
      | paket, import, filstruktur, jar, dokumentation, programlayout, JDK, 
      | main i Java vs Scala, java.lang.System.out.println,
      """.stripTrim),
      
    Module("Funktioner, objekt", 
      id = "funobj", exercise = "functions", lab = "blockmole", contents = """
      | definera funktion, anropa funktion,
      | parameter, returtyp, värdeandrop, namnanrop, default-argument, namngivna argument,
      | applicera funktion på alla element i en samling, procedur, 
      | värdeanrop vs namnanrop, uppdelad parameterlista, skapa egen kontrollstruktur, 
      | objekt, modul, punktnotation, tillstånd, metod, medlem, 
      | funktionsvärde, funktionstyp, äkta funktion, stegad funktion, apply, lazy val, 
      | lokala funktioner,    
      | anonyma funktioner, lambda,
      | aktiveringspost, anropsstacken, objektheapen, rekursion 
      | cslib.window.SimpleWindow, 
      """.stripTrim),

    Module("Datastrukturer", 
      id = "datastruct", exercise = "data", lab = "pirates", contents = """
      | attribut (fält), medlem, metod, 
      | tupel, klass, Any, isInstanceOf, toString, 
      | case-klass, 
      | samling, scala.collection, 
      | föränderlighet vs oföränderlighet, 
      | List, Vector, Set, Map, 
      | typparameter, generisk samling som parameter,
      | översikt samlingsmetoder, 
      | översikt strängmetoder, läsa/skriva textfiler, Source.fromFile, java.nio.file,
      """.stripTrim),

    Module("Sekvensalgoritmer", 
      id = "seqalg", exercise = "sequences", lab = "shuffle", contents = """
      | sekvensalgoritm,  algoritm: SEQ-COPY,   
      | in-place vs copy, algoritm: SEQ-REVERSE, algoritm: SEQ-REGISTER,
      | sekvenser i Java vs Scala, for-sats i Java,
      | java.util.Scanner, scala.collection.mutable.ArrayBuffer,
      | StringBuilder,
      | java.util.Random, slumptalsfrö, 
      """.stripTrim),

    Module("Klasser", 
      id = "class", exercise = "classes", lab = "turtlegraphics", contents = """
      | objektorientering, klass, Point, Square, Complex,
      | new, null, this, 
      | inkapsling, accessregler, private, private[this], kompanjonsobjekt,
      | getters och setters, 
      | klassparameter, primär konstruktor, objektfabriksmetod, 
      | överlagring av metoder,
      | referenslikhet vs strukturlikhet, eq vs ==, 
      """.stripTrim),

    Module("Arv", 
      id = "polymorf", exercise = "traits", lab = "turtlerace-team", contents = """
      | arv, polymorfism, trait, extends, asInstanceOf, with, inmixning,
      | supertyp, subtyp, bastyp, override, 
      | klasshierarkin i Scala: Any AnyRef Object AnyVal Null Nothing,
      | referenstyper vs värdetyper, 
      | klasshierarkin i scala.collection,
      | Shape som bastyp till Point och Rectangle, 
      | accessregler vid arv, protected, final,
      | klass vs trait, abstract class,
      | case-object, typer med uppräknade värden,  
      """.stripTrim),

    Module("KONTROLLSKRIVN.", id = "", exercise = "", lab = "", contents = "".stripTrim),

    Module("Mönster, undantag", 
      id = "matchpat", exercise = "matching", lab = "chords-team", contents = """
      | mönstermatchning, match, Option, throw, try, catch, Try, unapply, sealed, 
      | flatten, flatMap, partiella funktioner, collect, 
      | speciella matchningar: wildcard pattern; variable binding; sequence wildcard; back-ticks, 
      | equals, hashcode, exempel: equals för klassen Complex, 
      | switch-sats i Java,
      """.stripTrim),

    Module("Matriser, typparametrar", 
      id = "matrix", exercise = "matrices", lab = "maze", contents = """
      | matris, nästlad samling, nästlad for-sats,  
      | typparameter, generisk funktion, generisk klass, fri vs bunden typparameter, 
      | matriser i Java vs Scala, allokering av nästlade arrayer i Scala och Java,
      """.stripTrim),

    Module("Sökning, sortering", 
      id = "searchsort", exercise = "sorting", lab = "survey", contents = """
      | strängjämförelse, compareTo, imlicit ordning, 
      | linjärsökning, binärsökning, 
      | algoritm: LINEAR-SEARCH, algortim: BINARY-SEARCH, 
      | algoritmisk komplexitet, 
      | sortering till ny vektor, sortering på plats, 
      | insättningssortering, urvalssortering, 
      | algoritm: INSERTION-SORT, algoritm: SELECTION-SORT,
      | Ordering[T], Ordered[T], Comparator[T], Comparable[T],
      """.stripTrim),  
      //http://techie-notebook.blogspot.se/2014/07/difference-between-sorted-sortwith-and.html

    Module("Scala och Java", 
      id = "scalajava", exercise = "scalajava", lab = "lthopoly-team", contents = """
      | syntaxskillnader mellan Scala och Java, 
      | klasser i Scala vs Java, 
      | referensvariabler vs enkla värden i Java,
      | referenstilldelning vs värdetilldelning i Java,
      | alternativ konstruktor i Scala och Java, 
      | for-sats i Java, java for-each i Java, 
      | java.util.ArrayList, 
      | autoboxing i Java, primitiva typer i Java, wrapperklasser i Java, 
      | samlingar i Java vs Scala, scala.collection.JavaConverters,
      | namnkonventioner för konstanter,
      """.stripTrim),

    Module("Trådar, webb",  
      id = "threads", exercise = "threads", lab = "life", contents = """
      | tråd, jämlöpande exekvering,  
      | icke-blockerande anrop, callback,
      | java.lang.Thread, 
      | java.util.concurrent.atomic.AtomicInteger,
      | scala.concurrent.Future, 
      | kort om html+css+javascript+scala.js och webbprogrammering,
      """.stripTrim),

    Module("Design, api", 
      id = "design", exercise = "design", lab = "", contents = """
      | utvecklingsprocessen, krav-design-implementation-test,
      | gränssnitt, trait vs interface, programmeringsgränssnitt (api),
      | designexempel,
      """.stripTrim),

    Module("Tentaträning", id = "exam", exercise = "", lab = "", contents = ""),

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
     "Övn08,Övn09,Övn10,Övn11,Övn12,Uppsamling,Extenta,--,--").split(',').toVector
          
  lazy val exerciseOfWeek = for (w <- 0 until exerciseNumOfWeek.size) yield {
    if (exerciseNumOfWeek(w).startsWith("Ö") && modules(w).exercise != "")  
      modules(w).exercise
    else exerciseNumOfWeek(w)
  }
  
  lazy val labNumOfWeek = 
    "Lab01,--,Lab02,Lab03,Lab04,Lab05,Lab06,--,Lab07,Lab08,Lab09,Lab10,Lab11,Projekt,--,--,--".
       split(',').toVector

  lazy val labOfWeek = for (w <- 0 until labNumOfWeek.size) yield {
    if (labNumOfWeek(w).startsWith("L") && modules(w).lab != "")  
      modules(w).lab
    else labNumOfWeek(w)
  }

    
  lazy val startLp1 = Date(2016, 8, 29) // Måndag 2016-Aug-29 
  
  lazy val startLp2 = Date(2016, 10, 31) // Måndag 2016-Okt-31 
  
  lazy val ksdatum = Date(2016, 10, 25)  // Tisdag 2016-Okt-25
  
  lazy val tentadatum = Date(2017, 1, 9)  // Måndag 2017-Jan-9

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
