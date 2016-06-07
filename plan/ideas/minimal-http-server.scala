// run with: scala myserver.start

package myserver

import java.io.OutputStream
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import com.sun.net.httpserver.{Headers, HttpExchange, HttpHandler, HttpServer}

object Html {
  def minimalWebPage(body: String): String = 
    s"""<!DOCTYPE html>
       |<html><head><meta charset="UTF-8"><title>Min Sörver</title></head> 
       |<body>
       |$body
       |</body>
       |</html>
       """.stripMargin
}

object start {
  def main(args: Array[String]): Unit = {
    val server = HttpServer.create(new InetSocketAddress(8080), 0)
    server.createContext("/", new MyHandler)
    server.setExecutor(Executors.newCachedThreadPool())
    server.start();
    System.out.println("Surfa till servern på http://localhost:8080/hej" );
  }
}


class MyHandler  extends HttpHandler {
  def handle(exchange: HttpExchange ): Unit = {
    val requestMethod: String = exchange.getRequestMethod();
    if (requestMethod.equalsIgnoreCase("GET")) {
      val uri = exchange.getRequestURI
      println(s"GET URI=$uri")
      val response = "BAKLÄNGES: " + uri.toString.drop(1).reverse  
      val htmlResponse = Html.minimalWebPage(s"<H1>ADRESS: $uri</H1>\n$response")
      println("RESPONSE:\n" + htmlResponse)
      val responseHeaders: Headers = exchange.getResponseHeaders();
      responseHeaders.set("Content-Type", "text/html");
      exchange.sendResponseHeaders(200, 0);
      val responseBody: OutputStream  = exchange.getResponseBody();
      responseBody.write(htmlResponse.getBytes());
      responseBody.close();
    }
  }
}

/* from example:
   http://stackoverflow.com/questions/1186328/embedded-http-server-in-swing-java-app
   removed code for inspecting the request header keys
      val responseHeaders: Headers = exchange.getResponseHeaders();
      responseHeaders.set("Content-Type", "text/plain");
      exchange.sendResponseHeaders(200, 0);
      val responseBody: OutputStream  = exchange.getResponseBody();
      val requestHeaders: Headers = exchange.getRequestHeaders();
      val keySet: JSet[String] = requestHeaders.keySet();
      val iter: JIterator[String] = keySet.iterator();
      while (iter.hasNext()) {
        val key: String = iter.next();
        val values: JList[String] = requestHeaders.get(key);
        val s: String = key + " = " + values.toString() + "\n" + "HEJ GURKA!!\n"
        responseBody.write(s.getBytes());
      }
      responseBody.close();
*/
