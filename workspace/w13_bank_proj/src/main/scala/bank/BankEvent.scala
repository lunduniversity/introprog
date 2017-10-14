package bank

sealed trait BankEvent {
  def toLogFormat: String
}

case class Deposit(account: Int, amount: BigInt) extends BankEvent {
  def toLogFormat: String = s"D $account $amount"
}
case class Withdraw(account: Int, amount: BigInt) extends BankEvent {
  def toLogFormat: String = s"W $account $amount"
}
case class Transfer(accFrom: Int, accTo: Int, amount: BigInt) extends BankEvent {
  def toLogFormat: String = s"T $accFrom $accTo $amount"
}
case class NewAccount(id: Int, name: String) extends BankEvent {
  def toLogFormat: String = s"N $id $name"
}
case class DeleteAccount(account: Int) extends BankEvent {
  def toLogFormat: String = s"E $account"
}
