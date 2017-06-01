package cardSimulation

case class Hand(cards: Vector[Card]) {
  import Hand._

  /**
   * Yields a vector of length 14, with positions 1-13 containing
   * the number of cards of that rank. Position 0 is not used.
   */
  def tally: Vector[Int] = ???

  def isFlush:    Boolean = cards.forall(_.suit == cards(0).suit)
  def isStraight: Boolean = {
    def isInSeq(xs: Vector[Int]): Boolean = (0 until xs.length -1).forall(i => xs(i) == xs(i + 1) - 1)
    val ranksSorted = cards.map(_.rank).sorted
    if (ranksSorted(0) != 1) isInSeq(ranksSorted)
    else // special case with ace interpreted as either 1 or 14
      isInSeq(ranksSorted) || isInSeq(ranksSorted.drop(1).:+(14))
  }
  def isStraightFlush: Boolean = isStraight && isFlush
  def isFour:          Boolean = tally.contains(4)
  def isFullHouse:     Boolean = tally.contains(3) && tally.contains(2)
  def isThrees:        Boolean = tally.contains(3)
  def isTwoPair:       Boolean = tally.count(_ == 2) == 2
  def isOnePair:       Boolean = tally.contains(2)

  def categoryRank: Int =
    if (isStraight && isFlush) CategoryRank.StraightFlush
//  else if (isFour)           CategoryRank.Fours
//  else if (isFullHouse)      CategoryRank.FullHouse
    else if (isFlush)          CategoryRank.Flush
    else if (isStraight)       CategoryRank.Straight
//  else if (isThrees)         CategoryRank.Threes
//  else if (isTwoPair)        CategoryRank.TwoPair
//  else if (isOnePair)        CategoryRank.OnePair
    else                       CategoryRank.HighCard

  val nameEnglish: String = NameOfCategory.english(categoryRank)
  val nameSwedish: String = NameOfCategory.swedish(categoryRank)
}
object Hand {
  def apply(cardSeq: Card*): Hand = new Hand(cardSeq.toVector)
  def from(deck: Deck): Hand = new Hand(deck.peek(5))
  def removeFrom(deck: Deck): Hand = new Hand(deck.remove(5))

  object CategoryRank {
    val RoyalFlush = 0
    val StraightFlush = 1
    val Fours = 2
    val FullHouse = 3
    val Flush = 4
    val Straight = 5
    val Threes = 6
    val TwoPair = 7
    val OnePair = 8
    val HighCard = 9
    val indices = RoyalFlush to HighCard
  }

  object NameOfCategory {
    val english = Vector("royal flush", "straight flush", "four of a kind",
      "full house", "flush", "straight", "three of a kind", "two pairs",
      "pair", "high card")
    val swedish = Vector("royal flush", "färgstege", "fyrtal", "kåk", "färg",
      "stege", "tretal", "två par", "par", "högt kort")
  }
}
