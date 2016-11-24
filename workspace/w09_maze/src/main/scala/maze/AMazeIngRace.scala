package maze

import cslib.window.SimpleWindow
import java.awt.Color
import java.io.File
import java.nio.file.Paths

object AMazeIngRace {
  def printMazesFromResources(): Unit = {
    for (mazeNbr <- 1 to 4) {
      val filename = s"maze$mazeNbr.txt"
      val maze = Maze.fromFile(getResourcePath(filename))
      println(maze)
    }
  }

  def drawMazesFromResources(window: SimpleWindow): Unit = {
    for (mazeNbr <- 1 to 4) {
      val filename = s"maze$mazeNbr.txt"
      window.moveTo(10, 100)
      window.writeText(s"Click to draw $filename")
      window.waitForMouseClick()
      window.clear()
      
      /*
      //Run this code when draw in Maze is implemented (remove block comment)
      val maze = Maze.fromFile(getResourcePath(filename))
      maze.draw(window)
      */
      
      /*
      //Run this code when walk in MazeTurtle is implemented (remove block comment)
      val turtle = new MazeTurtle(window, maze, Color.MAGENTA)
      window.moveTo(10, 50)
      window.writeText(s"Click to walk!")
      window.waitForMouseClick()
      turtle.walk()
      */
    }
  }

  def createAndDrawRandomMaze(rows: Int, cols: Int, window: SimpleWindow): Unit = {
    if (rows < 20 || cols < 20) {
      println("Please choose a larger value for rows and cols!")
    } else {
      window.moveTo(10, 40)
      window.writeText(s"Click to draw random maze of size ($rows,$cols)")
      window.waitForMouseClick()
      window.clear()
      /*
      //Run this code when random in Maze is implemented
      val maze = Maze.random(rows, cols)
      maze.draw(window)
      val turtle = new MazeTurtle(window, maze, Color.RED)
      window.moveTo(10, 70)
      window.writeText(s"Click to walk!")
      window.waitForMouseClick()
      turtle.walk()
      */
    }
  }

  def getResourcePath(resource: String) : String = {
    Paths.get(getClass.getResource("/" + resource).toURI).toString
  }

  def main(args: Array[String]): Unit = {
    val path = getResourcePath("")
    println(s"Resource path detected: $path")
    val w = new SimpleWindow(800, 800, "A-Maze-Ing Race")
    printMazesFromResources()
    w.moveTo(10, 100)
    w.writeText(s"Printed mazes in $path")
    w.moveTo(10, 200); w.writeText("CLICK TO CONTINUE!")
    w.waitForMouseClick()
    w.clear()
    drawMazesFromResources(w)
    createAndDrawRandomMaze(50, 50, w) //recommended sizes is between 20-100
    w.moveTo(10, 20); w.writeText("GOODBYE! File -> Quit or Ctrl+Q to exit.")
  }

}
