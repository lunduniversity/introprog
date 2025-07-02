package turtlerace

import cslib.window.SimpleWindow

class RaceWindow(nbrOfStartPositions: Int = 8)
extends SimpleWindow(800, 400, "TurtleRace") {
  val startX = 100
  val endX = 700
  def startY(n: Int): Int = 100 + 20 * n

  def draw(): Unit = {
    setLineWidth(1)
    moveTo(startX, 100); lineTo(startX, 280)
    moveTo(endX, 100);   lineTo(endX, 280)
    for(i <- 1 to nbrOfStartPositions) {
      moveTo(startX + 10,98 + 20 * i)
      writeText(i.toString)
    }
    drawStartField(startX+5)
    drawStartField(endX+45)
  }

  def drawStartField(x: Int): Unit = {
    setLineWidth(10)
    for (i <- 1 to 4) {
      moveTo(x - 10 * i, 100)
      for (j <- 0 to nbrOfStartPositions) {
        if (i % 2 == 0) {
          moveTo(x - 10 * i, 110 + 20 * j)
          lineTo(x - 10 * i, 120 + 20 * j)
        } else {
          lineTo(x - 10 * i, 110 + 20 * j)
          moveTo(x - 10 * i, 120 + 20 * j)
        }
      }
    }
    setLineWidth(1)
  }

  def writeRacerList(racers: Seq[RaceTurtle], x: Int, heading: String) = {
    moveTo(x, 290); writeText(heading)
    for(i <- racers.indices) {
      moveTo(x, 300 + i * 10); writeText(s"${i + 1}. ${racers(i)}")
    }
  }

  def writeTitle(title: String): Unit = {
    moveTo(100, 92); writeText(title)
  }
}
