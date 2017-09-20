package object genquiz {

  // to understand the types below, see data in QuizData.scala

  case class QuizID(name: String, seed: Int)
  object QuizID {
    def apply(name: String) = new QuizID(name, name.hashCode)
  }

  type Term     = String
  type Defi     = String
  type Concept  = (Term, Defi)

  type Index = Int
  type Concepts = Vector[((Term, Index), (Defi, Index))]

  type Solution = Map[Int, Int]  // mapping between scrambled and unscrambled number

  object QuizUtils {

    def terms(cs: Concepts): Vector[(Term, Index)] = cs.map { case ((t, i), (d, j)) => (t,i)}
    def defis(cs: Concepts): Vector[(Term, Index)] = cs.map { case ((t, i), (d, j)) => (d,j)}

    def replaceDefiNumberWithOrdererdIndex(cs: Concepts): Concepts =
      cs.zipWithIndex.map{ case (((t, i), (d, j)), row) => ((t, i), (d, row))}

    def scramble(cs: Concepts, seed: Int): (Concepts, Solution) = {
      val shuffledDefis = (new scala.util.Random(seed)).shuffle(defis(cs))
      val solution = shuffledDefis.map { case (d, i) => i } .zipWithIndex.toMap
      val shuffledConcepts = terms(cs) zip shuffledDefis
      (replaceDefiNumberWithOrdererdIndex(shuffledConcepts), solution)
    }

    def unscramble(cs: Concepts, solution: Solution): Concepts =
      terms(cs) zip defis(cs).map { case (d, i) => (d, solution(i))}

    def indexToChar(i: Index): Char   = ('A' + i).toChar
    def indexToNum (i: Index): String = (i + 1)  .toString

    def toTaskRowLatex(row: ((Term, Index), (Defi, Index))): String = row match {
      case ((term, i), (defi, j)) =>
        s"""  $term & ${indexToNum(i)} & & ${indexToChar(j)} & $defi \\\\ """
    }

    def toSolutionRowLatex(row: ((Term, Index), (Defi, Index))): String = row match {
      case ((term, i), (defi, j)) =>
      val arrow = "~~\\Large$\\leadsto$~~"
      s"""  $term & ${indexToNum(i)} & $arrow &  ${indexToChar(j)} & $defi \\\\ """
    }

    def toLatexTaskSolution(q: QuizID): (String, String) = {
      val cs = QuizData.concepts(q)
      val (scs, sol) = scramble(cs, q.seed)
      val ucs = unscramble(cs, sol)

      val taskRows: String = scs.map(toTaskRowLatex).mkString("\n")
      val soluRows: String = ucs.map(toSolutionRowLatex).mkString("\n")

      (taskRows, soluRows)
    }

  }

}
