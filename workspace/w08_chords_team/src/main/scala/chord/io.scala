package chord
import scala.util.{Try, Success, Failure}

object io {
  import scala.io.Source
  import java.nio.file.{Paths, Files}
  import java.nio.charset.StandardCharsets.UTF_8  

  /**
   * Reads all lines from file fileName
   */
  def load(fileName: String): Vector[String] = {
    val result = Try {
      val lines = Source.fromFile(fileName).getLines
      lines.toVector
    } 
    result match {
      case Success(_) => println(s"Data loaded from: $fileName")
      case Failure(e) => println(s"Error: $e") 
    }
    result.getOrElse(Vector())
  }
  
  /**
   * Writes all lines to file fileName
   */
  def save(fileName: String, lines: Vector[String]): Unit = Try {
    val data = lines.mkString("\n")
    Files.write(Paths.get(fileName), data.getBytes(UTF_8))
  } match {
    case Success(_) => println(s"Saved to file: $fileName")
    case Failure(e) => println(s"Error: $e") 
  }
}