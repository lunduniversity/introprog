package chord;


object Notes {
  val notes: Vector[String] = Vector("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
  
  /**
   * Converts a number to a note, e.g. 1 -> C#
   */
  def fromNbrToNote(i: Int): String = ???
  
  /**
   * Converts a note to a number, e.g. E2 -> 16. C#2 will not be possible
   */
  def fromNoteToNbr(note: String): Int = unapply(note).getOrElse(0)
  
  val toNumber: Map[String, Int] = ???
  
  /**
   * Tries to convert a string into a number, e.g. E2 -> 16.
   */
  def unapply(s: String): Option[Int] = ???
}