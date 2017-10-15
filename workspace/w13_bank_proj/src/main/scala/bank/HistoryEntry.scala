package bank

import bank.time.Date

case class HistoryEntry(date: Date, event: BankEvent) {
  /**
   * Returns a string suitable for writing to log files.
   */
  def toLogFormat: String = s"${date.toLogFormat} ${event.toLogFormat}"

  /**
   * Returns a string suitable for showing to users.
   */
  def toNaturalFormat: String = s"${date.toNaturalFormat}: ${event.toNaturalFormat}"
}
