package introprog

import java.awt.Color

/** A class for creating games with block-based graphics. 
 * See example usage in [introprog.examples.TestBlockGame](https://github.com/lunduniversity/introprog-scalalib/blob/master/src/main/scala/introprog/examples/TestBlockGame.scala#L7)
 * 
 * @constructor Create a new game.
 * @param title the title of the window
 * @param dim the (width, height) of the window in number of blocks
 * @param blockSize the side of each square block in pixels
 * @param background the color used when clearing pixels
 * @param framesPerSecond the desired update rate in the gameLoop
 * @param messageAreaHeight the height in pixels of the message area
 * @param messageAreaBackground the color of the message area background
 */
abstract class BlockGame(
  val title: String = "BlockGame",
  val dim: (Int, Int) = (50, 50),
  val blockSize: Int = 15,
  val background: Color = Color.black,
  var framesPerSecond: Int = 50,
  val messageAreaHeight: Int = 2,
  val messageAreaBackground: Color = Color.gray.darker.darker
):
  import introprog.PixelWindow

  /** Called when a key is pressed. Override if you want non-empty action.
    * @param key is a string representation of the pressed key
    */
  def onKeyDown(key: String): Unit = ()

  /** Called when a key is released. Override if you want non-empty action.
    * @param key is a string representation of the released key
    */
  def onKeyUp(key: String): Unit = ()

  /** Called when mouse is pressed. Override if you want non-empty action.
    * @param pos the mouse position in underlying `pixelWindow` coordinates
    */
  def onMouseDown(pos: (Int, Int)): Unit = ()

  /** Called when mouse is released. Override if you want non-empty action.
    * @param pos the mouse position in underlying `pixelWindow` coordinates
    */
  def onMouseUp(pos: (Int, Int)): Unit = ()

  /** Called when window is closed. Override if you want non-empty action. */
  def onClose(): Unit = ()

  /** Called in each `gameLoop` iteration. Override if you want non-empty action. */
  def gameLoopAction(): Unit = ()

  /** Called if no time is left in iteration to keep frame rate.
    * Default action is to print a warning message.
    */
  def onFrameTimeOverrun(elapsedMillis: Long): Unit =
    println(s"Warning: Unable to handle $framesPerSecond fps. Loop time: $elapsedMillis ms")

  /** Returns the gameLoop delay in ms implied by `framesPerSecond`.*/
  def gameLoopDelayMillis: Int = (1000.0 / framesPerSecond).round.toInt

  /** The underlying window used for drawing blocks and messages. */
  protected val pixelWindow: PixelWindow =
    new PixelWindow(
      width = dim._1 * blockSize, 
      height = (dim._2  + messageAreaHeight) * blockSize + blockSize / 2, 
      title, 
      background
    )

  /** Internal buffer with block colors. */
  private val blockBuffer: Array[Array[Color]] =  Array.fill(dim._1, dim._2)(background)

  /** Internal buffer with update flags. */
  private val isBufferUpdated: Array[Array[Boolean]] =  Array.fill(dim._1, dim._2)(false)

  /** Internal buffer for post-update actions. */
  private val toDoAfterBlockUpdates = collection.mutable.Buffer.empty[() => Unit]

  /** Max time for awaiting events from underlying window in ms. */
  protected val MaxWaitForEventMillis = 1

  clearWindow()  // erase all blocks

  /** The game loop that continues while not `stopWhen` is true.
    * It draws only updated blocks aiming at the desired frame rate.
    * It calls each `onXXX` method if a corresponding event is detected.
    * Use the call-by-name `stopWhen` to pass a condition that ends the loop if false.
    * See example usage in `introprog.examples.TestBlockGame`.
    */
  protected def gameLoop(stopWhen: => Boolean): Unit = while !stopWhen do
    import PixelWindow.Event
    val t0 = System.currentTimeMillis
    pixelWindow.awaitEvent(MaxWaitForEventMillis.toLong)
    while pixelWindow.lastEventType != PixelWindow.Event.Undefined do
      pixelWindow.lastEventType match
        case Event.KeyPressed    => onKeyDown(pixelWindow.lastKey)
        case Event.KeyReleased   => onKeyUp(pixelWindow.lastKey)
        case Event.WindowClosed  => onClose()
        case Event.MousePressed  => onMouseDown(pixelWindow.lastMousePos)
        case Event.MouseReleased => onMouseUp(pixelWindow.lastMousePos)
        case _ =>
      pixelWindow.awaitEvent(1)
    gameLoopAction()
    drawUpdatedBlocks()
    val elapsed = System.currentTimeMillis - t0
    if (gameLoopDelayMillis - elapsed) < MaxWaitForEventMillis then
      onFrameTimeOverrun(elapsed)
    Thread.sleep((gameLoopDelayMillis - elapsed) max 0)

  /** Draw updated blocks and carry out post-update actions if any. */
  private def drawUpdatedBlocks(): Unit =
    for x <- blockBuffer.indices do
      for y <- blockBuffer(x).indices do
        if isBufferUpdated(x)(y) then
          val pwx = x * blockSize
          val pwy = y * blockSize
          pixelWindow.fill(pwx, pwy, blockSize, blockSize, blockBuffer(x)(y))
          isBufferUpdated(x)(y) = false
    toDoAfterBlockUpdates.foreach(_.apply())
    toDoAfterBlockUpdates.clear()

  /** Erase all blocks to background color. */
  def clearWindow(): Unit =
    pixelWindow.clear()
    clearMessageArea()
    for x <- blockBuffer.indices do
      for y <- blockBuffer(x).indices do
          blockBuffer(x)(y) = background

  /** Paint a block in color `c` at (`x`,`y`) in block coordinates. */
  def drawBlock(x: Int, y: Int, c: Color): Unit =
    if blockBuffer(x)(y) != c then
      blockBuffer(x)(y) = c
      isBufferUpdated(x)(y) = true

  /** Erase the block at (`x`,`y`) to `background` color. */
  def eraseBlock(x: Int, y: Int): Unit =
    if blockBuffer(x)(y) != background then
      blockBuffer(x)(y) = background
      isBufferUpdated(x)(y) = true

  /** Write `msg` in `color` in the middle of the window.
    * The drawing is postponed until the end of the current game loop
    * iteration and thus the text drawn on top of any updated blocks.
    */
  def drawCenteredText(msg: String, color: Color = pixelWindow.foreground, size: Int = blockSize): Unit =
    toDoAfterBlockUpdates.append( () =>
      pixelWindow.drawText(
        msg,
        (pixelWindow.width / 2 - msg.length * size / 3) max size,
        pixelWindow.height / 2 - size, color,
        size
      )
    )

 /** Write `msg` in `color` in the message area at ('x','y') in block coordinates. */
  def drawTextInMessageArea(msg: String, x: Int, y: Int, color: Color = pixelWindow.foreground, size: Int = blockSize): Unit =
    require(y < messageAreaHeight && y >= 0, s"not in message area: y = $y")
    require(x < dim._1 * blockSize && x >= 0, s"not in message area: x = $x")
    pixelWindow.drawText(msg, x * blockSize, (y + dim._2) * blockSize, color, size)

  /** Clear a rectangle in the message area in block coordinates. */
  def clearMessageArea(x: Int = 0, y: Int = 0, width: Int = dim._1, height: Int = messageAreaHeight): Unit =
    require(y < messageAreaHeight && y >= 0, s"not in message area: y = $y")
    require(x < dim._1 * blockSize && x >= 0, s"not in message area: x = $x")
    pixelWindow.fill(
      x * blockSize,     (y + dim._2) * blockSize,
      width * blockSize, messageAreaHeight * blockSize + blockSize / 2,
      messageAreaBackground
    )
