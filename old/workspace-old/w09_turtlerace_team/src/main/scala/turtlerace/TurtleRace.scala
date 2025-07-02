package turtlerace

import graphics.Turtle
import cslib.window._

class TurtleRace(val millisPerStep: Int = 5) {
  def race(
    turtles: Seq[RaceTurtle],
    rw: RaceWindow,
    title: String
  ): Seq[RaceTurtle] = {
    rw.writeTitle("TurtleRace NOT READY")
    rw.draw()
    Seq()
  }
}
