package music

import scala.math.floorMod
import scala.util.Random

case class Chord(ps: Vector[Pitch]):
  assert(ps.nonEmpty, "Chord pitch sequence is empty")

  val pitchClasses: Vector[Int] = ps.map(_.pitchClass).toVector

  def apply(i: Int): Pitch = ps(i)

  def intervals(root: Pitch = ps(0)): Vector[Int] = ps.map(_.nbr - root.nbr)

  def simpleIntervals(root: Pitch = ps(0)): Vector[Int] =
    intervals(root).map(i => floorMod(i, 12)).distinct.sorted

  def name(root: Pitch = ps(0)): String = simpleIntervals(root) match
    case Vector(0, 4, 7)     => root.pitchClassName
    case Vector(0, 3, 7)     => root.pitchClassName + "m"
    case Vector(0, 4, 7, 10) => root.pitchClassName + "7"
    case _ => root.pitchClassName + intervals(root).mkString("[", ",", "]")

  override def toString = ps.map(_.name).mkString("Chord(", ",", ")")

object Chord:
  def apply(xs: String*): Chord = Chord(xs.map(Pitch.apply).toVector)

  def random(pitchNumbers: Seq[Int] = 60 to 72, n: Int = 3): Chord =
    val shuffled = Random.shuffle(pitchNumbers).toVector
    Chord(shuffled.take(n).map(Pitch.apply))
