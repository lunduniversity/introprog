package glossary 

object StringExtras:
  import java.nio.file.{Paths, Files}
  import java.nio.charset.StandardCharsets.UTF_8

  extension (s: String) 
    def wrapConcept(
      concept: String, 
      pre: String = "\\textbf{", 
      post: String = "}", 
      capitalStart: Boolean = true
    ) =
      val i = s.toLowerCase.indexOf(concept.toLowerCase())
      if i < 0 then s else 
        val (start, end) = (s.substring(0, i), s.substring(i + concept.length))
        val wrapped = if start.length == 0 && capitalStart then concept.capitalize else concept
        s"$start$pre$wrapped$post$end"



    def stripTrim: String =
      s.stripMargin.trim.filter(_ != '\n').split(',').map(_.trim).mkString(", ")

    def save(fileName: String) = {
      println("Saving file: " + Paths.get(fileName))
      Files.write(Paths.get(fileName), s.getBytes(UTF_8))
    }

    def prepend(prep: String) = prep + s

    def toPath = Paths.get(s)

    def loadLines: Vector[String] = {
      val enc = "UTF-8"
      var result: Vector[String] = Vector()
      val source = scala.io.Source.fromFile(s, enc)
      try result = source.getLines().toVector finally source.close()
      result
    }

    def latexEscape = {
      val escapeChars = "&_"
      var result = s
      for (c <- escapeChars){
        result = result.replace(c.toString, "\\" + c)
      }
      result
    }

