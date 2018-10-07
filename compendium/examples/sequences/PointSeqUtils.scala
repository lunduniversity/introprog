object PointSeqUtils {
  type Pt = (Int, Int)  // a type alias to make the code more concise

  def primitiveInsertCopy(pts: Array[Pt], pos: Int, pt: Pt): Array[Pt] = {
    val result = new Array[Pt](pts.length + 1)  // initialized with null
    for (i <- 0 until pos) result(i) = pts(i)
    result(pos) = pt
    for (i <- pos + 1 to pts.length) result(i) = pts(i - 1)
    result
  }

  def primitiveRemoveCopy(pts: Array[Pt], pos: Int): Array[Pt] =
    if (pts.length > 0) {
      val result = new Array[Pt](pts.length - 1) // initialized with null
      for (i <- 0 until pos) result(i) = pts(i)
      for (i <- pos + 1 until pts.length) result(i - 1) = pts(i)
      result
    } else Array.empty

  // ovan metoder implementerade med hjälp av den kraftfulla metoden patch:

  def insertCopy(pts: Array[Pt], pos: Int, pt: Pt) = pts.patch(pos, Array(pt), 0)

  def removeCopy(pts: Array[Pt], pos: Int) = pts.patch(pos, Array.empty[Pt], 1)
}
