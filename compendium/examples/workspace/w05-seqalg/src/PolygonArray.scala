class PolygonArray(val maxSize: Int) {
  type Pt = (Int, Int)
  private val points = new Array[Pt](maxSize)  // initialized with null
  private var n = 0
  def size = n

  def draw(w: PolygonWindow): Unit = w.draw(points.take(n))

  def append(pts: Pt*): Unit = {
    for (i <- pts.indices) points(n + i) = pts(i)
    n += pts.length
  }

  def insert(pos: Int, pt: Pt): Unit = { // exercise: change pt to varargs pts
    for (i <- n until pos by -1) points(i) = points(i - 1)
    points(pos) = pt
    n += 1
  }

  def remove(pos: Int): Unit = { // exercise: change pos to fromPos, replaced
    for (i <- pos until n) points(i) = points(i + 1)
    n -= 1
  }

  override def toString = points.mkString("PrimitivePolygon(",",",")")
}
