package chord;

object Notes {
  val notes: Vector[String] = 
    Vector("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
  
  /**
   * Converts a number to a note string without octave 
   * 
   * Examples: 0 to "C", 1 to "C#", 12 to "C", 13 to "C#" etc.  
   */
  def fromNbrToNote(i: Int): String = ???
  
  /**
   * Converts a note string with octave to a number.
   *
   * Examples: "E2" to 16, "C#3" to 25 etc. 
   * If illegal note then 0 is returned.
   */
  def fromNoteToNbr(note: String): Int = unapply(note).getOrElse(0)
  
  /**
   * A Map with notes without octave as keys and 
   * its corresponding number as values
   *
   * Examples: toNumber("C#") == 1, toNumber("E") == 4)
   */
  val toNumber: Map[String, Int] = ???
  
  /**
   * Tries to convert a string into a number, 
   * Examples: E2 gives Some(16), "C#3" gives Some(25), "X" gives None 
   */
  def unapply(s: String): Option[Int] = ???
}
