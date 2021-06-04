object NanananananananaNanananananananaBatman:
  val na = "NanananananananaNanananananananaBatman"
  val nAppendsMax = math.pow(2, 15).toLong

  def batmanImmutable(n: Int): (Long, String) = Timer.measure {
    var result: String = na  //strings are immutable
    for i <- 2 to n do result = result + na //allocates new String for each append
    result  // return da String
  }

  def batmanMutable(n: Int): (Long, String) = Timer.measure {
    var sb = new StringBuilder(na)  //StringBuilder is mutable
    for i <- 2 to n do sb.append(na) //append ***mutates*** the instance in place
    sb.toString                     //convert to immutable String and return
  }

  def main(args: Array[String]): Unit =
    val warmupJVM = (batmanMutable(100), batmanImmutable(100))
    println("Warmup:\n" + warmupJVM._1 + "\n" + warmupJVM._2 + "\n\n")
    var n = 1
    println(s"Max number of appends = $nAppendsMax")
    println("#APPEND\tMUTABLE\tIMMUTABLE")
    while n <= nAppendsMax do
      print(n)
      val mutable = batmanMutable(n)
      print("\t"  + mutable._1   + " ms")
      val immutable = batmanImmutable(n)
      println("\t" + immutable._1 + " ms")
      n *= 2
    println("\nBATMAN READY!!!!!!!!!!")
