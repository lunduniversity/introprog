import StringExtras._
object Main {
  val quizConceptsFile = "../quiz/quiz-concepts.tsv"
  val conceptsFile = "concepts-generated"

  val Begr  = "Begrepp"
  val Beskr = "Beskrivning"

  def main(args: Array[String]): Unit = {
    println("hej")
    val concepts =  quizConceptsFile.loadLines
      .map(_.split("\t"))
      .map( xs => Map(Begr -> xs(0), Beskr -> xs(1)))
    val table = Table(Seq(Begr, Beskr), concepts)
    table.toMarkdown.save(s"$conceptsFile.md")
    table.toHtml.save(s"$conceptsFile.html")
    table.toLatex.save(s"$conceptsFile.tex")
  }
}
