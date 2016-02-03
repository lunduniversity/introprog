//gettersetter.scala

class Box(var nbr: Int)

class Box2(initNbr: Int) {
  private[this] var nbrInternal: Int = initNbr
  def nbr = nbrInternal
  def nbr_=(n: Int) { nbrInternal = n } 
}

class Box3(initNbr: Int) {
  private[this] var nbrInternal: Double = initNbr
  def nbr = nbrInternal.toInt
  def nbr_=(n: Int) { nbrInternal = n } 
}