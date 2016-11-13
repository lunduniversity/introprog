package maze
import cslib.window.SimpleWindow

/**
  * Represents a position in a 2D space
  * @param x  coordinate in the first dimension
  * @param y  coordinate in the second dimension
  */
case class Point(x: Double, y: Double) {
  /**
    * Returns a new Point at a new position relative to (x, y)
    * @param dx the relative movement in the first dimension
    * @param dy the relative movement in the second dimension
    * @return a new Point at (x + dx, y + dy)
    */
  def translate(dx: Double, dy: Double) = Point(x + dx, y + dy)
}

/**
  * A Turtle can be used to draw in a SimpleWindow.
  * @param window         The window where drawings are made.
  * @param initPosition   A Point representing the turtle's starting coordinates.
  * @param initAngle      The initial angle of the turtle direction measured in degrees from the X-axis.
  * @param initPenIsDown  A boolean representing the turtle's pen position. True if the pen is down.
  */
class Turtle(window: SimpleWindow,
             val initPosition: Point,
             val initAngle: Double = 90,  // north
             val initPenIsDown: Boolean = true) {

  protected var position: Point = initPosition
  protected var angle: Double = initAngle
  protected var isPenDown: Boolean = initPenIsDown

  /**
    * Moves the turtle to a new position without drawing a line.
    */
  def jumpTo(newPosition: Point) = {
    position = newPosition
  }

  /**
    * Moves the turtle forward in its current direction, drawing a line if the pen is down.
    * @param length The number of pixels to move forward.
    */
  def forward(length: Double): Unit = {
    val xChange = math.cos(math.toRadians(angle)) * length
    val yChange = math.sin(math.toRadians(angle)) * length
    val newX = position.x + xChange
    val newY = position.y - yChange
    window.moveTo(math.round(position.x).toInt, math.round(position.y).toInt)
    position = Point(newX, newY)
    if (isPenDown) {
      window.lineTo(math.round(position.x).toInt, math.round(position.y).toInt)
    }
  }

  /**
    * Turns the turtle to the right.
    *
    * @param turnAngle The number of degrees to turn.
    */
  def turnLeft(turnAngle: Double): Unit = {
    angle += turnAngle
    angle = angle % 360
  }

  /**
    * Turns the turtle to the left.
    *
    * @param turnAngle The number of degrees to turn.
    */
  def turnRight(turnAngle: Double): Unit = turnLeft(-turnAngle)

  /**
    * Turns the turtle straight up.
    */
  def turnNorth(): Unit = angle = 90

  /**
    * Sets the turtle's pen down, causing it to draw lines when the turtle moves.
    */
  def penDown(): Unit = isPenDown = true

  /**
    * Lifts the turtle's pen, preventing it from drawing lines, even when it moves.
    */
  def penUp(): Unit = isPenDown = false

  /**
    * Returns the x-coordinate of the turtle.
    */
  def getX: Int = position.x.toInt

  /**
    * Returns the y-coordinate of the turtle.
    */
  def getY: Int = position.y.toInt

  /**
    * Returns the angle of the turtles direction
    */
  def getDirection: Int = angle.toInt
}