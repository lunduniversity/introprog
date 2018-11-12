object exempelVego1 {

  trait Grönsak {
    var vikt: Double
    var ärSkalad: Boolean = false

    def skala(): Unit
  }

  class Gurka(var vikt: Double) extends Grönsak {
    def skala(): Unit = if (!ärSkalad) {
      println("Skalas med skalare.")
      vikt = 0.99 * vikt
      ärSkalad = true
    }
  }

  class Tomat(var vikt: Double) extends Grönsak {
    def skala(): Unit = if (!ärSkalad) {
      println("Skållas.")
      vikt = 0.99 * vikt
      ärSkalad = true
    }
  }
}
