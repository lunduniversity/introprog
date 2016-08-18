package turtlegraphics

/** Represents a sequence of Rectangles that have been translated, rotated,
  * and scaled down.
  *
  * @param rectangle    the rectangle to use as a base for the composite shape
  * @param count        the number of rectangles to draw
  * @param startAngle   the number of degrees to rotate the image. Positive
  *                     degrees indicate a counter clockwise rotation measured
  *                     from the X-axis
  * @param step         the number of pixels to move each rectangle
  *                     (in the direction of startAngle)
  * @param rotationStep the number of degrees to shift each rectangle with
  *                     each step of the animation
  * @param scaleStep    the scale factor to use in each step
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

  /** Returns a new RectangleSequence that has been moved a number of pixels */
  def translate(dx: Double, dy: Double): RectangleSequence = ???
}

