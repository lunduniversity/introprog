object RegisterToggleWinner {
  val chars: Vector[Char] = ('A' to 'Z').toVector   // Å+Ä -> A, Ö -> O
  def randomOrder(): String = scala.util.Random.shuffle(chars).mkString
  val reg = new Array[Int]('Z' - 'A' + 1)
  def table: Seq[(Char, Int)] = reg.indices.map(i => ((i + 'A').toChar, reg(i)))
  def winner = "\n\n*** ALLA SITT UTOM VINNARE: " + (reg.indexOf(reg.max) + 'A').toChar
  def report = "Registreringsrapport:\n" + table.mkString("\n") + winner

  def toggle(n: Int, delayMillis: Int = 4000): String = if (n <= 0) report else {
    val chars = randomOrder().take(n).sorted
    println(s"SITT NER om ditt förnamn börjar på: $chars")
    chars.foreach(ch => reg(ch - 'A') += 1);   Thread.sleep(delayMillis)
    println("ALLA STÅR UPP!");                 Thread.sleep(delayMillis)
    toggle(n - 1)
  }

  def main(args: Array[String]): Unit = {
    scala.io.StdIn.readLine("ALLA STÅR UPP!\nTryck ENTER!")
    if (args.length == 2) println(toggle(args(0).toInt, args(1).toInt))
    else println(toggle(5))
  }
}
