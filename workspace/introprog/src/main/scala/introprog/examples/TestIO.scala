package introprog.examples

/** Example of serializing objects to and from binary files on disk. */
object TestIO {
  import introprog.IO

  case class Person(name: String)

  def main(args: Array[String]): Unit = {
    println("Test of IO of serializable objects to/from disk:")
    val highscores = Map(Person("Sandra") -> 42, Person("Björn") -> 5)

    // serialize to disk:
    IO.saveObject(highscores,"highscores.ser")

    // de-serialize back from disk:
    val highscores2 = IO.loadObject[Map[Person, Int]]("highscores.ser")

    val isSameContents = highscores2 == highscores
    val testResult = if (isSameContents) "SUCCESS :)" else "FAILURE :("
    assert(isSameContents, s"$highscores != $highscores2")
    println(s"$highscores == $highscores2\n$testResult")
  }

// for file extension choice see:
// https://stackoverflow.com/questions/10433214/file-extension-for-a-serialized-object

}
