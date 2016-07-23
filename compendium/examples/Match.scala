object Match {
  def main(args: Array[String]): Unit = {
    val favorite = if (args.length > 0) args(0) else "selleri"
    println("Din favoritgrönsak: " + favorite)
    val firstChar = favorite.toLowerCase.charAt(0)
    val meThink = firstChar match {
      case 'g' => "gurka är gott!"
      case 't' => "tomat är gott!" 
      case 'b' => "broccoli är gott!"
      case _   =>  favorite + " är äckligt!"
    }
    println("Jag tycker " + meThink)
  }
}
