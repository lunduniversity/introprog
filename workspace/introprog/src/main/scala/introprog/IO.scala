package introprog

/** A module with input/output operations from/to the underlying file system. */
object IO {
  /**
    * Load a string from a text file called `fileName` using encoding `enc`.
    *
    * @param fileName the path of the file.
    * @param enc the encoding of the file.
    * @return the content loaded from the file.
    * */
  def loadString(fileName: String, enc: String = "UTF-8"): String = {
    var result: String = ""
    val source = scala.io.Source.fromFile(fileName, enc)
    try result = source.mkString finally source.close()
    result
  }

  /**
    * Load string lines from a text file called `fileName` using encoding `enc`.
    *
    * @param fileName the path of the file.
    * @param enc the encoding of the file.
    * */
  def loadLines(fileName: String, enc: String = "UTF-8"): Vector[String] = {
    var result = Vector.empty[String]
    val source = scala.io.Source.fromFile(fileName, enc)
    try result = source.getLines().toVector finally source.close()
    result
  }

  /**
    * Save `text` to a text file called `fileName` using encoding `enc`.
    *
    * @param text the text to be written to the file.
    * @param fileName the path of the file.
    * @param enc the encoding of the file.
    * */
  def saveString(text: String, fileName: String, enc: String = "UTF-8"): Unit = {
    val f = new java.io.File(fileName)
    val pw = new java.io.PrintWriter(f, enc)
    try pw.write(text) finally pw.close()
  }

  /**
    * Save `lines` to a text file called `fileName` using encoding `enc`.
    *
    * @param lines the lines to written to the file.
    * @param fileName the path of the file.
    * @param enc the encoding of the file.
    * */
  def saveLines(lines: Seq[String], fileName: String, enc: String = "UTF-8"): Unit =
    saveString(lines.mkString("\n"), fileName, enc)

  /**
    * Load a serialized object from a binary file called `fileName`.
    *
    * @param fileName the path of the file.
    * @return the serialized object.
    * */
  def loadObject[T](fileName: String): T = {
    val f = new java.io.File(fileName)
    val ois = new java.io.ObjectInputStream(new java.io.FileInputStream(f))
    try ois.readObject.asInstanceOf[T] finally ois.close()
  }

  /**
    * Serialize `obj` to a binary file called `fileName`.
    *
    * @param obj the object to be serialized.
    * @param fileName the path of the file.
    * */
  def saveObject[T](obj: T, fileName: String): Unit = {
    val f = new java.io.File(fileName)
    val oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(f))
    try oos.writeObject(obj) finally oos.close()
  }

  /**
    * Test if a file with name `fileName` exists.
    *
    * @param fileName the path of the file.
    * @return true if the file exists else false.
    * */
  def isExisting(fileName: String): Boolean = new java.io.File(fileName).exists

  /**
    * Create a directory with name `dir` if it does not exist.
    *
    * @param dir the path of the directory to be created.
    * @return true if and only if the directory was created,
    *         along with all necessary parent directories otherwise false.
    * */
  def createDirIfNotExist(dir: String): Boolean = new java.io.File(dir).mkdirs()

  /**
    * Gets the path of the current user's home directory.
    *
    * @return the path of the current user's home directory.
    * */
  def userDir(): String = System.getProperty("user.home")

  /**
    * Gets the path of the current working directory.
    *
    * @return the path of the current working directory.
    * */
  def currentDir(): String =
    java.nio.file.Paths.get(".").toAbsolutePath.normalize.toString

  /**
    * Gets a sequence of file names in the directory `dir`.
    *
    * @param dir the path of the directory to be listed.
    * @return a sequence of file names in the directory `dir
    * */
  def list(dir: String = "."): Vector[String] =
    Option(new java.io.File(dir).list).map(_.toVector).getOrElse(Vector())

  /**
    * Change name of file `from`, DANGER: silently replaces existing `to`.
    *
    * @param from the path of the file to be moved.
    * @param to the path the file will be moved to.
    * */
  def move(from: String, to: String): Unit = {
    import java.nio.file.{Files, Paths, StandardCopyOption}
    Files.move(Paths.get(from), Paths.get(to), StandardCopyOption.REPLACE_EXISTING)
  }

    /**
    * DANGER: silently deletes `fileName`.
    *
    * @param fileName the path the file that will be deleted.
    * */
  def delete(fileName: String): Unit = {
    import java.nio.file.{Files, Paths, StandardCopyOption}
    Files.delete(Paths.get(fileName))
  }

    /**
    * Loads an image from file
    *
    * @param fileName the path the image that will be loaded.
    * @return BufferedImage
    * */
  def loadImage(fileName: String): java.awt.image.BufferedImage = 
    javax.imageio.ImageIO.read(new java.io.File(fileName))
  
    /**
    * save an image to file
    *
    * @param fileName the path to save to image to.
    * @param image the image to save
    * @return true on success
    * */
  def saveImage(fileName: String, image: java.awt.image.BufferedImage) : Boolean = {
    javax.imageio.ImageIO.write(image, "png", new java.io.File(fileName))
  }

}
