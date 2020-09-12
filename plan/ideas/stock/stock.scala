object stock {
  import scala.util.Try
  
  def load(fileName: String): String = 
    scala.io.Source.fromFile(fileName, "UTF-8").getLines.mkString("\n")
  
  def save(fileName: String, data: String) = {
    import java.nio.file.{Paths, Files}
    import java.nio.charset.StandardCharsets.UTF_8
    println("Saving file: " + Paths.get(fileName))
    Files.write(Paths.get(fileName), data.getBytes(UTF_8))
  }

  def skipTo(data: String, from: String): String = data.drop(data.indexOfSlice(from)-1)
  
  def extract(data: String, init: String, end: String, from: Int = 0): (String, Int) = {
    val start = data.indexOfSlice(init, from) 
    if (start > 0) {
      val startAfterInit = start + init.size
      val stop = data.indexOfSlice(end, startAfterInit) 
      (data.slice(startAfterInit, stop), stop)
    } else ("", -1)
  }

  def aboutStockURL(adr: String) = s"https://www.avanza.se/aktier/om-aktien.html/$adr"
  def loadURL(url: String) = scala.io.Source.fromURL(url, "UTF-8").getLines.mkString 
  val avanza = "https://www.avanza.se/aktier/gamla-aktielistan.html"
  val stocksHtml = loadURL(avanza) 
  val initStock = """<a href="/aktier/om-aktien.html/"""
  val quote = "\""
  
  def extractStock(htmlSoup: String, valueId: String): String = {
    val (_, pos) =  extract(htmlSoup,valueId,"<dd><span", 0)
    val (result, _) = extract(htmlSoup,"<span>","</", pos)
    result
  } 
  
  def fixPeriod(d: String) = d.replace(",",".")
  
  def extractStockDouble(htmlSoup: String, valueId: String): Double = Try {
    fixPeriod(extractStock(htmlSoup, valueId)).toDouble
  }.getOrElse(-99999)

  
  case class Stock(
      url: String = "???", 
      da: Double = 0.0, 
      pe: Double = 0.0, 
      ps: Double = 0.0){

    def toCSV = productIterator.mkString(";")
  } 
  case object Stock {
      val heading = "URL;Dir.avk.;P/E;P/S\n"
  }
    
  def parse(data: String): Vector[Stock] = {
    var result = Vector.empty[Stock]
    var pos = 0
    while (pos < data.length && pos >= 0) {
      val (id, idAfter) = extract(data, initStock, quote, pos)
      if (id != "") {
        val url = aboutStockURL(id)
        val stockData = loadURL(url)
        val pef = extractStockDouble(stockData, "P/E-tal")
        val psf = extractStockDouble(stockData, "P/S-tal")
        val daf = extractStockDouble(stockData, "Direktavkastning %")
        val s = Stock(url, da = daf, pe = pef, ps = psf)
        println(s.toCSV)
        result :+= s
      }
      pos = idAfter
    }
    result
  } 
  

  def main(args: Array[String]): Unit =  {
    print(Stock.heading)
    val stocks = parse(stocksHtml)
    val topStocks = stocks
       .sortWith((s1, s2) => s1.da > s2.da)
       .filter(s => s.da > 3.0 && s.pe < 20 && s.pe > 0)
       .map(_.toCSV)
       .mkString("\n")
    save("top.csv", Stock.heading+topStocks)   
    save("all.csv", Stock.heading+stocks.map(_.toCSV).mkString("\n"))   
  }
}
