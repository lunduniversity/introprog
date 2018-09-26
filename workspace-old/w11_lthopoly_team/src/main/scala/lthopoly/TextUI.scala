package lthopoly

import scala.collection.mutable.{ArrayBuffer, Buffer}
import scala.collection.JavaConverters._

object TextUI {
  private val log = new ArrayBuffer[String]

  /**
   * Prints an ASCII plot of the total amount
   * of money in the game as a function of the turn index
   */
  def plotStatistics(x: Buffer[Int]): String = {
    if (x.length > 0) {
      val min = x.min
      val max = x.max

      val dy: Double = max - min
      val height = 20.0
      val normalized = x.map(_ - min).map(i => ((i / dy) * height).toInt)
      val map = invertRows(normalized)
      val indent = " " * (max.toInt.toString.length + 1)
      var sb = new StringBuilder
      var o = 20
      while (o >= 0) {
        val row = (max + dy * (o - 20) / 20).toInt + indent + " " * x.length * 3
        val xs =
          map.getOrElse(o, Vector())
        var sb2 = new StringBuilder(row)
        for (k <- xs) sb2.setCharAt(indent.length + 1 + k * 3, '*')
        sb.append(sb2.toString() + '\n')
        o -= 1
      }

      var xaxis = ""
      for (i <- 1 to x.length) xaxis = xaxis + i + " " * (3 - (i + "").length)
      sb.append(indent + " " + xaxis)
      "Relativ graf över total mängd pengar under spelets gång:" + '\n' +
      sb.toString
    } else "Spelet måste vara åtminstone en runda lång för att en graf skall ritas upp."
  }

  /**
   * Inverts provided rows
   */
  def invertRows(x: Buffer[Int]): Map[Int, Vector[Int]] = {
    val map = scala.collection.mutable.Map[Int, Vector[Int]]()
    for (i <- 0 until x.length) {
      var v = x(i)
      map.put(v, map.getOrElse(v, Vector()) :+ i)
    }

    map.toMap
  }

  /**
   * Appends the String s to the end of the UI's event log
   */
  def addToLog(s: String): Unit = {
    log.append(s)
  }

  /**
   * Reprints the current state of the UI using the given
   * GameBoard to print the status bar
   */
  def updateConsole(board: GameBoard): Unit = {
    lineBreak
    log.foreach(println(_))
    printStatus(board)
  }

  /**
   * Prints a GameBoard's Players and their statuses
   */
  def printStatus(board: GameBoard): Unit = {
    val format = "%-19s %-29s %-10s\n"
    print(format.format("Namn", "Position", "Pengar").replaceAll(" ", "-"))
    board.getPlayers().asScala.foreach{ player =>
      printf(format, player.toString+s"${if(player == board.getCurrentPlayer)'*' else ""}", board.getBoardSpaces.get(player.getPosition), player.getMoney)
    }
    print(format.format("", "", "").replaceAll(" ", "-"))
  }

  /** Asks the user to select an option from a list of options
   *
   * @param options An Array of tuples of the form (choice, description)
   *                where choice is the number the user must enter
   *                to select the choice represented by description. E.g.
   *                (0, "End Game") lets the user input 0 to end the game.
   *
   * @return The selected choice
   */
  def promptForInput(options: Array[(Int, String)]): Int = {
    println("Välj ett alternativ:\n")

    options.foreach {
      case (command, description) => printf("\t%s. %-30s\n", command, description)
    }
    lineBreak

    var choice = -1
    while (choice < 0 || !options.map(_._1).toList.contains(choice)) {
      try {
        choice = scala.io.StdIn.readInt()
       if(choice < 0 || !options.map(_._1).toList.contains(choice) ) throw new IllegalArgumentException();
      } catch {
        case e: Exception =>
          println("Välj ett alternativ som finns.")
      }
    }
    choice
  }

  /**
   * Prints the entire GameBoard
   */
  def printBoard(board: GameBoard) = {
    lineBreak
    println(board.toString)
  }
  
  /**
   * Prints a line separator
   */
  def lineBreak() = {
    print("\n")
    println("=" * 60)
    print("\n")

  }
}
