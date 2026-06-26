package blockbattle
object Main:
  def main(args: Array[String]): Unit =
    val msg = "TODO: blockbattle"
    println(msg)
    val w = blockbattle.BlockWindow(nbrOfBlocks = (40,20))
    w.write(msg, Pos(10,5), java.awt.Color.WHITE, 30)