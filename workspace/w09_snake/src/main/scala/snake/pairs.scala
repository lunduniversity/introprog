package snake

trait Pair[T] {
  def x: T
  def y: T
  def tuple: (T, T) = (x, y)
}

case class Dim(x: Int, y: Int) extends Pair[Int]
object Dim {
  def apply(dim: (Int, Int)): Dim = Dim(dim._1, dim._2)
}


case class Pos private (x: Int, y: Int, dim: Dim) extends Pair[Int] {
  import java.lang.Math.{floorMod => mod}

  def +(p: Pair[Int]): Pos =
    copy(x = mod(x + p.x, dim.x), y = mod(y + p.y, dim.y))

  def -(p: Pair[Int]): Pos =
    copy(x = mod(x - p.x, dim.x), y = mod(y - p.y, dim.y))
}

object Pos {
  import java.lang.Math.{floorMod => mod}
  import scala.util.Random

  def apply(x: Int, y: Int, dim: Dim): Pos =
    new Pos(mod(x, dim.x), mod(y, dim.y), dim)

  def random(dim: Dim): Pos = {
    Pos(Random.nextInt(dim.x), Random.nextInt(dim.y), dim)
  }
}

abstract sealed class Dir(val x: Int, val y: Int) extends Pair[Int]
case object North extends Dir( 0, -1)
case object South extends Dir( 0,  1)
case object East  extends Dir( 1,  0)
case object West  extends Dir(-1,  0)
