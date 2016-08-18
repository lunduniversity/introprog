package maze;
import cslib.window.SimpleWindow

/**
 *  A class representing a maze.
 */
case class Maze(data: Vector[Vector[Boolean]]) {
  private val wallChar: Char = '#' // or '\u2588' for full block unicode character
  private val blockSize = 9 // must be an odd number and at least 3!
  private val xPadding = (900 - data(0).length * blockSize) / 2
  private val yPadding = (900 - data.length * blockSize) / 2

  /**
   *  Returns a corresponding char from a boolean.
   *  @param b	The boolean which to convert to a char
   */
  def boolToChar(b: Boolean): Char = if (b) wallChar else ' '
  
  /**
   *  Returns a String representation of the maze.
   */
  override def toString =
    data.map(_.map(boolToChar).mkString).mkString("\n")

  /**
   *  Checks if the coordinates x, y is inside the maze and if so returns true, otherwise false.
   *  @param x		The x coordinate
   *  @param y		The y coordinate
   */
  private def insideMaze(x: Int, y: Int): Boolean = (x >= 0 && y >= 0 && x < data(0).length * blockSize && y < data.length * blockSize)

  /**
   *  Returns the x coordinate of the entry of the maze.
   */
  def getXEntry(): Int = xPadding + data(data.length - 1).indexOf(false) * blockSize

  /**
   * Returns the y coordinate of the entry of the maze.
   */
  def getYEntry(): Int = yPadding + (data.length - 1) * blockSize + blockSize / 2 + 1

  /**
   *  Checks if there is a wall left of the coordinates x, y at given direction and if so returns true, otherwise false.
   *  @param direction	The direction of the turtle
   *  @param x					The x coordinate
   *  @param y					The y coordinate
   */
  def wallAtLeft(direction: Int, x: Int, y: Int): Boolean = {
    val dir: Int = if (direction < 0) (360 + direction % 360) else direction % 360
    val xPos: Int = (x - xPadding) / blockSize
    val yPos: Int = (y - yPadding) / blockSize
    if (insideMaze(xPos, yPos)) {
      if (dir == 0) data(yPos - 1)(xPos)
      else if (dir == 90) data(yPos)(xPos - 1)
      else if (dir == 180) data(yPos + 1)(xPos)
      else if (dir == 270) data(yPos)(xPos + 1)
      else true
    } else false
  }

  /**
   *  Checks if there is a wall in front of the coordinates x, y at given direction and if so returns true, otherwise false.
   *  @param direction	The direction of the turtle
   *  @param x					The x coordinate
   *  @param y					The y coordinate
   */
  def wallInFront(direction: Int, x: Int, y: Int): Boolean = {
    val dir: Int = if (direction < 0) (360 + direction % 360) else direction % 360
    val xPos: Int = x - xPadding
    val yPos: Int = y - yPadding
    if (insideMaze(xPos / blockSize, yPos / blockSize)) {
      if (dir == 0) data(yPos / blockSize)((xPos + 1) / blockSize)
      else if (dir == 90) data((yPos - 1) / blockSize)(xPos / blockSize)
      else if (dir == 180) data(yPos / blockSize)((xPos - 1) / blockSize)
      else if (dir == 270) data((yPos + 1) / blockSize)(xPos / blockSize)
      else true
    } else false
  }

  /**
   *  Checks it the coordinates x, y is at the exit of the maze.
   *  @param x					The x coordinate
   *  @param y					The y coordinate
   */
  def atExit(x: Int, y: Int): Boolean = (y < yPadding + blockSize / 2)

  /**
   *  Goes through the the maze and for every spot that is a wall draws a brick of size blockSize in SimpleWindow.
   *  @param w		The window in which to draw the maze
   */
  def draw(w: SimpleWindow): Unit = {
    
    //Insert code here!

    /**
     *  Builds a brick in the wall at coordinates x, y of size blockSize.
     *  @param x					The x coordinate at which to build the brick
     *  @param y					The y coordinate at which to build the brick
     */
    def brickInTheWall(x: Int, y: Int): Unit = {
      w.setLineWidth(1)
      for (j <- 1 until blockSize - 1) {
        for (i <- 1 until blockSize - 1) {
          w.moveTo(y * blockSize + xPadding + i, x * blockSize + yPadding + j)
          w.lineTo(y * blockSize + xPadding + i, x * blockSize + yPadding + j)
        }
      }
    }
  }
}

