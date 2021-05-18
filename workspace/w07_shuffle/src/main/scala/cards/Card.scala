package cards

case class Card(rank: Int, suit: Int):
  import Card._

  require(rankRange.contains(rank), s"rank=$rank, must be in $rankRange")
  require(suitRange.contains(suit), s"suit=$suit, must be in $suitRange")

  val rankString: String = ranks(rank - 1)
  val suitChar:   Char   = suits(suit - 1)

  override def toString() = s"$rankString$suitChar "
  
object Card:
  val suitRange: Range = 1 to 4
  val rankRange: Range = 1 to 13
  val suits: Vector[Char] = "♠♥♣♦".toVector
  val ranks: Vector[String] =
    "A" +: ((2 to 10).map(_.toString).toVector ++ Vector("J", "Q", "K"))
