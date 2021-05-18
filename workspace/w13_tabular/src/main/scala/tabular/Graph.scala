package tabular

import introprog.PixelWindow
import java.awt.Color

/** An object for drawing graphs (bar charts, pie charts) from data. */
object Graph:

  val defaultColors = Vector(Color.pink, Color.blue, Color.green,
    Color.magenta, Color.orange, Color.red, Color.yellow, Color.cyan)

  /** Create a bar chart from data with pairs of (label, frequency) */
  def bar(
    data: Seq[(String, Int)],
    title: String = "Bar Chart",
    backgroundColor: Color = Color.white,
    lineColor:       Color = Color.black,
    textColor:       Color = Color.black,
    barColors:  Seq[Color] = defaultColors,
    windowWidth:  Int = 600,
    windowHeight: Int = 400
  ): Unit =
    val dataSorted = data.sortBy(_._2).reverse
    val (windowWidth, windowHeight) = (600, 400)
    val pw =
      new PixelWindow(windowWidth, windowHeight, title, backgroundColor, lineColor)
    val minSide = Math.min(windowWidth, windowHeight)
    val padding = Math.sqrt(minSide).toInt + 10

    //Graph
    val axisW = 1
    val lineHeight = 14
    val graphW = windowWidth - 2 * padding - axisW - lineHeight
    val graphH = windowHeight - 2 * padding - axisW
    val graphX = axisW + padding
    val graphY = windowHeight - padding
    val barW = graphW / (2 * data.size)
    val yMax = data.map(_._2).max + 1

    pw.line( // x-axis
      x1 = padding, y1 = windowHeight - padding,
      x2 = windowWidth - padding, y2 = windowHeight - padding,
      color = lineColor, lineWidth = axisW
    )

    pw.line( // y-axis
      x1 = padding, y1 = windowHeight - padding,
      x2 = padding, y2 = padding,
      color = lineColor, lineWidth = axisW
    )

    //Bars
    for (bar, index) <- dataSorted.zipWithIndex do
      //pw.setLineColor(Color.RED)
      val x = graphX + barW + graphW * index / dataSorted.size
      val y = graphY - graphH * bar._2 / yMax

      pw.line(
        x1 = x, y1 = graphY,
        x2 = x, y2 = y,
        color = barColors(index % barColors.size), lineWidth = barW
      )

      //text
      pw.drawText(
        text = bar._1,
        x = x - bar._1.length * 3,
        y = graphY + lineHeight / 2,
        color = textColor
      )

      //value
      pw.drawText(
        text = bar._2.toString,
        x = x - bar._2.toString.length * 3,
        y = y - lineHeight * 2,
        color = textColor
      )

  /** Create a pie chart from data with pairs of (label, frequency) */
  def pie(
    data: Vector[(String, Int)],
    title: String = "Pie Chart",
    backgroundColor: Color = Color.white,
    lineColor:       Color = Color.black,
    textColor:       Color = Color.black,
    pieColors:  Seq[Color] = defaultColors,
    windowWidth:  Int = 500,
    windowHeight: Int = 500
  ): Unit =
    val dataSorted = data.sortBy(_._2).reverse
    val sum = data.map(_._2).sum
    //Legend
    val textHeight = 15
    val legendHeight = data.size * textHeight

    //Window
    val pw = new PixelWindow(windowWidth, windowHeight + legendHeight, title, backgroundColor, textColor)
    val minSide = Math.min(windowHeight, windowWidth)
    val padding = Math.sqrt(minSide).toInt + 10

    //Graph
    val side = minSide - padding * 2
    val centerX = windowWidth / 2.0
    val centerY = windowHeight / 2.0
    val iterations = 5 * side
    val paintWidth = 2
    val legendWidth = data.map(_._1.length).max
    val maxWidth = 20
    var index = 0
    var colorStart = 0

    def addLegend(): Unit =
      val y = windowHeight - padding + textHeight * (index + 1)
      val current = dataSorted(index)
      val percent = (current._2 * 1000.0 / sum + 0.5).toInt.toDouble / 10
      pw.drawText(text = f"${current._1.padTo(legendWidth,' ').take(maxWidth)} ${current._2}%3d ($percent%2.0f%%)",
        x = padding + textHeight, y = y - textHeight, color = textColor,
        size = textHeight
      )

      pw.line(
        x1 = padding, y1 = y - (textHeight - 2) / 2,
        x2 = padding + textHeight - 2, y2 = y - (textHeight - 2) / 2,
        color = pieColors(index % pieColors.size),
        lineWidth = textHeight - 2
      )

    addLegend()
    val anglePrep = 2 * Math.PI / iterations
    val radius = side / 2

    for a <- 0 until iterations do
      if a - colorStart > dataSorted(index)._2 * iterations / sum then
        index += 1
        colorStart = a
        addLegend()
      val angle = anglePrep * a
      val x = centerX + radius * Math.cos(angle)
      val y = centerY - radius * Math.sin(angle)
      pw.line(
        x1 = centerX.round.toInt, y1 = centerY.round.toInt,
        x2 = x.round.toInt, y2 = y.round.toInt,
        color = pieColors(index % pieColors.size),
        lineWidth = 2
      )
