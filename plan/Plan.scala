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
      id = "intro", exercise = "expressions", lab = "textgame", contents = """
      | om kursen, sekvens, alternativ, repetition, abstraktion, 
      | programmeringsparadigmer, editera-kompilera-exekvera, datorns delar, 
      | virtuell maskin, värde, uttryck, variabel, typ, tilldelning, val, var,  
      | alternativ, if, else, true, false, logik, MinValue, MaxValue, aritmetik
      """.stripTrim),
      
    Module("Kodstrukturer", 
      id = "codestruct", exercise = "statements", lab = "", contents = """
      | while-sats, for-sats, algoritm: min/max, , 
      | paket, import, filstruktur, jar, dokumentation, programlayout, JDK, 
      | konstanter vs föränderlighet, objektorientering, klasser, objekt, 
      | referensvariabler, referenstilldelning, anropa metoder, block, namnsynlighet
      | SimpleWindow, 
      """.stripTrim),
      
    Module("Funktioner, Objekt", 
      id = "funobj", exercise = "functions", lab = "turtledraw", contents = """
      | parameter, returtyp, värdeandrop, namnanrop, namngivna parametrar,
      | aktiveringspost, rekursion, basfall, anropsstacken, objektheapen, 
      | objekt, modul, def, lazy val,  
      | aritmetik, slumptal, 
      """.stripTrim),

    Module("Datastrukturer", 
      id = "data", exercise = "data", lab = "files", contents = """
      | tupler, case-klasser, case-object i Scala vs enum i java
      | Array, Map, List, Vector, föränderlighet, iterering,
      | vektorer i Java vs Scala, Complex, Rational
      | filer, Source.fromFile, java.nio.file
      """.stripTrim),

    Module("Vektoralgoritmer", 
      id = "vect", exercise = "vectors", lab = "cardgame", contents = """
      | vektoralgoritmer, min/max, strängar, filer, 
      | java System.out.println, Scanner, 
      """.stripTrim),

    Module("Klasser, Likhet", 
      id = "classes", exercise = "classes", lab = "shapes", contents = """
      | klasser, klassparameter, primär konstruktor, alternativa konstruktorer,
      | referenslikhet, strukturlikhet, eq vs ==, compareTo, Shape, Point, Rectangle
      """.stripTrim),

    Module("Arv, Gränssnitt", 
      id = "polymorf", exercise = "traits", lab = "turtlerace-T", contents = """
      | klasser, arv, polymorfism, likhet, equals, 
      | accessregler, private, public, protected, private[this],
      | trait, inmixning, 
      | Any, AnyVal, AnyRef, Nothing, 
      """.stripTrim),

    Module("KONTROLLSKRIVN.", id = "", exercise = "", lab = "", contents = "".stripTrim),

    Module("Mönster, Undantag", 
      id = "pattern", exercise = "matching", lab = "mandelbrot", contents = """
      | match, Option, null, try, catch, Try, unapply, 
      """.stripTrim),

    Module("Matriser", 
      id = "matrix", exercise = "matrices", lab = "maze", contents = """
      | matriser, nästlade for-satser, designexempel: Tre-i-rad, 
      | matriser i Java vs Scala, 
      """.stripTrim),

    Module("Sökning, Sortering", 
      id = "sort", exercise = "sorting", lab = "bank", contents = """
      | linjärsökning, binärsökning, 
      | insättningssortering, urvalssortering,
      | sortering till ny vektor, sortering på plats, 
      | algoritmisk komplexitet, 
      """.stripTrim),

    Module("Scala och Java", 
      id = "scalajava", exercise = "scalajava", lab = "scalajava-T", contents = """
      | skillnader mellan Scala och Java, 
      | for-sats i Java, java for-each i Java, 
      | ArrayList<Integer>, scala.collection.JavaConversions, 
      | autoboxing i Java, primitiva typer i Java, wrapperklasser i Java, 
      """.stripTrim),

    Module("Trådar, Web, Android", 
      id = "thread", exercise = "threads", lab = "life", contents = """
      | Thread, Future, HTML, Javascript, css, Scala.js, Android,
      """.stripTrim),

    Module("Design", 
      id = "design", exercise = "design", lab = "", contents = """
      """.stripTrim),

    Module("Tentaträning", 
      id = "exam", exercise = "", lab = "", contents = """
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
    "Ö01,Ö02,Ö03,Ö04,Ö05,Ö06,Ö07,--,Ö08,Ö09,Ö10,Ö11,Ö12,Uppsamling,Extenta,--,--".
      split(',').toVector
      
  lazy val exerciseOfWeek = for (w <- 0 until exerciseNumOfWeek.size) yield {
    if (exerciseNumOfWeek(w).startsWith("Ö") && modules(w).exercise != "")  
      modules(w).exercise
    else exerciseNumOfWeek(w)
  }
  
  lazy val labNumOfWeek = 
    "Lab01,--,Lab02,Lab03,Lab04,Lab05,Lab06,--,Lab07,Lab08,Lab09,Lab10,Lab11,Inl.Uppg.,--,--,--".
       split(',').toVector

  lazy val labOfWeek = for (w <- 0 until labNumOfWeek.size) yield {
    if (labNumOfWeek(w).startsWith("L") && modules(w).lab != "")  
      modules(w).lab
    else labNumOfWeek(w)
  }

    
  lazy val startLp1 = Date(2016, 8, 29) // Måndag 2016-Aug-29 
  
  lazy val startLp2 = Date(2016, 10, 31) // Måndag 2016-Okt-31 

  def weeksOf(date: Date, n: Int): Seq[String] = 
    for (week <- 0 until n) yield date.addDays(week*7).workWeek

  lazy val weekDates: Vector[String] = (
    weeksOf(startLp1, 7) ++ Seq("ksdatum") ++ 
    weeksOf(startLp2, 7) ++ Seq("tentadatum")
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
