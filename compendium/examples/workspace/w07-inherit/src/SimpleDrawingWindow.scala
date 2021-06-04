class SimpleDrawingWindow(
  title: String = "Untitled", 
  size: (Int, Int) = (640, 400),
) extends 
    introprog.PixelWindow(title = title, width = size._1, height = size._2) 
    with DrawingWindow:

  private var penPos = (0.0, 0.0)

  override def penTo(pt: (Double, Double)): Unit  = penPos = pt

  override def drawTo(pt: (Double, Double)): Unit = 
    line(penPos._1.round.toInt, penPos._2.round.toInt, 
         pt._1.round.toInt, pt._2.round.toInt)
    penPos = pt
