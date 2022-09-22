package cards

object TestHand:

  def testCategorize(): Unit =
    println("\n*** TestHand.testCategorize:")
    val h = Hand(Card(1,1), Card(2,1), Card(3,1), Card(4,1), Card(5,1))
    println(h)
    println(s"isFlush:         ${h.isFlush}")
    println(s"isStraight:      ${h.isStraight}")
    println(s"isStraightFlush: ${h.isStraightFlush}")
    println(s"categoryRank:    ${h.category}")
    println(s"nameEnglish:     ${Hand.Category.Name.english(h.category)}")
    println(s"nameSwedish:     ${Hand.Category.Name.swedish(h.category)}")

  def testTally(): Unit =
    def runTest(hand: Hand, expected: Vector[Int]): Unit =
      val actual = hand.tally
      if expected == actual then
        println(s"OK: $hand gives $actual")
      else
        println(s"Test failed: $hand gives $actual but should give $expected")

    println("\n*** TestHand.testTally:")

    runTest(Hand(Card(1, 1)),
            Vector(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))

    runTest(Hand(Card(4, 2), Card(4, 1), Card(5, 3), Card(4, 3), Card(11, 1)),
            Vector(0, 0, 0, 0, 3, 1, 0, 0, 0, 0, 0, 1, 0, 0))

    runTest(Hand(Card(13, 4), Card(11, 1), Card(11, 4), Card(11, 2), Card(1, 4)),
            Vector(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 1))

    runTest(Hand(),
            Vector(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))

  def main(args: Array[String]): Unit =
    testCategorize()
    testTally()
