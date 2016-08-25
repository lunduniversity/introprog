package cardSimulation

case class Card(suit: Int, value: Int) {
  assume(value >= 1 && value <= 13)
  assume(suit >= 1 && suit <= 4)

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