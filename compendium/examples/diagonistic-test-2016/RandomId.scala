object RandomId:  
  def main(args: Array[String]): Unit =
    if args.length == 4 then
      val seed            = args(0).toInt
      val sequenceLength  = args(1).toInt
      val minValue        = args(2).toInt
      val maxValue        = args(3).toInt
      if maxValue - minValue + 1 < sequenceLength then
        println("Error: The interval is smaller than the sequence length")
      else
        val randomGenerator = new java.util.Random(seed)
        IdPrinter.print(randomGenerator, sequenceLength, minValue, maxValue)
    else println("Usage: scala RandomId <seed> <n> <min> <max>")
