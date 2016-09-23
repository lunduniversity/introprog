object polygonTest2 {
  def main(args: Array[String]): Unit = {
    val pw = new PolygonWindow(200,200)
    val pts = Array((50,50), (100,100), (50,100), (30,50))
    pw.draw(pts)

    val morePts = pointSeqUtils.primitiveInsertCopy(pts, 2, (90,130))
    //val morePts = pointSeqUtils.insertCopy(pts, 2, (90,130))
    pw.draw(morePts)

    val lessPts = pointSeqUtils.primitiveRemoveCopy(morePts, morePts.length - 1)
    //val lessPts = pointSeqUtils.removeCopy(morePts, morePts.length - 1)
    pw.draw(lessPts)

  }
}
