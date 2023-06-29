object seqCopy:

  def arrayCopy(xs: Array[Int]): Array[Int] =
    val result = new Array[Int](xs.length)
    var i = 0
    while i < xs.length do
      result(i) = xs(i)
      i += 1
    result

  def test: String =
    val xs = Array(1,2,3,4,42)
    val ys = arrayCopy(xs)
    if xs sameElements ys then "OK!" else "ERROR!"

  def main(args: Array[String]): Unit = println(test)
