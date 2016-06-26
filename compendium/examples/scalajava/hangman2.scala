object hangman {  
  val hangman = Vector(
    " ======  ",
    " |/   |  ",
    " |    O  ",
    " |   -|- ",
    " |   / \\",
    " |       ",
    " |       ",
    " ==========================   RIP  :(")
      
  def renderHangman(n: Int): String = hangman.take(n).mkString("\n")

  def hideSecret(secret: String, found: Set[Char]): String = 
      secret.map(ch => if (found(ch)) ch else '_') 

  def makeGuess(): Char = {
    val guess = scala.io.StdIn.readLine("Gissa ett tecken: ") 
    if (guess.length == 1) guess.toLowerCase.charAt(0) 
    else makeGuess()
  }

  def download(address: String, coding: String): Option[String] = 
    scala.util.Try {
      import scala.io.Source.fromURL   
      val words = fromURL(address, coding).getLines.toVector
      val rnd = (math.random * words.size).toInt
      words(rnd)
    }.recover{ case e: Exception =>  
      println(s"Error: $e")
      "lackalänga"
    }.toOption

  def play(secret: String): Unit = {
    def loop(found: Set[Char], bad: Int): (Int, Boolean) = 
      if (secret forall found) (bad, true)
      else if (bad >= hangman.length) (bad, false)
      else {
        println(renderHangman(bad) + s"\nFelgissningar: $bad\t") 
        println(hideSecret(secret, found))
        val guess = makeGuess()
        if (secret contains guess) loop(found + guess, bad) 
        else loop(found, bad + 1) 
      }
      
    val (badGuesses, won) = loop(Set(),0)
    if (won) println("BRA! :)") else println("Hängd! :(")
    println(s"Rätt svar: $secret")
    println(s"Antal felgissningar: $badGuesses")
  }

  def main(args: Array[String] ): Unit = {
    if (args.length == 0) {
      val runeberg = "http://runeberg.org/words/ord.ortsnamn.posten"
      download(runeberg, "ISO-8859-1").foreach(play)
    } else play(args((math.random * args.length).toInt))
  }  
}
