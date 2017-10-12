/** A simple wrapper of Java.time */

package bank.time

 /**
 * Creates a Date object based on date given as parameters.
 */
case class Date(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int) {
  import java.time;
  import java.time.LocalDateTime

  private val calender = LocalDateTime.of(year, month, dayOfMonth, hour, minute)

  /**
   * Short format of the date
   */
  lazy val shortDate = "" + calender.getDayOfMonth + "/" + (calender.getMonth)

  /**
   * Relaxed format of the date
   */
  lazy val relaxedTimeFormat = calender.getYear + " " + calender.getMonthValue + " " + calender.getDayOfMonth + " " + calender.getHour + " " + calender.getMinute

  /**
   * Standard output of the date
   */
  lazy val calendarTime = calender.getHour + ":" + calender.getMinute + ":" + calender.getSecond + " CET " + calender.getMonthValue + " / " +
        calender.getDayOfMonth + " - " + calender.getYear

  /**
   * Checks whether this date happened before, at the same time as or after the provided date.
   */
  def compare(date: Date): Int = {
    calender.compareTo(date.calender)
  }
}

object Date {
  /**
   * Creates a Date object based on the current time of the computer's clock.
   */
  def now() = {
    Date(java.time.LocalDateTime.now.getYear, java.time.LocalDateTime.now.getMonthValue, java.time.LocalDateTime.now.getDayOfMonth,
         java.time.LocalDateTime.now.getHour, java.time.LocalDateTime.now.getMinute)
  }
}
