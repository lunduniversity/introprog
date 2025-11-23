//> using scala 3
//> using dep "net.kogics:kojo-lib:0.3.1,url=https://github.com/litan/kojo-lib/releases/download/v0.3.1/kojo-lib-assembly-0.3.1.jar"

export net.kogics.kojo.Swedish.*, padda.*, CanvasAPI.*, TurtleAPI.*
export builtins.activateCanvas
export java.awt.Color

def selectColor(
  message: String = "Select a color",
  default: java.awt.Color = java.awt.Color.red
): java.awt.Color =
  import javax.swing.JColorChooser
  Option(JColorChooser.showDialog(null, message, default)).getOrElse(default)