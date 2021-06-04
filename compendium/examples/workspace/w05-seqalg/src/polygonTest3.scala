object polygonTest3:
  def main(args: Array[String]): Unit =
    val pw = new PolygonWindow(200,200)
    val poly = new PolygonArray(100)

    poly.append((50,50), (100,100), (50,100), (30,50))
    println(poly)
    poly.draw(pw)

    poly.insert(2, (100,150))
    println(poly)
    poly.draw(pw)

    poly.remove(0)
    println(poly)
    poly.draw(pw)
