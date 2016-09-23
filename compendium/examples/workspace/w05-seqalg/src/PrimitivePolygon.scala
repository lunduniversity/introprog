class PrimitivePolygon(val maxSize: Int) {
  type Pt = (Int, Int)
  private val pts = new Array[Pt](maxSize)
  private var n = 0
  def size: Int = n

  def draw(w: PolygonWindow): Unit = w.draw(pts)

  def append(pt: Pt): Unit = {
    pts(n) = pt
    n += 1
  }

  def insert(pos: Int, pt: Pt): Unit = {
    for (i <- n until pos by -1) pts(i) = pts(i - 1)
    pts(pos) = pt
    n += 1
  }

  def remove(pos: Int, pt: Pt): Unit = {
    for (i <- pos until n) pts(i) = pts(i + 1)
    n -= 1
  }
}