/**
 *  An object representing a maze.
 */
object Maze {

  /**
   *  Returns a Maze from a vector of Strings.
   *  @param xs		The vector of Strings that represent the maze
   */
  def fromStrings(xs: Vector[String]): Maze = {
    def charIsWall(ch: Char): Boolean = if (ch == ' ') false else true
    val data: Vector[Vector[Boolean]] = xs.map(_.map(charIsWall).toVector)
    new Maze(data)
  }

  /**
   *  Returns a Maze from a specified file.
   *  @param fileName		The name of the file that represent the maze
   */
  def fromFile(fileName: String): Maze = {
    val lines = scala.io.Source.fromFile(fileName).getLines
    fromStrings(lines.toVector)
  }

  /**
   *  Returns a Maze from a sequence of Strings.
   *  @param rows		The sequence of Strings that represent the maze
   */
  def apply(rows: String*): Maze = fromStrings(rows.toVector)

  /**
   *  Creates and returns a random maze.
   *  @param rows		The number of rows for the maze
   *  @param cols		The number of columns for the maze
   */
  def random(rows: Int, cols: Int): Maze = {
    import scala.collection.mutable.ArrayBuffer
    val rand = scala.util.Random
    val maze: ArrayBuffer[ArrayBuffer[Boolean]] = ArrayBuffer.fill(rows, cols)(true)
    var counter = 0
    val buffer: ArrayBuffer[List[Int]] = ArrayBuffer()

    /**
     *  Adds all surrounding walls of a specified coordinate to the buffer of possible walls to choose from unless they are already added or outside of the maze with a margin of one element.
     *  @param row		The row index of the coordinate
     *  @param col		The column index of the coordinate
     */
    def addSurroundingWallsToBuffer(row: Int, col: Int): Unit = {
      if (col < cols - 2 && maze(row)(col + 1)) {
        val eastWall = List(row, col + 1)
        if (!buffer.contains(eastWall))
          buffer += eastWall
      }
      if (col > 1 && maze(row)(col - 1)) {
        val westWall = List(row, col - 1)
        if (!buffer.contains(westWall))
          buffer += westWall
      }
      if (row > 1 && maze(row - 1)(col)) {
        val northWall = List(row - 1, col)
        if (!buffer.contains(northWall))
          buffer += northWall
      }
      if (row < rows - 2 && maze(row + 1)(col)) {
        val southWall = List(row + 1, col)
        if (!buffer.contains(southWall))
          buffer += southWall
      }
    }
    
    /**
     *  Checks whether there are three filled walls around the specified coordinate, returns true if that is the case, otherwise false.
     *  @param row		The row index of the coordinate
     *  @param col		The column index of the coordinate
     */
    def threeWallsAround(row: Int, col: Int): Boolean = {
      var counter = 0
      if (maze(row)(col + 1)) counter += 1
      if (maze(row)(col - 1)) counter += 1
      if (maze(row - 1)(col)) counter += 1
      if (maze(row + 1)(col)) counter += 1
      (counter == 3)
    }
    
    //Insert code here!

    new Maze(maze.map(_.toVector).toVector)
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
      for (r <- 0 until rows) yield if (row != r) data(r) else data(r).updated(col, elem)
    new BoolMatrix(copyUpdate.toVector)
  }

  override def toString =
    data.map(_.mkString("\n  Vector(", ", ", ")")).mkString("BoolMatrix(", ",", ")")
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

  def apply(row: Int, col: Int): T = {
    if (row < rows && col < cols) { data(row)(col) } else { println("ogiltig indexering!!"); return data(0)(0) }
  }

  def updated(row: Int, col: Int, elem: T) = {
    val copyUpdate =
      for (r <- 0 until rows) yield if (row != r) data(r) else data(r).updated(col, elem)
    new Matrix(copyUpdate.toVector)
  }

  override def toString =
    data.map(_.mkString("\n  Vector(", ", ", ")")).mkString("Matrix(", ",", ")")
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