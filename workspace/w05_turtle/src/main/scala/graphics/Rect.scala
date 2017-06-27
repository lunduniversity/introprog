package graphics

case class Rect(position: Point,
                width: Double,
                height: Double,
                angle: Double) {

  /** Draws this rectangle starting from position. */
  def draw(turtle: Turtle): Unit = ???

  /** Draw this rectangle centered around the position. */
  def drawCentered(turtle: Turtle): Unit = ???

  /** A new rectangle rotated to the left. */
  def rotatedLeft(degrees: Double): Rect = ???

  /** A new rectangle rotated to the right. */
  def rotatedRight(degrees: Double): Rect = ???

  /** A new rectangle with its size relatively scaled by a factor. */
  def scaled(factor: Double): Rect = ???

  /** Returns a new rectangle moved (dx, dy) relative to its position. */
  def translated(dx: Double, dy: Double): Rect = ???
}
