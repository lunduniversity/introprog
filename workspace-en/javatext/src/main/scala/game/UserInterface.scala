package game

object UserInterface:
  /** Prints the available choices, then reads an integer from the user */
  def readChoice(choices: Array[String]): Int = ???

  /** Reads a string from the user */
  def readString(): String = ???

  /** Prints scores in descending order */
  def showHighScores(scores: java.util.ArrayList[Game]): Unit = ???

  /** Prints scores achieved by a specific player in descending order */
  def showHighScores(scores: java.util.ArrayList[Game], player: String): Unit = ???

