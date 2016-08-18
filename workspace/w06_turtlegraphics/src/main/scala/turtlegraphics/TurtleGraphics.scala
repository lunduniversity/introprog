package turtlegraphics

import java.awt.Color
import cslib.window.SimpleWindow


/** Represents a single Point in the x,y plane. */
case class Point(val x: Double, val y: Double) {
  /** Returns a new Point which has been moved some number of pixels */
  def translate(dx: Double, dy: Double) = ???
}

/** A Kojo-like Turtle class that can be used to draw shapes in a SimpleWindow.
  *
  * @param window    The window the turtle should be placed in.
  * @param position  A Point representing the turtle's starting coordinates.
  * @param angle     The angle between the turtle direction and the X-axis measured in degrees.
  *                   Positive degrees indicate a counter clockwise rotation.
  * @param isPenDown A boolean representing the turtle's pen position. True if the pen is down. */
class Turtle(window: SimpleWindow,
             private var position: Point,
             private var angle: Double,
             private var isPenDown: Boolean) {

  /** Gets the Turtle's current pixel position on the x axis */
  def x: Int = ???

  /** Gets the Turtle's current pixel position on the y axis (measured from the top left) */
  def y: Int = ???

  /** Moves the turtle to a new position without drawing a line. */
  def jumpTo(newPosition: Point) = ???

  /** Moves the turtle forward in its current direction, drawing a line if the pen is down.
    *
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

  /** Sets the turtle's pen down, causing it to draw lines when the turtle moves. */
  def penDown(): Unit = ???

  /** Lifts the turtle's pen, preventing it from drawing lines, even when it moves. */
  def penUp(): Unit = ???
}

/** Immutable class representing a rectangle.
  * @param position a Point representing the upper left corner of the rectangle (before rotation)
  * @param width    the width of the rectangle
  * @param height   the height of the rectangle
  * @param angle    the angle of the rectangle (rotated around the upper left corner)
  *                 Positive degrees indicate a counter clockwise rotation measured from the X-axis
  */
case class Rectangle(
                      position: Point, width: Double, height: Double, angle: Double) {
  /** Draws the rectangle using a turtle */
  def draw(turtle: Turtle): Unit = ???

  /** Returns a new Rectangle that is rotated to the left */
  def rotateLeft(degrees: Double): Rectangle = ???

  /** Returns a new Rectangle that is rotated to the right */
  def rotateRight(degrees: Double): Rectangle = ???

  /** Returns a new Rectangle that has been scaled by a size factor */
  def scale(factor: Double): Rectangle = ???

  /** Returns a new Rectangle that has been moved some number of pixels */
  def translate(dx: Double, dy: Double): Rectangle = ???
}

/** Represents a sequence of Rectangles that have been translated, rotated, and scaled down.
  *
  * @param rectangle        the rectangle to use as a base for the composite shape
  * @param count            the number of rectangles to draw
  * @param startAngle       the number of degrees to rotate the image
  *                         Positive degrees indicate a counter clockwise rotation measured from the X-axis
  * @param step             the number of pixels to move each rectangle (in the direction of startAngle)
  * @param rotationStep     the number of degrees to shift each rectangle with each roll
  * @param scaleStep        the scale factor to use in each step
  */
case class RectangleSequence(rectangle: Rectangle,
                             count: Int,
                             startAngle: Double,
                             step: Double,
                             rotationStep: Double,
                             scaleStep: Double) {

  /** Draws the image using a given Turtle */
  def draw(turtle: Turtle): Unit = ???

  /** Returns a new RectangleSequence that has been scaled with a size factor */
  def scale(factor: Double): RectangleSequence = ???

  /** Returns a new RectangleSequence that has been rotated to the left */
  def rotateLeft(degrees: Double): RectangleSequence = ???

  /** Returns a new RectangleSequence that has been rotated to the right */
  def rotateRight(degrees: Double): RectangleSequence = ???

  /** Returns a new RectangleSequence that has been moved some number of pixels. */
  def translate(dx: Double, dy: Double): RectangleSequence = ???
}

object Main {
  def main(args: Array[String]): Unit = ???
}
