/** A simple wrapper of Java.time */

package bank.time

import java.time.LocalDateTime

 /**
 * Creates a Date object based on date given as parameters.
 */
case class Date(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int) {

  private val calendar = LocalDateTime.of(year, month, dayOfMonth, hour, minute)

  /**
   * Short format of the date
   */
  lazy val shortDate = "" + calendar.getDayOfMonth + "/" + (calendar.getMonth)

  /**
   * Relaxed format of the date
   */
  lazy val relaxedTimeFormat = calendar.getYear + " " + calendar.getMonthValue + " " + calendar.getDayOfMonth + " " + calendar.getHour + " " + calendar.getMinute

  /**
   * Standard output of the date
   */
  lazy val calendarTime = calendar.getHour + ":" + calendar.getMinute + ":" + calendar.getSecond + " CET " + calendar.getMonthValue + " / " +
        calendar.getDayOfMonth + " - " + calendar.getYear

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
  def now() = {
    Date(LocalDateTime.now.getYear, LocalDateTime.now.getMonthValue, LocalDateTime.now.getDayOfMonth,
         LocalDateTime.now.getHour, LocalDateTime.now.getMinute)
  }
}
