object personExample2:

  trait Person     { val namn: String }
  trait Akademiker { val universitet: String }
  trait Examinerad { val titel: String }

  class Student(val namn: String,
                val universitet: String,
                val program: String) extends Person, Akademiker

  class Forskare(val namn: String,
                 val universitet: String,
                 val titel: String) extends Person, Akademiker, Examinerad

  @main def Test: Unit =
    val f = new Forskare("B. Regnell", "Lunds universitet", "Professor")
    println(s"${f.titel} ${f.namn}, ${f.universitet}")