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
    val chapter    = "\\chapter{" + modulePlan.column("Modul")(w) + "}\n"
    val concepts   = modulePlan.column("Innehåll")(w).split(',').toVector
    val items      = concepts.map(toLatexItem).mkString.trim 
    val result     = chapter + "\\begin{itemize}[nosep]\n" + items + "\\end{itemize}"
    val weekName   = modulePlan.column("W")(w).toLowerCase
    val fileName   = s"../compendium/generated/$weekName-chaphead-generated.tex"
    result.save(fileName)
    //println("\n" + fileName + "\n" + latexItems)
  }
}
