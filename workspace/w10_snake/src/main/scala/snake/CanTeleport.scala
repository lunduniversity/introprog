package snake

trait CanTeleport extends Entity:
  private var _pos = teleport()
  
  def pos: Pos = _pos
  
  protected var nbrOfStepsSinceLastTeleport = 0

  def teleportAfterSteps: Int 

  def teleport(): Pos 

  def update(): Unit = 
    nbrOfStepsSinceLastTeleport += 1
    if nbrOfStepsSinceLastTeleport > teleportAfterSteps 
    then reset()
  
  def reset(): Unit = 
    nbrOfStepsSinceLastTeleport = 0
    _pos = teleport()

