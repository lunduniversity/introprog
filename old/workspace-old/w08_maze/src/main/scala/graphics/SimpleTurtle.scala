package graphics
import cslib.window.SimpleWindow

class SimpleTurtle(val window: SimpleWindow){
  var x: Double          = window.getWidth / 2.0
  var y: Double          = window.getHeight / 2.0
  var angle: Double      = 90.0 // towards north, in degrees
  var isPenDown: Boolean = true

  def pos: (Int, Int) = (math.round(x).toInt, math.round(y).toInt)
  def pos_=(p: (Int, Int)): Unit = { x = p._1; y = p._2 }
  def dir: Int        = math.round(angle).toInt

  def move(length: Double): Unit = {
    window.moveTo(pos._1, pos._2)
    x += math.cos(math.toRadians(angle)) * length
    y -= math.sin(math.toRadians(angle)) * length
    if (isPenDown) window.lineTo(pos._1, pos._2)
    else           window.moveTo(pos._1, pos._2)
  }

  def turn(leftwards: Double): Unit = {
    angle += leftwards
    angle = if (angle < 0) 360 + angle % 360 else angle % 360
  }

  def left():  Unit = turn(90)
  def right(): Unit = turn(-90)
}
