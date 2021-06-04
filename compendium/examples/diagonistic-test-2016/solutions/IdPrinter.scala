object IdPrinter:  
  import java.util.Random

  def print(rnd: Random, n: Int, min: Int, max: Int): Unit =
    var taken = Set.empty[Int]
    while taken.size < n do
      val r = rnd.nextInt(max - min + 1) + min
      if !taken.contains(r) then
        println(r)
        taken += r

