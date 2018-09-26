package game

object Main {
  def play() = {
    var playAgain = true
    while (playAgain) {
      val game = new Game()
      playAgain = game.run()
    }
  }

  def main(args: Array[String]): Unit = {
    println("Hello Game!")
    play()
  }
}
