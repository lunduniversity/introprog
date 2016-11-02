object irritext {
  def main(args: Array[String]): Unit = {
    println("Välkommen till ett lagom irriterande textspel!")
    println("Du står framför en vägg med två dörrar.")
    println("Du hör morrande och rytande.")
    val msg = "Vilken dörr? V=vänster H=Höger\n"
    val door = scala.io.StdIn.readLine(msg)
    if (door.startsWith("V")) println("Du klarade det!")
    else println("Du blev uppäten av lejonet och dog...")
  }
}