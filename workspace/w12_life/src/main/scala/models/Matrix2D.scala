package models

import rules.Rule

import scala.util.Random

trait Matrix2D {
  val cols: Int
  val rows: Int

  val states: Int

  def get(row: Int, col: Int): Int
  def set(row: Int, col: Int, state: Int)

  // Checks it the given position exist in the matrix.
  def isWithinMatrix(row: Int, col: Int): Boolean

  // Returns a list with the positions of all cells.
  def indices: Seq[(Int, Int)]

  // Creates a clone of the matrix.
  def cloneMatrix(): Matrix2D

  // Applies a rule to the entire matrix and returns a new one with the resulting matrix.
  // Use the indices method to get all positions.
  // Make sure to create a copy to put the result in using cloneMatrix,
  // the current matrix should *not* be changed.
  def applyRule(rule: Rule): Matrix2D

  // Returns a string-representation of the board that can be used for saving to file.
  // Part of an optional task, implementation not required to pass lab.
  def toFileFormat: String

  // Enables the `m(x)(y)` notation
  def apply(row: Int)(col: Int): Int = {
    get(row, col)
  }

  // Enables the `m(x)(y) = s` notation
  def update(row: Int, col: Int, cell: Int) = {
    set(row, col, cell)
  }

  // Zeroes the entire matrix
  def clear() {
    for((row, col) <- indices) this(row, col) = 0
  }

  // Places another matrix at a position on this matrix
  // with the upper-left corner at (row, col)
  def place(m2: Matrix2D, row: Int, col: Int) {
    for ((row2, col2) <- m2.indices) {
      this.set(row + row2, col + col2, m2(row2)(col2))
    }
  }

  // Returns an array with the positions of all Moore neighbors
  // surrounding the passed in cell position with manhattan distance r.
  // Should not check if a neighboring cell is within the matrix.
  // You only have to handle the case where r=1 to pass the lab.
  //  - https://en.wikipedia.org/wiki/Moore_neighborhood
  def mooreNeighborsPositions(row: Int, col: Int, r: Int = 1): Array[(Int, Int)] = ???

  // Returns an array with the cell values of all Moore neighbors
  // surrounding the passed in cell position with manhattan distance r.
  // The cell states can be stored in any order in the array.
  // Checks for if a certain position is within the matrix 
  // should be done by calling isWithinMatrix.
  // You only have to handle the case where r=1 to pass the lab.
  //  - https://en.wikipedia.org/wiki/Moore_neighborhood
  def mooreNeighborsStates(row: Int, col: Int, r: Int = 1): Array[Int] = ???

  // Returns an array with the positions of all Von Neumann neighbors
  // surrounding the passed in cell position with Chebyshev distance *r*.
  // Should not check if a neighboring cell is within the matrix.
  //  - https://en.wikipedia.org/wiki/Von_Neumann_neighborhood
  def vonNeumannNeighborsPositions(row: Int, col: Int, r: Int = 1): Array[(Int, Int)] = {
    val numberOfNeighbors = (Math.pow(r*2+1, 2).toInt-1)/2
    val neighborPositions = Array.fill(numberOfNeighbors){(0, 0)}
    var i = 0
    for(cur_row <- row-r to row+r;
        cur_col <- (col - r + Math.abs(row-cur_row)) to (col + r - Math.abs(row-cur_row))) {
      val isCenterCell = row == cur_row && col == cur_col
      if(!isCenterCell) {
        neighborPositions(i) = (cur_row, cur_col)
        i += 1
      }
    }
    neighborPositions
  }

  // Returns an array with the states of the Von Neumann neighbors of a specific cell.
  // The cell states in the array could be stored in any order.
  //  - https://en.wikipedia.org/wiki/Von_Neumann_neighborhood
  def vonNeumannNeighborsStates(row: Int, col: Int, r: Int = 1): Array[Int] = {
    val numberOfNeighbors = (Math.pow(r*2+1, 2).toInt-1)/2
    val neighborStates = Array.fill(numberOfNeighbors){0}
    var i = 0
    for((cur_row, cur_col) <- vonNeumannNeighborsPositions(row, col, r)) {
      // Check that the cell is within the matrix, otherwise we will get out of bounds.
      if(isWithinMatrix(cur_row, cur_col)) {
        neighborStates(i) = this(cur_row)(cur_col)
        i += 1
      }
    }
    neighborStates
  }

  def randomize(): Unit = {
    val random = new Random()
    for((row, col) <- this.indices) {
      this.set(row, col, random.nextInt(states))
    }
  }

  def flipHorizontal(): Matrix2D = {
    val m2 = this.cloneMatrix()
    for((row, col) <- this.indices) {
      m2(rows-1-row, col) = this(row)(col)
    }
    m2
  }

  def flipVertical(): Matrix2D = {
    val m2 = this.cloneMatrix()
    for((row, col) <- this.indices) {
      m2(row, cols-1-col) = this(row)(col)
    }
    m2
  }

  def rotateRight(): Matrix2D = {
    val m2 = this.cloneMatrix()
    for((row, col) <- this.indices) {
      m2(row, col) = this(cols-1-col)(row)
    }
    m2
  }
}
