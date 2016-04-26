import cslib.window.SimpleWindow

object hello { 
  def main(args: Array[String]): Unit = {
    println("HELLO main method! åäö ÅÄÖ") 
    val w = new SimpleWindow(600,600,"Hello Window! ÅÄÖ åäö")
    w.moveTo(100,100)
    w.writeText("HELLO SIMPLE WINDOW! ÅÄÖ åäö")
  }
}
