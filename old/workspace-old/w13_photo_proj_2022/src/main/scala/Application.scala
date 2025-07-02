package photo

object Application:

  /**Define filters*/
  val filters : Array[ImageFilter] = ???

  /**Ask for image, edit image etc*/
  def main(args: Array[String]): Unit = ???
      
      
  /**Ask if user wants to edit one more image or exit*/
  private def continue: Boolean = ???
      
  /**Get rid of text in terminal window*/
  def clearScreen() = print("\u001b[2J\u001b[;H")
