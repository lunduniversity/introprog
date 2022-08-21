object exempelVego2:

  trait Grönsak:  // innehåller gemensamma delar; hjälper oss undvika upprepning
    val skalningsmetod: String      // abstrakt
    val skalfaktor = 0.99           // konkret
    var vikt: Double                // abstrakt
    var ärSkalad: Boolean = false   // konkret
    
    def skala(): Unit = if !ärSkalad then // konkret
      println(skalningsmetod)
      vikt = skalfaktor * vikt
      ärSkalad = true

  class Gurka(var vikt: Double) extends Grönsak: // det som är speciellt för gurkor
    val skalningsmetod = "Skalas med skalare."

  class Tomat(var vikt: Double) extends Grönsak: // det som är speciellt för tomater
    val skalningsmetod = "Skållas."
