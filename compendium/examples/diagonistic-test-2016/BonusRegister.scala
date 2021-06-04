/** A mutable register of individual points for bonus calculation */
class BonusRegister:
  /** Add data for one student */
  def add(studentId: String, group: String, points: Int): Unit = ???

  /** Returns an immutable Map with the bonus points of each studentId */
  def calculateBonus(): Map[String, Int] = ???
object BonusRegister:
  /** Returns the average bonus calculated from a sequence of integers */
  def collaborationBonus(points: Seq[Int]): Int =
    (points.sum / points.size.toDouble).round.toInt
