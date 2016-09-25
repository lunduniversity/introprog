object Polygon {
  type Pt = (Int, Int)
  type Pts = Vector[Pt]
  def apply() = new Polygon(Vector())
}

import Polygon.{Pt, Pts}

case class Polygon(points: Pts) {
  def size = points.size // for convenience but not strictly necessary (why?)

  def append(pts: Pt*) = copy(points ++ pts.toVector)

  def insert(pos: Int, pts: Pt*) = copy(points.patch(pos, pts, 0))

  def remove(pos: Int, replaced: Int = 1) = copy(points.patch(pos, Seq(), replaced))
}
