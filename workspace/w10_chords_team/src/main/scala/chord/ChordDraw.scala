package chord
import cslib.window.SimpleWindow
import chord.model._

object ChordDraw {
  val w = new SimpleWindow(400, 600, "Chord draw")

  /**
   * Draws the chord c in a SimpleWindow
   */
  def apply(c: Chord): Unit = {
    SimpleWindow.setExitOnLastClose(false)
    w.clear
    val nbrOfStrings = c.tuning.length
    //Code to draw fret board and grip
    play(c)
  }
  
  /**
   * Waits for mouse click and plays the note of the string clicked on
   */
  def play(c: Chord): Unit = ???

  /**
   * Draws a cross with center i the coordinate (x,y)
   */
  def cross(x: Int, y: Int): Unit = ???
}