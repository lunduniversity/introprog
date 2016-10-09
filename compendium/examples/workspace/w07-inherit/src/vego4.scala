object exempelVego4 {

  trait Grönsak {
    val skalningsmetod: String
    final val skalfaktor = 0.99              // en final medlem kan ej överskuggas
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
    val skalningsmetod = "Skalas med skalare."
  }

  class Tomat(var vikt: Double) extends Grönsak {
    val namn = "tomat"
    val skalningsmetod = "Skållas."
//  override val skalfaktor = 0.95 // KOMPILERINGSFEL: "cannot override final member"
  }
}
