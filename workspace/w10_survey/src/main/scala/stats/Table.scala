package stats

/**
 * A representation of text data in matrix form.
 *
 * @param matrix   The data. Rows in the outer Vector, columns in the inner.
 * @param headings Column headings.
 * @param sep      The String character that separates the columns.
 */
case class Table(matrix: Vector[Vector[String]], headings: Vector[String], sep: String) {

  /**
   * Returns the width (columns) and height (rows) of the
   * matrix data.
   */
  val dim: (Int, Int) = ???

  /**
   * Returns the values from a single column.
   *
   *  @param c The column index.
   */
  def col(c: Int): Vector[String] = ???

  /** Returns the matrix in text format */
  override lazy val toString: String = ???

  /**
   * Returns a copy of the current table sorted according to
   * the specified column.
   *
   * @param c The column index.
   */
  def sort(c: Int): Table = ???

  /**
   * Returns a copy of the current table filtered according to
   * if the specified column is among the wanted values.
   *
   * @param c      The column index.
   * @param wanted The wanted values.
   */
  def filter(c: Int, wanted: Vector[String]): Table = ???

  /**
   * Returns the frequency registered data from a specified column.
   *
   * @param c The column index.
   */
  def register(c: Int): Vector[(String, Int)] = ???
}

object Table {

  /**
   * Opens and reads column separated text data from either
   * a file or a URL into a Table.
   *
   * @param uri The location of the data.
   * @param sep The separator that distinguishes columns.
   */
  def fromFile(uri: String, sep: String): Table = ???

  /**
   * Writes a text formatted Table to disk.
   *
   * @param path  The location of the file to write.
   * @param table The table to be written.
   */
  def toFile(path: String, table: Table): Unit = ???
}