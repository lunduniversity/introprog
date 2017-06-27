package graphics

case class Point(x: Double, y: Double) {
  def +(p: Point): Point = ???
  def negY: Point        = ???
  val r: Double          = ???
  val theta: Double      = ???
}
object Point {
  /** A new Point from polar coordinates, r is length, theta in radians. */
  def polar(r: Double, theta: Double): Point = ???
}
