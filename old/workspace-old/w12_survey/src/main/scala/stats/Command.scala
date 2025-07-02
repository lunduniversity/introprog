package stats

import scala.annotation.tailrec

object Command {

  val helpMsg = "Type 'help' for list of commands."

  val emptyMsg = "Table is empty. Use: 'load <filename> <sep>' or 'load <http> <sep>' or 'quit'"

  val defaultSep = ","

  val commandList =
    s"""Commands: help
      |             Prints this list of commands.
      |          load <location>
      |             Loads a table from <location> using $defaultSep as separator
      |          load <location> <separator>
      |             Loads a table from <location> using <separator>
      |          save <filepath>
      |             Saves the table to <filepath>
      |          sep <separator>
      |             Changes the separator of the table
      |          filter <column> <values: a b c>
      |             Filters the table so <column> only contains <values>
      |          sort <column>
      |             Sorts the table on <column>
      |          mysort <column>
      |             Sorts the table on <column> using my own sort implementation
      |          print
      |             Prints the table to the console
      |          printchart <column>
      |             Prints a barchart of <column> to the console
      |          pie <column>
      |             Draws a pie chart of current table in a SimpleWindow
      |          bar <column>
      |             Draws a bar chart of current table in a SimpleWindow
      |          quit
      |             Terminates the program""".stripMargin

  def load(uri: String, sep: String = defaultSep): Table = {
    println(s"""Loading $uri with separator '$sep' to table ...\n""")
    val t = Table.fromFile(uri, sep)
    println(s"Done. Size: ${t.dim._1}x${t.dim._2}.\n")
    t
  }

  @tailrec
  def loop(tableOpt: Option[Table] = None): Unit = {
    val command = scala.io.StdIn.readLine("> ")
    command match {
      case "quit" => println("Goodbye!")
      case _ =>
        val newTableOpt = runCommand(tableOpt, command)
        loop(newTableOpt)
    }
  }

  def runCommand(tableOpt: Option[Table], command: String): Option[Table] =
    command.split(' ').toVector match {
      case xs if xs.forall(_.trim.isEmpty) => tableOpt

      case Vector("help")              => println(commandList); tableOpt
      case Vector("load", uri)         => Some(load(uri))
      case Vector("load", uri, sep)    => Some(load(uri, sep))
      case _ if tableOpt.isEmpty       => println(emptyMsg); tableOpt
      case Vector("print")             => tableOpt.foreach(println); tableOpt
      case Vector("save", path)        => tableOpt.foreach(t => Table.save(path, t)); tableOpt
      case Vector("sep", sep)          => tableOpt.map(_.copy(separator = sep))
      case Vector("sort", column)      => tableOpt.map(_.sort(column.toInt))
      case Vector("mysort", column)    => tableOpt.map(_.mySort(column.toInt))

      case "filter" +: column +: values if values.nonEmpty =>
        tableOpt.map(_.filter(column.toInt, values))

      case Vector("printchart", column) => tableOpt.foreach{ t =>
          val (heading, total) +: data = t.register(column.toInt)
          println(f"Column: $heading ($total)")
          for ((key, value) <- data ) {
            println(s"$key: $value occurrence${if (value != 1) "s" else ""}")
            println("*" * value)
          }
        }
        tableOpt

      case Vector("pie", column) =>
        tableOpt.foreach{t => Chart.pie(t.register(column.toInt))}; tableOpt

      case Vector("bar", column) =>
        tableOpt.foreach{t => Chart.bar(t.register(column.toInt))}; tableOpt

      case _ => println(s"$command is not a valid command.$helpMsg"); tableOpt
  }
}