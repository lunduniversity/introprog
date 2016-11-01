object Rotate {
  def rotateArrayOfStrings(xs: Array[String], fromPos: Int): Unit = {
    if (fromPos >= 0 && fromPos < xs.length - 1) {
      val temp = xs(fromPos)
      for (i <- fromPos until xs.length - 1) xs(i) = xs(i + 1)
      xs(xs.length - 1) = temp
    }
  }

  def main(args: Array[String]): Unit = {
    val argsErrorMsg = "Error: Not enough arguments."
    if (args.length > 1) {
      val numberOfSteps = args(0).toInt
      for (i <- 1 to numberOfSteps) rotateArrayOfStrings(args, 1)
      for (i <- 1 until args.length) println(args(i) + " ")
    } else println(argsErrorMsg)
  }
}
