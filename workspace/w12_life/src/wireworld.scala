import javafx.scene.paint.Color

import models.{ArrayMatrix2D, Matrix2D}
import rules.WireworldRule
import views.LifeGuiView


// A setup with the Wireworld cellular automata.
object wireworld {
  def main(args: Array[String]): Unit = {
    val states = 4
    val matrix: Matrix2D = ArrayMatrix2D(25, 25, states)

    LifeGuiView.useColors(Array(Color.BLACK, Color.BLUE, Color.RED, Color.YELLOW))

    LifeGuiView.useRule(WireworldRule)
    LifeGuiView.display(matrix)
  }
}

