package cardSimulation

class CardDeck private (val initCards: Vector[Card]){
  private val rand = new scala.util.Random
  private var cards: Array[Card] = initCards.toArray

  def reset(): Unit = cards = initCards.toArray
  def apply(i: Int): Card = cards(i)
  def toVector: Vector[Card] = cards.toVector
  override def toString: String = cards.mkString(" ")

  def peek(n: Int): Vector[Card] = cards.take(n).toVector

  def remove(n: Int): Vector[Card] = {
    val init = peek(n)
    cards = cards.drop(n)
    init
  }

  def prepend(moreCards: Card*): Unit = cards = moreCards.toArray ++ cards

  def swap(i1: Int, i2: Int): Unit = ???

  def shuffle(): Unit = //??? Byt ut nedan mot din egen impl. av Knuth Shuffle
    cards = scala.util.Random.shuffle(cards.toSeq).toArray
}

object CardDeck {
  def empty: CardDeck = new CardDeck(Vector())
  def full(): CardDeck = {
    val xs = for (s <- Card.suitRange; r <- Card.rankRange) yield Card(r,s)
    new CardDeck(xs.toVector)
  }//???
  def apply(cards: Seq[Card]): CardDeck = new CardDeck(cards.toVector)
}
