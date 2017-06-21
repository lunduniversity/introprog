package turtlegraphics

import cslib.window.SimpleWindow

object Main {
  def main(args: Array[String]): Unit = {
    // Create the window and turtle objects
    val window = new SimpleWindow(500, 500, "Turtlegraphics")
    val t = new Turtle(window, Point(0, 0), 0, false)

    // Move to an initial position
    t.jumpTo(Point(100, 200));
    t.turnNorth()

    // Draw a rectangle
    t.penDown()
    for (i <- 1 to 4) {
      t.turnRight(90)
      t.forward(100)
    }
    t.penUp()

    // Move 50 pixels to the right of the first rectangle
    t.turnRight(90)
    t.forward(200)
    t.turnNorth()

    // Draw a rectangle
    t.penDown()
    for (i <- 1 to 4) {
      t.turnRight(90)
      t.forward(100)
    }
  }
}
