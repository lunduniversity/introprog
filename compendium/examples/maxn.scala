@main def maxn(args: String*): Unit =
  var max = Int.MinValue
  val n = args.size
  var i = 0
  while i < n do
    val x = args(i).toInt
    if x > max then
      max = x
    i += 1
  println(max)
