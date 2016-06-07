/** A class representing a square objects with position and side. */
class Square private (val x: Int, val y: Int, val side: Int, private var numberOfMoves: Int) {
  /** The area of this Square */
  val area: Int = side*side

  /** Moves this square to position (x + xd, y + dy) */
  def move(dx: Int, dy: Int): Square = 
    Square.create(x + dx, y + dy, side, numberOfMoves + 1)

  /** Tests if this Square has equal size as that Square */
  def isEqualSizeAs(that: Square): Boolean = (side == that.side)

  /** Multiplies the side with factor and rounded to nearest integer */
  def scale(factor: Double): Square = 
    Square.create(x, y, math.rint(side*factor).toInt, numberOfMoves)
    
  /** A string representation of this Square */
  override def toString: String = s"x: $x, y: $y, side: $side, #moved: $numberOfMoves"
}

object Square {
  private var created = Vector[Square]()
  
  private def create(x: Int, y: Int, side: Int, nMoves: Int): Square = {
    created :+= new Square(x, y, side, nMoves)    
    created.last    
  }
  
  /** A square placed in origin with size 1 */
  val unit: Square =  create(0, 0, 1, 0)

  /** Constructs a new Square object at (x, y) with size side */
  def apply(x: Int, y: Int, side: Int): Square = {
    require(side >= 0, s"side must be positive: $side")
    create(x, y, side, 0)
  }

  /** Constructs a new Square object at (0, 0) with side 1 */
  def apply(): Square = create(0, 0, 1, 0)

  /** Returns the total number of moves that have been made */
  def totalNumberOfMoves: Int = created.map(_.numberOfMoves).sum
}

