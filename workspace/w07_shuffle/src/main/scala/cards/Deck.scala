package cards

class Deck private (val initCards: Vector[Card]):
  private var cards: Array[Card] = initCards.toArray

  def reset(): Unit = cards = initCards.toArray
  def apply(i: Int): Card = cards(i)
  def toVector: Vector[Card] = cards.toVector
  override def toString: String = cards.mkString(" ")

  def peek(n: Int): Vector[Card] = cards.take(n).toVector

  def remove(n: Int): Vector[Card] =
    val init = peek(n)
    cards = cards.drop(n)
    init

  def prepend(moreCards: Card*): Unit = cards = moreCards.toArray ++ cards

  /** Swaps cards at position a and b. */
  def swap(a: Int, b: Int): Unit = ???

  /** Randomly reorders the cards in this deck. */
  def shuffle(): Unit = ???

object Deck:
  def empty: Deck = new Deck(Vector())
  def apply(cards: Seq[Card]): Deck = new Deck(cards.toVector)

  /** Creates a new full Deck with 52 cards in rank and suit order. */
  def full(): Deck = ???
