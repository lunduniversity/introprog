package webserver

import java.net.{ServerSocket, Socket}
import java.io.OutputStream
import java.util.Scanner
import scala.util.Try

object html:
  def page(body: String): String = //minimal web page
    s"""<!DOCTYPE html>
       |<html>
       |<head><meta charset="UTF-8"><title>Min Sörver</title></head> 
       |<body>
       |$body
       |</body>
       |</html>
       """.stripMargin          

  def header(length: Int): String = //standardized header of reply 
    s"HTTP/1.0 200 OK\nContent-length: $length\n" + 
     "Content-type: text/html\n\n"


object start:
  def handleRequest(cmd: String, uri: String, socket: Socket): Unit =
    val os = socket.getOutputStream
    val response = html.page("Baklänges: " + uri.reverse)
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

