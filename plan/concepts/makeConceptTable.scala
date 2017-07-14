//makeConceptTable.scala  **** THIS IS JUST A STUB ; WORK IN PROGRESS

//to be executed as scala script:

//scala makeConceptTable
//scala makeConceptTable -html
//scala makeConceptTable -tex

println("makeConceptTable " + args.mkString(" "))
if (args.size == 0 || args.contains("-html")) makeConceptTable.mkHtml
if (args.size == 0 || args.contains("-tex"))  makeConceptTable.mkTex

object makeConceptTable {
  val nl = "\n"
  val outFileHtml = "concepts.html"
  val outFileTex  = "concepts.tex"  //TODO .tex generation

  case class Concept(sv: String, en: String, expl: String, svref: String = "", enref: String = "") {
    def tag(text: String, t: String) = s"<$t>$text</$t>"
    def ref(text: String, link: String) =
      if (link.startsWith("http")) s"""<a href="$link">$text</a>""" else text
    def toHtmlTableRow(t: String)=
      "\n<tr>" + tag(ref(sv, svref), t) + tag(ref(en, enref), t) + tag(expl, t) + "</tr>\n"
    val toHtmlRow  = toHtmlTableRow("td")
    val toHtmlHead = toHtmlTableRow("th")
  }
  val heading = Concept("Svenska", "English", "Kort förklaring")
  val concepts = Seq(
    Concept("klass", "class",
      "En mall för att skapa objekt. Innehåller normalt attribut, metoder och konstruktorer.",
      "https://sv.wikipedia.org/wiki/Klass_%28programmering%29",
      "https://en.wikipedia.org/wiki/Class_%28computer_programming%29"),
    Concept("attribut", "attribute",
      "Beskriver en egenskap hos ett objekt. Kallas även (data)fält, (data)medlem.",
      "https://sv.wikipedia.org/wiki/Objektorienterad_programmering#Attribut",
      "https://en.wikipedia.org/wiki/Class_%28computer_programming%29#Structure" )
  ).sortBy(_.sv)

  def toHtml(pre:  String = """<table border="1" style="width:100%">""", post: String = "</table>") =
    pre + heading.toHtmlHead + concepts.map(_.toHtmlRow).mkString(nl) + post

  implicit class StringSave(s: String) {
    def save(fileName: String): Unit =  {
      scala.tools.nsc.io.File(fileName).writeAll(s)
      println("Written output to file: " + fileName)
    }
  }

  def mkHtml: Unit = toHtml().save(outFileHtml)
  def mkTex: Unit = ???
}
