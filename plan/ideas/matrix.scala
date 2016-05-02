class Turtle 
 
case class Maze(data: Vector[Vector[Boolean]]) {
  val wallChar: Char = '#'  // or '\u2588' for full block unicode character
  def boolToWall(b: Boolean): Char =  if (b) wallChar else ' '
  override def toString = 
    data.map(_.map(boolToWall).mkString).mkString("\n")  
  def draw(t: Turtle) = ???
}

object Maze {
  def fromStrings(xs: Vector[String]): Maze = {
    def charToBool(ch: Char): Boolean = if (ch != ' ') true else false
    val data: Vector[Vector[Boolean]] = xs.map(_.map(charToBool).toVector)
    new Maze(data)
  }
  def fromFile(fileName: String): Maze = {
    val lines = scala.io.Source.fromFile(fileName).getLines
    fromStrings(lines.toVector)
  }
  def apply(rows: String*): Maze = fromStrings(rows.toVector) 
  def random(rows: Int, cols: Int): Maze = ???
}

/** A Boolean Matrix */

class BoolMatrix private (data: Vector[Vector[Boolean]]) {
  val rows: Int = data.size

  val cols: Int = if (rows > 0) data(0).size else 0

  val size: (Int, Int) = (rows, cols)

  def apply(row: Int, col: Int): Boolean = data(row)(col)

  def updated(row: Int, col: Int, elem: Boolean) = {
    val copyUpdate = 
      for (r <- 0 until rows) yield 
        if (row != r) data(r) else data(r).updated(col, elem)
    new BoolMatrix(copyUpdate.toVector)
  }

  override def toString = 
    data.map(_.mkString("\n  Vector(",", ",")")).mkString("BoolMatrix(",",",")")
}

object BoolMatrix {
  def ofDim[T](rows: Int, cols: Int)(init: Boolean): BoolMatrix = 
    new BoolMatrix(Vector.fill(rows)(Vector.fill(cols)(init)))

  def apply(data: Vector[Boolean]*): BoolMatrix = {
    val maxRowColSize = (0 +: data.map(_.size)).max 
    val dataPadded = data.map(_.padTo(maxRowColSize, false))
    new BoolMatrix(dataPadded.toVector)
  }
}


/*** A generic Matrix ***/

class Matrix[T] private (data: Vector[Vector[T]]) {
  val rows: Int = data.size

  val cols: Int = if (rows > 0) data(0).size else 0

  val size: (Int, Int) = (rows, cols)

  def apply(row: Int, col: Int): T = data(row)(col)

  def updated(row: Int, col: Int, elem: T) = {
    val copyUpdate = 
      for (r <- 0 until rows) yield 
        if (row != r) data(r) else data(r).updated(col, elem)
    new Matrix(copyUpdate.toVector)
  }

  override def toString = 
    data.map(_.mkString("\n  Vector(",", ",")")).mkString("Matrix(",",",")")
}

object Matrix {
  def ofDim[T](rows: Int, cols: Int)(init: T): Matrix[T] = 
    new Matrix(Vector.fill(rows)(Vector.fill(cols)(init)))

  def apply[T](data: Vector[T]*): Matrix[T] = {
    val firstRowColSize = if (data.size > 0) data(0).size else 0 
    val isAllRowsEqualColSize = data.map(_.size).forall(_ == firstRowColSize)
    assert(isAllRowsEqualColSize, "all rows must have equal column size")
    new Matrix(data.toVector)
  }
}

/* Example usage

scala> val m1 = Matrix.ofDim(2,5)(false)
scala> val m2 = Matrix(Seq(1,2,3),Seq(1,2,3))

*/



