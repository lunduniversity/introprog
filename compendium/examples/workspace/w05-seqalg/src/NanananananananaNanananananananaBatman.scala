object NanananananananaNanananananananaBatman {
  val na = "NanananananananaNanananananananaBatman"

  def batmanImmutable(n: Int): (Long, String) = Timer.measure {
    var result: String = na  // Strings are immutable
    for (i <- 2 to n) {
      result = result + na   // Allocates a new String for each append
    }
    result  // return da String
  }

  def batmanMutable(n: Int): (Long, String) = Timer.measure {
    var sb = new StringBuilder(na)  // StringBuilder is mutable
    for (i <- 2 to n) {
        sb.append(na)     // append ***mutates*** the instance in place
    }
    sb.toString           // convert to immutable String and return
  }

  def main(args: Array[String]): Unit = {
    val warmupJVM = (batmanMutable(100), batmanImmutable(100))
    println("Warmup:\n" + warmupJVM._1 + "\n" + warmupJVM._2 + "\n\n")

    var n = 1
    val max = math.pow(2, 9)
    println(s"max = $max")
    println("#APPEND\tIMMUTABLE\tMUTABLE")
    while (n <= max) {
      print(n)
      val immutable = batmanImmutable(n)
      val mutable = batmanMutable(n)
      print("\t\t" + immutable._1 + " ms")
      println("\t\t"  + mutable._1   + " ms")
      n *= 2
    }
    println("BATMAN READY!!!!!!!!!!")
  }
}