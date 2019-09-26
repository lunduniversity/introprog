package snake

object Main {
  def main(args: Array[String]): Unit = {
    val buttons = Seq("One","Two", "Tournament", "Cancel")
    val selected =
      introprog.Dialog.select("Number of players?", buttons, "Snake")
    selected match {
      case "One" => (new OnePlayerGame).play("Green")
      case "Two" => (new TwoPlayerGame).play("Green", "Blue")
      case "Tournament" => ??? // valbart krav
      case _ => println("Exiting main... Goodbye!")
    }
  }
}
