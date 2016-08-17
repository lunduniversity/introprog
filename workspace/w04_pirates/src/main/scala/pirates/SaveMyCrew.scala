package pirates

object SaveMyCrew {
  def main(argh: Array[String]): Unit = {
    // change this code to your tests!

    val filename = if (!argh.isEmpty) argh(0) else "crew.txt"

  }

  def saveCrew(fileName: String): Unit = ??? 
  // add code for asking the user for crew members and save them to file

  def readCrew(fileName: String): Unit = ??? 
  // add code for reading the crew from the file
}


// Add your case class here!
