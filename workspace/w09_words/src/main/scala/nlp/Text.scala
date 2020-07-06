package nlp

case class Text(source: String){
  lazy val words: Vector[String] = ???  // dela upp source i ord

  lazy val distinct: Vector[String] = words.distinct

  lazy val wordSet: Set[String] = words.toSet

  lazy val wordsOfLength: Map[Int, Set[String]] = wordSet.groupBy(_.length)

  lazy val wordFreq: Map[String, Int] = ???  // använd FreqMapBuilder

  def ngrams(n: Int): Vector[Vector[String]] = ???  // använd sliding

  lazy val bigrams: Vector[(String, String)] =
    ngrams(2).map(xs => (xs(0), xs(1)))

  lazy val followFreq: Map[String, Map[String, Int]] = ??? //nästlad tabell

  lazy val follows: Map[String, String] =
    followFreq.map { case (key, followMap) => 
      val maxByFreq: (String, Int) = followMap.maxBy(_._2)
      val mostCommonFollower: String = maxByFreq._1
      (key, mostCommonFollower) 
    }
    //eller kortare med samma resultat: (lättare eller svårare att läsa?)
    //  followFreq.map(kv => kv._1 -> kv._2.maxBy(_._2)._1)
    //eller ännu lite kortare men samma resultat: 
    //  followFreq.view.mapValues(_.maxBy(_._2)._1).toMap
}

object Text {
  def fromFile(fileName: String, encoding: String = "UTF-8"): Text = {
    val source = scala.io.Source.fromFile(fileName, encoding).mkString
    Text(source)
  }
  def fromURL(url: String, encoding: String = "UTF-8"): Text = {
    val source = scala.io.Source.fromURL(url, encoding).mkString
    Text(source)
  }
}
