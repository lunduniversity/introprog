package introprog.examples

/** Example of a simple BlockGame app with overridden callbacks to handle events
  * See the documentation of BlockGame and the source code of TestBlockGame
  * for inspiration on how to inherit BlockGame to create your own block game.
  */
object TestBlockGame:
  /** Create Game and start playing. */
  def main(args: Array[String]): Unit = (new RandomBlocks).play()

  /** A class extending `introprog.BlockGame`, see source code. */
  class RandomBlocks extends introprog.BlockGame:

    sealed trait State
    case object Starting extends State
    case object Playing  extends State
    case object GameOver extends State

    var state: State = Starting
    var isDrawingRandomBlocks: Boolean = false

    def showEnterMessage(): Unit =
      drawTextInMessageArea("Press Enter to toggle random blocks.", 0,0)

    def showEscapeMessage(): Unit =
      drawTextInMessageArea("Press Esc to clear window.", 25, 0)

    override def onKeyDown(key: String): Unit =
      print(s" Key down: $key")
      key match
        case "Esc" =>
          clearWindow()
          drawCenteredText("ESCAPED TO BLACK SPACE!")
          showEnterMessage()
        case "Enter"  =>
          isDrawingRandomBlocks = !isDrawingRandomBlocks
          showEnterMessage()
          showEscapeMessage()
        case _ =>

    override def onKeyUp(key: String): Unit = print(s" Key up: $key")

    override def onMouseDown(pos: (Int, Int)): Unit = print(s" Mouse down: $pos")

    override def onMouseUp(pos: (Int, Int)): Unit = print(s" Mouse up: $pos")

    override def onClose(): Unit =
      print(" Window Closed.")
      state = GameOver
    override def gameLoopAction(): Unit =
      import scala.util.Random.nextInt
      def rndPos: (Int, Int) = (nextInt(dim._1), nextInt(dim._2))
      def rndColor = new java.awt.Color(nextInt(256), nextInt(256), nextInt(256))
      print(".")
      if isDrawingRandomBlocks then
        drawBlock(rndPos._1, rndPos._2, rndColor)

    def play(): Unit =
      state = Playing
      println(s"framesPerSecond == $framesPerSecond")
      showEnterMessage()
      gameLoop(stopWhen = state == GameOver)
      println("Goodbye!")
