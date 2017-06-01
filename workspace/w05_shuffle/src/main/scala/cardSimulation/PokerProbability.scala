package cardSimulation
import collection.mutable.Map

object PokerProbability {

  /**
   * For a given number of iterations, shuffles a deck, draws a hand and
   * registers the result.
   *
   * @param n The number of hands generated.
   * @param deck The card deck to draw hands from. The deck size is unchanged.
   * @return A vector containing the frequency of every category rank.
   */
  def register(n: Int, deck: Deck): Vector[Int] = ???

  def main(args: Array[String]): Unit = {
    println("Enter number of iterations:")
    val n = scala.io.StdIn.readInt()
    val deck = Deck.full()
    val frequencies = register(n, deck)
    for (i <- Hand.CategoryRank.indices if frequencies(i) > 0) {
        val name = Hand.NameOfCategory.swedish(i)
        val percentages = frequencies(i).toDouble / n * 100
        println(f"$name: $percentages%.4f%%")
    }
  }
}
