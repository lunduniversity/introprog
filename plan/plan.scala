object plan extends App {

  val eda016_2015 = Map(
    "W01" -> "om kursen, grundprinciper, paradigmer, editera, kompilera, exekvera, datorns delar, virtuell maskin, värde, uttryck, variabel, typ, tilldelning, utdata med System.out, indata med Scanner, alternativ, if, else, true, false", 
    "W02" -> "", 
    "W15" -> ""
  )

  trait Plan {
    lazy val body = Seq( 
      Map(
        "W"     -> "01",
        "Datum" -> "31/8-6/9",
        "Lp V"  -> "Lp1V1",
        "Tema"  -> "Introduktion",
        "Förel" -> "F01 F02",
        "Övn"   -> "Ö01 Uttryck, Program",
        "Lab"   -> "Lab01 Textgame"
      ),
      Map(
        "W"     -> "02",
        "Lp V"  -> "Lp1V2",
        "Tema"  -> "Kodstruktur",
        "Förel" -> "F03 ---",
        "Övn"   -> "Ö02 Satser, Objekt",
        "Lab"   -> "--"
      )
    )
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

    def maxSize(head: String) = 
      (head +: bodyMap.map(row => row(head))).map(_.size).max
      
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
