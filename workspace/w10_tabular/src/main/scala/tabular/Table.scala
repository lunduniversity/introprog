package tabular
import scala.util.{Try, Success, Failure}

sealed trait Cell { def value: String }
final case class Str(value: String) extends Cell
final case class Num(num: BigDecimal) extends Cell { def value = num.toString }

object Cell {
  def apply(s: String): Cell = ???  // Om BigDecimal(s) lyckas ge en Num annars Str

  def apply(i: Int): Num = Num(BigDecimal(i))

  def empty: Str = Str("")

  def zero: Num = Num(BigDecimal(0))
}

case class Table(
  headings: Vector[String],
  rows: Vector[Vector[Cell]],
  separator: Char
){
  val nRows = rows.length
  val nCols = rows.map(_.length).max
  val dim = (nRows, nCols)
  val indexOfHeading: Map[String, Int] = headings.zipWithIndex.toMap

  def apply(row: Int, col: Int): Cell = ??? // ger Cell.empty om indexering ej går

  def row(index: Int): Vector[Cell] = Try(rows(index)) getOrElse Vector()

  def col(index: Int): Vector[Cell] = rows.indices.map(apply(_, index)).toVector

  def col(heading: String): Vector[Cell] = col(indexOfHeading(heading))

  def freq(heading: String): Vector[(Cell, Int)] =
    col(heading).groupBy(x => x).mapValues(_.size).toVector

  def filter(heading: String, values: Set[String]): Table =
    copy(rows = rows.filter(r => values.contains(r(indexOfHeading(heading)).value)))

  def sortOn(heading: String): Table =
    copy(rows = rows.sortBy(r => r(indexOfHeading(heading)).value))

  def values(heading: String): Vector[String] = col(heading).map(_.value)

  def numbers(heading: String): Vector[Option[BigDecimal]] = ??? // None om Str

  def sum(heading: String): BigDecimal = numbers(heading).flatten.sum

  def avg(heading: String): BigDecimal = sum(heading) / numbers(heading).length

  def show: String = {
    def maxValueLength(heading: String): Int = col(heading).map(_.value.length).max
    val colSize = headings.map(h => maxValueLength(h) max h.length)
    val paddedHeadings = headings.map(h => h.padTo(colSize(indexOfHeading(h)), ' '))
    val heading = paddedHeadings.mkString("|","|","|")
    val line = "-" * heading.size
    val paddedRows = rows.map(r =>
      headings.indices.map(i => r(i).value.padTo(colSize(i),' ')).mkString("|","|","|")
    ).mkString("\n")
    s"$line\n$heading\n$line\n$paddedRows\n$line\n (nRows, nCols) == $dim\n"
  }
}

object Table {
  def load(from: String, separator: Char): Table = ???
  def save(fileName: String, separator: Char): Unit = ???
}
