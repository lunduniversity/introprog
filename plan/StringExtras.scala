object StringExtras {
  import java.nio.file.{Paths, Files}
  import java.nio.charset.StandardCharsets.UTF_8

  implicit class StringDecorator(s: String) {
    def stripTrim: String = 
      s.stripMargin.trim.filter(_ != '\n').split(',').map(_.trim).mkString(", ")
    
    def save(fileName: String) = {
      println("Saving file: " + Paths.get(fileName))
      Files.write(Paths.get(fileName), s.getBytes(UTF_8))
    }

    def prepend(prep: String) = prep + s
    
    def toPath = Paths.get(s)

    def latexEscape = {
      val escapeChars = "&_"
      var result = s
      for (c <- escapeChars){ 
        result = result.replaceAllLiterally(c.toString, "\\" + c)
      }
      result
    }
  }
}



