import StringExtras._
val nbrOfReadyLectures = 14   // ***** BUMP when new lectures are ready

// Check which dir we are in and if parent to plan then fix prefix
val here = ".".toPath.toAbsolutePath.getParent
val filesHere = here.toFile.list.toVector
val isPlanParent = filesHere.contains("plan")
val currentDir = if (isPlanParent) "plan/" else ""

val texUtf = "%!TEX encoding = UTF-8 Unicode\n"

def texRoot(fileName: String): String = s"%!TEX root = ../$fileName.tex\n"

val weeks = (0 to 6) ++ (8 to 14) //exlude exam weeks

val weekNumAlpha =  //as latex cannot have numbers in command names AARGH!!
    Vector("ONE","TWO","THREE","FOUR","FIVE","SIX","SEVEN", "",
          "EIGHT","NINE","TEN","ELEVEN","TWELVE","THIRTEEN","FOURTEEN", "")

val minConceptsForTwoCol = 28

def conceptBegin(n: Int) = "Begrepp som ingår i denna veckas studier:\n" +
  (if (n > minConceptsForTwoCol) """\begin{multicols}{2}""" else "") +
  """\begin{itemize}[noitemsep,label={$\square$},leftmargin=*]""" + "\n"

def conceptEnd(n: Int)   = """\end{itemize}""" +
  (if (n > minConceptsForTwoCol) """\end{multicols}""" else "") + "\n"

def isFirstUpper(s: String) = s.headOption.
  map(ch => ch.toString ==  ch.toString.toUpperCase).getOrElse(false)

def exerciseRow(s: String) = s"""\\ExeRow{$s}"""

def labRow(s: String) = s"""\\LabRow{$s}"""

val labs = row("Lab").map(labRow).mkString("\n")

def row(col: String) = 
  weeks.map(weekPlan.column(col)(_)).filterNot(_ == "--").filterNot(isFirstUpper)

val exercises = row("Övn")
      .filterNot(Set("Uppsamling","Extenta","Munta")
      .contains(_))
      .map(exerciseRow)
      .mkString("\n")

object weekPlan extends Plan with Table:
  override val heading =
    Seq("W", "Datum", "Lp V", "Modul", "Förel", "Övn", "Lab")

object modulePlan extends Plan with Table:
  override val heading = Seq("W", "Modul", "Innehåll")

  def toHtmlPatched: String =  // hack to insert links to lectures
    var htmlSoup = toHtml
    // adjust for the strange week with KONTROLLSKRIVNING
    val n = if (nbrOfReadyLectures < 8) nbrOfReadyLectures else nbrOfReadyLectures + 1
    column("Modul").zipWithIndex.take(n).foreach {
        case (m, i) if i != 7 =>
          println(s"Injecting html link patch in module: $m")
          def href(m: String): String = {
            val w = column("W").apply(i).toLowerCase
            s"""<a href="https://fileadmin.cs.lth.se/pgk/lect-$w.pdf">$m</a>"""
          }
          htmlSoup = htmlSoup.replace(s"$m</td>",s"${href(m)}</td>")

        case (m, _) => println(s"Ignoring html-patching of link in module: $m")
    }
    htmlSoup
    
end modulePlan

object overview extends Plan with Table:
  override val heading = Seq("W", "Modul", "Övn", "Lab")

@main def Main =
  println("*** plan generation started in: " + here)

  weekPlan.  toMarkdown.save(currentDir + "week-plan-generated.md")
  weekPlan.  toHtml    .save(currentDir + "week-plan-generated.html")
  weekPlan.  toLatex   .prepend(texUtf).save(currentDir + "week-plan-generated.tex")
  modulePlan.toMarkdown.    save(currentDir + "module-plan-generated.md")
  modulePlan.toHtmlPatched. save(currentDir + "module-plan-generated.html")
  modulePlan.latexTableBody.save(currentDir + "module-plan-generated.tex")
  overview  .toLatex   .prepend(texUtf).save(currentDir + "overview-generated.tex")

  for w <- weeks do
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
  end for

  labs
    .prepend(texRoot("compendium"))
    .prepend(texUtf)
    .save(currentDir+"../compendium/generated/labs-generated.tex")

  exercises.prepend(texUtf).save(currentDir + "../compendium/generated/exercises-generated.tex")

  def nameDefRow(week: Int, modName: String, labName: String, exeName: String) =
    s"""
    |\\newcommand{\\ModWeek${weekNumAlpha(week)}}{$modName}
    |\\newcommand{\\ExeWeek${weekNumAlpha(week)}}{$exeName}
    |\\newcommand{\\LabWeek${weekNumAlpha(week)}}{$labName}
    |""".stripMargin

  def namesOfWeek(w: Int) =
    nameDefRow(w, weekPlan.column("Modul")(w), weekPlan.column("Lab")(w), weekPlan.column("Övn")(w))
  val nameDefs = (for (w <- weeks) yield namesOfWeek(w)).mkString("\n")
  nameDefs.prepend(texUtf).save(currentDir + "../compendium/generated/names-generated.tex")

  def overviewTemplate(module: String, exe: String, lab: String, body: String, cols: Int = 3): String = 
    s"""
    Modul \\Emph{$module}: Övn \\Alert{\\texttt{$exe}} $$\\rightarrow$$ Labb \\Alert{\\texttt{$lab}}
    \\begin{multicols}{$cols}\\SlideFontTiny
    $body
    \\end{multicols}
    """

  for w <- weeks do
    val concepts   = modulePlan.column("Innehåll")(w).split(',').toVector.filterNot(_.isEmpty)
    def toLatexItem(s: String) = s"$$\\square$$ ${s.trim} \\\\\n"
    val items = if w < 14 then concepts.map(toLatexItem).mkString.trim else "Repetera begrepp"
    val output = overviewTemplate(
      module = overview.column("Modul")(w),
      exe    = overview.column("Övn")(w),
      lab    = overview.column("Lab")(w),
      body   = items
    )
    val weekName   = modulePlan.column("W")(w).toLowerCase
    output.prepend(texUtf).save(
      currentDir + s"../slides/generated/$weekName-overview-generated.tex")
  end for

  FindHeadings()

