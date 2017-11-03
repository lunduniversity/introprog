package maze
import cslib.window.SimpleWindow

case class Maze(
  val matrix: Vector[Vector[Boolean]],
  val canvasSize: (Int, Int) = (500, 500),
  val wallChar: Char         = '#',
  val noWallChar: Char       = ' ',
  val blockSize: Int         = 7
) {
  assert(blockSize % 2 == 1, "blockSize must be odd")
  assert(blockSize > 2,      "blockSize must be at least 3")

  private val (cols, rows) = (matrix(0).length, matrix.length)
  private val xPadding: Int = (canvasSize._1 - cols * blockSize) / 2
  private val yPadding: Int = (canvasSize._2 - rows * blockSize) / 2

  private def rowFromY(y: Int): Int = (y - yPadding) / blockSize
  private def colFromX(x: Int): Int = (x - xPadding) / blockSize

  private def matrixAt(x: Int, y: Int): Boolean = matrix(rowFromY(y))(colFromX(x))

  private def isInsideMaze(x: Int, y: Int): Boolean = {
    val (c, r) = (colFromX(x), rowFromY(y))
    c >= 0 && r >= 0 && c < cols && r < rows
  }

  def entryX: Int = xPadding + matrix(matrix.size - 1).indexOf(false) * blockSize
  def entryY: Int = yPadding + (matrix.size - 1) * blockSize + blockSize / 2 + 1
  def entryPos: (Int, Int) = (entryX, entryY)

  def isWallAtLeft(dir: Int, pos: (Int, Int)): Boolean = {
    val (x, y) = pos
    if (!isInsideMaze(x, y)) false
    else if (dir == 0)   matrixAt(x    , y - 1)
    else if (dir == 90)  matrixAt(x - 1, y    )
    else if (dir == 180) matrixAt(x    , y + 1)
    else if (dir == 270) matrixAt(x + 1, y    )
    else true
  }

  def isWallInFront(dir: Int, pos: (Int, Int)): Boolean = {
    val (x, y) = pos
    if (!isInsideMaze(x, y)) false
    else if (dir == 0)   matrixAt(x + 1, y    )
    else if (dir == 90)  matrixAt(x    , y - 1)
    else if (dir == 180) matrixAt(x - 1, y    )
    else if (dir == 270) matrixAt(x    , y + 1)
    else true
  }

  def isAtExit(pos: (Int, Int)): Boolean = pos._2 < yPadding + blockSize / 2

  def draw(w: SimpleWindow): Unit = {
    //Lokal metod som ritar ett väggelement givet rad och kolumn i matrix:
    def drawAnotherBrickInTheWall(row: Int, col: Int): Unit = {
      w.setLineWidth(1)
      for (dx <- 1 to blockSize - 1) {
          val x = col * blockSize + xPadding + dx
          val y = row * blockSize + yPadding + 1
          w.moveTo(x, y)
          w.lineTo(x, y + blockSize - 2)
      }
    }

    //**** BEGIN rita ut labyrint *****
    println("*** Saknad implementation: rita ut labyrint ***")
    //****** END rita ut labyrint ******
  }

  override def toString = {
    def toChar(b: Boolean): Char = if (b) wallChar else noWallChar
    matrix.map(_.map(toChar).mkString).mkString("\n")
  }
}

object Maze {
  def apply(rows: String*): Maze = {
    val booleanMatrix = rows.map(_.map(_ != ' ').toVector).toVector
    new Maze(booleanMatrix)
  }

  def fromFile(fileName: String): Maze = {
    println(s"Loading: $fileName")
    val lines = scala.io.Source.fromFile(fileName).getLines
    apply(lines.toVector:_*)
  }
}
