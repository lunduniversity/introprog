package imageproject

import java.awt.image.BufferedImage
import java.io.File

import scala.util.Failure
import scala.util.Success
import scala.util.Try

import javax.imageio.ImageIO
import java.util.Scanner

object ImageUI {
  
  val greeting = "*** ImageUI!\n Välj en av följande bilder genom att mata in en siffra\n"
  val extensions = List("jpeg", "jpg")
  val scan = new Scanner(System.in)
  
  var dir = List[File]()
  val d = new File(System.getProperty("user.dir") + "/images")
  if (d.exists && d.isDirectory) {
    dir = d.listFiles.filter(_.isFile).toList
    dir = dir.filter(file => extensions.exists(file.getName.endsWith(_)))
  }
  
  def getImage: BufferedImage = {
    showFiles
    print("Ditt val: ")
    var i = scan.nextInt()
    val result = Try(ImageIO.read(dir(i)))
    result match {
      case Success(_) => 
        println("Bild " + dir(i).getName + " laddad")
        result.get
      case Failure(e) => 
        println("Error; fel index, försök igen")
        getImage
    }
  }
  
  private def showFiles: Unit = {
    println(greeting)
    var i = 0
    for (file <- dir) {
      println(i + ". " + file.getName)
      i += 1
    }
  }
}