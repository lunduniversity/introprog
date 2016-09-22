object seqCopyFor {

  def arrayCopy(xs: Array[Int]): Array[Int] = {
    val result = new Array[Int](xs.length)
    for (i <- xs.indices) {
      result(i) = xs(i)
    }
    result
  }

  def test: String = {
    val xs = Array(1,2,3,4,42)
    val ys = arrayCopy(xs)
    if (xs sameElements ys) "OK!" else "ERROR!"
  }

  def main(args: Array[String]): Unit = println(test)
}
