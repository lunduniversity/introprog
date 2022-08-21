object exempelVego1:

  trait Grönsak:
    var vikt: Double              // abstrakt medlem, saknar implementation
    var ärSkalad: Boolean = false // konkret medlem, har implementation

    def skala(): Unit             // abstrakt medlem, saknar implementation

  class Gurka(var vikt: Double) extends Grönsak:
    def skala(): Unit =           // implementation specifik för Gurka
      if !ärSkalad then 
        println("Skalas med skalare.")
        vikt = 0.99 * vikt
        ärSkalad = true

  class Tomat(var vikt: Double) extends Grönsak:
    def skala(): Unit =           // implementation specifik för Tomat
      if !ärSkalad then 
        println("Skållas.")
        vikt = 0.99 * vikt
        ärSkalad = true
