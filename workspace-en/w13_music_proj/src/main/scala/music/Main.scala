package music

import scala.io.StdIn

object Main:
  val (helloMsg, exitMsg) = ("*** Welcome to music!", "Goodbye music!")
  def readLine(): String = StdIn.readLine("music> ")

  def main(args: Array[String]): Unit =
    println(helloMsg)
    Synth.playBlocking()
    Command.loopUntilExit(readLine)
