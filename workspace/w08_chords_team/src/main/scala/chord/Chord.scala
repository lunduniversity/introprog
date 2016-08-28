package chord

object model {
  type Grip = Vector[Int]
  type Tuning = Vector[String]

  trait Chord {
    def name: String
    def tuning: Tuning
    def grip: Grip
    
    override def toString: String = ???
    
    /**
     * Checks if chords toString match any part of the filter
     */
    def isIncludedBy(filter: String): Boolean = ???
  }
  
  /**
   * Converts grip String to a Vector[Int], e.g. "-1 -1 0 2 3 2" to Vector(-1, -1, 0, 2, 3, 2)
   */
  def stringToGripVec(s: String): Vector[Int] = s.split(" ").map(_.toInt).toVector
  
  object Chord {
    /**
     * Converts a string to chord. Example string: git:D:-1 -1 0 2 3 2
     */
    def fromString(chordText: String): Option[Chord] = {
      def isGit(s: String) = ???
      def isUku(s: String) = ???
      /**
       * Match the strings instr, name and grip againt the different types of chrods
       */
      def instToChord(instr: String, name: String, grip: String): Option[Chord] = ???
      val xs: Vector[String] = chordText.split(':').toVector
      xs match {
        case Vector(instr, name, gripString) => instToChord(instr,name,gripString) 
        case _ => None
      }
    }
  }

  case class Guitar(name: String, grip: Grip) extends Chord {
    override val tuning = ???

    override def toString: String = ???
  }
}