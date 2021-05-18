package snake

class Snake (
  val initPos: Pos,
  val initDir: Dir,
  val headColor: java.awt.Color,
  val tailColor: java.awt.Color,
  val game: SnakeGame
) extends CanMove:
  var dir: Dir = initDir

  val initBody: List[Pos] = List(initPos + initDir, initPos)

  val body: scala.collection.mutable.Buffer[Pos] = initBody.toBuffer

  val initTailSize: Int = 10 // välj själv vad som är lagom svårt

  // var nbrOfStepsSinceReset = 0
  // val growEvery = 10
  // val startGrowingAfter = 400
  // var nbrOfApples = 0

  def reset(): Unit = ???  // återställ starttillstånd, ge rätt svanslängd

  def grow(): Unit = ???   // väx i rätt riktning med extra svansposition

  def shrink(): Unit = ??? // krymp svansen om kroppslängden är större än 2

  def isOccupyingBlockAt(p: Pos): Boolean = ???    // kolla om p finns i kroppen

  def isHeadCollision(other: Snake): Boolean = ??? // kolla om huvudena krockar
  def isTailCollision(other: Snake): Boolean = ??? // mitt huvud i annans svans

  def move(): Unit = ??? // väx och krymp enl. regler; action om äter frukt

  def draw(): Unit = ???

  def erase(): Unit = ???

  override def toString =  // bra vid println-debugging
    body.map(p => (p.x, p.y)).mkString(">:)", "~", s" going $dir")
