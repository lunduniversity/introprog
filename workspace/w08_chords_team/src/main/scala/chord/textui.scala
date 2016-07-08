package chord

import chord.model.Chord
import scala.util.{Try, Success, Failure}

object textui {
  trait Cmd {
    def variants: Set[String]
    def helpText: String
    def doWith(args: Vector[String]): String
    def ===(s: String): Boolean = variants.contains(s.toLowerCase)
  }

  object Add extends Cmd {
    val variants = Set("add", "a", "+")
    val helpText = "Adds a chord, e.g.> add git:D:-1 -1 0 2 3 2;uku:C:0 0 0 3"
    def doWith(args: Vector[String]): String = ???
  }

  object Lst extends Cmd {
    val variants = Set("list", "li", "ls", "lst")
    val helpText = "Prints a numbered list of filtered chords (all chords if no filter is applied)"
    def doWith(args: Vector[String]): String = ???
  }

  object Del extends Cmd {
    val variants = Set("delete", "del", "-")
    val helpText = "Deletes a chord at the given position in the filtered list of chords. Example: del 42"
    def doWith(args: Vector[String]): String = ???
  }

  object Filter extends Cmd {
    val variants = Set("filter", "fil", "flt")
    val helpText = "Turn filter on/off. Example: filter git:Em;uku:C;:G"
    def doWith(args: Vector[String]): String = ???
  }

  object Find extends Cmd {
    val variants = Set("find", "fnd", "f")
    val helpText = "Finds a chords among filtered (all chords if no filter is applied)"
    def doWith(args: Vector[String]): String = ???
  }

  object Load extends Cmd {
    import scala.io.Source
    val variants = Set("load", "lo", "ld")
    val helpText = "Loads chords from file"
    def doWith(args: Vector[String]): String = ???
  }

  object Save extends Cmd {
    val variants = Set("save", "s")
    val helpText = "Saves all chords to file"
    def doWith(args: Vector[String]): String = ???
  }

  object Sort extends Cmd {
    val variants = Set("sort", "srt")
    val helpText = "Sorts all chords in instrument and chord name order"
    def doWith(args: Vector[String]): String = ???
  }

  object Help extends Cmd {
    val variants = Set("?", "help", "h")
    val helpText = "Prints help text for all or one commmand"
    def doWith(args: Vector[String]): String = {
      args match {
        case Vector(cmd) => doCommand.cmds.find(_ === cmd) match {
          case Some(command) => command.helpText
          case None => s"Unknown command: $cmd"          
        }
        case Vector() => (for(cmd <- doCommand.cmds) yield {
          cmd.variants.mkString(" ") + ": \n" + cmd.helpText
        }).mkString("\n\n")
        case Vector(_*) => (for(c <- args) yield{
          doCommand.cmds.find(_ === c) match {
            case Some(command) => command.helpText
            case None => s"Unknown command: $c"
          }
        }).mkString("\n")
      }
    }
  }

  object Quit extends Cmd {
    val variants = Set("quit", "q")
    val helpText = "Quits this app after a Y/N promtp"
    def doWith(args: Vector[String]): String = ???
    
    def quitPrompt: Boolean = ???
  }

  object doCommand {
    var quit = false
    def unknownCmdHelp(cmd: String) = s"Unkown command: $cmd\nType ? for help."
    val cmds = Vector[Cmd](Add, Lst, Del, Filter, Find, Load, Save, Sort, Help, Quit)
    def apply(cmd: String, args: Vector[String]): String = cmds.find(_ === cmd) match {
      case Some(command) => command doWith args
      case None => unknownCmdHelp(cmd)
    }
  }
}