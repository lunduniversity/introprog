object FixSleepyBrainRegisterChosen {
  val seed = 42
  val rnd = new java.util.Random(seed)
  val names = scala.io.Source.fromFile("src/names.txt").getLines.toSet
  val initPairs = names.map(n => n -> 0).toSeq
  val countChosen = scala.collection.mutable.Map(initPairs: _*)
  def delay = Thread.sleep(3000)

  def main(args: Array[String]): Unit = {
    println("*** FIX YOUR SLEEPY BRAIN ***\n\nTOGGLE WHEN YOUR NAME INCLUDES...")
    var n = 0
    while (countChosen.values.filter(_ == 0).size > 0) {
      n += 1
      val letter = (rnd.nextInt('Z' - 'A') + 'A').toChar
      val theChosenOnes = names.filter(_.toUpperCase.contains(letter))
      val action = if (theChosenOnes.isEmpty) "EVERYBODY SIT!!!" else "STAND or SIT"
      delay
      println(s"\n$n: $letter : $action $theChosenOnes")
      for (name <- theChosenOnes) countChosen(name) += 1
    }
    countChosen.toSeq.sortBy(_._2).foreach(println)
  }
}
