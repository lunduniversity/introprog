object SwapFirstLastArg {
  def main(args: Array[String]): Unit = {
    if (args.size > 1) {
      val temp = args(0)
      args(0) = args(args.size -1)
      args(args.size -1) = temp
    }
    println(args.mkString(" "))
  }
}
