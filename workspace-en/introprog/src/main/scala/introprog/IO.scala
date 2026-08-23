package introprog

import java.io.BufferedWriter
import java.io.FileWriter
import java.nio.charset.Charset

/** A module with input/output operations from/to the underlying file system. */
object IO:
  /**
    * Load a string from a text file called `fileName` using encoding `enc`.
    *
    * @param fileName the path of the file.
    * @param enc the encoding of the file.
    * @return the content loaded from the file.
    * */
  def loadString(fileName: String, enc: String = "UTF-8"): String =
    var result: String = ""
    val source = scala.io.Source.fromFile(fileName, enc)
    try result = source.mkString finally source.close()
    result

  /**
    * Load string lines from a text file called `fileName` using encoding `enc`.
    *
    * @param fileName the path of the file.
    * @param enc the encoding of the file.
    * */
  def loadLines(fileName: String, enc: String = "UTF-8"): Vector[String] =
    var result = Vector.empty[String]
    val source = scala.io.Source.fromFile(fileName, enc)
    try result = source.getLines().toVector finally source.close()
    result

  /**
    * Save `text` to a text file called `fileName` using encoding `enc`.
    *
    * @param text the text to be written to the file.
    * @param fileName the path of the file.
    * @param enc the encoding of the file.
    * */
  def saveString(text: String, fileName: String, enc: String = "UTF-8"): Unit =
    val f = new java.io.File(fileName)
    val pw = new java.io.PrintWriter(f, enc)
    try pw.write(text) finally pw.close()

  /**
    * Save `lines` to a text file called `fileName` using encoding `enc`.
    *
    * @param lines the lines to written to the file.
    * @param fileName the path of the file.
    * @param enc the encoding of the file.
    * */
  def saveLines(lines: Seq[String], fileName: String, enc: String = "UTF-8"): Unit =
    if lines.nonEmpty then saveString(lines.mkString("", "\n", "\n"), fileName, enc)

  /**
    * Appends `string` to the text file `fileName` using encoding `enc`.
    *
    * @param text the text to be appended to the file.
    * @param fileName the path of the file.
    * @param enc the encoding of the file.
    * */
  def appendString(text: String, fileName: String, enc: String = "UTF-8"): Unit =
    val f = new java.io.File(fileName);
    require(!f.isDirectory(), "The file you're trying to write to can't be a directory.")
    val w =
      if f.exists() then
        new BufferedWriter(new FileWriter(fileName, Charset.forName(enc), true))
      else
        new java.io.PrintWriter(f, enc)
    try w.write(text) finally w.close()

  /**
    * Appends `lines` to the text file `fileName` using encoding `enc`.
    *
    * @param lines the lines to append to the file.
    * @param fileName the path of the file.
    * @param enc the encoding of the file.
    * */
  def appendLines(lines: Seq[String], fileName: String, enc: String = "UTF-8"): Unit =
    if lines.nonEmpty then appendString(lines.mkString("","\n","\n"), fileName, enc)

  /**
    * Load a serialized object from a binary file called `fileName`.
    *
    * @param fileName the path of the file.
    * @return the serialized object.
    * */
  def loadObject[T](fileName: String): T =
    val f = new java.io.File(fileName)
    val ois = new java.io.ObjectInputStream(new java.io.FileInputStream(f))
    try ois.readObject.asInstanceOf[T] finally ois.close()

  /**
    * Serialize `obj` to a binary file called `fileName`.
    *
    * @param obj the object to be serialized.
    * @param fileName the path of the file.
    * */
  def saveObject[T](obj: T, fileName: String): Unit =
    val f = new java.io.File(fileName)
    val oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(f))
    try oos.writeObject(obj) finally oos.close()

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
  def move(from: String, to: String): Unit =
    import java.nio.file.{Files, Paths, StandardCopyOption}
    Files.move(Paths.get(from), Paths.get(to), StandardCopyOption.REPLACE_EXISTING)

    /**
    * Deletes `fileName`.
    *
    * @param fileName the path the file that will be deleted.
    * */
  def delete(fileName: String): Unit =
    import java.nio.file.{Files, Paths}
    Files.delete(Paths.get(fileName))

    /**
    * Load image from file.
    *
    * @param fileName the path to the image that will be loaded.
    * */
  def loadImage(fileName: String): Image = 
    loadImage(java.io.File(fileName))

    /**
    * Load image from file.
    *
    * @param file the file that will be loaded.
    * */
  def loadImage(file: java.io.File): Image = 
    Image(javax.imageio.ImageIO.read(file))

    /**
    * Save `img` to file as `JPEG`. Does not restore color of transparent pixels.
    *
    * @param image the image to save.
    * @param fileName the path to save the image to, `path/file.jpg` or just `path/file`
    * @param compression the compression factor to use `(0.0-1.0)`.
    * */
  def saveJPEG(img: Image, fileName: String, compression: Double) : Unit = 
    require(compression <= 1.0 && compression >= 0.0, "compression must be within 0.0 and 1.0")
    import javax.imageio.{stream, ImageIO, IIOImage, ImageWriteParam}
    import javax.imageio.plugins.jpeg.JPEGImageWriteParam
    import java.awt.image.BufferedImage
    //set compression values
    val jpegParams = JPEGImageWriteParam(null);
    jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    jpegParams.setCompressionQuality(compression.toFloat);
    //create writer
    val writer = ImageIO.getImageWritersByFormatName("jpg").next();
    // specifies where the jpg image has to be written
    val path = if fileName.endsWith(".jpg") then fileName else s"$fileName.jpg"
    writer.setOutput(new stream.FileImageOutputStream(
      java.io.File(path)))
    // writes the file with given compression level 
    // from JPEGImageWriteParam instance
    writer.write(
      null, 
      IIOImage(
        (if img.hasAlpha then img.withoutAlpha else img).underlying, //remove alpha channel
        null, 
        null)
      ,jpegParams) //add compression details


  /**
  * Save `img` to file as `JPEG` with a compression ratio of 0.75.
  * Restore color of transparent pixels.
  * @param img the image to save.
  * @param fileName the path to save the image to, `path/file.jpg` or just `path/file`.
  * */
  def saveJPEG(img: Image, fileName: String) : Unit = 
    import javax.imageio.ImageIO
    import java.io.File
    if !ImageIO.write(img.underlying, "jpg", File(if fileName.endsWith(".jpg") then fileName else s"$fileName.jpg")) then
    throw java.io.IOException("no appropriate writer is found")

  /**
  * Save `img` to file as `PNG`.
  *
  * @param img the image to save.
  * @param fileName the path to save the image to, `path/file.png` or just `path/file`.
  * */
  def savePNG(img: Image, fileName: String) : Unit = 
    import javax.imageio.ImageIO
    import java.io.File
    if !ImageIO.write(img.underlying, "png", File(if fileName.endsWith(".png") then fileName else s"$fileName.png")) then
    throw java.io.IOException("no appropriate writer is found")


  

