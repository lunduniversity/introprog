package poker

case class Card(rank: Card.Rank, suit: Card.Suit):
  lazy val show = s"${rank.toString.head}${Card.suitChars(suit.ordinal)}"

object Card:
  enum Rank:
    case Ace, `2`, `3`, `4`, `5`, `6`, `7`, `8`, `9`, Ten, Jack, Queen, King

  enum Suit:
    case Spades, Hearts, Clubs, Diamonds
  
  val suitChars = "♠♥♣♦"
end Card
