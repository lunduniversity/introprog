case class Maze(data: Vector[Vector[Boolean]]) {
  val wallString: String = "\u2588"  // full block unicode character
  override def toString = 
    data.map(_.map(b => if (b) wallString else " ").mkString).mkString("","\n","")  
}
object Maze {
  def apply(rows: String*): Maze =  {
    def charToBool(ch: Char): Boolean = if (ch != ' ') true else false
    val data = for (s <- rows) yield s.map(charToBool).toVector
    new Maze(data.toVector)
  }
  def fromFile(fileName: String): Maze = {
    val data = scala.io.Source.fromFile("maze-simple.txt").getLines.toVector
    Maze(data: _*)
  }
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



