package stats

/**
 * A representation of text data in matrix form.
 *
 * @param matrix   The data. Rows in the outer Vector, columns in the inner.
 * @param headings Column headings.
 * @param separator      The String character that separates the columns.
 */
case class Table(matrix: Vector[Vector[String]], headings: Vector[String], separator: String) {

  /** Returns the number of (rows, columns) of the matrix data. */
  val dim: (Int, Int) = ???

  /** Returns the values from a specified column. */
  def col(c: Int): Vector[String] = ???

  /** Returns the matrix in text format */
  override lazy val toString: String = ???

  /** Returns a new Table with the rows sorted on column c (implemented using sortBy). */
  def sort(c: Int): Table = ???

  /** Returns a new Table with the rows sorted on column c (implemented from scratch). */
  def mySort(c: Int): Table = ???

  /**
   * Returns a new Table filtered so that column c only contains the wanted values.
   */
  def filter(c: Int, wanted: Vector[String]): Table = ???

  /**
   * Returns the distinct values for the given column coupled with the number
   * of occurrences for that value. The pairs are sorted descending on the
   * number of occurrences. The first element is the column header together
   * with the total number of occurrences for all values.
   */
  def register(c: Int): Vector[(String, Int)] = ???
}

object Table {

  /**
    * Reads column separated text data from either a file or a URL into a Table.
    *
    * @param uri The location of the data.
    * @param sep The character that separates the columns.
    */
  def fromFile(uri: String, sep: String): Table = {

    val source = uri match {
      case url if url.startsWith("http") => scala.io.Source.fromURL(url)
      case path =>
        val fullPath = java.nio.file.Paths.get(path).toAbsolutePath.toString
        scala.io.Source.fromFile(fullPath)
    }
    val lines = source.getLines.toVector
    val rows = lines.map(_.split(sep).toVector)
    Table(rows.tail, rows.head, sep)
  }

  /** Write Table to disk. */
  def save(path: String, table: Table): Unit = {
    import java.nio.file.{ Paths, Files }
    import java.nio.charset.StandardCharsets
    val fullPath = Paths.get(path).toAbsolutePath
    println(s"Saving table to file: $fullPath")
    Files.write(fullPath, table.toString.getBytes(StandardCharsets.UTF_8))
  }

  /** Some tests of Table */
  def main(args: Array[String]): Unit = {
    def getResourcePath : String = getClass.getResource("/").getPath
    val (path, filename) = (getResourcePath, "favorit.csv")
    println(s"***TESTING Table***\nInput: $path$filename")
    val table = Table.fromFile(path + filename, ",")
    val tableFiltered = table.filter(4, Vector("Linux")).sort(6)
    println(tableFiltered.register(6).mkString("\n"))
    save("src/main/resources/out.csv", tableFiltered)
  }
}