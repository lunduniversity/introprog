package cslib.pixel

case class Color(rgb: Int) extends AnyVal {
  def red: Int   = (rgb>>16)&0x0ff
  def green: Int = (rgb>>8) &0x0ff
  def blue: Int  = (rgb)    &0x0ff
}
object Color {
  val Black = new Color(0)
  def apply(rgb:(Int, Int, Int)) = new Color(0x10000 * rgb._1 + 0x100 * rgb._2 + rgb._3)
}

object Implicits {
  implicit class ColorToJavaAwtColor(c: Color){
    def toJColor: java.awt.Color = new java.awt.Color(c.rgb, false)
  } 
  implicit class JavaAwtColorToColor(jc: java.awt.Color){
    def toColor: Color = Color(jc.getRed, jc.getGreen, jc.getRed)
  } 
}


trait PixelView {
  def dim: (Int, Int)
  def sizeX: Int = dim._1
  def sizeY: Int = dim._2
  def apply(pos: (Int, Int)): Color
  def update(x: Int, y: Int, color: Color): Unit 
  def update(pos: (Int, Int), color: Color): Unit = update(pos._1, pos._2, color)

}

trait KeyboardView {
}

trait MouseView {
}

object pix extends PixelMatrix {
  val dim: (Int, Int) = (100, 100)
  val pixel = Array.fill(sizeX,sizeY)(Color(0))
  def apply(pos: (Int, Int)): Color = pixel(pos._1)(pos._2)
  def update(x: Int, y: Int, color: Color): Unit = pixel(x)(y) = color
}
