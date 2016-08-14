package stats

import scala.util.Try

object Main {

  val Usage =
    s"Usage: <uri> <separator> [Commands]\n\n${Command.List}"

  def main(args: Array[String]): Unit = args.toVector match {

    case uri +: sep +: rest if rest != Vector() => {
      Try {
        val t = Table.fromFile(uri, sep)
        println(s"""Opened "$uri" with separator "$sep".""" +
          "Size: ${t.dim._1}x${t.dim._2}.\n")
        val commands = Command.parseAll(rest)
        Command.runAllWith(commands, t)

      }.recover {
        // Add more cases here to handle individual exceptions.
        case e: Exception => println(s"Error: $e")
      }
    }

    case _ => println(s"Not enough arguments.\n\n$Usage")
  }
}