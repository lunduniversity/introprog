object exempelVego3 {

  trait Grönsak {
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

  class Gurka(var vikt: Double) extends Grönsak {
    //nyckelordet override behövs ej om abstrakt medlem i supertyp
    val skalningsmetod = "Skalas med skalare."
  }

  class Tomat(var vikt: Double) extends Grönsak {
    //nyckelordet override behövs ej men tillåtet:
    override val skalningsmetod = "Skållas."
//  override val skalningmetod = "Skållas." //kompilatorn hittar felet (stavfel, s saknas)
    override val skalfaktor = 0.95  //överskuggning: override måste anges vid ny impl.
  }
}
