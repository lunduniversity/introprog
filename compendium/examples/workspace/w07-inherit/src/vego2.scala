object exempelVego2 {

  trait Grönsak {  // innehåller alla gemensamma delar; hjälper oss undvika upprepning
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

  class Gurka(var vikt: Double) extends Grönsak { // bara det som är speciellt för gurkor
    val namn = "gurka"
    val skalningsmetod = "Gurkan skalas med skalare."
  }

  class Tomat(var vikt: Double) extends Grönsak { // bara det som är speciellt för tomater
    val namn = "tomat"
    val skalningsmetod = "Tomaten skalas genom skållning."
  }
}
