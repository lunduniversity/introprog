object exempelVego4:

  trait Grönsak:
    val skalningsmetod: String
    final val skalfaktor = 0.99  // en final medlem kan ej överskuggas
    var vikt: Double
    var ärSkalad: Boolean = false

    def skala(): Unit = if !ärSkalad then
      println(skalningsmetod)
      vikt = skalfaktor * vikt
      ärSkalad = true

  class Gurka(var vikt: Double) extends Grönsak:
    val skalningsmetod = "Skalas med skalare."

  class Tomat(var vikt: Double) extends Grönsak:
    val skalningsmetod = "Skållas."
//  override val skalfaktor = 0.95 
//  ger KOMPILERINGSFEL: "cannot override final member"
