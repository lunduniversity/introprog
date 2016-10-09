trait DrawingWindow {
  def penTo(pt: (Double, Double)): Unit
  def drawTo(pt: (Double, Double)): Unit
}

trait CanDraw {
  def draw(dw: DrawingWindow): Unit
}

