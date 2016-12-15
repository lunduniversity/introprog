package models

import rules.Rule


case class ArrayMatrix2D(cols: Int, rows: Int, states: Int, protected val array: Array[Array[Int]])
  extends Matrix2D {

  override def get(row: Int, col: Int): Int = ???

  override def set(row: Int, col: Int, state: Int) = ???

  override def indices: Seq[(Int, Int)] = ???

  override def isWithinMatrix(row: Int, col: Int): Boolean = ???

  override def applyRule(rule: Rule): Matrix2D = ???

  override def cloneMatrix(): Matrix2D = ???

  // Part of an optional task, implementation not required to pass lab.
  override def toFileFormat: String = ???
}

object ArrayMatrix2D {
  def apply(cols: Int, rows: Int, states: Int = 2): ArrayMatrix2D = ???

  // Part of an optional task, implementation not required to pass lab.
  def fromFileFormat(s: String): ArrayMatrix2D = ???
}
