/** A simple wrapper of Java.time */

package bank.time

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import scala.util.Try

/**
 * Creates a Date object based on date given as parameters.
 */
case class Date(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int) {
  private val calendar = LocalDateTime.of(year, month, dayOfMonth, hour, minute)

  private def toFormat(format: String): String = calendar.format(DateTimeFormatter.ofPattern(format))

  /**
   * A short string showing the day and month
   */
  lazy val toShortFormat: String = toFormat("d/M")

  /**
   * A string suitable for writing to log files
   */
  lazy val toLogFormat: String = toFormat("yyyy M d H m")

  /**
   * A string suitable for showing to users
   */
  lazy val toNaturalFormat: String = toFormat("HH:mm d/M-yyyy")

  /**
   * Returns a negative number if this date happened before the argument date, returns a positive number
   * if this date happened after the argument date, and returns 0 if both dates are equal.
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

  /**
   * Converts a string obtained from toLogFormat into a Date object.
   */
  def fromLogFormat(str: String): Date = {
    Try{
      val xs = str.split(' ')
      Date(xs(0).toInt, xs(1).toInt, xs(2).toInt, xs(3).toInt, xs(4).toInt)
    }.recover{
      case e: IndexOutOfBoundsException =>
        throw new IllegalArgumentException(s"Invalid Date string: $str", e)
      case e: NumberFormatException =>
        throw new IllegalArgumentException(s"Invalid Date string: $str", e)
    }.get
  }
}
