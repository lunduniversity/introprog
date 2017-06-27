package graphics

/** A sequence of stepwise translated, rotated, and scaled down rectangles.
  *
  * @param rect        the start rectangle used as base for the drawing
  * @param count       the number of rectangles to draw
  * @param angle       the rectangle start angle (counter clockwise from X-axis)
  * @param deltaMove   the distance to move in each step in the direction of angle
  * @param deltaAngle  the number of degrees to rotate in each step
  * @param scaleFactor the scale factor to use when re-scaling in each step
  */
case class RectSeq(rect: Rect,
                   count: Int,
                   angle: Double,
                   deltaMove: Double,
                   deltaAngle: Double,
                   scaleFactor: Double) {

  /** Draws an image of this rectangle sequence using a given turtle. */
  def draw(turtle: Turtle): RectSeq = ???

  /** A new rectangle sequence scaled with a size factor. */
  def scaled(factor: Double): RectSeq = ???

  /** A new rectangle sequence rotated to the left. */
  def rotatedLeft(degrees: Double): RectSeq = ???

  /** A new rectangle sequence rotated to the right. */
  def rotatedRight(degrees: Double): RectSeq = ???

  /** A new rectangle sequence with rect moved by (dx, dy). */
  def translated(dx: Double, dy: Double): RectSeq = ???
}
