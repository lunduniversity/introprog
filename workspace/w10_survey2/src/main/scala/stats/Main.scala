package stats

import scala.util.Try

object Main {

  val welcome = "*** Welcome to stats for table data ***\n"
  val usage   = "\nFormat of optional args: <uri> <separator>"

  def main(args: Array[String]): Unit = {
    Try {
      val path = Table.getResourcePath("")
      println(welcome)
      println(s"Current dir: $path")
      println(Command.helpMsg)

      args.toVector match {
        case Vector() => Command.loop()

        case Vector(uri) =>
          val table = Command.load(uri)
          Command.loop(tableOpt = Some(table))

        case Vector(uri, sep) =>
          val table = Command.load(uri, sep)
          Command.loop(tableOpt = Some(table))

        case _ => println(s"""Unknown args: ${args.mkString(" ")}$usage""")
      }
    }.recover {
      case e: Throwable =>
        println("EXCEPTION THROWN! Printing stack trace:")
        e.printStackTrace(System.out)
        println("RECOVERING: Table is empty. Restarting command loop...")
        Command.loop(tableOpt=None)
    }
  }
}