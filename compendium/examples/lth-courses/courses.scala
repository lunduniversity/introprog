object courses {
  def download(year: String = "16_17"): Vector[Course]  = { 
    val urlStart = "http://kurser.lth.se/lot/?lasar="
    val urlSearch = "&soek_text=&sort=kod&val=kurs&soek=t"
    val url = s"$urlStart$year$urlSearch"
    println("*** Downloading from: " + url)
    println("*** This may take a while...")
    val lines = scala.io.Source.fromURL(url).getLines.toVector  
    lines.filter(_.contains("kurskod")).map(Course.fromHtml)
  }
  
  lazy val lth2016: Vector[Course] = download()
    
  case class Course(
    code:    String,
    nameSv:  String, 
    nameEn:  String, 
    credits: Double,
    level:   String
  )
    
  object Course {
    import scala.util.Try
    def fromHtml(s: String): Course = {
      def extract(s: String, init: String, stop: Char): String = 
        s.replaceAllLiterally(init, "").takeWhile(_ != stop)
      val codeInit = """<a href="/lot/?val=kurs&amp;kurskod="""
      val dataInit = """<td class="mitt">"""
      val xs = s.split("td>")
      val code = Try { extract(xs(1), codeInit, '"') }.getOrElse("???")
      val credits = Try {
        val s = extract(xs(2), dataInit, '<')
        s.replaceAllLiterally(",",".").toDouble //fix decimals
      }.getOrElse(0.0)
      val level = Try { extract(xs(3), dataInit, '<') }.getOrElse("???")
      val nameSv = Try { xs(5).takeWhile(_ != '<') }.getOrElse("???")
      val nameEn = Try { xs(7).takeWhile(_ != '<') }.getOrElse("???")
      Course(code, nameSv, nameEn, credits, level)
    }
  }
}
