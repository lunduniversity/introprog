//> using lib "se.lth.cs::introprog::1.3.1"

package blockbattle

class BlockWindow(
  val nbrOfBlocks: (Int, Int),
  val title: String = "BLOCK WINDOW",
  val blockSize: Int = 14
):
  import introprog.PixelWindow

  val pixelWindow = new PixelWindow(
    nbrOfBlocks._1 * blockSize, nbrOfBlocks._2 * blockSize, title)

  def setBlock(pos: Pos, color: java.awt.Color): Unit = ???

  def getBlock(pos: Pos): java.awt.Color = ???

  def write(
    text: String,
    pos: Pos,
    color: java.awt.Color,
    textSize: Int = blockSize
  ): Unit = pixelWindow.drawText(
      text, pos.x * blockSize, pos.y * blockSize, color, textSize)

  def nextEvent(maxWaitMillis: Int = 10): BlockWindow.Event.EventType  =
    import BlockWindow.Event._
    pixelWindow.awaitEvent(maxWaitMillis)
    pixelWindow.lastEventType match
      case PixelWindow.Event.KeyPressed   => KeyPressed(pixelWindow.lastKey)
      case PixelWindow.Event.WindowClosed => WindowClosed
      case _                              => Undefined

object BlockWindow:
  def delay(millis: Int): Unit = Thread.sleep(millis)

  object Event:
    trait EventType
    case class  KeyPressed(key: String) extends EventType
    case object WindowClosed            extends EventType
    case object Undefined               extends EventType
