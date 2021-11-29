package photo

import java.io.File
import scala.util.{Failure, Success, Try}
import introprog.{IO, Image}

object ImageChooser:
  
  val greeting = "Välj en av följande bilder genom att mata in en siffra\n"
  
  /** Accepted file formats */
  val extensions = List("jpeg", "JPG", "jpg", "png")
  
  /** Lists the images from the images folder */
  private def getFiles: List[File] = 
    var files = List[File]()
    val dir = File(System.getProperty("user.dir") + "/images")
    if dir.exists && dir.isDirectory then
      files = dir.listFiles.filter(_.isFile).toList
      files = files.filter(file => extensions.exists(file.getName.endsWith(_)))
    files
  

  
  /**
   * 	Returns a chosen image from the images folder.
   * 	Prints:	
	 *
 	 *	Välj en av följande bilder genom att mata in en siffra
	 *
	 *	0. boy.jpg
	 *	1. car.jpg
	 *	2. duck.jpg
	 *	3. jay.jpg
	 *	4. moon.jpg
	 *	5. shuttle.jpg
	 *	Ditt val: 
   */
  def getImage: Image = 
    val files = getFiles
    showFiles(files)

    val i = scala.io.StdIn.readLine("Ditt val: ")
    val result = Try(IO.loadImage(files(i.toInt)))
    result match 
      case Success(_) => 
        println(s"\n\nBild ${files(i.toInt).getName} laddad\n")
        result.get
      case Failure(e) => 
        println("Error; fel index, försök igen\n")
        getImage
    
  
  /**Print image names*/
  private def showFiles(files: List[File]): Unit = 
    println(greeting)
    var i = 0
    for file <- files do
      println(s"$i. ${file.getName}")
      i += 1