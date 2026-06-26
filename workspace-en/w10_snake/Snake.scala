package snake

import java.awt.Color

class Snake (
  val initPos: Pos,
  val initDir: Dir,
  val headColor: Color,
  val tailColor: Color,
 )(using ctx: SnakeGame, settings: Settings) extends CanMove: 
  var dir: Dir = initDir
  val initBody: List[Pos] = List(initPos + initDir, initPos)
  val body: scala.collection.mutable.Buffer[Pos] = initBody.toBuffer

  val initLength: Int = settings.snake.initLength
  val growEvery: Int = settings.snake.growEvery
  val startGrowingAfter: Int = settings.snake.startGrowingAfter

  private var _nbrOfSteps = 0
  def nbrOfSteps: Int = _nbrOfSteps

  private var _nbrOfApples = 0
  def nbrOfApples: Int = _nbrOfApples

  def reset(): Unit = ???  // reset to starting state, set correct tail length

  def grow(): Unit = ??? // grow in the right direction with extra tail position

  def shrink(): Unit = ??? // shrink the tail if the body length is greater than 2

  def isOccupyingBlockAt(p: Pos): Boolean = ??? // check if p is in the body

  def isHeadCollision(other: Snake): Boolean = ??? // kolla om huvudena krockar

  def isTailCollision(other: Snake): Boolean = ??? // my head in another's tail

  private var _isEatenByMonster: Boolean = false
  def isEatenByMonster: Boolean = _isEatenByMonster
  def eatenByMonster(): Unit = ???

  def move(): Unit = ??? 
    // grow and shrink according to rules
    // actions if eating fruit or being eaten by monsters

  override def toString = // bra vid println-debugging
    body.map(p => (p.x, p.y)).mkString(">:)", "~", s" going $dir")

  def draw(): Unit = ???

  def erase(): Unit = ???
