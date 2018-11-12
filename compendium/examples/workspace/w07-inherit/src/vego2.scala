object exempelVego2 {

  trait Grönsak {  // innehåller alla gemensamma delar; hjälper oss undvika upprepning
    val skalningsmetod: String
    val skalfaktor = 0.99
    var vikt: Double
    var ärSkalad: Boolean = false
    
    def skala(): Unit = if (!ärSkalad) {
      println(skalningsmetod)
      vikt = skalfaktor * vikt
      ärSkalad = true
    }
  }

  class Gurka(var vikt: Double) extends Grönsak { // bara det som är speciellt för gurkor
    val skalningsmetod = "Skalas med skalare."
  }

  class Tomat(var vikt: Double) extends Grönsak { // bara det som är speciellt för tomater
    val skalningsmetod = "Skållas."
  }
}
