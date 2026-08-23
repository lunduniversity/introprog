package vegomatch

trait Vegetable:
  def weight: Int
  def isRotten: Boolean

case class Cucumber(weight: Int, isRotten: Boolean) extends Vegetable
case class Tomato(weight: Int, isRotten: Boolean) extends Vegetable

object Main:
  def randomWeight:    Int       = (math.random() * 420 + 42).toInt
  def randomRotten:    Boolean   = math.random() > 0.8
  def randomCucumber:  Cucumber  = Cucumber(randomWeight, randomRotten)
  def randomTomato:    Tomato    = Tomato(randomWeight, randomRotten)
  def randomVegetable: Vegetable =
    if math.random() > 0.2 then randomCucumber else randomTomato

  def isWorthEating(g: Vegetable): Boolean = g match
    case Cucumber(v, rotten) if v > 100 && !rotten => true
    case Tomato(v, rotten)   if v >  50 && !rotten => true
    case _ => false

  def main(args: Array[String]): Unit =
    val harvest = Vector.fill(args(0).toInt)(randomVegetable)
    val worthEating = harvest.filter(isWorthEating)
    println("Number of harvested vegetables: " + harvest.size)
    println("Number of vegetables worth eating:  " + worthEating.size)
