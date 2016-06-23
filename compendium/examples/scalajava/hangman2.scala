object hangman {  
  val hangman = Array(
    " ======  ",
    " |/   |  ",
    " |    O  ",
    " |   -|- ",
    " |   / \\",
    " |       ",
    " |       ",
    " ==========================   RIP  :(")
      
  val runeberg = "http://runeberg.org/words/ord.ortsnamn.posten"
  
  val latin1 = "ISO-8859-1"

  def printHangman(n: Int): Unit = 
    println(hangman.take(n).mkString("\n"))
    
  def hideSecret(secret: String, found: Set[Char]): String = 
    secret.map(ch => if (found(ch)) ch else '_') 
    
  def foundAll(secret: String, found: Set[Char]): Boolean = 
    secret.forall(ch => found(ch))
    
  def makeGuess(): Char = {
    val guess = scala.io.StdIn.readLine("Gissa ett tecken: ") 
    if (guess.length == 1) guess.toLowerCase.charAt(0) 
    else makeGuess()
  }

  def play(secret: String): Int = {
    var found = Set.empty[Char]
    var bad = 0
    while (bad < hangman.length && !foundAll(secret, found)){
      printHangman(bad)
      print(s"\nFelgissningar: $bad\t")
      println(hideSecret(secret, found))
      val guess = makeGuess()
      if (secret.contains(guess)) found += guess else bad += 1
    }
    val (happyMsg, sadMsg) = 
      ("BRA! :)",s"Hängd! :(\n${hangman.length}")
    val msg = if (foundAll(secret, found)) happyMsg else sadMsg 
    println(s"$msg\nRätt svar: $secret")
    bad           
  }

  def download(address: String,coding: String): String = 
    scala.util.Try {
      import scala.io.Source.fromURL   
      val words = fromURL(address, coding).getLines.toVector
      val rnd = (math.random * words.size).toInt
      words(rnd)
    }.recover{ case e =>  
      println(s"Error: $e\nAnvänder nödlösning.")
      "lackalänga"
    }.get
    
  def main(args: Array[String] ): Unit = {
    var badGuesses = 0;
    if (args.length > 0) {
      val rnd = (math.random * args.length).toInt
      badGuesses = play(args(rnd));
    } else {
      val secret = download(runeberg, latin1)
      badGuesses = play(secret);
    }
    println(s"Antal felgissningar: $badGuesses")
  }
}
