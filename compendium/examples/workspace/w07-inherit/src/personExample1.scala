object personExample1 {

  class Person(val namn: String)

  class Akademiker(namn: String,
                   val universitet: String) extends Person(namn)

  class Student(namn: String,
                universitet: String,
                val program: String) extends Akademiker(namn, universitet)

  class Forskare(namn: String,
                 universitet: String,
                 val titel: String) extends Akademiker(namn, universitet)

  def main(args: Array[String]): Unit = {
    val kim = new Student("Kim Robinsson", "Lund", "Data")
    println(s"${kim.namn} ${kim.universitet} ${kim.program}")
  }

}
