trait Grönsak {
  def skala(): Unit
  var vikt: Double
  val namn: String
  var ärSkalad: Boolean = false
  override def toString = s"$namn ${if (!ärSkalad) "o" else ""}skalad $vikt g"
}

class Gurka(var vikt: Double) extends Grönsak {
  val namn = "gurka"
  def skala(): Unit = if (!ärSkalad) {
    println("Gurkan skalas med skalare.")
    vikt = 0.98 * vikt
    ärSkalad = true
  }
}

class Tomat(var vikt: Double) extends Grönsak {
  val namn = "tomat"
  def skala(): Unit = if (!ärSkalad) {
    println("Tomaten skalas genom skållning.")
    vikt = 0.99 * vikt
    ärSkalad = true
  }
}