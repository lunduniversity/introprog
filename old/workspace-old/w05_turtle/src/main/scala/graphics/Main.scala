package graphics

import cslib.window.SimpleWindow

object Main {
  def drawLine(p1: Point, p2: Point, w: SimpleWindow): Unit = {
    w.moveTo(p1.x.round.toInt, p1.y.round.toInt)
    w.lineTo(p2.x.round.toInt, p2.y.round.toInt)
  }

  def main(args: Array[String]): Unit = {
    val w = new SimpleWindow(500, 500, "Graphics")
    drawLine(Point(10.2,10.7), Point(300,300), w)
  }
}
