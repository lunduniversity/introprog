package snake

class Player(
  var name: String, 
  var keyMap: Player.KeyMap, 
  val snake: Snake,
  var points: Int = 0,    // TODO: count points when e.g. eating apple
):
  def handleKey(key: String): Unit = 
    ??? // if key is in keyMap then update snake.dir

object Player:
  enum KeyMap(left: String, right: String, up: String, down: String):
    val dir = Map(left -> West, right -> East, up -> North, down -> South) 
    case Letters extends KeyMap("a", "d", "w", "s")
    case Arrows extends KeyMap("Left", "Right", "Up", "Down")