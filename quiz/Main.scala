package genquiz

object Main extends {

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

    val n = for (q <- QuizData.quizNames if q contains partOfQuizName) yield {
      val (t, s) = QuizUtils.toLatexTaskSolution(q)

      println(s"\nGenerating: $q-taskrows" )
      t.save(currentDir + s"../compendium/generated/$q-taskrows-generated.tex")

      println(s"Generating: $q-solurows" )
      s.save(currentDir + s"../compendium/generated/$q-solurows-generated.tex")

      1  // to count number of files generated.
    }

    println(s"    ${n.sum} files generated.")
  }

}
