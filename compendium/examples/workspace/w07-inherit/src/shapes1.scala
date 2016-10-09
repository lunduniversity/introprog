object shapes1 {
  type Pt = (Double, Double)

  trait Shape {
    def pos: Pt
    def move(dx: Double, dy: Double): Shape
  }

  case class Rectangle(pos: Pt, dxy: Pt) extends Shape {
    def move(dx: Double, dy: Double): Shape =
      Rectangle((pos._1 + dx, pos._2 + dy), dxy)
  }

  case class Circle(pos: Pt, radius: Double) extends Shape {
    def move(dx: Double, dy: Double): Shape =
      Circle((pos._1 + dx, pos._2 + dy), radius)
  }

  case class Triangle(pos: Pt, dxy1: Pt, dxy2: Pt) extends Shape {
    override def move(dx: Double, dy: Double): Shape =
      Triangle((pos._1 + dx, pos._2 + dy), dxy1, dxy2)
  }
}
