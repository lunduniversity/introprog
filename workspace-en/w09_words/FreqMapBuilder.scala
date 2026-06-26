package nlp

class FreqMapBuilder:
  private val register = collection.mutable.Map.empty[String, Int]
  def toMap: Map[String, Int] = register.toMap
  def add(s: String): Unit = ??? 

object FreqMapBuilder:
  /*Create a new FreqMapBuilder and count the strings in xs */
  def apply(xs: String*): FreqMapBuilder = ???
