object exempelVego3 {

  trait Grönsak {
    val skalningsmetod: String
    val skalfaktor = 0.99
    def skala(): Unit = if (!ärSkalad) {
      println(skalningsmetod)
      vikt = skalfaktor * vikt
      ärSkalad = true
    }
    var vikt: Double
    val namn: String
    var ärSkalad: Boolean = false
    override def toString = s"$namn ${if (!ärSkalad) "o" else ""}skalad $vikt g"
  }

  class Gurka(var vikt: Double) extends Grönsak {
    val namn = "gurka"
    val skalningsmetod = "Gurkan skalas med skalare."
  }

  class Tomat(var vikt: Double) extends Grönsak {
    val namn = "tomat"
    val skalningsmetod = "Tomaten skalas genom skållning."
    override val skalfaktor = 0.95                           // överskuggning
  }
}
