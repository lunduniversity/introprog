package nlp

object Main {
  val defaultUrl = "https://fileadmin.cs.lth.se/pgk/skattkammaron.txt"
  val defaultN = 10

  def top(n: Int, freqMap: Map[String, Int]): Vector[(String, Int)] = ???

  def report(text: Text, from: String, n: Int): String = {
    val longestWordsWithLength =
      top(n, text.distinct.map(w => (w, w.length)).toMap).mkString(", ")
    s"""
    |Källa: $from
    |
    |*** Antal ord: ${text.words.size}
    |
    |*** De $n vanligaste orden och deras frekvens:
    |${top(n, text.wordFreq).mkString(", ")}
    |
    |*** De $n längsta orden och deras längd:
    |$longestWordsWithLength
    """.stripMargin
  }

  def main(args: Array[String]): Unit = {
    val location = if (args.isEmpty) defaultUrl else args(0)
    val n = if (args.length < 2) defaultN else args(1).toInt
    val text =
      if (location.startsWith("http")) Text.fromURL(location)
      else Text.fromFile(location)
    println(report(text, location, n))
  }
}
