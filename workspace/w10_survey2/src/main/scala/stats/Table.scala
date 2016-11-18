package stats

import java.net.MalformedURLException
import java.nio.file.{Files, Path, Paths}

import scala.io.Source
import scala.util.Try

/**
 * A representation of text data in matrix form.
 *
 * @param matrix   The data. Rows in the outer Vector, columns in the inner.
 * @param headings Column headings.
 * @param separator      The String character that separates the columns.
 */
case class Table(matrix: Vector[Vector[String]],
                 headings: Vector[String],
                 separator: String) {

  /** Returns the number of (rows, columns) of the matrix data. */
  val dim: (Int, Int) = ???

  /** Returns the values from a specified column. */
  def col(c: Int): Vector[String] = ???

  /** Returns the matrix in string format using separator between columns*/
  override lazy val toString: String = ???

  /** A new Table with rows sorted on column c (implemented using sortBy). */
  def sort(c: Int): Table = ???

  /** A new Table with rows sorted on column c (implemented from scratch). */
  def mySort(c: Int): Table = ???

  /**
   * A new Table filtered so that column c only contains the wanted values.
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
  def getResourcePath(resource: String): Path = {
    Paths.get(getClass.getResource("/" + resource).toURI)
  }

  /**
    * Reads column separated data from either a file or a URL into a Table.
    *
    * @param uri The location of the data.
    * @param sep The character that separates the columns.
    */
  def fromFile(uri: String, sep: String): Table = {
    val source = Try {
      Source.fromURL(uri)
    }.recover {
      case _: MalformedURLException =>
        Source.fromFile(getResourcePath("").resolve(uri).toFile)
    }
    fromFile(source.get, sep)
  }

  /**
    * Reads column separated text data from a Source into a Table.
    *
    * @param source The data to load.
    * @param sep The character that separates the columns.
    */
  def fromFile(source: Source, sep: String): Table = {
    val lines = source.getLines.toVector
    val rows = lines.map(_.split(sep).toVector)
    Table(rows.tail, rows.head, sep)
  }

  /** Write a Table to disk. */
  def save(path: String, table: Table): Unit = {
    save(Paths.get(path), table)
  }

  /** Write a Table to disk. */
  def save(path: Path, table: Table): Unit = {
    import java.nio.charset.StandardCharsets
    println(s"Saving table to file: ${path.toAbsolutePath}")
    Files.write(path, table.toString.getBytes(StandardCharsets.UTF_8))
  }

  /** Some tests of Table */
  def main(args: Array[String]): Unit = {
    val path = getResourcePath("favorit.csv")
    println(s"***TESTING Table***\nInput: $path")
    val table = Table.fromFile(Source.fromFile(path.toFile), ",")
    val tableFiltered = table.filter(4, Vector("Linux")).sort(6)
    println(tableFiltered.register(6).mkString("\n"))
    save(getResourcePath("out.csv"), tableFiltered)
  }
}