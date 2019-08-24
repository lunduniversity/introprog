object exempelVego3 {

  trait Grönsak {
    val skalningsmetod: String      // abstrakt 
    val skalfaktor = 0.99           // konkret
    var vikt: Double                // abstrakt
    var ärSkalad: Boolean = false   // konkret

    def skala(): Unit = if (!ärSkalad) {
      println(skalningsmetod)
      vikt = skalfaktor * vikt
      ärSkalad = true
    }
  }

  class Gurka(var vikt: Double) extends Grönsak {
//nyckelordet override behövs ej om abstrakt medlem i supertyp
    val skalningsmetod = "Skalas med skalare."
  }

  class Tomat(var vikt: Double) extends Grönsak {
//nyckelordet override behövs ej om abstrakt medlem i supertyp men tillåtet:
    override val skalningsmetod = "Skållas."
//  override val skalningmetod = "Skållas." //kompilatorn hittar stavfelet!
    override val skalfaktor = 0.95  // override måste anges vid ändrad impl.
  }
}
