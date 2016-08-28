import models.{ArrayMatrix2D, Matrix2D}
import rules.LifeRule
import views.CellularGuiView


// A cool demo where you can see 4 gliders exploding in a crash without leaving debree.
object glidercrash {
  def main(args: Array[String]): Unit = {
    val boardSize = 50
    val matrix: Matrix2D = ArrayMatrix2D(boardSize, boardSize)

    // 4 gliders will be created and rotated such that they are on a trajectory towards the center of the board
    // if t is a whole number it will cause a quick elimination
    // if t is a whole number + 1/2 it will cause a longer lived explosion
    val t = 10.5
    var vOffset = -1
    var hOffset = -1
    var glider = entities.glider
    for(i <- 0 until 4) {
      val row = ((boardSize-1)/2+hOffset*t).toInt
      val col = ((boardSize-1)/2+vOffset*t).toInt
      matrix.place(glider, row, col)
      val tmp = vOffset
      vOffset = -hOffset
      hOffset = tmp
      glider = glider.rotateRight()
    }

    CellularGuiView.useRule(LifeRule)
    CellularGuiView.display(matrix)
  }
}

