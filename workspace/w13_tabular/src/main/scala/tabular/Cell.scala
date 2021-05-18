package tabular
import scala.util.{Try, Success, Failure}

sealed trait Cell { def value: String }
case class Str(value: String) extends Cell
case class Num(num: BigDecimal) extends Cell { def value = num.toString }

object Cell:
  /** Ger en Num om BigDecimal(s) lyckas annars en Str. */
  def apply(s: String): Cell =  ???

  def apply(i: Int): Num = Num(BigDecimal(i))

  def empty: Str = Str("")

  def zero: Num = Num(BigDecimal(0))
