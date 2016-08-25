package chord

object database {
  import model._
  type Chords = Vector[Chord]
  private var db: Chords = Vector()  // populated when chords are loaded from file
  private var filter: String = ""  // empty string means no filter
  
  /**
   * Finds the chords among the filtered chords matching the string s
   */
  def find(s: String): Chords = ???
  
  /**
   * Adds the chord c to the database
   */
  def add(c: Chord): Unit = ???
  
  /**
   * Delete the chord with index i among the filtered chords
   */
  def delete(i: Int): Unit = ???
  
  /**
   * Update the filter to string s.
   * Empty string results in no filter
   */
  def updateFilter(f: String): Unit = ???
 
  /**
   * Returns all chords matching the filter
   */
  def filteredChords: Chords = ???
  
  /**
   * Return all chords i the database
   */
  def allChords: Chords = ???
  
  /**
   * Sorts the chords first by instrument and then by name
   */
  def sort: Unit = ???
}