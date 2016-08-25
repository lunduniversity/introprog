package cardSimulation

class TestingDeck extends CardDeck {
  override lazy val cards = (for (i <- 11 to 13) yield Card(1, i)).toArray
  val origin = cards.clone
  def reset = for (i <- 0 to (cards.length - 1)) cards(i) = origin(i)
}

object TestingDeck {
  def testShuffle(n: Int = 1000000): Unit = {
    val tested = new TestingDeck
    val f = collection.mutable.Map[List[Card], Int]()
    for (perm <- tested.cards.permutations) f += (perm.toList -> 0) //Converting to list for well defined equality
    for (i <- 1 to n) { // For each i, ignore i
      tested.reset
      tested.shuffle
      f(tested.cards.toList) += 1
    }
    println(AsciiBarGraph(f.map { case (k, v) => (k.mkString(""), v) }))
  }

  def main(args: Array[String]): Unit = {
    testShuffle()
  }
}