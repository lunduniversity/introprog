package fibserver.threaded.memcache

import java.net.{ServerSocket, Socket}
import java.io.OutputStream
import java.util.Scanner
import scala.util.{Try, Success, Failure}
import scala.concurrent._
import ExecutionContext.Implicits.global

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

  def insertBreak(s: String, n: Int = 80): String = 
    if s.size < n then s else s.take(n) + "</br>" + insertBreak(s.drop(n),n)

object compute:
  import java.util.concurrent.ConcurrentHashMap
  val memcache = new ConcurrentHashMap[BigInt, BigInt]

  def fib(n: BigInt): BigInt = 
    if memcache.containsKey(n) then 
      println("CACHE HIT!!! no need to compute: " + n)
      memcache.get(n)
    else
      println("cache miss :( must compute fib:  " + n)
      val f = fastFib(n)
      memcache.put(n, f)
      f

  private def fastFib(n: BigInt): BigInt =
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
        Future { handleRequest(cmd, uri, socket) }.recover {
          case e => println(s"Reqest failed: $e")
        }
      }.recover{ case e: Throwable => s"Connection failed: $e" } 

  def main(args: Array[String]) =
    val port = Try{ args(0).toInt }.getOrElse(8089)
    serverLoop(new ServerSocket(port))


