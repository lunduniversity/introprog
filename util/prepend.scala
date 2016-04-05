/** 
* This small utility was written as we needed to prepend this magic string 
* %!TEX encoding = UTF-8 Unicode
* to all .tex files to ensure that encoding on all platforms works as expected.
* The problem was discovered when opening the .tex files on Mac OSX.
* Usage:  scala prepend . 'line to prepend' .tex
* scala prepend with no params will prepend the magic tex utf-8 encoding directive 
*/

object prepend { 
  import java.nio.file.{Paths, Files}
  import java.nio.charset.StandardCharsets.UTF_8  
  
  def load(fileName: String): Vector[String] = 
    io.Source.fromFile(fileName).getLines.toVector
  
  def save(data: String, fileName: String): Unit = {
      println("Saving file: " + Paths.get(fileName))
      Files.write(Paths.get(fileName), data.getBytes(UTF_8))
  }
  
  def isDir(name: String): Boolean = (new java.io.File(name)).isDirectory
  
  def prepend(dir: String, prep: String, fileType: String): Unit = {
    lazy val here = Paths.get(dir).toFile
    println("Workdir: " + here)
    lazy val files = here.list.toVector.filter(_.endsWith(fileType)).filterNot(isDir)
    println("Line to prepend: '" + prep + "'")
    println("File type filter: " + fileType)
    println("Files to prepend it to:\n" + files.mkString("\n"))
    if (io.StdIn.readLine("Continue (Y/n)?  ").startsWith("Y")) {
      files.foreach{ fileName => 
        val in  = load(fileName)
        if (in.headOption != Some(prep)) {
          val out = (prep +: in).mkString("\n")
          save(data=out,fileName) 
        } else println(s"Not prepped: '$prep' is alread first line of $fileName")
      }
    }
  }
      
  def main(args: Array[String]) = {
    args.toSeq match {
      case Seq(dir, pre, fileType) => prepend(dir, pre, fileType)
      case Seq() => prepend(".", "%!TEX encoding = UTF-8 Unicode", ".tex")
      case _ =>  println("""Usage: scala prepend "~/dir" "line to prepend" .filetype""")
    }
  }
}

