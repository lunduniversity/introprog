package life

case class Matrix[T](data: Vector[Vector[T]]):
  require(data.forall(row => ???))

  val dim: (Int, Int) = ???

  def apply(row: Int, col: Int): T = ???

  def updated(row: Int, col: Int)(value: T): Matrix[T] = ???

  def foreachIndex(f: (Int, Int) => Unit): Unit = ???

  override def toString = ???

object Matrix:
  def fill[T](dim: (Int, Int))(value: T): Matrix[T] =
    Matrix[T](Vector.fill(dim._1, dim._2)(value))
