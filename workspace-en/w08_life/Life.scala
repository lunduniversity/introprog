package life

case class Life(cells: Matrix[Boolean]):

  /** Returns true if the cell at position (row, col) is alive, otherwise false.
    * Returns false if the indexing is outside the universe's boundaries.
    */
  def apply(row: Int, col: Int): Boolean = ???

  /** Sets the status of the cell at position (row, col) to value. */
  def updated(row: Int, col: Int, value: Boolean): Life = ???

  /** Toggles the status of the cell at position (row, col). */
  def toggled(row: Int, col: Int): Life = ???

  /** Counts the number of alive neighbors for the cell at (row, col). */
  def nbrOfNeighbours(row: Int, col: Int): Int = ???

  /** Creates a new Life instance with the next generation of the universe.
    * This is done by applying the function rule to all cells.
    */
  def evolved(rule: (Int, Int, Life) => Boolean = Life.defaultRule):Life =
    var nextGeneration = Life.empty(cells.dim)
    cells.foreachIndex( (r,c) =>
      ??? // update nextGeneration with rule applied to this instance
    )
    nextGeneration

  /** Text separated by rows where 0 is a live cell and - is a dead cell. */
  override def toString = ???

object Life:
  /** Creates a universe with dead cells. */
  def empty(dim: (Int, Int)): Life = ???

  /** Creates a universe with random life. */
  def random(dim: (Int, Int)): Life = ???

  /** Implementerar reglerna enligt Conways Game of Life. */
  def defaultRule(row: Int, col: Int, current: Life): Boolean = ???
