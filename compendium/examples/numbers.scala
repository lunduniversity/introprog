// INTE FÄRDIGT
package numbers

trait Number {
  def reduce: Number = this  // reduce if possible to a simpler number
  def isZero: Boolean 
  def isOne: Boolean 
  def +(that: Number): Number = ???  // should be abstract
}
object Number {
  def Zero = Nat(0)
  def One  = Nat(1)
  def Im(im: BigDecimal) = Complex(Real(0), Real(im))
  def Im(im: Real)       = Complex(Real(0), im)
  def j                  = Complex(Real(0), Real(1))
  def Re(re: BigDecimal) = Complex(Real(re), Real(0))
  def Re(re: Real)       = Complex(re, Real(0))
  implicit class IntDecorator(i: Int){ def j = Im(i) }
  implicit class DoubleDecorator(d: Double){ def j = Im(d) }
}

trait AbstractComplex extends Number {
  def re: AbstractReal
  def im: AbstractReal
  def abs = Real(math.hypot(re.decimal.toDouble,im.decimal.toDouble)) 
  def fi =  Real(math.atan2(im.decimal.toDouble, re.decimal.toDouble)) 
  override def isZero: Boolean = re.decimal == 0 && im.decimal == 0
  override def isOne: Boolean = abs.decimal == 1.0
  override def reduce: AbstractComplex = if (im.decimal == 0) re.reduce else this
}

case class Complex(re: Real, im: Real) extends AbstractComplex
case object Complex {
  def apply(re: BigDecimal, im: BigDecimal) = new Complex(Real(re), Real(im))
}

case class Polar(override val abs: Real, override val fi: Real) extends AbstractComplex {
  override def re = Real(abs.decimal.toDouble * math.cos(fi.decimal.toDouble))  
  override def im = Real(abs.decimal.toDouble * math.sin(fi.decimal.toDouble))
}
case object Polar {
  def apply(abs: BigDecimal, fi: BigDecimal) = new Polar(Real(abs), Real(fi))
}
 

trait AbstractReal extends AbstractComplex {
  def decimal: BigDecimal
  override def isZero = decimal == 0
  override def isOne = decimal == 1
  override def re = this
  override def im = Number.Zero
  override def reduce:  AbstractReal = 
    if (decimal == 0) Number.Zero else if (decimal == 1) Number.One else this
}

case class Real(decimal: BigDecimal) extends AbstractReal 

trait AbstractRational extends AbstractReal {
  def numerator: AbstractInteger
  def denominator: AbstractInteger 
  override def decimal = BigDecimal(numerator.integ)
  override def isOne = numerator.integ == denominator.integ
  override def reduce: AbstractRational = 
    if (denominator.isOne) numerator.reduce else this // should use gcd
}

case class Frac(numerator: Integ, denominator: Integ) extends AbstractRational {
  require(denominator.integ != 0, "denominator must be non-zero")
}
case object Frac {
  def apply(n: BigInt, d: BigInt) = new Frac(Integ(n), Integ(d))
}

trait AbstractInteger extends AbstractRational {
  def integ: BigInt
  override def numerator = this
  override def denominator = Number.One 
  override def isZero = integ == 0
  override def isOne = integ == 1
  override def decimal: BigDecimal = BigDecimal(integ)
  override def reduce: AbstractInteger = 
    if (isZero) Number.Zero else if (isOne) Number.One else this
}

case class Integ(integ: BigInt) extends AbstractInteger 

trait AbstractNatural extends AbstractInteger 

case class Nat(integ: BigInt) extends AbstractNatural{
  require(integ >= 0, "natural numnbers must be non-negative")
}
