package chord

object terminal {
  import java.util.Scanner
  import scala.util.{Try, Success, Failure}
  private var scan = new Scanner(System.in)
  var prompt: String = "> "
  
  /**
   * Read one line from command field
   */
  def readLine: String = Try { scan.nextLine } getOrElse {
    scan = new Scanner(System.in)
    "" 
  }
  
  /**
   * Splits a read line at " " and returns
   */
  def cmdLine: Vector[String] = {
    print(prompt)
    readLine.split(' ').toVector
  } 
}

object Main {
  def main(args: Array[String]): Unit = {println(greeting); commandLoop}

  val greeting = "*** Welcome to this awesome chords.Main app!\n*** Type ? for help.\n"

  /**
   * Waits for new command until user quits
   */
  def commandLoop: Unit = {
    import textui.{doCommand, Quit}
    val (response: String, isQuit: Boolean) = terminal.cmdLine match {
      case Vector() => ("", false)
      case cmd +: cmdArgs => (doCommand(cmd, cmdArgs), doCommand.quit)
    }
    println(response)
    if (isQuit) System.exit(0) else commandLoop
  }
}