package rules

import models._

// A rule that returns a matrix with the number of neighbors for every cell.
// Useful in debugging.
object NeighborsRule extends Rule {
  def apply(m: Matrix2D, i: Int, j: Int): Int = {
    val aliveNeighbors = m.mooreNeighborsStates(i, j).sum
    aliveNeighbors
  }
}