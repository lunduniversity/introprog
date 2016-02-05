trait Plan {
  import StringExtras._
  
  lazy val moduleContents = Seq(
    "Introduktion" ->"""
      | om kursen, sekvens, alternativ, repetition, abstraktion, 
      | programmeringsparadigmer, editera-kompilera-exekvera, datorns delar, 
      | virtuell maskin, värde, uttryck, variabel, typ, tilldelning, val, var,  
      | alternativ, if, else, true, false, logik, """.stripTrim,

    "Kodstrukturer" -> """
      | while-sats, for-sats, algoritm: min/max, MIN_VALUE, MAX_VALUE, 
      | paket, import, filstruktur, jar, dokumentation, programlayout, JDK, 
      | konstanter vs föränderlighet, objektorientering, klasser, objekt, 
      | referensvariabler, referenstilldelning, anropa metoder,
      | SimpleWindow, """.stripTrim,

    "Funktioner, Objekt" -> """
      | parameter, returtyp, värdeandrop, namnanrop, namngivna parametrar,
      | aktiveringspost, rekursion, basfall, anropsstacken, objektheapen, 
      | objekt, modul, def, lazy val,  
      | aritmetik, slumptal, """.stripTrim,

    "Datastrukturer" -> """
      | tupler, Array, Map, List, Vector, föränderlighet, iterering""".stripTrim,

    "Vektoralgoritmer"-> """
      | vektoralgoritmer, min/max, strängar, filer, 
      | utdata med System.out, indata med Scanner, """.stripTrim,

    "Klasser, Likhet" -> """ 
      | klasser, klassparameter, primär konstruktor, alternativa konstruktorer,
      | referenslikhet, strukturlikhet, eq vs ==, compareTo, """.stripTrim,

    "Arv, Gränssnitt" -> """
      | klasser, arv, polymorfism, likhet, equals, 
      | accessregler, private, public, protected, private[this],
      | trait, inmixning, 
      | Any, AnyVal, AnyRef, Nothing, """.stripTrim,

    "KONTROLLSKRIVN." -> "", //---------------------

    "Mönster, Undantag" -> """
      | match, Option, null, try, catch, Try, unapply, """.stripTrim,

    "Java, Scala" -> """ 
      | skillnader mellan Scala och Java, 
      | autoboxing i Java, primitiva typer i Java, wrapperklasser i Java, 
      | vektorer i Java, matriser i Java, 
      | for-sats i Java, java for-each i Java, scala.collection.JavaConversions, """.stripTrim,
        
    "Matriser" -> "matriser, nästlade for-satser, designexempel: Tre-i-rad",
    
    "Sökning, Sortering" -> """
      | linjärsökning, binärsökning, 
      | insättningssortering, urvalssortering,
      | sortering till ny vektor, sortering på plats, 
      | algoritmisk komplexitet, """.stripTrim,

    "Trådar, Web, Android" -> "Thread, Future, HTML, Javascript, css, Scala.js, Android",

    "Designexempel"     -> "",

    "Tentaträning"      -> "",

    "TENTAMEN"          -> ""
  ) // END OF moduleContents --------------------------------------------------------
  
  lazy val moduleContentsOfKey: Map[String, String] = moduleContents.toMap
  
  lazy val moduleKeyOfIndex: Vector[String] = moduleContents.map(_._1).toVector
  
  lazy val moduleConceptsOfKey: Map[String, Vector[String]] = 
    moduleContentsOfKey.collect{case (key, s) => (key, s.split(",").toVector)}   
  
  def studyWeek(week: Int) = "W" + (if (week < 9) "0" else "") + (week + 1)
  
  def periodWeek(week: Int) = {
    val (lp, v) = (1 + week / 8, 1 + week % 8)
    if (v < 8) "Lp" + lp + "V" + v else "TP" + lp
  }

  lazy val lecturesOfWeek = 
    Vector("F01 F02","F03 F04","F05 F06","F07 F08","F09 F10", "F11 F12", "F13 F14", "--") ++
    Vector("F15 F16","F17 F18","F19 F20","F21 F22","F23 F24", "F25 F26", "F27 F28", "--")

  lazy val exerciseOfWeek =
    "Ö01,Ö02,Ö03,Ö04,Ö05,Ö06,Ö07,--,Ö08,Ö09,Ö10,Ö11,Ö12,Uppsamling,Extenta,--,--".
      split(',').toVector

  lazy val labOfWeek = 
    "Lab01,--,Lab02,Lab03,Lab04,Lab05,Lab06,--,Lab07,Lab08,Lab09,Lab10,Lab11,Inl.Uppg.,--,--,--".
       split(',').toVector
    
  lazy val startLp1 = Date(2016, 8, 29) // Måndag 2016-Aug-29 
  
  lazy val startLp2 = Date(2016, 10, 31) // Måndag 2016-Okt-31 

  def weeksOf(date: Date, n: Int): Seq[String] = 
    for (week <- 0 until n) yield date.addDays(week*7).workWeek

  lazy val weekDates: Vector[String] = (
    weeksOf(startLp1, 7) ++ Seq("ksdatum") ++ 
    weeksOf(startLp2, 7) ++ Seq("tentadatum")
  ).toVector 
  
  def bodyItem(w: Int): Map[String, String] = Map(
    "W" -> studyWeek(w), 
    "Datum" -> weekDates(w), 
    "Lp V" -> periodWeek(w),
    "Modul" -> moduleKeyOfIndex(w),
    "Förel" -> lecturesOfWeek(w),
    "Resurstid" -> exerciseOfWeek(w), 
    "Lab" -> labOfWeek(w),
    "Innehåll" -> moduleContentsOfKey(moduleKeyOfIndex(w))
  )
    
  lazy val body = (0 until 16).map(bodyItem)
}
