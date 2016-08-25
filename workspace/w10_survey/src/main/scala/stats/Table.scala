package stats

/**
 * A representation of text data in matrix form.
 *
 * @param matrix   The data. Rows in the outer Vector, columns in the inner.
 * @param headings Column headings.
 * @param sep      The String character that separates the columns.
 */
case class Table(
    matrix: Vector[Vector[String]], headings: Vector[String], sep: String) {

  /** Returns the width (columns) and height (rows) of the matrix data. */
  val dim: (Int, Int) = ???

  /** Returns the values from a specified column. */
  def col(c: Int): Vector[String] = ???

  /** Returns the matrix in text format */
  override lazy val toString: String = ???

  /** Returns a copy of itself with the rows sorted on the specified column. */
  def sort(c: Int): Table = ???

  /**
   * Returns a copy of itself with the rows filtered so that the given column
   * only contains the wanted values.
   */
  def filter(c: Int, wanted: Vector[String]): Table = ???

  /**
   * Returns the distinct values for the given column coupled with the number
   * of occurrences for that value. The tuples are sorted descendingly on the
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
  def fromFile(uri: String, sep: String): Table = ???

  /** Writes a text formatted Table to disk. */
  def toFile(path: String, table: Table): Unit = ???

  /** To test Table */
  def main(args: Array[String]): Unit = {
    val table = Table.fromFile("src/main/resources/favorit.csv", ",")
    val tablefs = table.filter(4, Vector("Linux")).sort(6)
    println(tablefs.register(6).mkString("\n"))
    toFile("src/main/resources/out.csv", tablefs)
  }
}