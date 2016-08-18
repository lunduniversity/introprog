package imageproject

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.File
import java.awt.Color

class Image(val image: BufferedImage) {
	val height = image.getHeight
	val width = image.getWidth

  /** Returns a matrix of Color-objects that represents an image */
  def getColorMatrix: Array[Array[Color]] = {
    val pixels: Array[Array[Color]] = Array.ofDim(height, width)

    for (i <- 0 until height; j <- 0 until width) {
      pixels(i)(j) = new Color(image.getRGB(j, i))
    }
    pixels
  }

  /** Updates the image in accordance with the given Color-matrix */
  def updateImage(pixels: Array[Array[Color]]): Unit = {
    val height = pixels.length
    val width = pixels(0).length

    for (i <- 0 until height; j <- 0 until width) {
      image.setRGB(j, i, pixels(i)(j).getRGB)
    }
  }

}