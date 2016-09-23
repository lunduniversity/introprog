class PolygonWindow(width: Int, height: Int) {
  val w = new cslib.window.SimpleWindow(width, height, "PolyWin")

  def draw(pts: Array[(Int, Int)]): Unit = if (pts.size > 0) {
    w.moveTo(pts(0)._1, pts(0)._2)
    for (i <- 1 until pts.length) w.lineTo(pts(i)._1, pts(i)._2)
    w.lineTo(pts(0)._1, pts(0)._2)
  }
}