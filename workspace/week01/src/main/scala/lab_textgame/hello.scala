package lab_textgame

import cslib.window.SimpleWindow

object hello { 
  def main(args: Array[String]): Unit = {
    println("HELLO LAB01") 
    val w = new SimpleWindow(600,600,"hello")
    w.moveTo(100,100)
    w.writeText("HELLO LAB1")
  }
}
