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
      heading.map(maxSize).map(s => "|:--".padTo(s + 3, '-')).mkString("","","|\n")
    
    lazy val toMarkdown: String = 
      heading.map(h => h.padTo(maxSize(h), ' ')).mkString("| "," | "," |\n") + 
        markdownHeadingSeparatorRow + 
        bodyMap.map(markdownBodyRow).mkString("\n")
    
    lazy val htmlHeadings = 
      heading.map(head => """<th align="left">""" + head + "</th>").
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
