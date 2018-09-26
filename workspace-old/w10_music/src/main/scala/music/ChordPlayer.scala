package music

object ChordPlayer {
  
  case class Strike(
    velocity: Int         = 50,   // hur hårt anslag i Range(0, 128)
    duration: Long        = 500,  // hur länge i millisekunder
    spread:   Long        = 50,   // millisekunder mellan tonerna
    after:    Long        = 0     // millisekunder innan första tonen
  )

  def play(chord: Chord, strike: Strike = Strike(), channel: Int = 0): Unit =
    strike match { case Strike(v, d, s, a) =>
      ???
    }
}
