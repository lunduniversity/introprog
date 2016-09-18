object localMutability {
  def kastaTärningTillsAllaUtfallUtomEtt(sidor: Int = 6): (Int, Set[Int]) = {
    import scala.collection.mutable
    val s = mutable.Set.empty[Int]
    var n = 0
    while (s.size < sidor - 1) {
      s += (math.random * sidor + 1).toInt
      n += 1
    }
    (n, s.toSet)
  }

  def kastaImmutable(sidor: Int = 6): (Int, Set[Int]) = {
    var s = Set.empty[Int]
    var n = 0
    while (s.size < sidor - 1) {
      s += (math.random * sidor + 1).toInt
      n += 1
    }
    (n, s)
  }
}
