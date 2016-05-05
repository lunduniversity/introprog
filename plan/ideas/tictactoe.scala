trait Piece
case object X extends Piece
case object O extends Piece

type Pieces = Vector[Option[Piece]]

case class Board(board: Pieces) {
  require(board.size == 9, "board.size must be 9 but is" + board.size)
  def checkPos(pos: Int) = require(pos>=0 && pos < 9, "pos must be inside[0,9[")
  def isFree(pos: Int) = {
    checkPos(pos)
    !board(pos).isDefined
  }
  def put(pos: Int, piece: Piece): Board = {
    checkPos(pos)
    require(isFree(pos), s"pos $pos must be free")
    Board(board.updated(pos, Some(piece))) 
  }
  def row(i: Int): Pieces = board.drop(i * 3).take(3)
  def col(i: Int): Pieces = (0 to 2).map(k => board(k*3 + i)).toVector  
  override def toString: String = {
    val strings = board.map(p  => if (!p.isDefined) "." else p.get.toString)
    def maybeNewline(i: Int) = if (i % 3 == 2) "\n" else ""
    val addNl = for (i <- 0 until strings.size) yield strings(i) + maybeNewline(i)
    addNl.mkString
  } 
}
object Board {
  def empty: Board = new Board(Vector.fill(9)(None))
  def isAllSame(xs: Pieces, piece: Piece): Boolean = 
    xs.forall(_ == Some(piece)) 
}
