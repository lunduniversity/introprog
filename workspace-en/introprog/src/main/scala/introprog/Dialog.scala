package introprog

/** A module with utilities for creating standard GUI dialogs. */
object Dialog:
  import javax.swing.{JFileChooser, JOptionPane, JColorChooser}

  Swing.init() // get platform-specific look and feel

  /** Show a file choice dialog starting in `startDir` with confirm `button` text. */
  def file(button: String = "Open", startDir: String = "~"): String =
    val fs = new JFileChooser(new java.io.File(startDir))
    fs.showDialog(null, button) match
      case JFileChooser.APPROVE_OPTION => Option(fs.getSelectedFile.toString).getOrElse("")
      case _ => ""

  /** Show a dialog with a `message` text. */
  def show(message: String): Unit = JOptionPane.showMessageDialog(null, message)

  /** Show a `message` asking for input with `init` value. Return user input.
    *
    * Returns empty string on Cancel. */
  def input(message: String, init: String = ""): String =
    Option(JOptionPane.showInputDialog(message, init)).getOrElse("")

  /** Show a confirmation dialog with `question` and OK and Cancel buttons. */
  def isOK(question: String = "Ok?", title: String = "Confirm"): Boolean =
    JOptionPane.showConfirmDialog(
      null, question, title, JOptionPane.OK_CANCEL_OPTION
    ) == JOptionPane.OK_OPTION

  /** Show a selection dialog with `buttons`. Return a string with the chosen button text. */
  def select(message: String, buttons: Seq[String], title: String = "Select"): String =
    scala.util.Try{
      val chosenIndex =
        JOptionPane.showOptionDialog(null, message, title, JOptionPane.DEFAULT_OPTION,
          JOptionPane.QUESTION_MESSAGE, null, buttons.reverse.toArray, null)
      buttons(buttons.length - 1 - chosenIndex)
    }.getOrElse("")

  /** Show a color selection dialog and return the color that the user selected. */
  def selectColor(
    message: String = "Select a color",
    default: java.awt.Color = java.awt.Color.red
  ): java.awt.Color =
    Option(JColorChooser.showDialog(null, message, default)).getOrElse(default)
