package nlp

object Main:
  val defaultUrl = "https://fileadmin.cs.lth.se/pgk/skattkammaron.txt"
  val defaultN = 10

  def mostFrequentWords(n: Int, freqMap: Map[String, Int]): Vector[(String, Int)] = ???

  def report(text: Text, from: String, n: Int): String =
    val longestWordsWithLength =
      mostFrequentWords(n, text.distinct.map(w => (w, w.length)).toMap).mkString(", ")
    s"""
    |Source: $from
    |
    |*** Number of words: ${text.words.size}
    |
    |*** The $n most common words and their frequency:
    |${mostFrequentWords(n, text.wordFreq).mkString(", ")}
    |
    |*** The $n longest words and their lengths:
    |$longestWordsWithLength
    """.stripMargin

  def main(args: Array[String]): Unit =
    val location = if args.isEmpty then defaultUrl else args(0)
    val n = if args.length < 2 then defaultN else args(1).toInt
    val text =
      if location.startsWith("http") then Text.fromURL(location)
      else Text.fromFile(location)
      
    println(report(text, location, n))
