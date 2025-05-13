package genquiz

object Main {

  import java.nio.file.{Paths, Files}
  import java.nio.charset.StandardCharsets.UTF_8

  implicit class StringDecorator(s: String) {
    def toPath = Paths.get(s)

    def save(fileName: String) = {
      println("Saving file: " + Paths.get(fileName))
      Files.write(Paths.get(fileName), s.getBytes(UTF_8))
    }
  }

  def main(args: Array[String]): Unit = {  // run with args(0) part of quiz name to update

    val partOfQuizName = args.lift(0).getOrElse("")

    // Check which dir we are in and if parent to plan then fix prefix
    //   to allow for running wbt at diffrent levels
    val here = ".".toPath.toAbsolutePath.getParent
    val filesHere = here.toFile.list.toVector
    val isPlanParent = filesHere.contains("quiz")
    val currentDir = if (isPlanParent) "quiz/" else ""

    println("*** Generating quiz files for inclusion in compendium:")

    if (partOfQuizName.nonEmpty) println(s"    for quiz names containing args(0)=$partOfQuizName")
    else println("    as no args(0) is given: (re-)generating all existing quiz files")

    val ns = for q <- QuizData.quizIDs if q.name.contains(partOfQuizName) yield {
      val (t, s) = QuizUtils.toLatexTaskSolution(q)

      //println(s"\nGenerating: ${q.name}-taskrows" )
      t.save(currentDir + s"../compendium/generated/${q.name}-taskrows-generated.tex")

      //println(s"Generating: ${q.name}-solurows" )
      s.save(currentDir + s"../compendium/generated/${q.name}-solurows-generated.tex")

      2  // to count number of files generated.
    }

    println(s"    ${ns.sum} quiz files generated.")

    // code to make a textfile with all concept definitions:

      val conceptQuizes = QuizData.quizIDs.filter(_.name.contains("concept"))
      def fixPair(p: (String, String)): (String, String) = {
        def replaceStuff(s: String): String =
          s.replace("\\code|", "").replace("|","")
        (replaceStuff(p._1).trim, replaceStuff(p._2).trim)
      }
      val concepts = conceptQuizes
        .flatMap(id => QuizData.quizes(id))
        .map(fixPair)
        .distinct
        .sortBy(_._1.toLowerCase)
        .map{case (key, value) => s"$key\t$value"}

      val conceptFile = s"$currentDir../quiz/quiz-concepts.tsv"
      concepts.mkString("\n").save(conceptFile)
  }

}
