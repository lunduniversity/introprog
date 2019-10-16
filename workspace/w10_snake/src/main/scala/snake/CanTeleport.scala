package snake

trait CanTeleport extends Entity {
  def game: SnakeGame

  var pos = game.randomFreePos()

  protected var nbrOfStepsSinceLastTeleport = 0

  def teleportAfterSteps: Int

  def update(): Unit = {
    nbrOfStepsSinceLastTeleport += 1
    if (nbrOfStepsSinceLastTeleport > teleportAfterSteps) reset()
  }

  def reset(): Unit = {
    nbrOfStepsSinceLastTeleport = 0
    pos = game.randomFreePos()
  }
}
