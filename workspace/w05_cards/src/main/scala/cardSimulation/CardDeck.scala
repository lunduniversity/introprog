package cardSimulation

class CardDeck {
  val rand = new util.Random

  lazy val cards: Array[Card] = ???

  def shuffle: Unit = ???
}

object CardDeck {
  def main(args: Array[String]): Unit = {
    val d = new CardDeck
    println(d.cards.mkString(" "))
    d.shuffle
    println()
    println(d.cards.mkString(" "))
  }
}