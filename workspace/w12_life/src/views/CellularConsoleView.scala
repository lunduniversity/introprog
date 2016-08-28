package views

import models.Matrix2D

object CellularConsoleView extends CellularView2D {
  def display(m: Matrix2D): Unit = {
    println()
    for(i <- 0 until m.cols) {
      for(j <- 0 until m.rows) {
        if(m(i)(j) == 0) {
          print("  ")
        } else {
          print(m(i)(j) + " ")
        }
      }
      println()
    }
  }
}
