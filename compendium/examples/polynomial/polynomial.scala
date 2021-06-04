object polynomial:

  sealed trait Term:
    def *(that: Term): Term

  case class Const(value: BigDecimal) extends Term:

    def toSilentString: String = this match
      case Const.One      => ""
      case Const.MinusOne => "-"
      case _              => value.toString

    override def toString = value.toString

    override def *(that: Term): Term = that match
      case Const(d)    => Const(d * value)
      case v: Var      => Prod(this, Set(v))
      case Prod(c, vs) => Prod(Const(c.value * value), vs)

    def *(d: BigDecimal): Const = Const(d * value)

    def ^(e: Int): Const = Const(value.pow(e))


  object Const:
    final val Zero     = Const(BigDecimal(0))
    final val One      = Const(BigDecimal(1))
    final val MinusOne = Const(BigDecimal(-1))

  case class Var(name: Char, exp: Int = 1) extends Term:

    private def silentExpString: String =
      if exp == 1 then "" else "^"+exp.toString

    override def toString = s"$name$silentExpString"

    def ^(e: Int): Var = Var(name, e * exp)

    def *(c: BigDecimal) = Prod(Const(c), Set(this))

    override def *(that: Term): Term = that match
      case c: Const => Prod(c, Set(this))

      case v: Var =>
        if v.name == name then Var(name, v.exp + exp)
        else Prod(Const.One, Set(this, v))

      case p: Prod => p * this


  object Var:

    def apply(d: BigDecimal, name: Char): Prod =
      Prod(Const(d), Set(Var(name)))

    def apply(d: BigDecimal, name: Char, exp: Int): Prod =
      Prod(Const(d), Set(Var(name, exp)))

    def addExp(v1: Var, v2: Var): Var = Var(v1.name, v1.exp + v2.exp)

    def multiply(v1: Var, vs: Set[Var]): Set[Var] =
      if !vs.contains(v1) then vs + v1
      else vs.map(v2 => if v1.name == v2.name then addExp(v1, v2) else v2)

    def multiply(vs1: Set[Var], vs2: Set[Var]): Set[Var] =
      var result = vs2
      vs1.foreach{ v1 => result = multiply(v1, result) }
      result


  case class Prod(const: Const, vars: Set[Var]) extends Term :

    override def toString = s"${const.toSilentString}${vars.mkString}"

    override def *(that: Term): Term = that match
      case Const(d) => Prod(Const(d * const.value), vars)

      case v: Var => Prod(const, Var.multiply(v, vars))

      case Prod(Const(d), vs)  =>
        Prod(Const(const.value * d), Var.multiply(vs, vars))

    def ^(e: Int) = Prod(const ^ e, vars.map(_ ^ e))

  case class Poly(xs: Set[Term]):
    override def toString = xs.mkString(" + ")

  object Poly:
    def apply(ts: Term*) : Poly = Poly(ts.toSet)

  val (x, y, z, s, t) = (Var('x'), Var('y'), Var('z'), Var('s'), Var('t'))

