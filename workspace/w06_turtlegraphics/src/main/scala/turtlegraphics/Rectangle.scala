package turtlegraphics

/** Immutable class representing a rectangle.
  * @param position a Point representing the upper left corner of the rectangle
  *                 (before rotation)
  * @param width    the width of the rectangle
  * @param height   the height of the rectangle
  * @param angle    the angle of the rectangle (rotated around the upper
  *                 left corner) Positive degrees indicate a counter clockwise
  *                 rotation measured from the X-axis
  */
case class Rectangle(position: Point,
                     width: Double,
                     height: Double,
                     angle: Double) {
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

