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

  // LaTeX sources searched for the author's official English terms via \Eng{...}
  val engSourceDirs = Seq(s"${inputPrefix}compendium/modules", s"${inputPrefix}slides/body")

  val Begr  = "Begrepp"
  val Eng   = "Engelska"
  val Beskr = "Beskrivning"

  /** All .tex files under the given dir, recursively, in stable (sorted) order. */
  def texFiles(dir: String): Seq[String] = {
    val root = java.nio.file.Paths.get(dir)
    if (!java.nio.file.Files.exists(root)) Seq()
    else {
      import scala.jdk.CollectionConverters._
      val stream = java.nio.file.Files.walk(root)
      try
        stream.iterator().asScala
          .filter(p => java.nio.file.Files.isRegularFile(p) && p.toString.endsWith(".tex"))
          .map(_.toString).toVector.sorted
      finally stream.close()
    }
  }

  /** Whole-file contents of every LaTeX source, in document order (modules then slides). */
  lazy val engCorpus: Seq[String] =
    engSourceDirs.flatMap(texFiles).map(_.loadLines.mkString("\n"))

  /** The professor's official English translation for a Swedish concept, taken from
    * the first `\Eng{...}` that DIRECTLY follows an occurrence of the concept in the
    * compendium/slides. "Directly" means: the concept appears as a whole word and the
    * `\Eng{...}` follows with only an enclosing macro's closing brace(s) and whitespace
    * in between (no other words) — e.g. `\Emph{kompanjonsobjekt} \Eng{companion object}`
    * or `s.k. matris \Eng{matrix}`. This avoids grabbing an unrelated, distant \Eng.
    * Returns "" when no such translation is found (then the column is left blank). */
  def findEnglish(concept: String): String = {
    import java.util.regex.Pattern
    val q = Pattern.quote(concept)
    // (?<![\p{L}\-]) : concept starts at a word boundary (not mid-word, not after a hyphen
    //                  so "klass" does not match inside "case-klass")
    // [}\s]* : allow only an enclosing macro's closing brace(s) and whitespace before \Eng
    val pat = Pattern.compile(
      s"(?<![\\p{L}\\-])$q[}\\s]*\\\\Eng\\{([^}]*)\\}",
      Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    )
    engCorpus.iterator
      .map { text => val m = pat.matcher(text); if (m.find()) Some(m.group(1).trim) else None }
      .collectFirst { case Some(e) => e }
      .getOrElse("")
  }

  def main(args: Array[String]): Unit = {
    println(s"""
      |*** GENERATING GLOSSARY based on quiz data
      |  current directory: $currentDir
      |  input file:        $quizConceptsFile
      |  English terms from: ${engSourceDirs.mkString(", ")} (${engCorpus.size} .tex files)
      |""".stripMargin
    )
    val concepts =  quizConceptsFile.loadLines
      .map(_.split("\t"))
      .map( xs => Map(Begr -> xs(0), Eng -> findEnglish(xs(0)), Beskr -> xs(1)))

    val foundEng = concepts.count(_(Eng).nonEmpty)
    println(s"  found official English term for $foundEng of ${concepts.size} concepts via \\Eng{...}")

    val table = Table(Seq(Begr, Eng, Beskr), concepts)
    table.toMarkdown.save(s"$conceptsFile.md")
    table.toHtml.save(s"$conceptsFile.html")
    table.toLatex.save(s"$conceptsFile.tex")

    println(s"""Generating explanations""")

    val rows = explain.allConcepts.map: c =>
      s"""\\newcommand{\\Explain${c.en.toCamelCase}}{${c.svLongExplanation.wrapConcept(c.sv)}}"""

    rows.mkString("\n").save(explanationsFile)
  }

}
