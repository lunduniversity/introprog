package snake

trait Entity {
  def draw():  Unit
  def erase():  Unit
  def update(): Unit
  def reset():  Unit
  def isOccupyingBlockAt(p: Pos): Boolean
}
