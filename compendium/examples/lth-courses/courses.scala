object courses:
  def download(): Vector[Course] =
    val url = "https://fileadmin.cs.lth.se/pgk/lthkurser201819.txt"
    val lines = scala.io.Source.fromURL(url, "UTF-8").getLines.toVector
    lines.drop(1).map(s => Course.fromLine(s, '\t'))

  lazy val lth: Vector[Course] = download()

  case class Course(
    code:    String,
    nameSv:  String,
    nameEn:  String,
    credits: Double,
    level:   String
  )

  object Course:
    def fromLine(line: String, separator: Char): Course =
      import scala.util.Try
      val xs = line.split(separator).toSeq
      def str(i: Int): String = Try(xs(i)).getOrElse("")
      def num(i: Int): Double = Try(xs(i).toDouble).getOrElse(0.0)
      Course(str(0), str(1), str(2), num(3), str(4))
