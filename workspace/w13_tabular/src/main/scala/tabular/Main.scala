package tabular

object Main:

  val welcome = "Welcome to tabular: an app for analysis of data in tables"
  val usage   = "\nFormat of optional main args: <uri> <separator>"

  def main(args: Array[String]): Unit =
    scala.util.Try {
      println(s"$welcome\nCurrent dir: ${introprog.IO.currentDir()}")
      args.toVector match
        case Vector() => println("No args given. Starting with empty table.")

        case Vector(uri) => Command.load(uri)

        case Vector(uri, sep) if sep.nonEmpty =>
          Command.currentSeparator = sep(0)
          Command.load(uri)

        case _ => println(s"""Unkown args: ${args.mkString(" ")}$usage""")
      Command.loopUntilQuit()
    }.recover { case e: Throwable =>
      println("EXCEPTION THROWN! Printing stack trace:")
      e.printStackTrace(System.out)
      println("RECOVERING: Table is empty. Restarting command loop...")
      Command.loopUntilQuit()
    }
