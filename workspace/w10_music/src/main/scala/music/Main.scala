package music

// https://maven2repo.com/jline/jline/2.14.4/jar
//  wget -O lib/jline-2.14.4.jar http://search.maven.org/remotecontent?filepath=jline/jline/2.14.4/jline-2.14.4.jar

// https://stackoverflow.com/questions/34360822/midi-device-not-showing-up-for-midisystem-getmidideviceinfo

// https://stackoverflow.com/questions/18676712/java-sound-devices-found-when-run-in-intellij-but-not-in-sbt

// http://www.scala-sbt.org/1.x/docs/Forking.html

object Main {
  val (helloMsg, exitMsg) = ("*** Welcome to music!", "Goodbye music!")
  val console = new jline.console.ConsoleReader
  def readLine(): String = console.readLine("music> ")

  def main(args: Array[String]): Unit = {
    println(helloMsg)
    Synth.playBlocking()
    Command.loopUntilExit(readLine _)
  }
}
