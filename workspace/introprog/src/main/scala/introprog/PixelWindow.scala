package introprog

/** A module with utilities for event handling in `PixelWindow` instances. */
object PixelWindow:
  /** Immediately exit running application, close all windows, kills all threads. */
  def exit(): Unit = System.exit(0)

  /** Idle waiting for `millis` milliseconds. */
  def delay(millis: Long): Unit = Thread.sleep(millis)

  /** A map with string representations for special key codes. */
  private val keyTextLookup: Map[Int, String] =
    import java.awt.event.KeyEvent._
    Map(
      VK_META       -> "Meta",
      VK_WINDOWS    -> "Meta",
      VK_CONTROL    -> "Ctrl",
      VK_ALT        -> "Alt",
      VK_ALT_GRAPH  -> "Alt Gr",
      VK_SHIFT      -> "Shift",
      VK_CAPS_LOCK  -> "Caps Lock",
      VK_ENTER      -> "Enter",
      VK_DELETE     -> "Delete",
      VK_BACK_SPACE -> "Backspace",
      VK_ESCAPE     -> "Esc",
      VK_RIGHT      -> "Right",
      VK_LEFT       -> "Left",
      VK_UP         -> "Up",
      VK_DOWN       -> "Down",
      VK_PAGE_UP    -> "Page up",
      VK_PAGE_DOWN  -> "Page down",
      VK_HOME       -> "Home",
      VK_END        -> "End",
      VK_CLEAR      -> "Clear",
      VK_TAB        -> "Tab",
      VK_SPACE      -> " ",
    )

  /** An object with integers representing events that can happen in a PixelWindow. */
  object Event:
    /** An integer representing a key down event.
    *
    * This value is returned by [[introprog.PixelWindow.lastEventType]] when
    * the last event was that a user pressed a key on the keyboard.
    * You can get a text describing the key by calling [[introprog.PixelWindow.lastKey]]
    */
    val KeyPressed    = 1

   /** An integer representing a key up event.
   *
   * This value is returned by [[introprog.PixelWindow.lastEventType]] when
   * the last event was that a user released a key on the keyboard.
   * You can get a text describing the key by calling [[introprog.PixelWindow.lastKey]]
   */
    val KeyReleased   = 2

    /** An integer representing a mouse button down event.
    *
    * This value is returned by [[introprog.PixelWindow.lastEventType]] when
    * the last event was that a user pressed a mouse button.
    * You can get the mouse position by calling [[introprog.PixelWindow.lastMousePos]]
    */
    val MousePressed  = 3

    /** An integer representing a mouse button up event.
    *
    * This value is returned by [[introprog.PixelWindow.lastEventType]] when
    * the last thing that happened was that a user released a mouse button.
    * You can get the mouse position by calling [[introprog.PixelWindow.lastMousePos]]
    */
    val MouseReleased = 4

    /** An integer representing that a window close event has happened.
    *
    * This value is returned by [[introprog.PixelWindow.lastEventType]] when
    * the last event was that a user has closed a window.
    */
    val WindowClosed  = 5

    /** An integer representing that no event is available.
      *
      * This value is returned by [[introprog.PixelWindow.lastEventType]] when
      * [[introprog.PixelWindow.awaitEvent]] has waited until its timeout
      * or if [[introprog.PixelWindow.lastEventType]] was called before awaiting any event.
      */
    val Undefined     = 0

    /** Returns a descriptive text for each `event`. */
    def show(event: Int): String = event match
      case KeyPressed    => "KeyPressed"
      case KeyReleased   => "KeyReleased"
      case MousePressed  => "MousePressed"
      case MouseReleased => "MouseReleased"
      case WindowClosed  => "WindowClosed"
      case Undefined     => "Undefined"
      case _             =>
        throw new IllegalArgumentException(s"Unknown event number: $event")

/** A window with a canvas for pixel-based drawing.
  *
  * @constructor Create a new window for pixel-based drawing.
  * @param width the number of horizontal pixels
  * @param height number of vertical pixels
  * @param title the title of the window
  * @param background the color used when clearing pixels
  * @param foreground the foreground color, default color in drawing operations
  */
