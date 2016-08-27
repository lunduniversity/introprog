package cardSimulation
import collection.mutable.Map

object PokerProbability {

  val RoyalFlush = "Royal flush"
  val StraightFlush = "Straight flush"
  val Fours = "Four of a kind"
  val FullHouse = "Full house"
  val Flush = "Flush"
  val Straight = "Straight"
  val Threes = "Three of a kind"
  val TwoPair = "Two pairs"
  val OnePair = "Pair"
  val NoPair = "High card"

  val everyHand = Vector(RoyalFlush, StraightFlush, Fours, FullHouse, Flush,
    Straight, Threes, TwoPair, OnePair, NoPair);

  /**
   * For a fixed number of iterations, shuffles a deck, draws a hand and
   *  registers the result
   *
   * @param iterationCount The number of iterations
   * @return A map containing, for every hand, the number of times it was found
   */
  def test(iterationCount: Int): Map[String, Int] = ???

  def testHand(hand: Hand): String = {
    try {
      if (hand.isStraight && hand.isFlush) StraightFlush
      else if (hand.tally.contains(4)) Fours
      else if (hand.tally.contains(3) && hand.tally.contains(2)) FullHouse
      else if (hand.isFlush) Flush
      else if (hand.isStraight) Straight
      else if (hand.tally.contains(3)) Threes
      else if (hand.tally.count(_ == 2) == 2) TwoPair
      else if (hand.tally.contains(2)) OnePair
      else NoPair
    } catch {
      case e: NotImplementedError =>
        if (hand.isStraight && hand.isFlush) StraightFlush
        else if (hand.isFlush) Flush
        else if (hand.isStraight) Straight
        else NoPair
    }

  }

  def main(args: Array[String]): Unit = {
    println("Type in the number of iterations:")
    val n = scala.io.StdIn.readInt()
    val res = test(n)
    for (hand <- everyHand)
      if (res(hand) > 0) {
        val percent = res(hand).toDouble / n * 100
        println(f"$hand: $percent%.4f%%")
      }
  }
}