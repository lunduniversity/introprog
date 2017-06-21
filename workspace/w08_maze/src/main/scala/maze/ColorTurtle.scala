package maze
import cslib.window.SimpleWindow
import java.awt.Color

/**
  * A Turtle class that can be used to walk and draw in a SimpleWindow with a specified color.
  * @param window     The window the turtle should be placed in.
  * @param initPosition   A Point representing the turtle's starting coordinates.
  * @param initAngle      The angle between the turtle direction and the X-axis measured in degrees.
  * @param initPenIsDown  A boolean representing the turtle's pen position. True if the pen is down.
  * @param initColor      The initial color of the pen used when drawing while moving with pen down
  */
class ColorTurtle(window: SimpleWindow,
                  initPosition: Point,
                  initAngle: Double = 90,
                  initPenIsDown: Boolean = true,
                  val initColor: Color)
  extends Turtle(window, initPosition, initAngle, initPenIsDown) {

  /**
    * The color of the pen used when drawing while moving with pen down
    */
  var color: Color = initColor

  /**
    * Moves the turtle forward in its current direction, drawing a line with line color color if the pen is down.
    * @param length The number of pixels to move forward.
    */
  override def forward(length: Double): Unit = {
    window.setLineColor(color)
    super.forward(length)
    window.setLineColor(Color.BLACK)
  }

}