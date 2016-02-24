object Main extends App {

  import StringExtras._

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
  
  //println("\n" + weekPlan.toMarkdown   + "\n")
  //println(       modulePlan.toMarkdown + "\n")
  
  weekPlan.  toMarkdown.save("week-plan-generated.md")
  weekPlan.  toHtml    .save("week-plan-generated.html")
  weekPlan.  toLatex   .save("week-plan-generated.tex")
  modulePlan.toMarkdown.save("module-plan-generated.md")
  modulePlan.toHtml    .save("module-plan-generated.html")
  overview  .toLatex   .save("overview-generated.tex")
  
  val weeks = (0 to 6) ++ (8 to 14) //exlude exam weeks
  
  // *** Generate chapter heads with topics of each module
  for (w <- weeks) {
    def toLatexItem(s: String) = s"\\item ${s.trim}\n"
    val chapter    = "\\chapter{" + modulePlan.column("Modul")(w) + "}\n"
    val concepts   = modulePlan.column("Innehåll")(w).split(',').toVector
    val items      = concepts.map(toLatexItem).mkString.trim 
    val result     = chapter + "\\begin{itemize}[nosep]\n" + items + "\n\\end{itemize}"
    val weekName   = modulePlan.column("W")(w).toLowerCase
    val fileName   = s"../compendium/generated/$weekName-chaphead-generated.tex"
    result.latexEscape.save(fileName)
    //println("\n" + fileName + "\n" + latexItems)
  }
  
  // *** Generate table body rows of progress protocoll in compendium prechapters
  def exerciseRow(s: String) = s"""\\ExeRow{$s}""" 
  def labRow(s: String) = s"""\\LabRow{$s}""" 
  def row(col: String) = weeks.map(weekPlan.column(col)(_)).filterNot(_ == "--")
  val labs = row("Lab").map(labRow).mkString("\n")
  labs.save("../compendium/generated/labs-generated.tex")
  val exercises = 
        row("Övn").
        filterNot(Set("Uppsamling","Extenta").
        contains(_)).
        map(exerciseRow).mkString("\n")
  exercises.save("../compendium/generated/exercises-generated.tex")

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
  nameDefs.save("../compendium/generated/names-generated.tex")
  
  // *** Generate dummy files if not exists
  def isExistingPath(path: String) = {
    import java.nio.file.{Paths, Files}
    Files.exists(Paths.get(path))
  }
  
  def createFilesOfWeek(week: Int): Unit = {
    val weekNum = weekPlan.column("W")(week).drop(1)
    val prefix = "../compendium/modules"
    val (chap, exe, lab, sol) = (
      s"$prefix/w$weekNum-chapter.tex",
      s"$prefix/w$weekNum-exercise.tex",
      s"$prefix/w$weekNum-lab.tex",
      s"$prefix/w$weekNum-solutions.tex"
    )
    def create(fileName: String, data: String) = if (!isExistingPath(fileName)) {
      println("Creating " + fileName)
      //data.save(faleName)
    }
    create(chap,"")
    create(exe, "")
    create(lab, "")
    create(sol, "")  
  }
  
  weeks.drop(2).foreach(createFilesOfWeek)
  
}




