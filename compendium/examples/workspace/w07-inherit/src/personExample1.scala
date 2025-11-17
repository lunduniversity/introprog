object personExample1:

  class Person(val namn: String)

  class Akademiker(
    override val namn: String,
    val universitet: String) extends Person(namn)

  class Student(
    override val namn: String,
    override val universitet: String,
    val program: String) extends Akademiker(namn, universitet)

  class Forskare(
    override val namn: String,
    override val universitet: String,
    val titel: String) extends Akademiker(namn, universitet)

  @main def Test: Unit =
    val kim = new Student("Kim Robinsson", "Lund", "Data")
    println(s"${kim.namn} ${kim.universitet} ${kim.program}")

