object StringExtras {

  implicit class StringTrimmer(s: String) {
    def stripTrim: String = 
      s.stripMargin.trim.filter(_ != '\n').split(',').map(_.trim).mkString(", ")
  }
  
    
  implicit class StringSaver(contents: String) {
    import java.nio.file.{Paths, Files}
    import java.nio.charset.StandardCharsets.UTF_8
    def save(fileName: String) = {
      println("Saving file: " + Paths.get(fileName))
      Files.write(Paths.get(fileName), contents.getBytes(UTF_8))
    }
  }

}
