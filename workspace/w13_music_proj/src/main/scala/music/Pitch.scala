package music

import scala.util.Try

case class Pitch(nbr: Int): // Tonhöjd
  assert((0 to 127) contains nbr, s"Error: nbr $nbr outside (0 to 127)")
  def pitchClass: Int = nbr % 12
  def pitchClassName: String = Pitch.pitchClassNames(pitchClass)
  def name: String = s"$pitchClassName$octave"
  def octave: Int = nbr / 12 - 1
  def +(offset: Int): Pitch = Pitch(nbr + offset)
  override def toString = s"Pitch($name)"

object Pitch:
  val defaultOctave = 4 // mittenoktaven på ett pianos tangentbord

  val pitchClassNames: Vector[String] =
    Vector("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

  val pitchClassIndex: Map[String, Int] = pitchClassNames.zipWithIndex.toMap

  def fromString(s: String): Option[Pitch] = Try {
    val (pitchClassName, octaveName) = s.partition(c => c.isLetter)
    val octave = if octaveName.nonEmpty then octaveName.toInt else defaultOctave
    Pitch(pitchClassIndex(pitchClassName) + (octave + 1) * 12)
  }.toOption

  def apply(s: String): Pitch =
    fromString(s).getOrElse(throw new IllegalArgumentException)
