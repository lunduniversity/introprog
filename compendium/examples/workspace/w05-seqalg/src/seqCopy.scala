object seqCopy {
  def arrayCopy(xs: Array[Int]): Array[Int] = {
    val result = new Array[Int](xs.size)
    var i = 0
    while (i < xs.size) {
      result(i) = xs(i)
      i += 1
    }
    result
  }

  def test(copy: Array[Int] => Array[Int]): String = {
    val xs = Array(1,2,3,4,42)
    val ys = copy(xs)
    if (xs.sameElements(ys)) "OK!" else "ERROR!"
  }

  def main(args: Array[String]): Unit = {
    println(test(arrayCopy))
  }
}