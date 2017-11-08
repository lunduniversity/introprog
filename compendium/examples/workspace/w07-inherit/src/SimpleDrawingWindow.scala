class SimpleDrawingWindow(title: String = "Untitled",
                          size: (Int, Int) = (640, 400))
  extends cslib.window.SimpleWindow(size._1, size._2, title)
    with DrawingWindow {

  override def penTo(pt: (Double, Double)): Unit  =
    moveTo(pt._1.round.toInt, pt._2.round.toInt)

  override def drawTo(pt: (Double, Double)): Unit =
    lineTo(pt._1.round.toInt, pt._2.round.toInt)
}