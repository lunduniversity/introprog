package introprog.examples

/** Example of a simple PixelWindow app with an event loop that inspects key typing
  * and mouse clicking by the user. See source code for inspiration on how to use
  * PixelWindow for easy 2D game programming.
  */
object TestPixelWindow {
  import introprog.PixelWindow
  import introprog.PixelWindow.Event

  /** A reference to an instance of class `PixelWindow`. */
  val w = new PixelWindow(400, 400, "Hello PixelWindow!")

  /** The color used by `square`. */
  var color = java.awt.Color.red

  /** Draw a square with (`x`, `y`) as top left corner and size `side`. */
  def square(x: Int, y: Int, side: Int): Unit = {
    w.line(x, y,               x + side, y,        color)
    w.line(x + side, y,        x + side, y + side, color)
    w.line(x + side, y + side, x, y + side,        color)
    w.line(x, y + side,        x, y,               color)
  }

  /** Draw squares and start an event loop that prints events in terminal. */
  def main(args: Array[String]): Unit = {
    println("Key and mouse events are printed. Close window to exit.")
    w.drawText("HELLO WORLD! 012345ÅÄÖ", 0, 0)
    square(200, 100, 50)
    w.fill(x = 50, y = 100, width = 50, height = 50, color = java.awt.Color.blue)
    color = java.awt.Color.orange
    square(50,100, 50)
    color = java.awt.Color.green
    square(150,200, 50)
    w.line(0,0,w.width,w.height)

    while (w.lastEventType != Event.WindowClosed) {
      w.awaitEvent(10)  // wait for next event for max 10 milliseconds

      if (w.lastEventType != Event.Undefined) {
        println(s"lastEventType: ${w.lastEventType} => ${Event.show(w.lastEventType)}")
      }

      w.lastEventType match {
        case Event.KeyPressed    => println("lastKey == " + w.lastKey)
        case Event.KeyReleased   => println("lastKey == " + w.lastKey)
        case Event.MousePressed  => println("lastMousePos == " + w.lastMousePos)
        case Event.MouseReleased => println("lastMousePos == " + w.lastMousePos)
        case Event.WindowClosed  => println("Goodbye!"); System.exit(0)
        case _ =>
      }

      Thread.sleep(100) // wait for 0.1 seconds
    }
  }
}
