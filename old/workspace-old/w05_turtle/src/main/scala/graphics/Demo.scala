package graphics

import java.awt.Color
import cslib.window.SimpleWindow

object Demo {

  def drawSquares(t: Turtle): Unit = {
    def square = (1 to 4) foreach { _ => t.turnRight(90).forward(100) }
    def init = t.jumpTo(Point(100, 200)).turnNorth.penDown
    def move = t.penUp.turnRight(90).forward(200).turnNorth.penDown
    init; square; move; square
  }

  def animateRectangles(t: Turtle): Unit = {
    val pos = Point(t.window.getWidth / 2, t.window.getHeight / 2)
    var r1 = Rect(pos, width = 100, height = 50)
    var r2 = Rect(pos, width = 80,  height = 40)
    var r3 = Rect(pos, width = 90,  height = 30)

    for (i <- 1 to 100) {
      t.window.clear(); t.window.setLineColor(new Color(255, 0, 0))
      r1.draw(t)      ; t.window.setLineColor(new Color(0, 125, 0))
      r2.draw(t)      ; t.window.setLineColor(new Color(0, 0, 255))
      r3.draw(t)
      r1 = r1.rotatedLeft(15).translated(0, 0.2).scaled(1.003)
      r2 = r2.rotatedLeft(-20).scaled(0.999)
      r3 = r3.rotatedLeft(-10).scaled(1.005)
      SimpleWindow.delay(40)
    }
  }

  def main(args: Array[String]): Unit = {
    val w = new SimpleWindow(500, 500, "Graphics: click for next")
    def waitAndClear = { w.waitForMouseClick() ; w.clear }
    val t = new Turtle(w)
    drawSquares(t)       ; waitAndClear
    animateRectangles(t) ; waitAndClear
    sys.exit(0)
  }
}
