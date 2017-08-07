package graphics

import cslib.window.SimpleWindow

class Turtle(val window: SimpleWindow,
             val initPosition: Point = Point(0.0, 0.0),
             val initDirection: Double = 0.0,
             val initIsPenDown: Boolean = true) {

  /** The current x axis position rounded to nearest integer. */
  def x: Int = ???

  /** The current y axis position rounded to nearest integer. */
  def y: Int = ???

  /** The current direction in degrees. */
  def direction: Double = ???

  /** Revert to initial position, direction and pen state. */
  def home(): Turtle = ???

  /** The state of the pen: true if pen is down, false if pen is up. */
  def isPenDown: Boolean = ???

  /** Move to a new position without drawing a line. */
  def jumpTo(newPosition: Point): Turtle = ???

  /** Move distance in direction, drawing a line if pen is down. */
  def forward(distance: Double): Turtle = ???

  /** Turn the turtle counter-clockwise from current direction. */
  def turnLeft(degrees: Double): Turtle = ???

  /** Turn the turtle clockwise from current direction. */
  def turnRight(degrees: Double): Turtle = ???

  /** Turn the turtle straight up. */
  def turnNorth(): Turtle = ???

  /** Set the turtle's pen down. */
  def penDown(): Turtle = ???

  /** Lifts the turtle's pen. */
  def penUp(): Turtle = ???
}
