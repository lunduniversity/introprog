object NanananananananaNanananananananaBatman {
  import Timer.measure

  val na = "NanananananananaNanananananananaBatman"

  def batmanImmutableTime(n: Int): (Long, String) = measure {
    var result: String = na  // Strings are immutable
    for (i <- 2 to n) {
      result = result + na   // Creates a new String
    }
    result  // return da String
  }

  def batmanMutableTime(n: Int): (Long, String) = measure {
    var sb = new StringBuilder(na)  // StringBuilder is mutable
    for (i <- 2 to n) {
        sb.append(na)     // append ***mutates*** StringBuilder
    }
    sb.toString       // convert to immutable String and return
  }

  def main(args: Array[String]): Unit = {
    val warmupJVM = (batmanMutableTime(100), batmanImmutableTime(100))
    println("Warmup:\n" + warmupJVM._1 + "\n" + warmupJVM._2 + "\n\n")

    var n = 1
    val max = math.pow(2, 10)
    println(s"max = $max")
    println("#APPEND\tIMMUTABLE\tMUTABLE")
    while (n <= max) {
      print(n)
      val immutable = batmanImmutableTime(n)
      val mutable = batmanMutableTime(n)
      print("\t\t" + immutable._1 + " ms")
      println("\t\t"  + mutable._1   + " ms")
      n *= 2
    }
    println("BATMAN READY!!!!!!!!!!")
  }
}