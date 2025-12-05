object FindHeadings:
  def apply(): Unit = 
    println("FindHeadings using pdftk")
    val wd = os.pwd / "compendium"
    val in = wd / "compendium.pdf"
    println(s"Reading: $in")

    val lines = os.proc("pdftk", in, "dump_data_utf8").call().out.text().split("\n")
    
    val bookmarks = lines
      .filter(_.startsWith("Bookmark")).mkString("\n")
      .split("BookmarkBegin")
      .map: xs =>
        xs.split("\n")
          .filter(_.nonEmpty)
          .map(_.replaceAllLiterally("Bookmark", ""))
          .map(_.split(":").toSeq)
          .toSeq

    println(bookmarks.mkString("\n"))