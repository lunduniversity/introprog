object FixSleepyBrainRegisterChosen {
  val seed = 42
  val rnd = new java.util.Random(seed)
  val names = scala.io.Source.fromFile("src/names.txt").getLines.toSet
  val initPairs = names.map(n => n -> 0).toSeq
  val countChosen = scala.collection.mutable.Map(initPairs: _*)
  def delay = Thread.sleep(4000)

  def main(args: Array[String]): Unit = {
    println("*** FIX YOUR SLEEPY BRAIN ***\n\nTOGGLE WHEN YOUR NAME INCLUDES...")
    while (countChosen.values.filter(_ == 0).size > 0) {
      val letter = (rnd.nextInt('Z' - 'A') + 'A').toChar
      val theChosenOnes = names.filter(_.toUpperCase.contains(letter))
      val action = if (theChosenOnes.isEmpty) "EVERY BODY SIT!!!" else "STAND or SIT"
      delay
      println(s"\n$letter : $action $theChosenOnes")
      for (name <- theChosenOnes) countChosen(name) += 1
    }
    countChosen.toSeq.sortBy(_._2).foreach(println)
  }
}