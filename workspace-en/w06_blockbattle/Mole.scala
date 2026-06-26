package blockbattle

class Mole(
  val name: String,
  var pos: Pos,
  var dir: (Int, Int),
  val color: java.awt.Color,
  val keyControl: KeyControl
):
  var points = 0

  override def toString =
    s"Mole[name=$name, pos=$pos, dir=$dir, points=$points]"

  /** If keyControl.has(key), then update the direction dir according to keyControl */
  def setDir(key: String): Unit = ???

  /** Uppdaterar dir till motsatta riktningen. */
  def reverseDir(): Unit = ???

  /** Updating pos to become nextPos */
  def move(): Unit = ???

  /** Gives the next position according to the direction dir without updating pos */
  def nextPos: Pos = ???
