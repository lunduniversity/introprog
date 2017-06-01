package cardSimulation

object TestHand {

  def testCategorize(): Unit = {
    println("\n*** TestHand.testCategorize:")
    val h1 = Hand(Card(1,1), Card(2,1), Card(3,1), Card(4,1), Card(5,1))
    println(h1)
    println(s"isFlush:         ${h1.isFlush}")
    println(s"isStraight:      ${h1.isStraight}")
    println(s"isStraightFlush: ${h1.isStraightFlush}")
    println(s"categoryRank:    ${h1.categoryRank}")
    println(s"nameEnglish:     ${h1.nameEnglish}")
    println(s"nameSwedish:     ${h1.nameSwedish}")
  }

  def main(args: Array[String]): Unit = {
    testCategorize()
  }
}
