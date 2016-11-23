package cardSimulation

case class Card(suit: Int, value: Int) {
  require(value >= 1 && value <= 13, "value must be between 1 and 13")
  require(suit >= 1 && suit <= 4, "suit must be between 1 and 13")

  def valStr = value match {
    case 1  => "A"
    case 11 => "J"
    case 12 => "Q"
    case 13 => "K"
    case _  => value
  }
  
  def suitStr = suit match {
    case 1 => "♠"
    case 2 => "♥"
    case 3 => "♣"
    case 4 => "♦"
  }

  override def toString() = suitStr + valStr
}