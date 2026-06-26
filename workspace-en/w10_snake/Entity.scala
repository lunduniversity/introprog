package snake

trait Entity:
  def draw(): Unit

  def erase(): Unit

  def update(): Unit

  def reset(): Unit

  infix def isOccupyingBlockAt(p: Pos): Boolean
