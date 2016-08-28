import models.{Matrix2D, ArrayMatrix2D}

package object entities {
  val glider: Matrix2D = {
    val m = ArrayMatrix2D(3, 3)
    m.set(0, 1, 1)
    m.set(1, 2, 1)
    m.set(2, 0, 1)
    m.set(2, 1, 1)
    m.set(2, 2, 1)
    m
  }
}

