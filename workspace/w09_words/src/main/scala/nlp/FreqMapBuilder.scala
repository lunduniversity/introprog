package nlp

class FreqMapBuilder:
  private val register = scala.collection.mutable.Map.empty[String, Int]
  def toMap: Map[String, Int] = register.toMap
  def add(s: String): Unit = ??? 

object FreqMapBuilder:
  /** Skapa ny FreqMapBuilder och räkna strängarna i xs */
  def apply(xs: String*): FreqMapBuilder = ???
