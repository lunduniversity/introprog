class Snake private (initPos: (Int, Int), initDir: (Int, Int)):
  private var pos = initPos
  
  private var dir = initDir
  def dx = dir._1
  def dy = dir._2
  
  def north: Snake = { dir = (0, -1); this }
  def south: Snake = { dir = (0, 1);  this }
  def east:  Snake = { dir = (1, 0);  this }
  def west:  Snake = { dir = (-1, 0); this }
  
  private var snake = scala.collection.mutable.ListBuffer(initPos)  
  def x = snake.head._1
  def y = snake.head._2
   
  def grow: Snake =
    snake.prepend((x + dx, y + dy))
    this
  
  def shrink: Snake =
    if snake.size > 1 then snake.remove(snake.size - 1)
    this
  
  def isCollidingWith(other: Snake) = other.snake.contains(snake.head)

  override def toString = snake.mkString("Snake:", "~", s" going $dir")

object Snake:
  def goingEastAt(x: Int, y: Int) = new Snake((x, y), (1, 0))
  def goingWestAt(x: Int, y: Int) = new Snake((x, y), (-1, 0))
