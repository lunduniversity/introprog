package music

trait StringInstrument { def toChord: Chord }

case class Piano(isKeyDown: Set[Int]) extends StringInstrument {
  override def toChord: Chord =
    Chord(isKeyDown.toVector.sorted.map(Pitch.apply))
}

trait FrettedInstrument extends StringInstrument {
  def nbrOfStrings: Int
  def tuning: Vector[Pitch]
  def grip:   Vector[Int]
  override def toChord: Chord = {
    val notes =
      for (i <- grip.indices if grip(i) >= 0) yield tuning(i) + grip(i)
    Chord(notes.toVector)
  }
}

case class Guitar(pos: (Int,Int,Int,Int,Int,Int)) extends FrettedInstrument {
  override val grip = Vector(pos._1, pos._2, pos._3, pos._4, pos._5, pos._6)
  override val nbrOfStrings = 6
  override val tuning =
    "E3 A3 D4 G4 B4 E5".split(' ').map(Pitch.apply).toVector
}

case class Ukulele(pos: (Int,Int,Int,Int)) extends FrettedInstrument {
  override val grip = Vector(pos._1, pos._2, pos._3, pos._4)
  override val nbrOfStrings = 4
  override val tuning =
    "A5 D5 F#5 B5".split(' ').map(Pitch.apply).toVector
}
