class BonusRegister {
  import scala.collection.mutable
  private val groupOfStudent: mutable.Map[String, String] = mutable.Map.empty
  private val pointsOfGroup: mutable.Map[String, Vector[Int]] = mutable.Map.empty

  def add(studentId: String, group: String, points: Int): Unit = {
    groupOfStudent(studentId) = group
    if (pointsOfGroup.isDefinedAt(group))
      pointsOfGroup(group) = pointsOfGroup(group) :+ points
    else
      pointsOfGroup(group) = Vector(points)
  }

  def calculateBonus(): Map[String, Int] = {
    val students = groupOfStudent.keySet
    val nameAndBonusPairs = for (s <- students) yield {
      val gr = groupOfStudent(s)
      val cb = BonusRegister.collaborationBonus(pointsOfGroup(gr))
      (s, cb)
    }
    nameAndBonusPairs.toMap
  }
}

object BonusRegister {
  def collaborationBonus(points: Seq[Int]): Int =
    (points.sum / points.size.toDouble).round.toInt
}
