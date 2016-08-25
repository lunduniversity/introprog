package pirates

object PirateSpeech {
  def main(argh: Array[String]): Unit = {
    val filename = if (!argh.isEmpty) argh(0) else "skattkammaron.txt"
    // add your tests here!!!
  }

  def readBook(bookFile: String): Map[String, WordCounter] = {
  	val words = ??? // ("herr", "trelawney", "doktor", "livesey", "och", ...)
  	val wordSet = ??? //all words only once
    
  	val counterMap = ??? 
    
  	??? // go through the book looking at two words at a time 
    
    counterMap // return the map
  }

  /* Optional */
  def readBook(bookFile: String, saveToFile: String):  Unit = ???

  /* Optional */
  def testSpeech(file: String): Unit = ???
}