package rules

import models._

trait Rule {
  def apply(m: Matrix2D, row: Int, col: Int): Int
}
