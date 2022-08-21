class Point(val x: Int, val y: Int, save: Boolean = false):
  import Point.*
  
  if save then saved.prepend(this)
  
  def this() = this(0, 0)
  
  def distanceTo(that: Point) = distanceBetween(this, that)
  
  override def toString = s"Point($x, $y)"

object Point:
  import scala.collection.mutable.{ArrayBuffer, Buffer}
  
  private val saved: Buffer[Point] = ArrayBuffer.empty
  
  def distanceBetween(p1: Point, p2: Point) = 
    math.hypot(p1.x - p2.x, p1.y - p2.y)    
  
  def showSaved: Unit = 
    println(saved.mkString("Saved: ", ", ", "\n"))
