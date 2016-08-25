package stats

/** Defines, creates and executes commands. */
object Command {

  val list =
    """Commands: -save <filepath>
      |             Saves the table to <filepath>.
      |          -sep <separator>
      |             Changes the separator of the table.
      |          -filter <column> <values: a b c>
      |             Filters the table so <column> only contains <values>.
      |          -sort <column>
      |             Sorts the table on <column>.
      |          -printchart <columns: 3 4 5>
      |             Prints barcharts of the <columns> to the console.
      |          -print
      |             Prints the table to the console.""".stripMargin

  type Command = Table => Table

  /**
   * Takes arguments and matches them to a single command or throws an
   * exception if no match is found.
   */
  def parseOne(args: Vector[String]): Command = ???

  /** Turns a collection of arguments into a collection of commands. */
  def parseAll(args: Vector[String]): Vector[Command] = ???

  /**
   * Executes the specified commands in a chain. The input to the next command
   * is the output from the previous.
   */
  def runAllWith(commands: Vector[Command], table: Table): Table = ???
}