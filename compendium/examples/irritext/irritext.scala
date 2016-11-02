object irritext {
  import scala.io.StdIn.readLine
  
  def youAreDead: Unit = println("DÖÖÖÖÖÖD!")
  
  def youMadeIt(points: Int): Unit = 
    println(s"GRATTIS DU FICK $points POÄNG!")
  
  def main(args: Array[String]): Unit = {
    println("Välkommen till ett lagom irriterande textspel!")
    println("Du står framför en vägg med två dörrar.")
    println("Du hör morrande och rytande.")
    val msg = "Vilken dörr? V=vänster H=Höger\n"
    val door = readLine(msg)
    var dead = false
    var ready = false
    var points = 0
    while (!dead && !ready) {
      if (door.toLowerCase.startsWith("v")) {
        println("Du klarade det!")
        ready = true
      } else if (door.toLowerCase.startsWith("h")) {
        println("Du blev uppäten av lejonet och dog...")
        dead = true
      } else {
        println("FEL! Den dörren finns inte. Men försöka duger.")
        points += 10
      }
    }
    if (dead) youAreDead else youMadeIt(points)
  }
}