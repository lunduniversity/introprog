// kod till facit
// run in terminal with:       scala fibserver.start
package fibserver

import scala.util.Try
import java.io.OutputStream
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import com.sun.net.httpserver.{
  Headers, HttpExchange, HttpHandler, HttpServer
}

object Html {
  def minimalWebPage(body: String, title: String = "Min Sörver") = 
    s"""<!DOCTYPE html>
       |<html>
       |<head><meta charset="UTF-8">
       |<title>$title</title>
       |</head> 
       |<body>
       |$body
       |</body>
       |</html>
       """.stripMargin
}

object start {
  def main(args: Array[String]): Unit = {
    val server = HttpServer.create(new InetSocketAddress(8080), 0)
    server.createContext("/", new FibHandler)
    server.setExecutor(Executors.newCachedThreadPool)
    server.start
    println("Surfa till servern på http://localhost:8080/akrug" )
  }
}

class FibHandler extends HttpHandler {
  def fib(n: BigInt): BigInt = {
    if (n < 0) 0 else 
    if (n == 1 || n == 2) 1  
    else fib(n - 1) + fib(n -2)
  }
    
  def handle(exchange: HttpExchange ): Unit = {
    val requestMethod: String = exchange.getRequestMethod();
    if (requestMethod.equalsIgnoreCase("GET")) {
      val uri = exchange.getRequestURI.toString
      println(s"GET URI='$uri'")
      val response = if (uri.toLowerCase.startsWith("/fib")) Try[String] {
        val n = uri.drop(5).toInt
        s"fib($n) == ${fib(n)}"
      }.getOrElse("SKRIV ETT TAL") else "FATTAR NOLL"
      val htmlResponse = 
        Html.minimalWebPage(response, "Fibonacci")
      println("RESPONSE:\n" + htmlResponse)
      val responseHeaders: Headers = exchange.getResponseHeaders
      responseHeaders.set("Content-Type", "text/html")
      exchange.sendResponseHeaders(200, 0)
      val responseBody: OutputStream  = exchange.getResponseBody
      responseBody.write(htmlResponse.getBytes)
      responseBody.close
    }
  }
}
