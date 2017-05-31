package cardSimulation

case class Hand(cards: Vector[Card]) {
  def isFlush: Boolean = cards.forall(_.suit == cards(0).suit)

  def isStraight: Boolean = {
    def isInSeq(xs: Vector[Int]): Boolean = xs.indices.forall(i => xs(i) == xs(i + 1) - 1)
    val ranksSorted = cards.map(_.rank).sorted
    if (ranksSorted(0) != 1) isInSeq(ranksSorted)
    else // special case with ace interpreted as either 1 or 14
      isInSeq(ranksSorted) || isInSeq(ranksSorted.drop(1).:+(14))
  }

  /**
   * Yields a vector of length 14, with positions 1-13 containing
   * the number of cards of that value. Position 0 is not used.
   */
  def tally: Vector[Int] = ???
}
object Hand {
  def drawFrom(deck: CardDeck): Hand = Hand(deck.peek(5))
}
