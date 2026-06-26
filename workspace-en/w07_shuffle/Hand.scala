package poker

import scala.collection.immutable.ArraySeq

case class Hand(cards: ArraySeq[Card]):
  import Hand._

  /**
   * Yields a sequence of length 13, with positions 0-12 containing the 
   * number of cards of that card's rank's ordinal number (zero based).
   */
  lazy val tally: ArraySeq[Int] = ???

  lazy val ranksSorted: ArraySeq[Int] = cards.map(_.rank.ordinal).sorted

  lazy val isFlush: Boolean = cards.length > 0 && cards.forall(_.suit == cards(0).suit)

  lazy val isStraight: Boolean = 
    def isInSeq(xs: ArraySeq[Int]): Boolean =
      xs.length > 1 && (0 to xs.length - 2).forall(i => xs(i) == xs(i + 1) - 1)

    isInSeq(ranksSorted) ||  // special case with ace interpreted as 13:
      (ranksSorted(0) == 0) && isInSeq(ranksSorted.drop(1) :+ 13)

  lazy val isStraightFlush: Boolean = isStraight && isFlush
  lazy val isRoyalFlush:    Boolean = ???
  lazy val isFour:          Boolean = tally.contains(4)
  lazy val isFullHouse:     Boolean = tally.contains(3) && tally.contains(2)
  lazy val isThrees:        Boolean = tally.contains(3)
  lazy val isTwoPair:       Boolean = tally.count(_ == 2) == 2
  lazy val isOnePair:       Boolean = tally.contains(2)

  lazy val category: Category = 
    if      /* isRoyalFlush    then Category.RoyalFlush
    else if */ isStraightFlush then Category.StraightFlush
    else if isFour          then Category.Fours
    else if isFullHouse     then Category.FullHouse
    else if isFlush         then Category.Flush
    else if isStraight      then Category.Straight
    else if isThrees        then Category.Threes
    else if isTwoPair       then Category.TwoPair
    else if isOnePair       then Category.OnePair
    else                         Category.HighCard

object Hand:
  def apply(cardSeq: Card*): Hand = new Hand(cardSeq.to(ArraySeq))
  def from(deck: Deck): Hand = Hand(deck.peek(5))
  def removeFrom(deck: Deck): Hand = Hand(deck.remove(5))

  enum Category:
    case 
      RoyalFlush, StraightFlush, Fours, FullHouse, Flush, 
      Straight, Threes, TwoPair, OnePair, HighCard
end Hand
