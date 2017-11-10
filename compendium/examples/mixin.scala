object exempel1 {
  // subtypning med inmixning och anonym klass

  class Bas(val i: Int){ def hello: String = "hej " + i }

  trait Sub1 extends Bas { override def hello = "goddag " + i }
  trait Sub2 extends Bas { override def hello = "tjena " + i }

  val b = new Bas(42)
  val b1 = new Bas(42) with Sub1 // anonym klass med inmixad trait
  val b2 = new Bas(42) with Sub2
}

object exempel2 {
  // arv av klass med parameter utan inmixning

  class Bas(val i: Int){ def hello: String = "hej " + i }

  class Sub1(n: Int) extends Bas(n) { override def hello: String = "goddag " + i }
  class Sub2(n: Int) extends Bas(n) { override def hello: String = "tjena " + i }
  // Eller utan en ny n som ovan, utan använder här samma i:
  class Sub3(override val i: Int) extends Bas(i) // etc

  val b = new Bas(42)
  val b1 = new Sub1(42)
  val b2 = new Sub2(42)
}

object exempel3 {
  // om man vill ha abstrakt bastyp, vilket är vanligast (?) och enklast (?):

  trait Bas {
    val i : Int
    def hello: String
  }

  class Sub1(val i: Int) extends Bas { def hello: String = "goddag " + i }
  class Sub2(val i: Int) extends Bas { def hello: String = "tjena " + i }

  val b1 = new Sub1(42)
  val b2 = new Sub2(42)

  // man kan instansiera Bas som anonym klass
  val b = new Bas { val i = 42; val hello = "hejsan"} // anonym klass
}
