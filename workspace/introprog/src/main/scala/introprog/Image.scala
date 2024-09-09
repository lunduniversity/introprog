package introprog

/** Companion object to create Image instances. */ 
object Image:
  import java.awt.image.BufferedImage
  /** Create new empty Image with specified dimensions `(width, height)`*/
  def ofDim(width: Int, height: Int) =
    Image(BufferedImage(width, height, BufferedImage.TYPE_INT_RGB))

/** Image represents pixel arrays backed by underlying java.awtimage.BufferedImage */
class Image (val underlying: java.awt.image.BufferedImage):
  import java.awt.Color
  import java.awt.image.BufferedImage
  
  /** Get color of pixel at `(x, y)`.*/
  def apply(x: Int, y: Int): Color = Color(underlying.getRGB(x, y))

  /** Set color of pixel at `(x, y)`.*/
  def update(x: Int, y: Int, c: Color): Unit = underlying.setRGB(x, y, c.getRGB)

  /** Set color of pixels by passing `f(x, y)`*/
  def update(f: (Int, Int) => Color): Unit = 
    for x <- 0 until width; y <- 0 until height do
        update(x, y, f(x, y))

  /** Set color of pixels by passing `f(x, y)` and return self. */
  def updated(f: (Int, Int) => Color): Image =
    for x <- 0 until width; y <- 0 until height do
        update(x, y, f(x, y))
    this
    
  /** Extract and return image pixels. */
  def toMatrix: Array[Array[Color]] = 
    val xs: Array[Array[Color]] = Array.ofDim(width, height)
    for x <- 0 until width; y <- 0 until height do
      xs(x)(y) = apply(x, y)
    xs

  /** Copy subsection of image defined by top left corner `(x, y)` and `(width, height)`.*/
  def subsection(x: Int, y: Int, width: Int, height: Int): Image =   
    val bi = BufferedImage(width, height, underlying.getType)
    bi.createGraphics().drawImage(underlying, 0, 0, width, height, x, y, x + width, y + height, null)
    Image(bi)

  /** Copy image and scale to `(width, height)`.*/
  def scaled(width: Int, height: Int): Image = 
    val bi = BufferedImage(width, height, underlying.getType)
    bi.createGraphics().drawImage(underlying, 0, 0, width, height, null)
    Image(bi)
  
  /** Copy image and change image type to ARGB, including alpha channel*/
  def withAlpha: Image = toImageType(BufferedImage.TYPE_INT_ARGB)

  /** Copy image and change image type to RGB, removing alpha channel*/
  def withoutAlpha: Image = toImageType(BufferedImage.TYPE_INT_RGB)

  /** Copy image and change image type ex. BufferedImage.TYPE_INT_RGB*/
  private def toImageType(imageType: Int): Image =
    val bi = BufferedImage(width, height, imageType)
    bi.createGraphics().drawImage(underlying, 0, 0, width, height, null)
    Image(bi)
  
  /** Test if alpha channel is supperted. */
  val hasAlpha = underlying.getColorModel.hasAlpha
  
  /** The height of this image. */ 
  val height = underlying.getHeight
  
  /** The width of this image. */ 
  val width = underlying.getWidth
  