@main
def sum(args: String*): Unit =
  val n = 1000
  val summa = (1 to n).sum
  println(s"Summan av de $n första talen är: $summa")
