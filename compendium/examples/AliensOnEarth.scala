object AliensOnEarth {
  def userSelect(msg: String, options: Vector[String]): String = {
    options.indices.foreach(i => println(i + ": " + options(i)))
    val selected = scala.io.StdIn.readLine(msg).toInt
    options(selected)
  }

  def isUserOk(msg: String): Boolean =
    scala.io.StdIn.readLine(s"$msg (Y/n)").toLowerCase.startsWith("y")

  def randomSelect(options: Vector[String]): String = {
    val selected = scala.util.Random.nextInt(options.size)
    options(selected)
  }

  def play(alien: String, maxPoints: Int = 1000): Int = {
    val objects = Vector("penguin", "window", "apple")
    val correct =
      if (math.random < 0.8) objects(0) else randomSelect(objects)
    val cheatCode = (objects.indexOf(correct) + 1) * math.Pi
    println(s"""Hello $alien!
               |You are an alien on Earth.
               |Your encrypted password is $cheatCode.
               |You see three strange Earth objects.""".stripMargin)
    val choice = userSelect(s"$alien wants? ", objects)
    if (choice == correct) maxPoints else 0
  }

  def main(args: Array[String]): Unit =
   try {
      val name = if (args.size > 0) args(0) else "Master of Universe"
      val points = play(alien = name, maxPoints = 1000)
      if (points > 0) println(s"Congratulations $name! :)")
      println(s"You got $points points.")
    } catch { case e: Exception =>
      println(s"Game over. The Earth was hit by an asteroid. :(")
      if (isUserOk("Do you want to trace the asteroid?"))
        e.printStackTrace
    }
}
