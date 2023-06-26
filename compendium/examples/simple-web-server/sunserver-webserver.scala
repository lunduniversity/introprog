package sunserver

import java.io.OutputStream
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import com.sun.net.httpserver.{
  Headers, HttpExchange, HttpHandler, HttpServer
}

object Html:
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

object start:
  def main(args: Array[String]): Unit =
    val server = HttpServer.create(new InetSocketAddress(8080), 0)
    server.createContext("/", new BaklängesHandler)
    server.setExecutor(Executors.newCachedThreadPool)
    server.start
    println("Surfa till servern på http://localhost:8080/akrug" )

class BaklängesHandler extends HttpHandler:
  def handle(exchange: HttpExchange ): Unit =
    val requestMethod: String = exchange.getRequestMethod();
    if requestMethod.equalsIgnoreCase("GET") then
      val uri = exchange.getRequestURI.toString
      if uri == "/killserver" then {println("HACKAD :("); System.exit(1)}
      println(s"GET URI='$uri'")
      val response = "BAKLÄNGES: " + uri.drop(1).reverse  
      val htmlResponse = 
        Html.minimalWebPage(s"<H1>ADRESS: $uri</H1>\n$response")
      println("RESPONSE:\n" + htmlResponse)
      val responseHeaders: Headers = exchange.getResponseHeaders
      responseHeaders.set("Content-Type", "text/html")
      exchange.sendResponseHeaders(200, 0)
      val responseBody: OutputStream  = exchange.getResponseBody
      responseBody.write(htmlResponse.getBytes)
      responseBody.close
