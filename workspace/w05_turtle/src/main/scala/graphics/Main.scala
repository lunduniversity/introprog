package graphics

import java.awt.Color
import cslib.window.SimpleWindow

object Main {

  def drawSquares(t: Turtle): Unit = {
    def square = (1 to 4) foreach { ??? }
    def init   = ???
    def move   = ???
    init; square; move; square
  }

  def animateRectangles(t: Turtle): Unit = {
    val start = Point(t.window.getWidth / 2, t.window.getHeight / 2)
    var r1 = Rect(start, 100, 50, 0)
    var r2 = Rect(start, 80, 40, 0)
    var r3 = Rect(start, 90, 30, 0)

    for (i <- 1 to 100) {
      t.window.clear(); t.window.setLineColor(new Color(255, 0, 0))
      r1.draw(t)      ; t.window.setLineColor(new Color(0, 125, 0))
      r2.draw(t)      ; t.window.setLineColor(new Color(0, 0, 255))
      r3.draw(t)
      r1 = ???
      r2 = ???
      r3 = ???
      SimpleWindow.delay(40)
    }
  }

  def main(args: Array[String]): Unit = {
    val t = new Turtle(new SimpleWindow(500, 500, "Graphics: click for next"))
    def waitAndClear = { t.window.waitForMouseClick() ; t.window.clear }
    drawSquares(t)       ; waitAndClear
//  animateRectangles(t) ; waitAndClear
  }
}
