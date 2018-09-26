package music

abstract class Command(val str: String, val help: String){
  def apply(args: Seq[String]): String
}

object Command {
  val all: Seq[Command] = Seq(Help, Quit, Play)
  val allHelpTexts: String  =
    Command.all.map(c => c.str.padTo(10,' ') + c.help).mkString("\n")

  def find(command: String): Option[Command] = all.find(_.str == command)

  def apply(cmd: String, args: Seq[String]): String =
    all.find(_.str == cmd) match {
      case Some(c) => c(args)
      case None => s"Unkown command: $cmd\nType ? for help."
    }

  def loopUntilExit(nextLine: () => String): Unit = {
    val line = nextLine()
    if (line != null) {
      val result = line.split(' ').toSeq match {
        case Seq() => ""
        case cmd +: args => Command(cmd, args)
      }
      if (result != "") println(result)
      if (result != Main.exitMsg) loopUntilExit(nextLine)
    } else println("\n" + Main.exitMsg)
  }
}

object Help extends Command("?", "print help") {
  def apply(args: Seq[String]): String = args match {
    case Seq() => Command.allHelpTexts
    case Seq(cmd) => Command.find(cmd).map(_.help).getOrElse(s"Unkown: $cmd")
    case _ => s"Usage: $str [cmd]"
  }
}

object Quit extends Command(":q", "quit this app") {
  def apply(args: Seq[String]): String = args match {
    case Seq() => Main.exitMsg
    case _ => s"Error: $args after :q not allowed"
  }
}

object Play extends Command("!", "play chord TODO") {
  def apply(args: Seq[String]): String = args match {
    case _ => Synth.playBlocking(); s"play chord TODO"
  }
}
