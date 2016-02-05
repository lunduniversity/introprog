object Main extends App {

  import StringExtras._

  object weekPlan extends Plan with Table {
    lazy val heading = Seq("W", "Datum", "Lp V", "Modul", "Förel", "Resurstid", "Lab")
  }
  
  object lecturePlan extends Plan with Table {
    lazy val heading = Seq("W", "Modul", "Förel", "Innehåll")
  }
  
  object conceptList extends Plan with Table {
    lazy val heading = Seq("Modul", "Innehåll")
    def toItem(s: String) = s"\\item ${s.trim}\n"
    def toLatexItems(module: String) = 
      s"${moduleConceptsOfKey(module).map(toItem).mkString.trim}".stripMargin
  }
 
  println("\n" + weekPlan.toMarkdown    + "\n")
  println(       lecturePlan.toMarkdown + "\n")
  
  weekPlan.   toMarkdown.save("weekplan-generated.md")
  weekPlan.   toHtml    .save("weekplan-generated.html")
  weekPlan.   toLatex   .save("weekplan-generated.tex")
  lecturePlan.toMarkdown.save("lectureplan-generated.md")
  lecturePlan.toHtml    .save("lectureplan-generated.html")
  
  for (w <- 1 to 7) {
    val key = conceptList.moduleKeyOfIndex(w-1) 
    val list = conceptList.toLatexItems(key)
    val fileName = s"../compendium/generated/week0$w-concepts-generated.tex"
    list.save(fileName)
  }
}
