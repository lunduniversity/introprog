package rules

import models._

// Implements the famous Game of Life rule.
// https://en.wikipedia.org/wiki/Conway's_Game_of_Life
object LifeRule extends Rule {
  def apply(m: Matrix2D, row: Int, col: Int): Int = ???
}