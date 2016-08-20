package stats

import scala.util.Try

object Main {

  val usage =
    s"Usage: <uri> <separator> [Commands]\n\n${Command.list}"

  def main(args: Array[String]): Unit = args.toVector match {

    case uri +: sep +: rest if rest != Vector() => {
      Try {
        println(s"""Loading "$uri" "$sep" to table ...\n""")
        val t = Table.fromFile(uri, sep)
        println(s"Done. Size: ${t.dim._1}x${t.dim._2}.\n")
        val commands = Command.parseAll(rest)
        Command.runAllWith(commands, t)
      }.recover {
        case e: Exception => println(s"Error: $e\n\n$usage")
      }
    }
    case _ => println(s"Not enough arguments.\n\n$usage")
  }
}