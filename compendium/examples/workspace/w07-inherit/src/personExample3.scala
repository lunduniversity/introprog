object personExample3:

  trait Person     { val namn: String }
  trait Akademiker { val universitet: String }
  trait Examinerad { val titel: String }

  class Student(val namn: String,
                val universitet: String,
                val program: String) extends Person, Akademiker

  class Forskare(val namn: String,
                 val universitet: String,
                 val titel: String) extends Person, Akademiker, Examinerad

  def main(args: Array[String]): Unit =
    var p: Person = new Forskare("B. Regnell", "Lunds universitet", "Professor")
    if p.isInstanceOf[Akademiker] then println(p.namn + " är akademiker")
    p = new Student("Kim Robinson", "Lund", "Data")  // går det att göra p.program?
    if p.isInstanceOf[Student] then println(p.asInstanceOf[Student].program)
  // Ovan görs hellre med match
