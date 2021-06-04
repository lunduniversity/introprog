class PolygonVector:
  type Pt = (Int, Int)
  private var points = Vector.empty[Pt]  // note var declaration to allow mutation
  def size = points.size

  def draw(w: PolygonWindow): Unit = w.draw(points.take(size))

  def append(pts: Pt*): Unit =
    points ++= pts.toVector

  def insert(pos: Int, pt: Pt): Unit = // exercise: change pt to varargs pts
    points = points.patch(pos, Vector(pt), 0)

  def remove(pos: Int): Unit = // exercise: change pos to fromPos, replaced
    points = points.patch(pos, Vector(), 1)

  override def toString = points.mkString("PrimitivePolygon(",",",")")
