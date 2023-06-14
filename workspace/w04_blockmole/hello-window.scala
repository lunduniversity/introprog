//> using scala 3.3
//> using option -unchecked -deprecation

import introprog.PixelWindow

@main
def run: Unit = 
  println("HELLO main method! åäö ÅÄÖ")
  val w = new PixelWindow(width = 800, height = 600, title = "Hello Window!")
  w.drawText(x = 100, y = 200, text = "HELLO PIXEL WINDOW! ÅÄÖ åäö")
  introprog.examples.TestPixelWindow.main(Array())

