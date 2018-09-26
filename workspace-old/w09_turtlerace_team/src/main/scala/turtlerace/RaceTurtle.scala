package turtlerace

import graphics.Point
import cslib.window.SimpleWindow
import java.awt.Color

class RaceTurtle(
  override val window: RaceWindow,
  var nbr: Int,
  val name: String,
  override val initColor: Color
) extends ColorTurtle(
    window,
    initPosition = Point(window.startX, window.startY(nbr)),
    initDirection = 0.0,
    initColor
) {
  val defaultStep = 6

  /** Tar ett steg av slumpmässig längd i Range(0,defaultStep) */
  def raceStep(): Unit = ???

  /** Återgår till initPosition */
  def restart(): Unit  = ???

  override def toString: String = s"#$nbr name: $name"
}
