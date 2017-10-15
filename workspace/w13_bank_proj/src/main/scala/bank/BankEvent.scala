package bank

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
