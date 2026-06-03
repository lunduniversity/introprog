//> using scala 3.8.3
//> using dep org.knowm.xchart:xchart:4.0.0

import org.knowm.xchart.{XYChart, XYChartBuilder, BitmapEncoder}
import org.knowm.xchart.BitmapEncoder.BitmapFormat
import org.knowm.xchart.style.markers.SeriesMarkers
import org.knowm.xchart.style.Styler.LegendPosition
import java.awt.{Color, Font, BasicStroke}

// matplotlib's default "tab10" color cycle, in the order series are added
val tab10 = Seq(
  Color(0x1f, 0x77, 0xb4), // C0 blue
  Color(0xff, 0x7f, 0x0e), // C1 orange
  Color(0x2c, 0xa0, 0x2c), // C2 green
  Color(0xd6, 0x27, 0x28), // C3 red
  Color(0x94, 0x67, 0xbd), // C4 purple
  Color(0x8c, 0x56, 0x4b)  // C5 brown
)

val gridGray = Color(0xb0, 0xb0, 0xb0)
val dashedThin =
  BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, Array(3f, 3f), 0f)
val lineStroke = BasicStroke(1.5f)
val sansLarge = Font("SansSerif", Font.PLAIN, 16)
val sansMed   = Font("SansSerif", Font.PLAIN, 13)
val sansSmall = Font("SansSerif", Font.PLAIN, 12)

case class Row(algorithm: String, inputSize: Double, inputType: String, timeMs: Double)

val inputSizes: Seq[Double] =
  for i <- 0 until 3; _ <- 0 until 3 yield 1000.0 * math.pow(10, i)

val inputTypes = Seq("Random", "Sorted", "Reversed")

// ---- The faked data  ----
val times = Seq(
  12, 1, 30, 150, 2, 450, 1800, 10, 9000,   // QuickSort
  20, 20, 20, 200, 200, 200, 2500, 2500, 2500 // MergeSort
).map(_.toDouble)

val algorithms = Seq.fill(9)("QuickSort") ++ Seq.fill(9)("MergeSort")
val inputSizeCol = inputSizes ++ inputSizes
val inputTypeCol = Seq.fill(6)(inputTypes).flatten

val data: Seq[Row] =
  algorithms.indices.map(i => Row(algorithms(i), inputSizeCol(i), inputTypeCol(i), times(i)))

// ---- Build a chart; logScale toggles log axes ----
def buildChart(logScale: Boolean): XYChart =
  val chart = XYChartBuilder()
    .width(1000).height(600)
    .title("Comparison of QuickSort and MergeSort Performance by Input Type")
    .xAxisTitle("Input Size (n)")
    .yAxisTitle("Time (ms)")
    .build()

  val s = chart.getStyler
  // white backgrounds, like matplotlib
  s.setChartBackgroundColor(Color.WHITE)
  s.setPlotBackgroundColor(Color.WHITE)
  // thin black axes border (matplotlib spines)
  s.setPlotBorderColor(Color.BLACK)
  s.setPlotBorderVisible(true)
  // light gray dashed gridlines
  s.setPlotGridLinesVisible(true)
  s.setPlotGridLinesColor(gridGray)
  s.setPlotGridLinesStroke(dashedThin)
  // legend: white box with a light gray border, upper-left
  s.setLegendPosition(LegendPosition.InsideNW)
  s.setLegendBackgroundColor(Color.WHITE)
  s.setLegendBorderColor(gridGray)
  // fonts
  s.setChartTitleFont(sansLarge)
  s.setAxisTitleFont(sansMed)
  s.setAxisTickLabelsFont(sansSmall)
  s.setLegendFont(sansSmall)
  s.setChartTitleBoxVisible(false)
  // plain integer tick labels (no thousands separators)
  s.setXAxisDecimalPattern("#")
  s.setYAxisDecimalPattern("#")
  s.setMarkerSize(6)
  if logScale then
    s.setXAxisLogarithmic(true)
    s.setYAxisLogarithmic(true)

  var idx = 0
  for
    algo <- data.map(_.algorithm).distinct
    it   <- inputTypes
  do
    val filtered = data.filter(r => r.algorithm == algo && r.inputType == it)
    val xs = filtered.map(_.inputSize).toArray
    val ys = filtered.map(_.timeMs).toArray
    val series = chart.addSeries(s"$algo - $it", xs, ys)
    val color = tab10(idx % tab10.length)
    series.setMarker(SeriesMarkers.CIRCLE)
    series.setLineColor(color)
    series.setMarkerColor(color)
    series.setLineStyle(lineStroke)
    idx += 1

  chart

val outLin = "plot_sort_linear"
val outLog = "plot_sort_log"

@main def run(): Unit =
  BitmapEncoder.saveBitmap(buildChart(false), outLin, BitmapFormat.PNG)
  BitmapEncoder.saveBitmap(buildChart(true), outLog, BitmapFormat.PNG)
  println(s"Wrote $outLin.png and $outLog.png")
