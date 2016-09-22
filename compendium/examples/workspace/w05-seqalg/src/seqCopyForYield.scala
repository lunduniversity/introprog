object seqCopyForYield {

  def arrayCopy(xs: Array[Int]): Array[Int] = {
    val result = for (i <- xs.indices) yield xs(i)
    result.toArray
  }

  def test: String = {
    val xs = Array(1,2,3,4,42)
    val ys = arrayCopy(xs)
    if (xs sameElements ys) "OK!" else "ERROR!"
  }

  def main(args: Array[String]): Unit = println(test)
}
