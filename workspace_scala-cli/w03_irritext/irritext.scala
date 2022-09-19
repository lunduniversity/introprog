import scala.io.StdIn.readLine

def printDead(): Unit = println("Du är nu DÖÖÖÖÖÖD! Tack och hej :(")

def printSolved(points: Int): Unit = println(s"GRATTIS DU FICK $points POÄNG!")

def printWelcomeMessage(): Unit = println("""
  |Välkommen till ett lagom irriterande textspel (om du läst koden)!
  |Du står framför en fuktdrypande vägg med två tunga dörrar.
  |Du hör morrande och rytande.
""".stripMargin)

def doorChoice(): String = readLine("Vilken dörr väljer du? V=vänster H=Höger\n")

var isDead = false
var isSolved = false
var points = 0

def play(): Unit =
  while !isDead && !isSolved do
    val door: String = doorChoice()
    if door.toLowerCase.startsWith("v") then
      println("Du klarade det!")
      isSolved = true
    else if door.toLowerCase.startsWith("h") then
      println("Du blev uppäten av lejonet och dog...")
      isDead = true
    else
      println("FEL! Den dörren finns inte. Men försöka duger.")
      points += 10

@main 
def run: Unit =
  printWelcomeMessage()
  play()
  if isDead then printDead() else printSolved(points)
