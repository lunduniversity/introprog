/** An object with many useful input/output methods. */
object mio:
  import java.nio.file.{Path, Paths, Files}
  import java.nio.charset.StandardCharsets.UTF_8

  def load(fileName: String): Vector[String] =
    io.Source.fromFile(fileName).getLines.toVector

  def cat(fileName: String): Unit = load(fileName).foreach(println)

  def listFiles(dir: String): Vector[Path] =
    Files.list(Paths.get(dir)).toArray.map(_.asInstanceOf[Path]).toVector

  def ls: Unit = listFiles("").foreach(println)

  def ls(dir: String): Unit = listFiles(dir).foreach(println)

  def currentDir: Path = Paths.get("").toAbsolutePath

  def pwd: Unit = println(currentDir)

  def save(data: String, fileName: String = "untitled.txt"): Unit = 
    println("Saving to file: " + Paths.get(fileName).toAbsolutePath)
    Files.write(Paths.get(fileName), data.getBytes(UTF_8))
  
  def isDir(name: String): Boolean = (new java.io.File(name)).isDirectory

  private lazy val console = new jline.console.ConsoleReader

  def readln(prompt: String): String = console.readLine(prompt)

  def readln: String = console.readLine("")

  /** Run `ls` if args is empty or else run ls on each dir in args. */
  def main(args: Array[String]): Unit =
   if (args.isEmpty) ls else args.foreach(ls)

