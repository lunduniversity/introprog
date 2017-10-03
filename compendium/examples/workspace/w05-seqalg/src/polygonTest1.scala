object polygonTest1 {
  def main(args: Array[String]): Unit = {
    val pw = new PolygonWindow(200,200)
    val pts = Array((0,0), (100,100), (50,100), (30,50))
    pw.draw(pts)
  }
}
