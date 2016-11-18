package stats

import cslib.window.SimpleWindow
import java.awt.Color

/** An object for drawing statistical charts from frequency data. */
object Chart {

  /** Create a bar chart from regData with pairs of (label, frequency) */
  def bar(regData: Vector[(String, Int)]): Unit = ???

  /** Create a pie chart from regData with pairs of (label, frequency) */
  def pie(regData: Vector[(String, Int)]): Unit = {
    val title = regData.head._1
    val data = regData.tail
    val sum = regData.head._2

    //Legend
    val textHeight = 14
    val legendHeight = data.size * textHeight

    //Window
    val (width, height) = (400, 400)
    val sw = new SimpleWindow(width, height + legendHeight, title)
    val minSide = Math.min(height, width)
    val padding = Math.sqrt(minSide).toInt + 10

    //Graph
    val side = minSide - padding * 2
    val centerX = width / 2
    val centerY = height / 2
    val iterations = 5 * side
    val paintWidth = 2
    var index = 0
    var colorStart = 0
    val colors = Vector(Color.RED, Color.BLUE, Color.GREEN,
      Color.MAGENTA, Color.ORANGE, Color.PINK, Color.YELLOW, Color.CYAN)

    def addLegend(): Unit = {
      sw.setLineColor(Color.BLACK)
      val y = height - padding + textHeight * (index + 1)
      sw.moveTo(padding + textHeight, y)
      val current = data(index)
      val percent = (current._2 * 1000.0 / sum + 0.5).toInt.toDouble / 10
      sw.writeText(s"$percent%, ${current._1} (${current._2})")

      sw.setLineColor(colors(index % colors.size))
      sw.setLineWidth(textHeight - 2)
      sw.moveTo(padding, y - (textHeight - 2) / 2)
      sw.lineTo(padding + textHeight - 2, y - (textHeight - 2) / 2)
      sw.setLineWidth(paintWidth)
    }

    addLegend()
    val anglePrep = 2 * Math.PI / iterations
    val radius = side / 2

    for (a <- 0 until iterations) {
      if (a - colorStart > data(index)._2 * iterations / sum) {
        index += 1
        colorStart = a
        addLegend()
      }
      sw.moveTo(centerX, centerY)
      val angle = anglePrep * a
      val x = centerX + radius * Math.cos(angle)
      val y = centerY - radius * Math.sin(angle)
      sw.lineTo(x.toInt, y.toInt)
    }
  }
}