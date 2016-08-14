package pirates 

object FileUtils {
  import java.nio.file.{Paths, Files}

  def write(s: String, fileName: String): Unit = 
    Files.write(Paths.get(fileName), s.getBytes("UTF-8"))

  def readLines(fileName: String): Vector[String] = 
    scala.io.Source.fromFile(fileName)("UTF-8").getLines.toVector

  def readWords(fileName: String): Vector[String] = {
    val data: String             = readLines(fileName).mkString(" ")
    def convertSpace(c: Char)    = if (c.isWhitespace) ' ' else c
    val spaceConverted           = data.map(convertSpace)
    def isLetterOrSpace(c: Char) = c.isLetter || c.isSpaceChar 
    val lettersAndSpaces         = spaceConverted.filter(isLetterOrSpace)
    val splitted: Array[String]  = lettersAndSpaces.split(' ')
    splitted.filterNot(_.isEmpty).toVector
  }
}
