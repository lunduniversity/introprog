object AliensOnEarth {
  def readChoice(msg: String, options: Vector[String]): String = {
    options.indices.foreach(i => println(i + ": " + options(i)))
    val selected = io.StdIn.readLine(msg).toInt
    options(selected)
  }

  def isAnswerYes(msg: String): Boolean =
    io.StdIn.readLine(s"$msg (Y/n)").toLowerCase.startsWith("y")

  def randomChoice(options: Vector[String]): String = {
    val selected = util.Random.nextInt(options.size)
    options(selected)
  }

  def playGame(alien: String, maxPoints: Int = 1000): Int = {
    val os = Vector("penguin", "window", "apple")
    val correct = if (math.random < 0.5) os(0) else randomChoice(os)
    val cheatCode = (os.indexOf(correct) + 1) * math.Pi
    println(s"""Hello $alien!
               |You are an alien on Earth.
               |Your encrypted password is $cheatCode.
               |You see three strange Earth objects.""".stripMargin)
    val choice = readChoice(s"$alien wants? ", os)
    if (choice == correct) maxPoints else 0
  }

  def main(args: Array[String]): Unit =
    try {
      val name = if (args.size > 0) args(0) else "Captain Zoom"
      val points = playGame(alien = name)
      if (points > 0) println(s"Congratulations $name! :)")
      println(s"You got $points points.")
    } catch { case e: Exception =>
      println(s"Game over. The Earth was hit by an asteroid. :(")
      if (isAnswerYes("Do you want to trace the asteroid?"))
        e.printStackTrace()
    }
}
