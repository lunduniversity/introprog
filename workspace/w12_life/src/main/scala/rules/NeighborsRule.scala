package rules

import models._

// A rule that returns a matrix with the number of neighbors for every cell.
// Useful in debugging.
object NeighborsRule extends Rule {
  def apply(m: Matrix2D, row: Int, col: Int): Int = {
    val aliveNeighbors = m.mooreNeighborsStates(row, col).sum
    aliveNeighbors
  }
}