object Main extends App {

  import StringExtras._
  
  
  // Check which dir we are in and if parent to plan then fix prefix
  lazy val here = ".".toPath.toAbsolutePath.getParent
  lazy val filesHere = here.toFile.list.toVector
  lazy val isPlanParent = filesHere.contains("plan")  
  lazy val currentDir = if (isPlanParent) "plan/" else "" 
  
  lazy val texUtf = "%!TEX encoding = UTF-8 Unicode\n"
  
  println("*** plan generation started in: " + here)
  
  object weekPlan extends Plan with Table {
    override val heading = 
      Seq("W", "Datum", "Lp V", "Modul", "Förel", "Övn", "Lab")
  }
  
  object modulePlan extends Plan with Table {
    override val heading = Seq("W", "Modul", "Innehåll")
  }
  
  object overview extends Plan with Table {
    override val heading = Seq("W", "Modul", "Övn", "Lab")
  }
  
 
  weekPlan.  toMarkdown.save(currentDir + "week-plan-generated.md")
  weekPlan.  toHtml    .save(currentDir + "week-plan-generated.html")
  weekPlan.  toLatex   .prepend(texUtf).save(currentDir + "week-plan-generated.tex")
  modulePlan.toMarkdown.save(currentDir + "module-plan-generated.md")
  modulePlan.toHtml    .save(currentDir + "module-plan-generated.html")
  modulePlan.latexTableBody.save(currentDir + "module-plan-generated.tex")
  overview  .toLatex   .prepend(texUtf).save(currentDir + "overview-generated.tex")
  
  val weeks = (0 to 6) ++ (8 to 14) //exlude exam weeks
  
  // *** Generate chapter heads with topics of each module
  val minConceptsForTwoCol = 28
  def conceptBegin(n: Int) = "Begrepp som ingår i denna veckas studier:\n" +
    (if (n > minConceptsForTwoCol) """\begin{multicols}{2}""" else "") + 
    """\begin{itemize}[noitemsep,label={$\square$},leftmargin=*]""" + "\n"
  def conceptEnd(n: Int)   = """\end{itemize}""" +
    (if (n > minConceptsForTwoCol) """\end{multicols}""" else "") + "\n"
  for (w <- weeks) {
    def toLatexItem(s: String) = s"\\item ${s.trim}\n"
    val label      = "\\label{chapter:" + modulePlan.column("W")(w) + "}"
    val chapter    = "\\chapter{" + modulePlan.column("Modul")(w) + s"}$label\n"
    val concepts   = modulePlan.column("Innehåll")(w).split(',').toVector.filterNot(_.isEmpty)
    val items      = concepts.map(toLatexItem).mkString.trim 
    val result     = chapter + (if (items.size == 0) "" else {
      conceptBegin(concepts.size) + items + conceptEnd(concepts.size)
    })
    val weekName   = modulePlan.column("W")(w).toLowerCase
    val fileName   = s"../compendium/generated/$weekName-chaphead-generated.tex"
    result.latexEscape.prepend(texUtf).save(currentDir+fileName)
  }
  
  // *** Generate table body rows of progress protocoll in compendium prechapters
  def exerciseRow(s: String) = s"""\\ExeRow{$s}""" 
  def labRow(s: String) = s"""\\LabRow{$s}""" 
  def row(col: String) = weeks.map(weekPlan.column(col)(_)).filterNot(_ == "--")
  val labs = row("Lab").map(labRow).mkString("\n")
  labs.prepend(texUtf).save(currentDir+"../compendium/generated/labs-generated.tex")
  val exercises = 
        row("Övn").
        filterNot(Set("Uppsamling","Extenta").
        contains(_)).
        map(exerciseRow).mkString("\n")
  exercises.prepend(texUtf).save(currentDir + "../compendium/generated/exercises-generated.tex")

  // *** Generate latex commands for lab and exercise names
  val weekNumAlpha =  //as latex cannot have numbers in command names AARGH!!
    Vector("ONE","TWO","THREE","FOUR","FIVE","SIX","SEVEN", "",
           "EIGHT","NINE","TEN","ELEVEN","TWELVE","THIRTEEN","FOURTEEN", "")
  def nameDefRow(week: Int, labName: String, exeName: String) =
    s"""\\newcommand{\\ExeWeek${weekNumAlpha(week)}}{$exeName}\n""" +
    s"""\\newcommand{\\LabWeek${weekNumAlpha(week)}}{$labName}\n""" 

  def namesOfWeek(w: Int) = 
    nameDefRow(w, weekPlan.column("Lab")(w), weekPlan.column("Övn")(w))
  val nameDefs = (for (w <- weeks) yield namesOfWeek(w)).mkString("\n")
  nameDefs.prepend(texUtf).save(currentDir + "../compendium/generated/names-generated.tex")
  
  
  //*** Generate overview slides per week ***
  def overviewTemplate(module: String, exe: String, lab: String, body: String, cols: Int = 3) = 
    s"""
Modul \\Emph{$module}: Övn \\Alert{\\texttt{$exe}} $$\\rightarrow$$ Labb \\Alert{\\texttt{$lab}}
\\begin{multicols}{$cols}\\SlideFontTiny
$body     
\\end{multicols}
"""
    for (w <- weeks) {
      val concepts   = modulePlan.column("Innehåll")(w).split(',').toVector.filterNot(_.isEmpty)
      def toLatexItem(s: String) = s"$$\\square$$ ${s.trim} \\\\\n"
      val items = if (w < 14) concepts.map(toLatexItem).mkString.trim else "Repetera begrepp"
      val output = overviewTemplate(
        module = overview.column("Modul")(w),
        exe    = overview.column("Övn")(w),
        lab    = overview.column("Lab")(w),
        body   = items
      )
      val weekName   = modulePlan.column("W")(w).toLowerCase
      output.prepend(texUtf).save(
        currentDir + s"../slides/generated/$weekName-overview-generated.tex")
  }  
 
}




