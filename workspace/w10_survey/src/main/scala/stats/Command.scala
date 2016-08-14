package stats

/**
 * Defines, creates and executes commands.
 */
object Command {

  val List =
    """Commands: -save <filename>
      |          -sep <separator>
      |          -filter <column> <values: a b c>
      |          -sort <column>
      |          -printchart <columns: 3 4 5>
      |          -print""".stripMargin

  type Command = Table => Table

  case class InvalidCommandException(message: String) extends Exception(message)

  /**
   * Takes arguments and matches them to a  single command or 
   * throws an exception if no match is found.
   * 
   * @param args                     The arguments.
   * @throws InvalidCommandException
   */
  def parseOne(args: Vector[String]): Command = ???

  /**
   * Turns a collection of arguments into a collection commands.
   * 
   * @param args The arguments.
   */
  def parseAll(args: Vector[String]): Vector[Command] = ???

  /**
   * Executes the specified commands in a chain. The input to the
   * next command is the output from the previous.
   * 
   * @param commands The collection of commands.
   * @param table    The table to execute the commands with.
   */
  def runAllWith(commands: Vector[Command], table: Table): Table = ???
}