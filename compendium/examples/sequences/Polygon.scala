object Polygon:
  type Pt = (Int, Int)
  type Pts = Vector[Pt]
  def apply(pts: Pt*) = new Polygon(pts.toVector)

case class Polygon(points: Polygon.Pts):
  import Polygon.Pt

  def size = points.size // for convenience but not really necessary (why?)

  def append(pts: Pt*): Polygon = copy(points ++ pts.toVector)

  def insert(pos: Int, pts: Pt*): Polygon = copy(points.patch(pos, pts, 0))

  def remove(pos: Int, replaced: Int = 1): Polygon =
    copy(points.patch(pos, Seq(), replaced))

  override def toString = points.mkString("Polygon(", "," ,")")
