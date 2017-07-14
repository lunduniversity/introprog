object Main extends App {

  import java.nio.file.{Paths, Files}
  import java.nio.charset.StandardCharsets.UTF_8

  implicit class StringDecorator(s: String) {
    def toPath = Paths.get(s)

    def save(fileName: String) = {
      println("Saving file: " + Paths.get(fileName))
      Files.write(Paths.get(fileName), s.getBytes(UTF_8))
    }
  }

  // Check which dir we are in and if parent to plan then fix prefix
  //   to allow for running wbt at diffrent levels
  lazy val here = ".".toPath.toAbsolutePath.getParent
  lazy val filesHere = here.toFile.list.toVector
  lazy val isPlanParent = filesHere.contains("quiz")
  lazy val currentDir = if (isPlanParent) "quiz/" else ""

  println("*** Generating quiz files for inclusion in compendium:")
  for (q <- ConceptQuiz.quizes.keys) {
    val (t, s) = ConceptQuiz.toLatexTaskSolution(q)
    t.save(currentDir + s"../compendium/generated/$q-taskrows-generated.tex")
    s.save(currentDir + s"../compendium/generated/$q-solurows-generated.tex")
  }

}
