package stats

/** Defines, creates and executes commands. */
object Command {

  val list =
    """Commands: -save <filename>
      |          -sep <separator>
      |          -filter <column> <values: a b c>
      |          -sort <column>
      |          -printchart <columns: 3 4 5>
      |          -print""".stripMargin

  type Command = Table => Table

  /**
   * Takes arguments and matches them to a single command or
   * throws an exception if no match is found.
   */
  def parseOne(args: Vector[String]): Command = ???

  /**
   * Turns a collection of arguments into a collection of commands.
   */
  def parseAll(args: Vector[String]): Vector[Command] = ???

  /**
   * Executes the specified commands in a chain. The input to the
   * next command is the output from the previous.
   */
  def runAllWith(commands: Vector[Command], table: Table): Table = ???
}