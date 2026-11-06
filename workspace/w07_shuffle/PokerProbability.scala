package poker

import scala.collection.immutable.ArraySeq
import Hand.Category

object PokerProbability:

  /**
   * For a given number of iterations, shuffles a deck, draws a hand and
   * returns a sequence with the frequency of each hand category.
   * Prints a dot every dotStep iteration
   */
  def register(n: Long, deck: Deck, dotStep: Long = 1e6.toLong): ArraySeq[Int] = 
    ???
  end register

  def stdBinomalDev(expectedPercentage: Double, n: Long): Double =
    val p = expectedPercentage / 100
    math.sqrt(n * p * (1-p))


  @main def simulate: Unit =
    val expectedPercentages: Map[Category, Double] = Map(
      Category.RoyalFlush    -> 0.000154,
      Category.StraightFlush -> 0.00139,
      Category.Fours         -> 0.02401,
      Category.FullHouse     -> 0.1441,
      Category.Flush         -> 0.1965,
      Category.Straight      -> 0.3925,
      Category.Threes        -> 2.1128,
      Category.TwoPair       -> 4.7539,
      Category.OnePair       -> 42.2569,
      Category.HighCard      -> 50.1177
    )

    val defaultIter = 5
    val in = scala.io.StdIn.readLine(s"number of million iterations ($defaultIter): ")
    val n = (in.toIntOption.getOrElse(defaultIter) * 1e6).toLong
    val deck = Deck.full()
    val t0 = System.currentTimeMillis()
    val frequencies = register(n, deck)
    for c <- Hand.Category.values do
        val name = c.toString
        val occurences = frequencies(c.ordinal).toDouble
        val percentages = occurences / n * 100

        val stdDev = stdBinomalDev(expectedPercentages(c), n)
        val expectedOccurences = expectedPercentages(c) / 100 * n
        val standardScore = (occurences - expectedOccurences) / stdDev
        
        print(f"$name%16s $percentages%10.6f%%")
        println(f" | Standard score: $standardScore")
    end for
    val secs = (System.currentTimeMillis() - t0)/1000.0
    println:
      f"\n*** Total execution time: $secs%3.2f seconds"
