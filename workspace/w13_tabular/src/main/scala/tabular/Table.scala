package tabular
import scala.util.Try

case class Table(
  headings: Vector[String],
  rows: Vector[Vector[Cell]],
  separator: Char
):
  val nRows = rows.length
  val nCols = rows.map(_.length).max
  val dim = (nRows, nCols)

  /** Uppgift: Ändra så att resultatet blir -1 om nyckel inte finns. */
  val indexOfHeading: Map[String, Int] = headings.zipWithIndex.toMap

  /** Cell på plats (row, col), Cell.empty om indexering utanför gräns. */
  def apply(row: Int, col: Int): Cell = ??? //

  /** Some(cell) på plats (row, col), None om indexering utanför gräns. */
  def get(row: Int, col: Int): Option[Cell] = ??? //

  def row(index: Int): Vector[Cell] = rows(index)

  def col(index: Int): Vector[Cell] = rows.indices.map(apply(_, index)).toVector

  def col(heading: String): Vector[Cell] = col(indexOfHeading(heading))

  /** Registrering av frekvens för varje unikt värde i kolumn heading.*/
  def freq(heading: String): Vector[(Cell, Int)] = ???

  def filter(heading: String, values: Set[String]): Table =
    copy(rows = rows.filter(r => values.contains(r(indexOfHeading(heading)).value)))

  def sortOn(heading: String): Table =
    copy(rows = rows.sortBy(r => r(indexOfHeading(heading)).value))

  def values(heading: String): Vector[String] = col(heading).map(_.value)

  def show: String =
    def maxValueLength(heading: String): Int = col(heading).map(_.value.length).max
    val colSize = headings.map(h => maxValueLength(h) max h.length)
    val paddedHeadings = headings.map(h => h.padTo(colSize(indexOfHeading(h)), ' '))
    val heading = paddedHeadings.mkString("|","|","|")
    val line = "-" * heading.size
    val paddedRows = rows.map(r =>
      headings.indices.map(i => r(i).value.padTo(colSize(i),' ')).mkString("|","|","|")
    ).mkString("\n")
    s"$line\n$heading\n$line\n$paddedRows\n$line\n (nRows, nCols) == $dim\n"

  /** Strängvektor för varje rad i `rows`, kolumner skilda med `separator`. */
  def toLines(separator: Char): Vector[String] = ???

object Table:
  /** Skapa tabell från fil el. webbadress; kolumner skilda med `separator`. */
  def load(fileOrUrl: String, separator: Char): Table = ???
