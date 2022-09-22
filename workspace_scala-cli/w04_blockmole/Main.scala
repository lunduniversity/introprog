//> using lib "se.lth.cs::introprog::1.3.1"
import introprog.PixelWindow

@main
def run: Unit = 
  println("HELLO main method! åäö ÅÄÖ")
  val w = new PixelWindow(width = 800, height = 600, title = "Hello Window!")
  w.drawText(x = 100, y = 200, text = "HELLO PIXEL WINDOW! ÅÄÖ åäö")
  introprog.examples.TestPixelWindow.main(Array())