class PixelWindow(
  val width: Int = 800,
  val height: Int = 640,
  val title: String = "PixelWindow",
  val background: java.awt.Color = java.awt.Color.black,
  val foreground: java.awt.Color = java.awt.Color.green
) {
  import PixelWindow.Event

  private val frame = new javax.swing.JFrame(title)
  private val canvas = new Swing.ImagePanel(width, height, background)

  private val queueCapacity = 1000
  private val eventQueue =
    new java.util.concurrent.LinkedBlockingQueue[java.awt.AWTEvent](queueCapacity)

  @volatile private var _lastEventType = Event.Undefined

  /** The event type of the latest event in the event queue.
    *
    * Returns Event.Undefined if no event has occurred. See also [[introprog.PixelWindow.awaitEvent]]
    */
  def lastEventType: Int = _lastEventType

  @volatile private var _lastKeyText = ""

  /** A string representing the last key pressed.
    *
    * Returns an empty string if no key event has occurred.
    */
  def lastKey: String = _lastKeyText

  @volatile private var _lastMousePos = (-1, -1)

  /** A pair of integers with the coordinates of the last mouse event.
    *
    * Returns `(-1, -1)` if no mouse event has occurred.
    */

  def lastMousePos: (Int, Int) = _lastMousePos

  initFrame()  // initialize listeners, show frame, etc.

  /** Event dispatching, translating internal AWT events to exposed events. */
  private def handleEvent(e: java.awt.AWTEvent): Unit = e match
    case me: java.awt.event.MouseEvent =>
      _lastMousePos = (me.getX, me.getY)
      me.getID match
        case java.awt.event.MouseEvent.MOUSE_PRESSED =>
          _lastEventType = Event.MousePressed
        case java.awt.event.MouseEvent.MOUSE_RELEASED =>
            _lastEventType = Event.MouseReleased
        case _ =>
          throw new IllegalArgumentException(s"Unknown MouseEvent: $e")

    case ke: java.awt.event.KeyEvent =>
      if ke.getKeyChar == java.awt.event.KeyEvent.CHAR_UNDEFINED || ke.getKeyChar < ' ' then
        _lastKeyText = PixelWindow.keyTextLookup.getOrElse(ke.getKeyCode, java.awt.event.KeyEvent.getKeyText(ke.getKeyCode))
      else _lastKeyText = ke.getKeyChar.toString

      ke.getID match
        case java.awt.event.KeyEvent.KEY_PRESSED =>
          _lastEventType = Event.KeyPressed
        case java.awt.event.KeyEvent.KEY_RELEASED =>
          _lastEventType = Event.KeyReleased
        case _ =>
          throw new IllegalArgumentException(s"Unknown KeyEvent: $e")

    case we: java.awt.event.WindowEvent =>
      we.getID match
        case java.awt.event.WindowEvent.WINDOW_CLOSING =>
          _lastEventType = Event.WindowClosed
        case _ =>
          throw new IllegalArgumentException(s"Unknown WindowEvent: $e")

    case _ =>
      throw new IllegalArgumentException(s"Unknown Event: $e")

  /** Return `true` if `(x, y)` is inside windows borders else `false`. */
  def isInside(x: Int, y: Int): Boolean = x >= 0 && x < width && y >= 0 && y < height

  private def requireInside(x: Int, y: Int): Unit =
    require(isInside(x,y), s"(x=$x, y=$y) out of window bounds (0 until $width, 0 until $height)")

  /** Wait for next event until `timeoutInMillis` milliseconds.
    *
    * If time is out, `lastEventType` is `Undefined`.
    */
  def awaitEvent(timeoutInMillis: Long = 1): Unit =
    val e = eventQueue.poll(timeoutInMillis, java.util.concurrent.TimeUnit.MILLISECONDS)
    if e != null then handleEvent(e) else _lastEventType = Event.Undefined

  /** Draw a line from (`x1`, `y1`) to (`x2`, `y2`) using `color` and `lineWidth`. */
  def line(x1: Int, y1: Int, x2: Int, y2: Int, color: java.awt.Color = foreground, lineWidth: Int = 1): Unit =
    canvas.withGraphics { g =>
      import java.awt.BasicStroke
      val s = new BasicStroke(lineWidth.toFloat, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER)
      g.setStroke(s)
      g.setColor(color)
      g.drawLine(x1, y1, x2, y2)
    }

  /** Fill a rectangle with upper left corner at `(x, y)` using `color`. */
  def fill(x: Int, y: Int, width: Int, height: Int, color: java.awt.Color = foreground): Unit =
    canvas.withGraphics { g =>
      g.setColor(color)
      g.fillRect(x, y, width, height)
    }

  /** Set the color of the pixel at `(x, y)`.
    *
    * If (x, y) is outside of window bounds then an IllegalArgumentException is thrown.
    */
  def setPixel(x: Int, y: Int, color: java.awt.Color = foreground): Unit =
    requireInside(x, y)
    canvas.withImage { img =>
      img.setRGB(x, y, color.getRGB)
    }

  /** Clear the pixel at `(x, y)` using the `background` class parameter.
    *
    * If (x, y) is outside of window bounds then an IllegalArgumentException is thrown.
    */
  def clearPixel(x: Int, y: Int): Unit =
    requireInside(x, y)
    canvas.withImage { img =>
      img.setRGB(x, y, background.getRGB)
    }

  /** Return the color of the pixel at `(x, y)`.
    *
    * If (x, y) is outside of window bounds then an IllegalArgumentException is thrown.
    */
  def getPixel(x: Int, y: Int): java.awt.Color =
    requireInside(x, y)
    Swing.await { new java.awt.Color(canvas.img.getRGB(x, y)) }


  /** Return image of PixelWindow. */
  def getImage: Image =
    import java.awt.image.BufferedImage
    val img = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    Swing.await{img.getGraphics.drawImage(canvas.img, 0, 0, null)}
    Image(img)

  /** Return image of PixelWindow section defined by top left corner `(x, y)` and `(width, height)`. */
  def getImage(x: Int, y: Int, width: Int, height: Int) : Image =
    getImage.subsection(x, y, width, height)

  /** Set the PixelWindow frame title. */
  def setTitle(title: String): Unit = Swing { frame.setTitle(title) }

  /** Show the window. Has no effect if the window is already visible. */
  def show(): Unit = Swing { frame.setVisible(true) }

  /** Hide the window. Has no effect if the window is already hidden. */
  def hide(): Unit = Swing { frame.setVisible(false); frame.dispose() }

  /** Set window position on screen*/
  def setPosition(x: Int, y: Int): Unit = frame.setBounds(x, y, width, height)

  /** Clear all pixels using the `background` class parameter. */
  def clear(): Unit = canvas.withGraphics { g =>
    g.setColor(background)
    g.fillRect(0, 0, width, height)
  }

  /** Draw `text` at `(x, y)` using `color`, `size`, `style and `fontName`. */
  def drawText(
    text: String,
    x: Int,
    y: Int,
    color: java.awt.Color = foreground,
    size: Int = 16,
    style: Int = java.awt.Font.BOLD,
    fontName: String = java.awt.Font.MONOSPACED
  ) =
    canvas.withGraphics { g =>
      import java.awt.RenderingHints._
      // https://docs.oracle.com/javase/tutorial/2d/text/renderinghints.html
      g.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON)
      //val f = g.getFont
      g.setFont(new java.awt.Font(fontName /*f.getName*/, style, size))
      g.setColor(color)
      g.drawString(text, x, y + size)
    }

  
  /** Draw `img` at `(x, y)` scaled to `(width, height)` and rotated `(angle)` radians clockwise. 
    *
    * If angle is 0 then no rotation is applied.
    */
  def drawImage(
    img: Image,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    angle: Double = 0
  ): Unit = 
    if angle == 0 then
      canvas.withGraphics(_.drawImage(img.underlying, x, y, width, height, null))
    else 
      val at = new java.awt.geom.AffineTransform()
      at.translate(x, y)
      at.rotate(angle, width/2, height/2)
      canvas.withGraphics(_.drawImage(img.scaled(width, height).underlying, at, null))
  
  /** Draw `img` at `(x, y)` unscaled. */
  def drawImage(img: Image, x: Int, y: Int): Unit = 
    drawImage(img, x, y, img.width, img.height)

  /** Draw `matrix` at `(x, y)` unscaled. */
  def drawMatrix(matrix: Array[Array[java.awt.Color]], x: Int, y: Int): Unit = 
    for
      xx <- 0 until matrix.length 
      yy <- 0 until matrix(xx).length
    do
      setPixel(xx+x, yy+y, matrix(xx)(yy))


  /** Create the underlying window and add listeners for event management. */
  private def initFrame(): Unit = Swing {
    Swing.init() // first time calls setPlatformSpecificLookAndFeel
    javax.swing.JFrame.setDefaultLookAndFeelDecorated(true)

    frame.setFocusTraversalKeysEnabled(false);

    frame.addWindowListener(new java.awt.event.WindowAdapter {
      override def windowClosing(e: java.awt.event.WindowEvent): Unit = {
        frame.setVisible(false)
        frame.dispose()
        eventQueue.offer(e)
      }
    })

    frame.addKeyListener(new java.awt.event.KeyAdapter {
      override def keyPressed(e: java.awt.event.KeyEvent): Unit = eventQueue.offer(e)
      override def keyReleased(e: java.awt.event.KeyEvent): Unit = eventQueue.offer(e)
    })

    canvas.addMouseListener(new java.awt.event.MouseAdapter {
      override def mousePressed(e: java.awt.event.MouseEvent): Unit = eventQueue.offer(e)
      override def mouseReleased(e: java.awt.event.MouseEvent): Unit = eventQueue.offer(e)
    })

    val box = new javax.swing.Box(javax.swing.BoxLayout.Y_AXIS)
    box.add(javax.swing.Box.createVerticalGlue())
    box.add(canvas)
    box.add(javax.swing.Box.createVerticalGlue())
    frame.add(box)

    val outsideOfCanvasColorShownIfResized = java.awt.Color.BLACK.brighter.brighter
    frame.getContentPane().setBackground(outsideOfCanvasColorShownIfResized)
    frame.pack()
    frame.setVisible(true)
  }
}