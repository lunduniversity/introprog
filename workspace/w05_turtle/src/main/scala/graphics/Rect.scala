package graphics

case class Rect(pos:    Point,
                width:  Double,
                height: Double,
                angle:  Double = 0.0) {

  /** Draws this rectangle starting from position. */
  def draw(turtle: Turtle): Rect = ???

  /** A new rectangle rotated to the left. */
  def rotatedLeft(degrees: Double): Rect = ???

  /** A new rectangle rotated to the right. */
  def rotatedRight(degrees: Double): Rect = ???

  /** A new rectangle with its size relatively scaled by a factor. */
  def scaled(factor: Double): Rect = ???

  /** Returns a new rectangle moved (dx, dy) relative to its position. */
  def translated(dx: Double, dy: Double): Rect = ???
}
