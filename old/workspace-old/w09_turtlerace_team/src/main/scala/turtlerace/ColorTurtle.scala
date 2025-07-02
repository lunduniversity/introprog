package turtlerace

import graphics.{Turtle, Point}
import cslib.window.SimpleWindow
import java.awt.Color

class ColorTurtle(
  override val window: SimpleWindow,
  override val initPosition: Point = Point(0.0, 0.0),
  override val initDirection: Double = 0.0,
  val initColor: Color = Color.green
) extends Turtle(window, initPosition, initDirection, true) {
  var color = initColor

  /** Går length steg och ritar med color om isPenDown */
  override def forward(length: Double): ColorTurtle = ???
}
