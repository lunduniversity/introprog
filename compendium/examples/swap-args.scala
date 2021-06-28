@main
def swapFirstLastArg(args: String*): Unit =
  val xs = args.toArray
  if xs.size > 1 then
    val temp = xs(0)
    xs(0) = xs(xs.size - 1)
    xs(xs.size - 1) = temp
  println(xs.mkString(" "))
