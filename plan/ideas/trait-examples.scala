trait AbstractPerson {
  def name: String  // abstract
  def action: String = "do abstract stuff"  // change to val and see what happens
  def show: Unit = print(name + " " + action)  // concrete def
  final def pr: Unit = {show; println}  // final prevents override
}

class Person(val name: String) extends AbstractPerson {
  override val action = "idle"
}

trait HasUniversity extends AbstractPerson {
  def univ: String   
  override def show: Unit = {super.show; print(" @ " + univ)}
}

class Student(val name: String, val univ: String)
  extends AbstractPerson with HasUniversity {
  override def action = "study" // change to val and see what happens
 // http://stackoverflow.com/questions/32577993/how-do-i-access-an-overridden-data-member-in-scala
}

trait HasTitle extends AbstractPerson {
  val title: String 
  override def show = {print(title + " "); super.show}
}

class Researcher(val name: String, val title: String, val univ: String) 
  extends AbstractPerson with HasUniversity with HasTitle {
  override val action = "does important research"
}

class PhDStudent(override val name: String, override val univ: String)  
              //  ^ override needed as it is assigned in primary constructor of Student
  extends Student(name, univ) with HasTitle {
  val title = "Doctoral Candidate"
  override val action = "study a lot & " + super.action
}

object Test {
  def main(args: Array[String]): Unit = {
    (new Person("Kim Robinsson")).pr
    (new Student("Oddput Clementin", "Lund University")).pr
    (new Researcher("Marie Currie", "Professor", "Lund University")).pr
    (new PhDStudent("Alfons Einstein", "Lund University")).pr
    (new Person("Oddput Clementin") with HasTitle { val title = "Mr."}).pr      
  }
}
