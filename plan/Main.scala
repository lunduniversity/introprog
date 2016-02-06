object Main extends App {

  import StringExtras._

  object weekPlan extends Plan with Table {
    override val heading = 
      Seq("W", "Datum", "Lp V", "Modul", "Förel", "Övn", "Lab")
  }
  
  object modulePlan extends Plan with Table {
    override val heading = Seq("W", "Modul", "Innehåll")
  }
  
  println("\n" + weekPlan.toMarkdown   + "\n")
  println(       modulePlan.toMarkdown + "\n")
  
  weekPlan.  toMarkdown.save("week-plan-generated.md")
  weekPlan.  toHtml    .save("week-plan-generated.html")
  weekPlan.  toLatex   .save("week-plan-generated.tex")
  modulePlan.toMarkdown.save("module-plan-generated.md")
  modulePlan.toHtml    .save("module-plan-generated.html")
  
  val weeks = (0 to 6) ++ (8 to 14)
  for (w <- weeks) {
    def toLatexItem(s: String) = s"\\item ${s.trim}\n"
    val concepts   = modulePlan.column("Innehåll")(w).split(',').toVector
    val latexItems = concepts.map(toLatexItem).mkString.trim 
    val weekName   = modulePlan.column("W")(w).toLowerCase
    val fileName   = s"../compendium/generated/$weekName-concepts-generated.tex"
    latexItems.save(fileName)
    //println("\n" + fileName + "\n" + latexItems)
  }
}
