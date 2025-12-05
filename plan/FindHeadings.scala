object FindHeadings:

  def apply(): Unit = util.Try {
    val wd = os.pwd / "compendium"
    val in = wd / "compendium.pdf"
    val source = "headings-GENERATED.scala"
    val out = os.pwd / "target" / source
    val dest = os.pwd / os.up / os.up / "bjornregnell" / "muntabot" / "src" / "main" / "scala"
    println(s"FindHeadings using pdftk on compendium.pdf")
    println(s"Reading: $in")

    val lines = os.proc("pdftk", in, "dump_data_utf8").call().out.text().split("\n")
    
    val bookmarks: Seq[Seq[Seq[String]]] = lines
      .filter(_.startsWith("Bookmark")).mkString("\n")
      .split("BookmarkBegin")
      .map: xs =>
        xs.split("\n")
          .filter(_.nonEmpty)
          .map(_.replace("Bookmark", ""))
          .map(_.split(":").map(_.trim).filter(_.nonEmpty).toSeq)
          .toSeq
      .toSeq

    case class Title(level: String, heading: String, page: String):
      def show: String = s"""("$heading", $page, $level)"""

    extension (s: String) def removeStuff = 
      s.replace("[basicstyle=]", "").replace("''", "").replace("–","--")

    val titles = 
      bookmarks.map: ref => 
        val titleInfo = ref.find(pair => pair.lift(0) == Some("Title"))
        val pageInfo = ref.find(pair => pair.lift(0) == Some("PageNumber"))
        val levelInfo = ref.find(pair => pair.lift(0) == Some("Level"))
        (levelInfo, titleInfo, pageInfo) match
          case (Some(l), Some(t), Some(p)) if l.length > 1 && t.length > 1 && p.length > 1 => 
            Some(Title(l(1), t(1).removeStuff, p(1)))
          case _ => None
      .flatten.sortBy(t => (t.page.toIntOption, t.level.toIntOption))

    val generatedCode = 
      s"""|package shared
          |
          |  lazy val headingsWithPageAndLevel: Seq[(String, Int, Int)] = Seq(
          |${titles.map(_.show).mkString("    ", ",\n    ", ",\n    ")}
          |  )
          |""".stripMargin 
    println(s"Saving: $out")
    os.write.over(out, generatedCode)
    if os.exists(dest) then  
      println(s"Saving: $dest/$source")
      os.write.over(dest / source, generatedCode)
    else 
      println(Console.YELLOW + s"Cannot copy file to missing dir $dest \n  clone bjornregnell/muntabot" + Console.RESET)
  } match
    case util.Failure(exception) => println(Console.RED + s"Failed to generate headings: $exception" + Console.RESET)
    case util.Success(_) => println(Console.GREEN + "OK! Successful Headings generation done!" + Console.RESET)
  