package introprog

/** A module with input/output operations from/to the underlying file system. */
object IO {
  /** Load a string from a text file called `fileName` using encoding `enc`. */
  def loadString(fileName: String, enc: String = "UTF-8"): String = {
    var result: String = ""
    val source = scala.io.Source.fromFile(fileName, enc)
    try result = source.mkString finally source.close()
    result
  }

  /** Load string lines from a text file called `fileName` using encoding `enc`. */
  def loadLines(fileName: String, enc: String = "UTF-8"): Vector[String] = {
    var result = Vector.empty[String]
    val source = scala.io.Source.fromFile(fileName, enc)
    try result = source.getLines().toVector finally source.close()
    result
  }

  /** Save `text` to a text file called `fileName` using encoding `enc`. */
  def saveString(text: String, fileName: String, enc: String = "UTF-8"): Unit = {
    val f = new java.io.File(fileName)
    val pw = new java.io.PrintWriter(f, enc)
    try pw.write(text) finally pw.close()
  }

  /** Save `lines` to a text file called `fileName` using encoding `enc`. */
  def saveLines(lines: Seq[String], fileName: String, enc: String = "UTF-8"): Unit =
    saveString(lines.mkString("\n"), fileName, enc)

  /** Load a serialized object from a binary file called `fileName`. */
  def loadObject[T](fileName: String): T = {
    val f = new java.io.File(fileName)
    val ois = new java.io.ObjectInputStream(new java.io.FileInputStream(f))
    try ois.readObject.asInstanceOf[T] finally ois.close()
  }

  /** Serialize `obj` to a binary file called `fileName`. */
  def saveObject[T](obj: T, fileName: String): Unit = {
    val f = new java.io.File(fileName)
    val oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(f))
    try oos.writeObject(obj) finally oos.close()
  }

  /** Test if a file with name `fileName` exists. */
  def isExisting(fileName: String): Boolean = new java.io.File(fileName).exists

  /** Create a directory with name `dir` if it does not exist. */
  def createDirIfNotExist(dir: String): Boolean = new java.io.File(dir).mkdirs()

  /** Return the path name of the current user's home directory. */
  def userDir(): String = System.getProperty("user.home")

  /** Return the path name of the current working directory. */
  def currentDir(): String =
    java.nio.file.Paths.get(".").toAbsolutePath.normalize.toString

  /** Return a sequence of file names in the directory `dir`. */
  def list(dir: String = "."): Vector[String] =
    Option(new java.io.File(dir).list).map(_.toVector).getOrElse(Vector())

  /** Change name of file `from`, DANGER: silently replaces existing `to`. */
  def move(from: String, to: String): Unit = {
    import java.nio.file.{Files, Paths, StandardCopyOption}
    Files.move(Paths.get(from), Paths.get(to), StandardCopyOption.REPLACE_EXISTING)
  }
}
