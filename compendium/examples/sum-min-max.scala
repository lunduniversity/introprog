object SumMinMax {
  def main(args: Array[String]): Unit = {
    val ints = args.map(_.toInt)
    println(ints.sum + " " + ints.min + " " + ints.max)
  }
}
