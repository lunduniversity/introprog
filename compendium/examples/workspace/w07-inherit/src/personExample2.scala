object personExample2 {

  trait Person { val namn: String }

  trait Akademiker { val universitet: String }

  trait Examinerad { val titel: String }

  class Student(val namn: String,
                val universitet: String,
                val program: String) extends Person with Akademiker

  class Forskare(val namn: String,
                 val universitet: String,
                 val titel: String) extends Person with Akademiker with Examinerad

  def main(args: Array[String]): Unit = {
    var p: Person = new Forskare("Robin Smith", "Lund", "Professor Dr")
    println(s"${p.namn}")  // staiska typen Person har inget universitet el. titel
    if (p.isInstanceOf[Akademiker]) println(p.namn)
  }
}
