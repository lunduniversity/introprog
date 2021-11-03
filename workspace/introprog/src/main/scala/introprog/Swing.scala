package introprog

/** A module with Swing utilities used by [[introprog.PixelWindow]]. */
object Swing:

  private def runInSwingThread(callback: => Unit): Unit =
    javax.swing.SwingUtilities.invokeLater(() => callback)

  /** Run `callback` asynchronously in the Swing thread. */
  def apply(callback: => Unit): Unit = runInSwingThread(callback)

  /** Run `callback` in the Swing thread and block until completion. */
  def await[T: scala.reflect.ClassTag](callback: => T): T =
    val ready = new java.util.concurrent.CountDownLatch(1)
    val result = new Array[T](1)
    runInSwingThread {
      result(0) = callback
      ready.countDown
    }
    ready.await
    result(0)

  /** Return a sequence of available look and feel options. */
  def installedLookAndFeels: Vector[String] =
    javax.swing.UIManager.getInstalledLookAndFeels.toVector.map(_.getClassName)

  /** Find a look and feel with a name including `partOfName`. */
  def findLookAndFeel(partOfName: String): Option[String] =
    installedLookAndFeels.find(_.toLowerCase contains partOfName)

  /** Test if the current operating system name includes `partOfName`. */
  def isOS(partOfName: String): Boolean =
    if partOfName.toLowerCase.startsWith("win") && isInProc("windows", "wsl", "microsoft") then true //WSL
    else scala.sys.props("os.name").toLowerCase.contains(partOfName.toLowerCase)

  /** Check whether `/proc/version` on this filesystem contains any of the strings in `parts`. 
    * Can be used to detect if we are on WSL instead of "real" linux/ubuntu.
    */
  private def isInProc(parts: String*): Boolean =
    util.Try(parts.map(_.toLowerCase)
    .exists(part => IO.loadString("/proc/version").toLowerCase.contains(part)))
    .getOrElse(false)

  private var isInit = false

  /** Init the Swing GUI toolkit and set platform-specific look and feel.*/
  def init(): Unit = if !isInit then
    setPlatformSpecificLookAndFeel()
    isInit = true

  private def setPlatformSpecificLookAndFeel(): Unit =
    import javax.swing.UIManager.setLookAndFeel
    if isOS("win") then findLookAndFeel("win").foreach(setLookAndFeel)
    else if isOS("linux") then findLookAndFeel("gtk").foreach(setLookAndFeel)
    else if isOS("mac") then findLookAndFeel("apple").foreach(setLookAndFeel)
    else javax.swing.UIManager.setLookAndFeel(
      javax.swing.UIManager.getSystemLookAndFeelClassName()
    )

  /** A Swing `JPanel` to create drawing windows for 2D graphics. */
  class ImagePanel(
    val initWidth: Int,
    val initHeight: Int,
    val initBackground: java.awt.Color
  ) extends javax.swing.JPanel:
    val img: java.awt.image.BufferedImage = java.awt.GraphicsEnvironment
      .getLocalGraphicsEnvironment
      .getDefaultScreenDevice
      .getDefaultConfiguration
      .createCompatibleImage(initWidth, initHeight, java.awt.Transparency.OPAQUE)

    val g: java.awt.Graphics2D = img.createGraphics()
    g.setColor(initBackground)
    g.fillRect(0, 0, initWidth, initHeight)

    setBackground(initBackground)
    setDoubleBuffered(true)
    setPreferredSize(new java.awt.Dimension(initWidth, initHeight))
    setMinimumSize(new java.awt.Dimension(initWidth, initHeight))
    setMaximumSize(new java.awt.Dimension(initWidth, initHeight))

    override def paintComponent(g: java.awt.Graphics): Unit = g.drawImage(img, 0, 0, this)

    override def imageUpdate(img: java.awt.Image, infoFlags: Int, x: Int, y: Int, width: Int, height: Int): Boolean =
      repaint()
      true

    /** Execute `action` in the Swing thread with graphics context as param. */
    def withGraphics(action: java.awt.Graphics2D => Unit) = runInSwingThread {
      action(img.createGraphics())
      repaint()
    }

    /** Execute `action` in the Swing thread with raw image as param. */
    def withImage(action: java.awt.image.BufferedImage => Unit) = runInSwingThread {
      action(img)
      repaint()
    }
