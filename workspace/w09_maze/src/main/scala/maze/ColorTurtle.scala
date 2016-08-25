package maze;
import cslib.window.SimpleWindow
import java.awt.Color;

/**
 * A Turtle class that can be used to walk and draw in a SimpleWindow with a specified color.
 * @param window     The window the turtle should be placed in.
 * @param position   A Point representing the turtle's starting coordinates.
 * @param angle      The angle between the turtle direction and the X-axis measured in degrees.
 * @param isPenDown  A boolean representing the turtle's pen position. True if the pen is down.
 * @param color		   The color with which the turtle will draw.
 */
class ColorTurtle(window: SimpleWindow,
    private var position: Point,
    private var angle: Double,
    private var isPenDown: Boolean,
    color: Color) extends Turtle(window, position, angle, isPenDown) {

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