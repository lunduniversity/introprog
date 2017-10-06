package cards

object TestHand {

  def testCategorize(): Unit = {
    println("\n*** TestHand.testCategorize:")
    val h = Hand(Card(1,1), Card(2,1), Card(3,1), Card(4,1), Card(5,1))
    println(h)
    println(s"isFlush:         ${h.isFlush}")
    println(s"isStraight:      ${h.isStraight}")
    println(s"isStraightFlush: ${h.isStraightFlush}")
    println(s"categoryRank:    ${h.category}")
    println(s"nameEnglish:     ${Hand.Category.Name.english(h.category)}")
    println(s"nameSwedish:     ${Hand.Category.Name.swedish(h.category)}")
  }

  def main(args: Array[String]): Unit = {
    testCategorize()
  }
}
