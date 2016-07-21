import models.{ArrayMatrix2D, Matrix2D}
import rules.LifeRule
import views.LifeGuiView


// A setup with the standard Game of Life rule.
object life {
  def main(args: Array[String]): Unit = {
    val boardSize = 40
    var matrix: Matrix2D = ArrayMatrix2D(boardSize, boardSize)
    matrix.randomize()

    LifeGuiView.useRule(LifeRule)
    LifeGuiView.display(matrix)
  }
}

