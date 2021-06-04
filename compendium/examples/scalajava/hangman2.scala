object hangman:  
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
      secret.map(ch => if found(ch) then ch else '_') 

  def makeGuess(): Char =
    val guess = scala.io.StdIn.readLine("Gissa ett tecken: ") 
    if guess.length == 1 then guess.toLowerCase.charAt(0) 
    else makeGuess()

  def download(address: String, coding: String): Option[String] = 
    scala.util.Try {
      import scala.io.Source.fromURL   
      val words = fromURL(address, coding).getLines.toVector
      val rnd = (math.random() * words.size).toInt
      words(rnd)
    }.toOption

  def play(secret: String): Unit =
    def loop(found: Set[Char], bad: Int): (Int, Boolean) = 
      if secret forall found then (bad, true)
      else if bad >= hangman.length then (bad, false)
      else
        println(renderHangman(bad) + s"\nFelgissningar: $bad\t") 
        println(hideSecret(secret, found))
        val guess = makeGuess()
        if secret contains guess then loop(found + guess, bad) 
        else loop(found, bad + 1) 
      
    val (badGuesses, won) = loop(Set(),0)
    val msg = if won then "BRA! :)" else "Hängd! :("
    println(s"$msg\nRätt svar: $secret")
    println(s"Antal felgissningar: $badGuesses")

  def main(args: Array[String] ): Unit =
    if args.length == 0 then
      val runeberg = "http://runeberg.org/words/ord.ortsnamn.posten"
      val secret = download(runeberg, "ISO-8859-1").getOrElse("läckalånga")
      play(secret)
    else play(args((math.random() * args.length).toInt))
