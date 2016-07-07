package pirates

object PirateSpeech {
  	def main(argh: Array[String]): Unit = {
			val filename = if(!argh.isEmpty) argh(0) else "skattkammarön.txt"
			// add your tests here!!!

	}
  
  
	def readBook(bookFile: String, saveToFile: String): Unit = ???
  
	def testSpeech(file: String): Unit = ???
	 
  
  
	class FrequenceCounter{
	  
	  // add your code here
	  
	  def getBestGuess(): String = ???
	}
}