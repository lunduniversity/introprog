package stats

object Main { 
  def main(args: Array[String]): Unit = scala.util.Try {
      println(s"Statistikens grunder: ${1/0}")
  }.recover{ case e: Exception =>
      println(s"Oj oj, nu blev det klydd: $e\n\nStacken:") 
      e.getStackTrace.take(5).foreach(println)
  }
}
