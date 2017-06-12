package nlp

class FreqMapBuilder {
  private val register = scala.collection.mutable.Map.empty[String, Int]
  def toMap: Map[String, Int] = register.toMap
  def add(s: String): Unit = ???
}
object FreqMapBuilder {
  def apply(xs: String*): FreqMapBuilder = ???
}
