package glossary
import StringExtras._
object Main {
  val currentDir = java.nio.file.Paths.get("").toAbsolutePath.toString
  val inputPrefix = if (currentDir.endsWith("glossary")) "../" else ""
  val outputPrefix = if (inputPrefix.isEmpty) "glossary/" else ""
  //above lines are a hack to allow run of nested sbt projects at different levels

  val quizConceptsFile = s"${inputPrefix}quiz/quiz-concepts.tsv"
  val conceptsFile = s"${outputPrefix}concepts-generated"
  val explanationsFile = s"${outputPrefix}explanations-generated.tex"

  val Begr  = "Begrepp"
  val Beskr = "Beskrivning"

  def main(args: Array[String]): Unit = {
    println(s"""
      |*** GENERATING GLOSSARY based on quiz data
      |  current directory: $currentDir
      |  input file:        $quizConceptsFile
      |""".stripMargin
    )
    val concepts =  quizConceptsFile.loadLines
      .map(_.split("\t"))
      .map( xs => Map(Begr -> xs(0), Beskr -> xs(1)))
    val table = Table(Seq(Begr, Beskr), concepts)
    table.toMarkdown.save(s"$conceptsFile.md")
    table.toHtml.save(s"$conceptsFile.html")
    table.toLatex.save(s"$conceptsFile.tex")

    println(s"""Generating explanations""")

    val rows = explain.allConcepts.map: c =>
      s"""\\newcommand{\\Explain${c.en.toCamelCase}}{${c.svLongExplanation.wrapConcept(c.sv)}}"""

    rows.mkString("\n").save(explanationsFile)
  }

}
