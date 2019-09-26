package snake

case class Player(
  name: String,
  left: String, down: String, right: String, up: String, // control keys
  snake: Snake
){
  def handleKey(key: String): Unit = { // mutate snake dir if control key
    key.toUpperCase match {
      case `left`  => snake.dir = West
      case `right` => snake.dir = East
      case `down`  => snake.dir = South
      case `up`    => snake.dir = North
      case _       =>
    }
  }
}
