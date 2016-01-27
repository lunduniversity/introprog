object plan extends App {

  trait Plan {
    lazy val themeContents = Seq(
      "Introduktion"      -> 
          "om kursen, sekvens, alternativ, repetition, abstraktion, programmeringsparadigmer, editera-kompilera-exekvera, datorns delar, virtuell maskin, värde, uttryck, variabel, typ, tilldelning, alternativ, if, else, true, false",
      "Kodstrukturer"       -> 
          "loop-strukturer: while-sats, for-sats, algoritm: min/max, Integer.MIN_VALUE, Integer.MAX_VALUE, Paket, import, filstruktur, jar, dokumentation, programlayout, JDK, konstanter vs föränderlighet, objektorientering, klasser, objekt, referensvariabler, referenstilldelning, anropa metoder, SimpleWindow",
      "Funktioner"        -> "parameter, returtyp, värdeandrop, namnanrop, aritmetik, logik, slumptal",
      "Typer"             -> "klasser, arv, typhärledning, polymorfism, likhet, equals",
      "Datastrukturer"    -> "tupler, Array, Map, List, Vector, föränderlighet, iterering",
      "Sekvenser"         -> "vektoralgoritmer, min/max, strängar, filer, Scanner,utdata med System.out, indata med Scanner, matriser",
      "Mönster"           -> "match, Option, try, catch, Try, unapply, rekursion",
      "KONTROLLSKRIVNING" -> "",
      "Matriser"          -> "matriser, nästlade for-satser, designexempel: Tre-i-rad",
      "Java"              -> 
          "skillnader mellan Scala och Java, autoboxing i Java, primitiva typer i Java, wrapperklasser i Java, vektorer i Java,  for-sats i Java, java for-each i Java",
      "Sortering"         -> "compareTo, binärsökning, insättningssortering, urvalssortering, komplexitet, sortering på plats",
      "Trådar"            -> "Thread, Future, synchronized",
      "Plattformar"       -> "HTML, Javascript, css, Scala.js, Android",
      "Designexempel"     -> "",
      "Tentaträning"      -> "",
      "TENTAMEN"          -> ""
    )
    
    lazy val themeContentsOf = themeContents.toMap
    
    lazy val theme = themeContents.map(_._1).toVector
    def studyWeek(week: Int) = "W" + (if (week < 9) "0" else "") + (week + 1)
    def periodWeek(week: Int) = {
      val (lp, v) = (1 + week / 8, 1 + week % 8)
      if (v < 8) "Lp" + lp + "V" + v else "TP" + lp
    }
    lazy val lecturesOfWeek = 
      Vector("F01 F02","F03 F04","F05 F06","F07 F08","F09 F10", "F11 F12", "F13 F14", "--") ++
      Vector("F15 F16","F17 F18","F19 F20","F21 F22","F23 F24", "F25 F26", "F27 F28", "--")
    lazy val exerciseOfWeek = "Ö01,Ö02,Ö03,Ö04,Ö05,Ö06,Ö07,--,Ö08,Ö09,Ö10,Ö11,Ö12,Ö13,Extenta,--,--".split(',').toVector
    lazy val labOfWeek = "Lab01,--,Lab02,Lab03,Lab04,Lab05,Lab06,--,Lab07,Lab08,Lab09,Lab10,Lab11,Inl.Uppg.,--,--,--".split(',').toVector
      
    case class Date(year: Int, month: Int, dayOfMonth: Int) { 
      import java.util.{GregorianCalendar, Calendar}, Calendar._
      private val calendar = new GregorianCalendar(year, month - 1, dayOfMonth) 
      lazy val shortDate = "" + calendar.get(DAY_OF_MONTH) + "/" + (calendar.get(MONTH) + 1)
      lazy val calendarTime = calendar.getTime 
      def addDays(days: Int): Date = {
        val cal =  new GregorianCalendar(year, month - 1, dayOfMonth) 
        cal.add(DAY_OF_YEAR, days)
        Date(cal.get(YEAR), cal.get(MONTH) + 1, cal.get(DAY_OF_MONTH))
      }
      lazy val weekPeriod = shortDate + "-" + addDays(4).shortDate
    }
    lazy val startLp1 = Date(2016, 8, 29) // Måndag 2016-Aug-29 
    lazy val startLp2 = Date(2016, 10, 31) // Måndag 2016-Okt-31 
    def weeksOf(date: Date, n: Int): Seq[String] = for (week <- 0 until n) yield date.addDays(week*7).weekPeriod
    lazy val weekDates: Seq[String] = (weeksOf(startLp1, 7) ++ Seq("ksdatum") ++ weeksOf(startLp2, 7) ++ Seq("tentadatum")).toVector 
    
    def bodyItem(w: Int): Map[String, String] = Map(
      "W" -> studyWeek(w), 
      "Datum" -> weekDates(w), 
      "Lp V" -> periodWeek(w),
      "Tema" -> theme(w),
      "Förel" -> lecturesOfWeek(w),
      "Övn" -> exerciseOfWeek(w), 
      "Lab" -> labOfWeek(w),
      "Innehåll" -> themeContentsOf(theme(w))
    )
      
    lazy val body = (0 until 16).map(bodyItem)
  }

  object weekPlan extends Plan with Table {
    lazy val heading = Seq("W", "Datum", "Lp V", "Tema", "Förel", "Övn", "Lab")
  }
  
  object lecturePlan extends Plan with Table {
    lazy val heading = Seq("W", "Tema", "Förel", "Innehåll")
  }

  
  trait Table {
    type Row    = Seq[String]
    type RowMap = Map[String, String]
    type Grid   = Seq[RowMap]
     
    def heading: Row 
    def body: Grid 
    
    lazy val bodyMap: Seq[RowMap] = body.map(_.withDefaultValue(" "))

    def maxSize(head: String) = {
      val m = (head +: bodyMap.map(row => row(head))).map(_.size).max
      if (m > 40) 0 else m
    }
    def markdownBodyRow(row: RowMap) = 
      heading.map(h => row(h).padTo(maxSize(h), ' ') ).mkString("| "," | "," |")  

    def markdownHeadingSeparatorRow = 
      heading.map(maxSize).map(s => "|:-".padTo(s + 3, '-')).mkString("","","|\n")
    
    lazy val toMarkdown: String = 
      heading.map(h => h.padTo(maxSize(h), ' ')).mkString("| "," | "," |\n") + 
        markdownHeadingSeparatorRow + 
        bodyMap.map(markdownBodyRow).mkString("\n")
    
    lazy val htmlHeadings = 
      heading.map(head => """<td align="left">""" + head + "</td>").
        mkString(s"""<tr class="header">\n""", "\n", "</tr>")
    
    def htmlBodyRow(row: RowMap) = 
      heading.map(h => row(h)).map(x => """<td align="left">""" + x + "</td>").
      mkString(s"""<tr>\n""", "\n", "</tr>")

    
    def toHtml: String =  
      s"""|<table>
          |<thead>
          |${htmlHeadings}
          |</thead>
          |<tbody>
          |${bodyMap.map(htmlBodyRow).mkString("\n")}
          |</tbody>
          |</table>
          |""".stripMargin
          
    lazy val latexHeadings = 
      heading.map(h => s"\\textit{$h}").mkString(""," & "," \\\\ \\hline \\hline")
    
    def latexBodyRow(row: RowMap) = 
      heading.map(h => row(h).padTo(maxSize(h), ' ') ).mkString(""," & "," \\\\")
          
    def toLatex: String = 
      s"""|\\begin{tabular}${Seq.fill(heading.size)("l").mkString("{","|","}")}
          |${latexHeadings}
          |${bodyMap.map(latexBodyRow).mkString("\n")}
          |\\end{tabular}
          |""".stripMargin
            }

  implicit class StringSaver(contents: String) {
    import java.nio.file.{Paths, Files}
    import java.nio.charset.StandardCharsets.UTF_8
    def save(fileName: String) = {
      println("Saving file: " + Paths.get(fileName))
      Files.write(Paths.get(fileName), contents.getBytes(UTF_8))
    }
  }
  
  println("\n" + weekPlan.toMarkdown + "\n")
  println(lecturePlan.toMarkdown + "\n")
  
  weekPlan.   toMarkdown.save("weekplan-generated.md")
  weekPlan.   toHtml    .save("weekplan-generated.html")
  weekPlan.   toLatex   .save("weekplan-generated.tex")
  lecturePlan.toMarkdown.save("lectureplan-generated.md")
  lecturePlan.toHtml    .save("lectureplan-generated.html")

}
