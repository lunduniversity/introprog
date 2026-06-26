package photo

import introprog.Image

trait Filter:
  def name: String

  def argDescriptions: Seq[String] = Seq()

  def nbrOfArgs = argDescriptions.length
  
  def apply(im: Image, args: Double*): Image
  
object Filter:
  val byIndex: Vector[Filter] = Vector(Identity)
  
  val byName: Map[String, Filter] = byIndex.map(f => f.name -> f).toMap
  
  def intensity(im: Image): Matrix = ???

  def convolve(p: Matrix, x: Int, y: Int, kernel: Matrix, weight: Int): Short = ???
  
  object Identity extends Filter:
    val name = "Identity"
    def apply(im: Image, args: Double*): Image = 
      val result = Image.ofDim(im.width, im.height)
      result.updated((x, y) => im(x, y))





