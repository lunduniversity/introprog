/** A simple wrapper of GregorianCalendar with non-zero-based months */
case class Date(year: Int, month: Int, dayOfMonth: Int): 
  import java.util.{GregorianCalendar, Calendar}
  import Calendar._
  
  val calendar = new GregorianCalendar(year, month - 1, dayOfMonth) 
  
  def shortDate: String = "" + calendar.get(DAY_OF_MONTH) + "/" + (calendar.get(MONTH) + 1)
  
  def calendarTime: java.util.Date = calendar.getTime 
  
  def addDays(days: Int): Date = 
    val cal =  new GregorianCalendar(year, month - 1, dayOfMonth) 
    cal.add(DAY_OF_YEAR, days)
    Date(cal.get(YEAR), cal.get(MONTH) + 1, cal.get(DAY_OF_MONTH))
  
  def workWeek: String = shortDate + "-" + addDays(4).shortDate
