package turtlegraphics

import cslib.window.SimpleWindow

/** A Kojo-like Turtle class that can be used to draw shapes in a SimpleWindow.
  *
  * @param window    The window the turtle should be placed in.
  * @param position  A Point representing the turtle's starting coordinates.
  * @param angle     The angle between the turtle direction and the X-axis
  *                  measured in degrees. Positive degrees indicate a counter
  *                  clockwise rotation.
  * @param isPenDown A boolean representing the turtle's pen position. True if
  *                  the pen is down. */
class Turtle(window: SimpleWindow,
             private var position: Point,
             private var angle: Double,
             private var isPenDown: Boolean) {

  /** Gets the Turtle's current pixel position on the x axis */
  def x: Int = ???

  /** Gets the Turtle's current pixel position on the y axis
    * (measured from the top left) */
  def y: Int = ???

  /** Moves the turtle to a new position without drawing a line. */
  def jumpTo(newPosition: Point) = ???

  /** Moves the turtle forward in its current direction, drawing a line if
    * the pen is down.
    * @param length The number of pixels to move forward. */
  def forward(length: Double): Unit = ???

  /** Turns the turtle to the left.
    *
    * @param turnAngle The number of degrees to turn. */
  def turnLeft(turnAngle: Double): Unit = ???

  /** Turns the turtle to the right.
    *
    * @param turnAngle The number of degrees to turn. */
  def turnRight(turnAngle: Double): Unit = ???

  /** Turns the turtle straight up. */
  def turnNorth(): Unit = ???

  /** Sets the turtle's pen down, causing it to draw lines when it moves */
  def penDown(): Unit = ???

  /** Lifts the turtle's pen, preventing it from drawing lines. */
  def penUp(): Unit = ???
}

