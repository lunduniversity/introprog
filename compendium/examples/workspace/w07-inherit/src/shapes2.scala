object shapes2:
  type Pt = (Double, Double)

  trait Shape:
    def pos: Pt
    def move(dx: Double, dy: Double): Shape

  case class Rectangle(pos: Pt, dxy: Pt) extends Shape with CanDraw: // inmixning
    def move(dx: Double, dy: Double): Rectangle =
      Rectangle((pos._1 + dx, pos._2 + dy), dxy)

    def draw(dw: DrawingWindow): Unit = // implementation av draw
      dw.penTo(pos)
      dw.drawTo((pos._1 + dxy._1, pos._2))
      dw.drawTo((pos._1 + dxy._1, pos._2 + dxy._2))
      dw.drawTo((pos._1, pos._2 + dxy._2))
      dw.drawTo(pos)
