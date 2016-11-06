trait Movable {
  def move(dx: Double, dy: Double): Movable
}

case class Point(x: Double = 0.0, y: Double = 0.0) extends Movable {
  def distanceTo(other: Point): Double = math.hypot(x - other.x, y - other.y)
  def move(dx: Double, dy: Double): Point = Point(x + dx, y + dy)
}

case class Polygon(pts: Vector[Point]) extends Movable {
  def append(p: Point): Polygon = Polygon(pts :+ p)
  def move(dx: Double, dy: Double): Polygon = Polygon(pts.map(_.move(dx, dy)))
}
object Polygon {
  def apply(pts: Point*): Polygon = new Polygon(pts.toVector)
}
