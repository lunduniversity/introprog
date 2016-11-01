object RandomId {  
  import java.util.Random

  def printNumbers(rnd: Random, n: Int, min: Int, max: Int): Unit = {
    var taken = Set.empty[Int]
    while (taken.size < n) {
      val r = rnd.nextInt(max - min + 1) + min
      if (!taken.contains(r)) {
        println(r)
        taken += r
      }
    }
  }

  def main(args: Array[String]): Unit = {
    if (args.length == 4) {
      val seed            = args(0).toInt
      val sequenceLength  = args(1).toInt
      val minValue        = args(2).toInt
      val maxValue        = args(3).toInt
      if (maxValue - minValue + 1 < sequenceLength)
        println("Error: Interval is smaller than sequence length")
      else {
        val randomGenerator = new Random(seed)
        printNumbers(randomGenerator, sequenceLength, minValue, maxValue)
      }
    } else println("Usage: scala RandomId <seed> <n> <min> <max>")
  }
}
