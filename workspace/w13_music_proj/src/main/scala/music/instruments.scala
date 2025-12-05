package music

trait StringInstrument { def toChordOpt: Option[Chord] }

case class Piano(isKeyDown: Set[Int]) extends StringInstrument:
  def toChordOpt: Option[Chord] =
    if isKeyDown.nonEmpty then
      Some(Chord(isKeyDown.toVector.sorted.map(Pitch.apply)))
    else None

trait FrettedInstrument extends StringInstrument:
  def nbrOfStrings: Int
  def tuning: Vector[Pitch]
  def grip: Vector[Int]
  def toChordOpt: Option[Chord] =
    val notes =
      for i <- grip.indices if grip(i) >= 0
      yield tuning(i) + grip(i)
    if notes.nonEmpty then Some(Chord(notes.toVector)) else None

case class Guitar(pos: (Int, Int, Int, Int, Int, Int))
    extends FrettedInstrument:
  val grip = Vector(pos._1, pos._2, pos._3, pos._4, pos._5, pos._6)
  val nbrOfStrings = 6
  val tuning =
    "E3 A3 D4 G4 B4 E5".split(' ').map(Pitch.apply).toVector

case class Ukulele(pos: (Int, Int, Int, Int)) extends FrettedInstrument:
  val grip = Vector(pos._1, pos._2, pos._3, pos._4)
  val nbrOfStrings = 4
  val tuning =
    "A5 D5 F#5 B5".split(' ').map(Pitch.apply).toVector
