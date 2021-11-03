package introprog.examples

/** Example of serializing objects to and from binary files on disk. */
object TestIO:
  import introprog.IO

  case class Person(name: String)

  def main(args: Array[String]): Unit =
    println("Test of IO of serializable objects to/from disk:")
    val highscores = Map(Person("Sandra") -> 42, Person("Björn") -> 5)

    // serialize to disk:
    IO.saveObject(highscores,"highscores.ser")

    // de-serialize back from disk:
    val highscores2 = IO.loadObject[Map[Person, Int]]("highscores.ser")

    val isSameContents = highscores2 == highscores
    val testResult = if isSameContents then "SUCCESS :)" else "FAILURE :("
    assert(isSameContents, s"$highscores != $highscores2")
    println(s"$highscores == $highscores2\n$testResult")

    testImageLoadAndDraw()

  def testImageLoadAndDraw(): Unit =
    import introprog.*
    import java.awt.Color
    import java.awt.Color.*

    val wSize = (4*128, 3*128)
    val w =  new PixelWindow(wSize._1, wSize._2, "DrawImage");
    val w2 = new PixelWindow(wSize._1, wSize._2, "DrawMatrix")
    val w3 = new PixelWindow((wSize._1*1.5).toInt, (wSize._2*1.5).toInt, "SaveLoadAsJpeg")
    w.setPosition(0,0)
    w2.setPosition(wSize._1, 0)
    w3.setPosition(0, wSize._2+50)
    //draw text top right
    val testMatrix = Array[Array[Color]](Array[Color](blue, yellow, blue), 
                                        Array[Color](yellow, yellow,  yellow),
                                        Array[Color](blue, yellow,  blue),
                                        Array[Color](blue, yellow,  blue))
    var flagPos = (0, 0)
    var flagSize = (4, 3)
    
    //draw small flag                
    w.drawMatrix(testMatrix, 0, 0)
    for i <- 1 to 7 do
      // extract and save Image
      var img = w.getImage(flagPos._1, flagPos._2, flagSize._1, flagSize._2)
      IO.savePNG(img, "screenshot")
      //draw in other window using drawMatrix
      w2.drawMatrix(img.toMatrix, flagPos._1, flagPos._2)
      if i != 7 then
        //update pos and size
        flagPos = (flagPos._1 + flagSize._1,flagPos._2 + flagSize._2)
        flagSize = (flagSize._1 * 2,flagSize._2 * 2)
        //draw new flag from file
        img = IO.loadImage("screenshot.png")
        w.drawImage(img.scaled(img.width*2, img.height*2), flagPos._1, flagPos._2)

    var im = w2.getImage
    IO.saveJPEG(im, "screenshot.jpg", 0.2) 
    im = IO.loadImage("screenshot.jpg")

    
    for i <- 0 to 200 do
      w3.clear()
      w3.drawImage(im, 0, 0, (im.width*0.5).toInt, (im.height*0.5).toInt, Math.toRadians(i*2))
      Thread.sleep(100/6)


    println("Windows should be identical and display 7 flags each.")
    println("Press enter to quit.")
    val _ = scala.io.StdIn.readLine()
    IO.delete("screenshot.png")
    IO.delete("screenshot.jpg")
    PixelWindow.exit()

// for file extension choice see:
// https://stackoverflow.com/questions/10433214/file-extension-for-a-serialized-object

