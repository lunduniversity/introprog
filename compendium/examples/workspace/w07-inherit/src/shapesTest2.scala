import shapes2.*

object shapesTest2:
  def main(args: Array[String]): Unit =
    val sdw = new SimpleDrawingWindow(title="Shapes")
    val r = Rectangle(pos = (100, 100), dxy = (75, 120))
    r.draw(sdw)
    r.move(dx=42, dy=84).draw(sdw)
