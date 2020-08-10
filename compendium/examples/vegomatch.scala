package vegomatch

trait Grönsak { 
  def vikt: Int
  def ärRutten: Boolean 
}

case class Gurka(vikt: Int, ärRutten: Boolean) extends Grönsak
case class Tomat(vikt: Int, ärRutten: Boolean) extends Grönsak

object Main {
  def slumpvikt:    Int     = (math.random() * 420 + 42).toInt
  def slumprutten:  Boolean = math.random() > 0.8
  def slumpgurka:   Gurka   = Gurka(slumpvikt, slumprutten)
  def slumptomat:   Tomat   = Tomat(slumpvikt, slumprutten)
  def slumpgrönsak: Grönsak = 
    if (math.random() > 0.2) slumpgurka else slumptomat
  
  def ärÄtvärd(g: Grönsak): Boolean = g match {
    case Gurka(v, rutten) if v > 100 && !rutten => true 
    case Tomat(v, rutten) if v >  50 && !rutten => true 
    case _ => false
  }
  
  def main(args: Array[String]): Unit = {
    val skörd = Vector.fill(args(0).toInt)(slumpgrönsak)
    val ätvärda = skörd.filter(ärÄtvärd)
    println("Antal skördade grönsaker: " + skörd.size)    
    println("Antal ätvärda grönsaker:  " + ätvärda.size)    
  }
}
