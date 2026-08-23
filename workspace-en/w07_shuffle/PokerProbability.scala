package poker

import scala.collection.immutable.ArraySeq

object PokerProbability:

  /**
   * For a given number of iterations, shuffles a deck, draws a hand and
   * returns a sequence with the frequency of each hand category.
   * Prints a dot every dotStep iteration
   */
  def register(n: Long, deck: Deck, dotStep: Long = 1e6.toLong): ArraySeq[Int] = 
    ???
  end register

  @main def simulate: Unit = 
    val defaultIter = 5
    val in = scala.io.StdIn.readLine(s"number of million iterations ($defaultIter): ")
    val n = (in.toIntOption.getOrElse(defaultIter) * 1e6).toLong
    val deck = Deck.full()
    val t0 = System.currentTimeMillis()
    val frequencies = register(n, deck)
    for c <- Hand.Category.values do
        val name = c.toString
        val percentages = frequencies(c.ordinal).toDouble / n * 100
        println(f"$name%16s $percentages%10.6f%%")
    end for
    val secs = (System.currentTimeMillis() - t0)/1000.0
    println:
      f"\n*** Total execution time: $secs%3.2f seconds"


