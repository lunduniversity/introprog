//> using scala 3.5

import scala.io.StdIn.readLine

def printDead(): Unit = println("You are now DEEEEEAD! Thanks and bye :(")

def printSolved(points: Int): Unit = println(s"CONGRATULATIONS YOU GOT $points POINTS!")

def printWelcomeMessage(): Unit = println("""
  |Welcome to a moderately irritating text game (if you read the code)!
  |You stand in front of a dripping wet wall with two heavy doors.
  |You hear snorting and rumbling.
""".stripMargin)

def doorChoice(): String = readLine("Which door do you choose? V=Left H=Right\n")

var isDead = false
var isSolved = false
var points = 0

def play(): Unit =
  while !isDead && !isSolved do
    val door: String = doorChoice()
    if door.toLowerCase.startsWith("v") then
      println("You did it!")
      isSolved = true
    else if door.toLowerCase.startsWith("h") then
      println("You were devoured by the lion and died...")
      isDead = true
    else
      println("ERROR! That door does not exist. But trying is okay.")
      points += 10

@main 
def run: Unit =
  printWelcomeMessage()
  Thread.sleep(2000) // wait 2 seconds
  play()
  if isDead then printDead() else printSolved(points)
