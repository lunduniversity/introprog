package cards

case class Hand(cards: Vector[Card]):
  import Hand._

  /**
   * A vector of length 14 with positions 1-13 containing the number of
   * cards of that rank. Position 0 contains 0.
   */
  def tally: Vector[Int] = ???

  def ranksSorted: Vector[Int] = cards.map(_.rank).sorted.toVector

  def isFlush: Boolean = cards.length > 0 && cards.forall(_.suit == cards(0).suit)

  def isStraight: Boolean =
    def isInSeq(xs: Vector[Int]): Boolean =
      xs.length > 1 && (0 to xs.length - 2).forall(i => xs(i) == xs(i + 1) - 1)

    isInSeq(ranksSorted) ||  // special case with ace interpreted as 14:
      (ranksSorted(0) == 1) && isInSeq(ranksSorted.drop(1) :+ 14)

  def isStraightFlush: Boolean = isStraight && isFlush
  def isFour:          Boolean = tally.contains(4)
  def isFullHouse:     Boolean = tally.contains(3) && tally.contains(2)
  def isThrees:        Boolean = tally.contains(3)
  def isTwoPair:       Boolean = tally.count(_ == 2) == 2
  def isOnePair:       Boolean = tally.contains(2)

  def category: Int = // TODO: add more tests when tally is implemented
    if isStraight && isFlush then Category.StraightFlush
    else if isFlush          then Category.Flush
    else if isStraight       then Category.Straight
    else                          Category.HighCard

object Hand:
  def apply(cardSeq: Card*): Hand = new Hand(cardSeq.toVector)
  def from(deck: Deck): Hand = new Hand(deck.peek(5))
  def removeFrom(deck: Deck): Hand = new Hand(deck.remove(5))

  object Category:
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
    val values = RoyalFlush to HighCard

    object Name:
      val english = Vector("royal flush", "straight flush", "four of a kind", "full house",
        "flush", "straight", "three of a kind", "two pairs", "pair", "high card")
      val swedish = Vector("royal flush", "färgstege", "fyrtal", "kåk", "färg",
        "stege", "tretal", "två par", "par", "högt kort")
