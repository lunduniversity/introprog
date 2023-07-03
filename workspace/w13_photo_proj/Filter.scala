package photo

import java.awt.Color
import introprog.Image
import scala.util.Random

/** A base trait for image filters */
trait Filter:
  def name: String

  /** Override `argDescriptions` with information about filter arguments if any. */ 
  def argDescriptions: Seq[String] = Seq()

  def nbrOfArgs = argDescriptions.length
  
  /** Apply filter on `im`. Return a new Image using the arguments in `args`. */
  def apply(im: Image, args: Double*): Image
  
  /** Return an intensity matrix of `im` with values in (0 to 255). */
  def computeIntensity(im: Image): Matrix = ???

  /** Convolve `p[i][j]` with the 3x3 matrix `kernel`, normalized by `weight`. */
  def convolve(p: Matrix, i: Int, j: Int, kernel: Matrix, weight: Int): Short = ???

/** An object with available Filter implementations. */
object Filter:
  val byIndex: Vector[Filter] = Vector(Identity)  // Add more filters here

  val byName: Map[String, Filter] = byIndex.map(f => f.name -> f).toMap

  object Identity extends Filter:
    val name = "Identity"
    def apply(im: Image, args: Double*): Image = 
      val result = Image.ofDim(im.width, im.height)
      result.updated((x, y) => im(x, y)) // just copy all pixels







