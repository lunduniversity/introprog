object Bonus {
  def main(args: Array[String]): Unit = {
    val lines = scala.io.Source.fromFile(args(0)).getLines.toVector
    val reg = new BonusRegister
    for (line <- lines) {
      val xs = line.split(' ')
      reg.add(studentId=xs(0), group=xs(1), points=xs(2).toInt)
    }
    val bonusMap = reg.calculateBonus
    val sortedPairs: Vector[(String, Int)] = bonusMap.toVector.sorted
    sortedPairs.foreach(println)
  }
}