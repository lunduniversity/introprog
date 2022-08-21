package blockbattle

object Game:
  val windowSize = (30, 50)
  val windowTitle = "EPIC BLOCK BATTLE"
  val blockSize = 14
  val skyRange    = 0 to 7
  val grassRange  = 8 to 8
  object Color { ??? }
  /** Used with the different ranges and eraseBlocks */
  def backgroundColorAtDepth(y: Int): java.awt.Color = ???

class Game(
  val leftPlayerName: String  = "LEFT",
  val rightPlayerName: String = "RIGHT"
):
 import Game.* // direkt tillgång till namn på medlemmar i kompanjon

 val window    = new BlockWindow(windowSize, windowTitle, blockSize)
 val leftMole: Mole  = ???
 val rightMole: Mole = ???

 def drawWorld(): Unit = ???

 /** Use to erase old points, e.g updated score */
 def eraseBlocks(x1: Int, y1: Int, x2: Int, y2: Int): Unit = ???

 def update(mole: Mole): Unit = ???  // update, draw new, erase old

 def gameLoop(): Unit = ???

 def start(): Unit =
   println("Start digging!")
   println(s"$leftPlayerName ${leftMole.keyControl}")
   println(s"$rightPlayerName ${rightMole.keyControl}")
   drawWorld()
   gameLoop()
