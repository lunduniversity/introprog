package util

object md {

  def isTable(line: String): Boolean = line.trim.startsWith("|") 

  def isTableHeadSep(line: String): Boolean = 
    line.trim.startsWith("|:") || line.trim.startsWith("|-")  

  def isTableHead(head: String, sep: String, firstRow: String): Boolean = 
    isTable(head) && isTableHeadSep(sep) && isTable(firstRow)

  def splitTableRow(row: String): List[String] = row.trim.drop(1).dropRight(1).split('|').map(_.trim).toList

  def mkHtmlHeadRow(headRow: String): String = 
    splitTableRow(headRow).map(x => """<td align="left">""" + x + "</td>").
      mkString(s"""<tr class="header">\n""", "\n", "</tr>")

  def mkHtmlBodyRow(bodyRow: String): String = 
    splitTableRow(bodyRow).map(x => """<td align="left">""" + x + "</td>").
      mkString(s"""<tr>\n""", "\n", "</tr>")


  def mkHtmlTableWithHead(headRow: String, lines: List[String]): String = {
    val (tableLines, rest) = lines.partition(isTable)
    s"""|<table>
        |<thead>
        |${mkHtmlHeadRow(headRow)}
        |</thead>
        |<tbody>
        |${tableLines.map(mkHtmlBodyRow).mkString("\n")}
        |</tbody>
        |</table>
        |""".stripMargin + toHtml(rest)
  }

  def toHtml(lines: List[String]): String = lines match {
      case Nil => ""
      case headRow :: sep :: firstRow :: xs if isTableHead(headRow, sep, firstRow) => 
        mkHtmlTableWithHead(headRow, firstRow :: xs)
      case x :: xs => x + "\n" + toHtml(xs)
  }   

}
