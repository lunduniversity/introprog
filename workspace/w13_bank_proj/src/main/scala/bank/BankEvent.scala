package bank

import scala.util.Try

sealed trait BankEvent {
  /**
   * Returns a string suitable for writing to log files.
   */
  def toLogFormat: String

  /**
   * Returns a string suitable for showing to users.
   */
  def toNaturalFormat: String
}

case class Deposit(account: Int, amount: BigInt) extends BankEvent {
  def toLogFormat: String = s"D $account $amount"
  def toNaturalFormat: String = s"Satte in $amount kr i konto $account"
}

case class Withdraw(account: Int, amount: BigInt) extends BankEvent {
  def toLogFormat: String = s"W $account $amount"
  def toNaturalFormat: String = s"Tog ut $amount kr från konto $account"
}

case class Transfer(accFrom: Int, accTo: Int, amount: BigInt) extends BankEvent {
  def toLogFormat: String = s"T $accFrom $accTo $amount"
  def toNaturalFormat: String = s"Överförde $amount kr från konto $accFrom till konto $accTo"
}

case class NewAccount(id: Long, name: String) extends BankEvent {
  def toLogFormat: String = s"N $id $name"
  def toNaturalFormat: String = s"Skapade ett konto tillhörandes $name, id $id"
}

case class DeleteAccount(account: Int) extends BankEvent {
  def toLogFormat: String = s"E $account"
  def toNaturalFormat: String = s"Raderade konto $account"
}

object BankEvent {
  /**
   * Converts a string obtained from toLogFormat into a BankEvent object.
   */
  def fromLogFormat(str: String): BankEvent = {
    Try{
      val xs = str.split(' ')
      xs(0) match {
        case "D" => Deposit(xs(1).toInt, BigInt(xs(2)))
        case "W" => Withdraw(xs(1).toInt, BigInt(xs(2)))
        case "T" => Transfer(xs(1).toInt, xs(2).toInt, BigInt(xs(3)))
        case "N" => NewAccount(xs(1).toLong, xs.drop(2).mkString(" "))
        case "E" => DeleteAccount(xs(1).toInt)
        case s => throw new IllegalArgumentException(s"Unknown BankEvent type: $str")
      }
    }.recover{
      case e: IndexOutOfBoundsException =>
        throw new IllegalArgumentException(s"Invalid BankEvent string: $str", e)
      case e: NumberFormatException =>
        throw new IllegalArgumentException(s"Invalid BankEvent string: $str", e)
    }.get
  }
}
