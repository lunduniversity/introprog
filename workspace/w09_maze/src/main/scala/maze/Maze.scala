package maze
import cslib.window.SimpleWindow

/**
 *  A class representing a maze.
 */
case class Maze(data: Vector[Vector[Boolean]]) {
  val wallChar: Char = '#' // or '\u2588' for full block unicode character
  val blockSize = 7 // must be an odd number and at least 3!
  val (windowSizeX, windowSizeY) = (900, 900)  // should not be hardcoded in an improved design...
  val xPadding = (windowSizeX - data(0).length * blockSize) / 2
  val yPadding = (windowSizeY - data.length * blockSize) / 2

  /**
   *  Returns a corresponding wall char from a boolean.
   *  @param b	The boolean which to convert to a char
   */
  def boolToChar(b: Boolean): Char = if (b) wallChar else ' '
  
  /**
   *  Returns a String representation of the maze.
   */
  override def toString: String =
    data.map(_.map(boolToChar).mkString).mkString("\n")

  /**
   *  Checks if the coordinates x, y is inside the maze and if so returns true, otherwise false.
   *  @param x		The x coordinate
   *  @param y		The y coordinate
   */
  private def insideMaze(x: Int, y: Int): Boolean =
    x >= 0 && y >= 0 && x < data(0).length * blockSize && y < data.length * blockSize

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
    val dir: Int = if (direction < 0) 360 + direction % 360 else direction % 360
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
  def atExit(x: Int, y: Int): Boolean = y < yPadding + blockSize / 2

  /**
   *  Goes through the the maze and for every spot that is a wall draws a brick of size blockSize in SimpleWindow.
   *  @param w		The window in which to draw the maze
   */
  def draw(w: SimpleWindow): Unit = {
    // Local function that builds a brick in the wall at coordinates x, y of size blockSize:
    def brickInTheWall(x: Int, y: Int): Unit = {
      w.setLineWidth(1)
      for (j <- 1 until blockSize - 1) {
        for (i <- 1 until blockSize - 1) {
          w.moveTo(y * blockSize + xPadding + i, x * blockSize + yPadding + j)
          w.lineTo(y * blockSize + xPadding + i, x * blockSize + yPadding + j)
        }
      }
    }

    /* *** REPLACE THE CODE BELOW WITH YOUR DRAWING ALGORITHM *** */
    w.writeText("Draw the maze!")  // <---- REMOVE AND REPLACE

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
    val maze: Array[Array[Boolean]] = Array.fill(rows, cols)(true)  //initially all walls
    val wallCandidates: ArrayBuffer[(Int, Int)] = ArrayBuffer()

    //Add surrounding walls of position (row, col) to wallCandidates, if position is inside
    def addSurroundingWalls(row: Int, col: Int): Unit = {
      if (col < cols - 2 && maze(row)(col + 1)) {
        val eastWall = (row, col + 1)
        if (!wallCandidates.contains(eastWall))
          wallCandidates += eastWall
      }
      if (col > 1 && maze(row)(col - 1)) {
        val westWall = (row, col - 1)
        if (!wallCandidates.contains(westWall))
          wallCandidates += westWall
      }
      if (row > 1 && maze(row - 1)(col)) {
        val northWall = (row - 1, col)
        if (!wallCandidates.contains(northWall))
          wallCandidates += northWall
      }
      if (row < rows - 2 && maze(row + 1)(col)) {
        val southWall = (row + 1, col)
        if (!wallCandidates.contains(southWall))
          wallCandidates += southWall
      }
    }
    
    // Returns true if there are three filled walls around the specified position
    def isThreeWallsAround(row: Int, col: Int): Boolean = {
      var counter = 0
      if (maze(row)(col + 1)) counter += 1
      if (maze(row)(col - 1)) counter += 1
      if (maze(row - 1)(col)) counter += 1
      if (maze(row + 1)(col)) counter += 1
      counter == 3
    }
    
    /******** INSERT YOUR CODE HERE BASED ON GIVEN ALGORITHM *******/

    new Maze(maze.map(_.toVector).toVector)
  }

}
