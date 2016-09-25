object FixSleepyBrain {
  val seed = 42
  val rnd = new java.util.Random(seed)
  val names = scala.io.Source.fromFile("src/names.txt").getLines.toSet
  def delay = Thread.sleep(3000)
  def main(args: Array[String]): Unit = {
    println("*** FIX YOUR SLEEPY BRAIN ***\n\nWHEN YOUR NAME STARTS WITH...")
    while (true) {
      val letter = (rnd.nextInt('Z' - 'A') + 'A').toChar
      val theChosenOnes = names.filter(_.contains(letter))
      val action = if (theChosenOnes.isEmpty) "EVERY BODY SIT!!!" else "STAND UP"
      delay
      println(s"\n$letter : $action $theChosenOnes")
    }
  }
}
