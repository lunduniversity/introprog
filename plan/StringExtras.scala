object StringExtras {

  implicit class StringTrimmer(s: String) {
    def stripTrim: String = 
      s.stripMargin.trim.filter(_ != '\n').split(',').map(_.trim).mkString(", ")
  }
  
    
  implicit class StringSaver(s: String) {
    import java.nio.file.{Paths, Files}
    import java.nio.charset.StandardCharsets.UTF_8
    
    def save(fileName: String) = {
      println("Saving file: " + Paths.get(fileName))
      Files.write(Paths.get(fileName), s.getBytes(UTF_8))
    }
  }
  
  implicit class StringLatexEscaper(s: String) {
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


