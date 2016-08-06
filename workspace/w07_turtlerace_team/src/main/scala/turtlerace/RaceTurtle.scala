package turtlerace;

class RaceTurtle(private val w: RaceWindow, var nbr: Int, val name: String) {
  /**
   * Takes one step of a random length 1 to 5
   */
  def raceStep(): Unit = ???
  
  /**
   * Restarts the turtle at the finish line.
   * To be used before each race
   */
  def restart: Unit = ???
  
  override def toString: String = ???
}