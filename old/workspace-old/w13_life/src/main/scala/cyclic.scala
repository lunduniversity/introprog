import javafx.scene.paint.Color

import models.{ArrayMatrix2D, Matrix2D}
import rules.CyclicRule
import views.CellularGuiView


// A setup with the colorful cyclic cellular automata.
object cyclic {
  def main(args: Array[String]): Unit = {
    // Try changing the number of states to 8, 12, 16 or 20!
    val states = 14
    val matrix: Matrix2D = ArrayMatrix2D(150, 150, states)
    matrix.randomize()

    // Generate the colors to use for the states
    val colorRange = 360 // 360 is the full range of colors, 180 is half, etc.
    val colors = for(hue <- 0 until colorRange by colorRange/states) yield Color.hsb(hue, 1, 1)
    CellularGuiView.useColors(colors.toArray)

    CellularGuiView.useRule(CyclicRule)
    CellularGuiView.display(matrix)
  }
}

