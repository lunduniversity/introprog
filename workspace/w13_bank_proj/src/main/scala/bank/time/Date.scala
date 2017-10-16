/** A simple wrapper of Java.time */

package bank.time

import java.time.LocalDateTime

/**
 * Creates a Date object based on date given as parameters.
 */
case class Date(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int) {
  private val calendar = LocalDateTime.of(year, month, dayOfMonth, hour, minute)

  /**
   * A short string showing the day and month
   */
  lazy val toShortFormat: String = "" + calendar.getDayOfMonth + "/" + (calendar.getMonth)

  /**
   * A string suitable for writing to log files
   */
  lazy val toLogFormat: String = calendar.getYear + " " + calendar.getMonthValue + " " + calendar.getDayOfMonth + " " + calendar.getHour + " " + calendar.getMinute

  /**
   * A string suitable for showing to users
   */
  lazy val toNaturalFormat: String = calendar.getHour + ":" + calendar.getMinute + ":" + calendar.getSecond + " CET " + calendar.getDayOfMonth + " / " +
        calendar.getMonthValue + " - " + calendar.getYear

  /**
   * Checks whether this date happened before, at the same time as or after the provided date.
   */
  def compare(date: Date): Int = {
    calendar.compareTo(date.calendar)
  }
}

object Date {
  /**
   * Creates a Date object based on the current time of the computer's clock.
   */
  def now(): Date = {
    Date(LocalDateTime.now.getYear, LocalDateTime.now.getMonthValue, LocalDateTime.now.getDayOfMonth,
         LocalDateTime.now.getHour, LocalDateTime.now.getMinute)
  }
}
