package poker

import scala.collection.immutable.ArraySeq
import Card.{Rank, Suit}

object Test:
  def testCreate(): Unit = 
    println("\n*** testCreate:")
    val deck = Deck.full() ; println(s"FULL DECK:\n${deck.toSeq.map(_.show)}")
    deck.shuffle()         ; println(s"SHUFFLED: \n${deck.toSeq.map(_.show)}")
    deck.reset()           ; println(s"RESET:    \n${deck.toSeq.map(_.show)}")
  end testCreate

  def testShuffle(n: Long = 10_000_000L): Unit = 
    println("\n*** testShuffle:")
    val testCards = Seq(
      Card(Rank.Jack, Suit.Spades), 
      Card(Rank.Queen, Suit.Spades), 
      Card(Rank.King, Suit.Spades)
    )
    val perms: Seq[Seq[Card]] = testCards.permutations.toSeq
    val freq: Array[Long] = Array.fill(perms.length)(0L)
    val deck = Deck(testCards)
    for i <- 1L to n do
      if (i % 1e6.toLong == 0L) print(".")
      deck.shuffle()
      val permIndex = perms.indexOf(deck.toSeq)
      freq(permIndex) += 1L
    end for
    val listingOfPercentages = perms.indices
      .map(i => s"${perms(i).map(_.show).mkString}: ${freq(i).toDouble/n}%")
      .mkString("\n")
    println(s"\n$listingOfPercentages")
  end testShuffle

  def testCategorize(): Unit = 
    println("\n*** testCategorize:")
    val h = Hand(
      Card(Rank.Ace,Suit.Spades), 
      Card(Rank.`2`,Suit.Spades), 
      Card(Rank.`3`,Suit.Spades), 
      Card(Rank.`4`,Suit.Spades), 
      Card(Rank.`5`,Suit.Spades)
    )
    println(h.cards.map(_.show))
    println(s"isFlush:         ${h.isFlush}")
    println(s"isStraight:      ${h.isStraight}")
    println(s"isStraightFlush: ${h.isStraightFlush}")
    println(s"category:        ${h.category}")
    println(s"categoryRank:    ${h.category.ordinal + 1}")
  end testCategorize

  def testTally(): Unit =
    def runTest(hand: Hand, expected: ArraySeq[Int]): Unit =
      require(expected.length == 13, "expected must be of length 13")
      val actual = hand.tally
      if expected == actual then
        println(s"OK: $hand gives $actual")
      else
        println(s"Test failed: $hand gives $actual but should give $expected")

    println("\n*** testTally:")

    runTest(
      Hand(Card(Rank.Ace, Suit.Spades)),
      ArraySeq(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))

    runTest(
      Hand(
        Card(Rank.`4`, Suit.Hearts), 
        Card(Rank.`4`, Suit.Spades), 
        Card(Rank.`5`, Suit.Clubs), 
        Card(Rank.`4`, Suit.Clubs), 
        Card(Rank.Jack, Suit.Spades)),
      ArraySeq(0, 0, 0, 3, 1, 0, 0, 0, 0, 0, 1, 0, 0))

    runTest(
      Hand(
        Card(Rank.King, Suit.Diamonds), 
        Card(Rank.Jack, Suit.Spades), 
        Card(Rank.Jack, Suit.Diamonds), 
        Card(Rank.Jack, Suit.Hearts), 
        Card(Rank.Ace,  Suit.Diamonds)),
      ArraySeq(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 1))

    runTest(Hand(), ArraySeq(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
  end testTally

  @main def testDeck(): Unit = 
    testCategorize()
    //testCreate()
    //testShuffle()
    //testTally()
  end testDeck

end Test


