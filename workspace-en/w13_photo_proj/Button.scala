package photo 

import introprog.PixelWindow

case class Button(text: String, pos: (Int,Int), size: (Int, Int), fontSize: Int, onClick: () => Unit):
  val textPos = 
    (pos._1 + size._1 / 2 - (fontSize / 3) * text.length, 
     pos._2 + size._2 / 2 - (1.3 * fontSize / 2).toInt)

  def draw(w: PixelWindow): Unit = 
    w.fill(pos._1, pos._2, size._1, size._2, Button.Color)
    w.drawText(text, textPos._1, textPos._2, Button.TextColor, size = fontSize)

  def drawOnClick(w: PixelWindow): Unit = 
    w.fill(pos._1, pos._2, size._1, size._2, Button.ClickColor)
    w.drawText(text, textPos._1, textPos._2, Button.TextClickColor, size = fontSize)

  def isClickInside(clickedPos: (Int, Int)): Boolean = 
    (pos._1 to (pos._1 + size._1)).contains(clickedPos._1) &&
    (pos._2 to (pos._2 + size._2)).contains(clickedPos._2)

object Button:
  val Size = (200, 40)
  val Pad = Size._2 / 10
  val FontSize = Size._2 / 2
  val Color = java.awt.Color.GRAY
  val ClickColor = java.awt.Color.GREEN.darker
  val TextColor = java.awt.Color.BLACK
  val TextClickColor = java.awt.Color.WHITE

  def column(nameActionPairs: (String, () => Unit)*): Seq[Button] = 
    for ((name, action), i) <- nameActionPairs.zipWithIndex yield
      Button(name, (Pad, Pad * (i + 1) + Size._2 * i), Size, FontSize, action)
