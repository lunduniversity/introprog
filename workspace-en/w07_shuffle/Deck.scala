package poker

import scala.collection.immutable.ArraySeq

class Deck private (val initCards: ArraySeq[Card]):
  private var cards: Array[Card] = initCards.toArray

  def reset(): Unit = cards = initCards.toArray

  def apply(i: Int): Card = cards(i)

  def toSeq: ArraySeq[Card] = cards.to(ArraySeq)

  def show: String = cards.map(_.show).mkString(" ")

  def peek(n: Int): ArraySeq[Card] = 
    cards.take(n).to(ArraySeq)

  def remove(n: Int): ArraySeq[Card] =
    val init = peek(n)
    cards = cards.drop(n)
    init
  end remove

  def prepend(moreCards: Card*): Unit = 
    cards = (moreCards ++ cards).toArray

  /** Swaps cards at position a and b. */
  def swap(a: Int, b: Int): Unit = ???

  /** Randomly reorders the cards in this deck. */
  def shuffle(): Unit = ???

object Deck:
  def apply(cards: Seq[Card]): Deck = new Deck(cards.to(ArraySeq))

  /** Creates a new full Deck with 52 cards in rank and suit order. */
  def full(): Deck = ???

