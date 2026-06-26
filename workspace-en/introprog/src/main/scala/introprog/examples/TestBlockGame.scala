package introprog.examples

/** Examples of a simple BlockGame app with overridden callbacks to handle events
  * See the documentation of BlockGame and the source code of TestBlockGame
  * for inspiration on how to inherit BlockGame to create your own block game.
  */
object TestBlockGame:
  /** Create Game and start playing. */
  def main(args: Array[String]): Unit = 
    println("Press Enter to toggle random blocks. Close window to continue.")
    (new RandomBlocks).play()
    println("Opening MovingBlock. Press Ctrl+C to exit.")
    (new MovingBlock).start()
    println("MovingBlock has ended.")

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

  end RandomBlocks

  class MovingBlock extends introprog.BlockGame(
    title                 = "MovingBlock",
    dim                   = (10,5),
    blockSize             = 40,
    background            = java.awt.Color.BLACK,
    framesPerSecond       = 50,
    messageAreaHeight     = 1,
    messageAreaBackground =  java.awt.Color.DARK_GRAY
  ):

    var movesPerSecond: Double = 2

    def millisBetweenMoves: Int = (1000 / movesPerSecond).round.toInt max 1

    var _timestampLastMove: Long = System.currentTimeMillis

    def timestampLastMove = _timestampLastMove

    var x = 0

    var y = 0

    def move(): Unit = 
      if x == dim._1 - 1 then 
          x = -1
          y += 1 
      end if
      x = x+1

    def erase(): Unit = drawBlock(x, y, java.awt.Color.BLACK)
    
    def draw(): Unit = drawBlock(x, y, java.awt.Color.CYAN)

    def update(): Unit  =
      if System.currentTimeMillis > _timestampLastMove + millisBetweenMoves then
        move()
        _timestampLastMove = System.currentTimeMillis()
    
    var loopCounter: Int = 0

    override def gameLoopAction(): Unit = 
        erase()
        update()
        draw()
        clearMessageArea()
        drawTextInMessageArea(s"Loop number: $loopCounter", 1, 0, java.awt.Color.PINK, size = 30)
        loopCounter += 1

    final def start(): Unit = 
        pixelWindow.show()  // show window again if closed and start() is called again
        gameLoop(stopWhen = x == dim._1 - 1 && y == dim._2 - 1)

  end MovingBlock
