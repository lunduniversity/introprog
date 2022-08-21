import exempelVego2.*

object vego2Test:
  def main(args: Array[String]): Unit =
    val g = new Gurka(200)
    val t = new Tomat(70)
    println(g)
    println(t)
    val gs = Vector(g, t)
    println(gs)
    gs.foreach(_.skala())
    gs.foreach(println)
