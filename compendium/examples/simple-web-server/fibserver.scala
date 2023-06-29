package fibserver

import java.net.{ServerSocket, Socket}
import java.io.OutputStream
import java.util.Scanner
import scala.util.{Try, Success, Failure}

object html:
  def page(body: String): String =  //minimal web page
    s"""<!DOCTYPE html>
       |<html><head><meta charset="UTF-8"><title>Min Sörver</title></head> 
       |<body>
       |$body
       |</body>
       |</html>
       """.stripMargin          

  def header(length: Int): String = //standardized header of reply to client
    s"HTTP/1.0 200 OK\nContent-length: $length\nContent-type: text/html\n\n"

object compute:
  def fib(n: BigInt): BigInt =
    if n < 0 then 0 else 
    if n == 1 || n == 2 then 1  
    else fib(n - 1) + fib(n -2)

object start:
  def fibResponse(num: String) = 
    num.toIntOption match 
      case Some(n) => html.page(s"fib($n) == " + compute.fib(n))
      case None    => html.page(s"FEL: skriv ett heltal, inte $num")

  def errorResponse(uri:String) = html.page(s"Error: $uri </br> use /fib/heltal")

  def handleRequest(cmd: String, uri: String, socket: Socket): Unit = 
    val os = socket.getOutputStream
    val afterSlash = uri.toString.drop(1) // skip initial slash
    println(s"afterSlash:$afterSlash")
    val response: String = 
      if afterSlash.startsWith("fib/") then fibResponse(afterSlash.stripPrefix("fib/"))
      else errorResponse(uri)
    os.write(html.header(response.size).getBytes("UTF-8"))
    os.write(response.getBytes("UTF-8"))
    os.close
    socket.close
  end handleRequest
  
  def serverLoop(server: ServerSocket): Unit =
    println(s"http://localhost:${server.getLocalPort}/hej")
    while true do
      Try {
        var socket = server.accept  // blocks thread until connect
        val scan = new Scanner(socket.getInputStream, "UTF-8")
        val (cmd, uri) = (scan.next, scan.next)
        println(s"Request: $cmd $uri")
        handleRequest(cmd, uri, socket)
      }.recover{ case e: Throwable => s"Connection failed: $e" } 

  def main(args: Array[String]) =
    val port = Try{ args(0).toInt }.getOrElse(8089)
    serverLoop(new ServerSocket(port))


