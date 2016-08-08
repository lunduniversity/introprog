package pirates

object SaveCrew {
  	def main(argh: Array[String]): Unit = {
  	  // change this code to your tests!
  	  
			val filename = if(!argh.isEmpty) argh(0) else "crew.txt"
			saveCrew(filename)
			
			
	}
  	
  	def saveCrew(fileName:String): Unit = ??? // add code for asking the user for crew members and save them to file
  	
  	
	  def readCrew(fileName: String): Unit = ??? // add code for reading the crew from the file
}

// Help functions. Usage: Utils.write("Blah blah blah", "blahFile.txt")
object Utils{
  def write(s: String, fileName: String): Unit = {
			import java.nio.file.{Paths, Files, StandardOpenOption}
			Files.write(Paths.get(fileName), s.getBytes("UTF-8"))
  } 
  def readLines(fileName: String): Vector[String] = {
			scala.io.Source.fromFile(fileName)("UTF-8").getLines.toVector
	}		
	def readWords(fileName: String): Vector[String] = {
			scala.io.Source.fromFile(fileName).getLines.
			map(_.replaceAll("[^a-zA-ZåäöÅÄÖ\\s]", " ")).
			flatMap(_.split("\\s+")).filter(!_.isEmpty).toVector
	}
}

// Add your case class here!
