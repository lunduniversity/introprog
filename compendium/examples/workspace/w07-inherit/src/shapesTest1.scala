import shapes1.*

object shapesTest1:
  def main(args: Array[String]): Unit =
    val r = Rectangle(pos = (100, 100), dxy = (75, 120))
    println(r)
    val r2 = r.move(dx = 42, dy = 84).move(dx = -1, dy = -1)
    println(r2)
    val t = Triangle((0,0),(4,0), (4,3))
    println(t)
    println(t.move(1,1))
    println(t)
