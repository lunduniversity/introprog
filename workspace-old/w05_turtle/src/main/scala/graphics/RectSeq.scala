package graphics

/** A sequence of stepwise transformed rectangles.
  *
  * @param rect   start rectangle used as base for the drawing
  * @param n      number of rect transformation steps when calling draw
  * @param dist   distance to move rect in each step in direction of rect.angle
  * @param rot    degrees to left rotate rect in each step
  * @param scale  scale factor used in each step when re-scaling rect
  */
case class RectSeq(rect:  Rect,
                   n:     Int,
                   dist:  Double = 0.0,
                   rot:   Double = 0.0,
                   scale: Double = 1.0) {

  /** Draws an image of this rectangle sequence using a given turtle. */
  def draw(turtle: Turtle): RectSeq =  ???

  /** A new rectangle sequence scaled with a size factor. */
  def scaled(factor: Double): RectSeq = ???

  /** A new rectangle sequence rotated to the left. */
  def rotatedLeft(degrees: Double): RectSeq = ???

  /** A new rectangle sequence rotated to the right. */
  def rotatedRight(degrees: Double): RectSeq = ???

  /** A new rectangle sequence with rect moved by (dx, dy). */
  def translated(dx: Double, dy: Double): RectSeq = ???
}
