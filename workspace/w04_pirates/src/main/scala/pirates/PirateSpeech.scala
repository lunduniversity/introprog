package pirates

object PirateSpeech {
  	def main(argh: Array[String]): Unit = {
			val filename = if(!argh.isEmpty) argh(0) else "skattkammaron.txt"
			// add your tests here!!!

	}
  	
	def readBook(bookFile: String): Unit = ???
	
  /* Optional */
	def readBook(bookFile: String, saveToFile: String): Unit = ???
  
  
  /* Optional */
	def testSpeech(file: String): Unit = ???
	 
  
  class FrequencyCounter(word: String){
	  
	  import scala.collection.mutable.Map
	  
	  // add your code here
	  
	  def addWord(other: String): Unit = ??? 
 	  
	  def getBestGuess(): (String, Int) =  ???
	  
	  override def toString(): String = ???
	}
}