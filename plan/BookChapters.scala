object BookChapters {
  val lewisRef = """''Introduction to Programming and Problem-Solving'', Lewis and Lacher, Second Ed., CRC Press, 2017."""
  val pinsRef = """''Programming in Scala'', Odersky et al., Third Ed., Artima 2016."""
  case class Chap(lewis: String = "", pins: String = "")
  val chap = Map(
    "intro"       -> Chap(lewis = "1.1-1.6,2.1-2.2,2.4-2.10,3.4-3.5", pins = ""),
    "programs"    -> Chap(lewis = "3.1-3.3,8.1-8.3", pins = ""),
    "functions"   -> Chap(lewis = "4.1-4.7,5.1-5.2,7.5-7.9", pins = ""),
    "objects"     -> Chap(lewis = "2.3,5.3, 9.3-9.4,", pins = ""),
    "classes"     -> Chap(lewis = "10", pins = ""),
    "sequences"   -> Chap(lewis = "6.1-6.7", pins = ""),
    "data"        -> Chap(lewis = "", pins = ""),
    "matrices"    -> Chap(lewis = "7.3,7.10,", pins = ""),
    "inheritance" -> Chap(lewis = "7.4, 10", pins = ""),
    "patterns"    -> Chap(lewis = "5.5-5.6", pins = ""),
    "scalajava"   -> Chap(lewis = "", pins = ""),
    "sorting"     -> Chap(lewis = "13", pins = ""),
    "extra"       -> Chap(lewis = "", pins = "")
  )
}