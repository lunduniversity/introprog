import models.{ArrayMatrix2D, Matrix2D}
import rules.LifeRule
import views.{CellularConsoleView, CellularGuiView}


// A setup with the standard Game of Life rule, displayed in the console.
object life_console {
  def main(args: Array[String]): Unit = {
    val boardSize = 20
    var matrix: Matrix2D = ArrayMatrix2D(boardSize, boardSize)
    matrix.randomize()


    CellularConsoleView.display(matrix)

    // To test out a particular rule, calculate the next generation with:
    //    matrix = matrix.applyRule(LifeRule)
    // Then display it just as above
  }
}
