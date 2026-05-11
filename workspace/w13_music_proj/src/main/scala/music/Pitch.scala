package music

import scala.util.Try

case class Pitch(number: Int): // Tonhöjd
  assert((0 to 127) contains number, s"Error: number $number outside (0 to 127)")

  def pitchClass: Pitch.Class = Pitch.Class.fromOrdinal(number % 12)
  def pitchClassName: String  = pitchClass.toString
  def name: String            = s"$pitchClassName$octave"
  def octave: Int             = number / 12 - 1
  def +(offset: Int): Pitch   = Pitch(number + offset)
  override def toString       = s"Pitch($name)"

object Pitch:
  val defaultOctave = 4 // mittenoktaven på ett pianos tangentbord

  enum Class:
    case C, Db, D, Eb, E, F, Gb, G, Ab, A, Bb, B

  def fromString(s: String): Option[Pitch] = Try {
    val (pitchClassName, octaveName) = s.partition(c => c.isLetter)

    // Ändra denna raden om du vill acceptera förtecken utöver `b`.
    val pitchClass = Class.valueOf(pitchClassName)
    val octave = if octaveName.nonEmpty then octaveName.toInt else defaultOctave

    Pitch(pitchClass.ordinal + (octave + 1) * 12)
  }.toOption

  def apply(s: String): Pitch =
    fromString(s).getOrElse(throw new IllegalArgumentException)
