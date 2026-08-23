package music

object ChordPlayer:

  case class Strike(
      velocity: Int  = 50,  // how hard impact in Range(0, 128)
      duration: Long = 500, // how long in milliseconds
      spread: Long   = 50,  // millisekunder mellan tonerna
      after: Long    = 0    // milliseconds before the first tone
  )

  def play(chord: Chord, strike: Strike = Strike(), channel: Int = 0): Unit =
    strike match
      case Strike(v, d, s, a) => ???
