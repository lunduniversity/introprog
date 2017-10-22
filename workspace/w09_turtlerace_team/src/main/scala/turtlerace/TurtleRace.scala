package turtlerace

import graphics.Turtle
import cslib.window._

object TurtleRace {
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
