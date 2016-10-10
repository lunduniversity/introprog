trait CanDraw {
  def draw(dw: DrawingWindow): Unit
}

trait DrawingWindow {
  def penTo(pt: (Double, Double)): Unit
  def drawTo(pt: (Double, Double)): Unit
}



