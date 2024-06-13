object StringExtras:
  import java.nio.file.{Paths, Path, Files}
  import java.nio.charset.StandardCharsets.UTF_8

  extension (s: String)
    def stripTrim: String = 
      s.stripMargin.trim.filter(_ != '\n').split(',').map(_.trim).mkString(", ")
    
    def save(fileName: String): Unit = 
      println("Saving file: " + Paths.get(fileName))
      Files.write(Paths.get(fileName), s.getBytes(UTF_8))

    def prepend(prep: String): String = prep + s
    
    def toPath: Path = Paths.get(s)

    def latexEscape: String = 
      val escapeChars = "&_"
      var result = s
      for c <- escapeChars do 
        result = result.replace(c.toString, "\\" + c)
      result
  




