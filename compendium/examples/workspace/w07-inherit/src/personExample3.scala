object personExample3 {

  trait Person     { val namn: String }
  trait Akademiker { val universitet: String }
  trait Examinerad { val titel: String }

  class Student(val namn: String,
                val universitet: String,
                val program: String) extends Person with Akademiker

  class Forskare(val namn: String,
                 val universitet: String,
                 val titel: String) extends Person with Akademiker with Examinerad

  def main(args: Array[String]): Unit = {
    var p: Person = new Forskare("B. Regnell", "Lunds universitet", "Professor Dr")
    if (p.isInstanceOf[Akademiker]) println(p.namn + " är akademiker")
    p = new Student("Kim Robinson", "Lund", "Data")  // går det att göra p.program?
    if (p.isInstanceOf[Student]) println(p.asInstanceOf[Student].program)
  }  // Ovan görs hellre med match som kommer nästa vecka.
} 
