class SimpleDrawingWindow(title: String = "Untitled", size: (Int, Int) = (640, 400))
  extends cslib.window.SimpleWindow(size._1, size._2, title)
    with DrawingWindow {

  def xPos(pt: (Double, Double)): Int = pt._1.round.toInt
  def yPos(pt: (Double, Double)): Int = pt._2.round.toInt

  override def penTo(pt: (Double, Double)): Unit  = moveTo(xPos(pt), yPos(pt))
  override def drawTo(pt: (Double, Double)): Unit = lineTo(xPos(pt), yPos(pt))
}