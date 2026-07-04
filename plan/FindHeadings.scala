object FindHeadings:

  extension (s: String)
    def removeStuff: String =
      s.replace("[basicstyle=]", "")
        .replace("''", "")
        .replace("–", "--")
        .trim

  /** Read the brace-delimited group whose opening '{' is at index `open`.
    * Returns (innerContent, indexAfterClosingBrace). Handles nested braces. */
  def braceGroup(s: String, open: Int): (String, Int) =
    var depth = 0
    var i = open
    val sb = StringBuilder()
    var result: Option[(String, Int)] = None
    while i < s.length && result.isEmpty do
      s(i) match
        case '{' =>
          if depth > 0 then sb.append('{')
          depth += 1
        case '}' =>
          depth -= 1
          if depth == 0 then result = Some((sb.toString, i + 1))
          else sb.append('}')
        case c => sb.append(c)
      i += 1
    result.getOrElse((sb.toString, i))

  def skipSpaces(s: String, from: Int): Int =
    var i = from
    while i < s.length && s(i) == ' ' do i += 1
    i

  /** Strip leftover LaTeX from a .toc title and normalise to match pdftk titles. */
  def cleanTocTitle(raw: String): String =
    raw
      .replaceAll("""\\hspace\s*\{[^{}]*\}""", " ")       // part spacing -> space
      .replaceAll("""\\[a-zA-Z]+\s*\{([^{}]*)\}""", "$1") // keep arg: \texttt {Map} -> Map
      .replaceAll("""\\[a-zA-Z]+\s*""", "")               // stray bare commands
      .replace("---", "—")
      .removeStuff

  /** Section number + printed page per heading, parsed from compendium.toc.
    * .toc line: \contentsline {type}{[\numberline {NUM}]TITLE}{PRINTEDPAGE}{anchor} */
  def tocInfo(tocText: String): Map[String, (String, Int)] =
    tocText.linesIterator.flatMap: line =>
      if !line.startsWith("\\contentsline") then None
      else
        val i0 = line.indexOf('{')
        if i0 < 0 then None
        else
          val (_, i1) = braceGroup(line, i0) // {type}
          val j1 = skipSpaces(line, i1)
          if j1 >= line.length || line(j1) != '{' then None
          else
            val (titlePart, i2) = braceGroup(line, j1) // {titlePart}
            val j2 = skipSpaces(line, i2)
            if j2 >= line.length || line(j2) != '{' then None
            else
              val (pageStr, _) = braceGroup(line, j2) // {printedPage}
              val nl = "\\numberline"
              val k = titlePart.indexOf(nl)
              val (number, titleRaw) =
                if k >= 0 then
                  val (num, after) =
                    braceGroup(titlePart, titlePart.indexOf('{', k))
                  (num.trim, titlePart.substring(after))
                else ("", titlePart)
              val heading = cleanTocTitle(titleRaw)
              val printed = pageStr.trim.toIntOption.getOrElse(-1)
              if heading.nonEmpty then Some(heading -> (number, printed))
              else None
    .toMap

  /** Generate one edition's headings file from its PDF + `.toc`: pdftk bookmarks
    * (title -> physical page) joined with the `.toc` (heading -> section number +
    * printed page). `valName` is the emitted `lazy val` — SV: `headings`; EN:
    * `headingsEn` — with the SAME tuple shape for both editions, so muntabot can
    * join a Swedish heading to its English page via the language-independent
    * section number. Skips cleanly (SV build unaffected) when the PDF is absent,
    * so the EN pass stays dormant until `sbt pdfCompendiumEn` has built it. */
  def generate(subdir: String, pdfName: String, valName: String, source: String): Unit =
    val wd = os.pwd / subdir
    val in = wd / pdfName
    val tocFile = wd / pdfName.replace(".pdf", ".toc")
    if !os.exists(in) then
      println(
        Console.YELLOW + s"Skipping $valName: no $in (build it first, e.g. sbt pdfCompendiumEn)" + Console.RESET
      )
    else
      util.Try {
        val out = os.pwd / "target" / source
        val dest =
          os.pwd / os.up / os.up / "bjornregnell" / "muntabot" / "src" / "main" / "scala"
        println(s"FindHeadings using pdftk on $pdfName")
        println(s"Reading: $in")

        // 1) pdftk bookmarks: rendered title -> physical PDF page (and level)
        val lines = os.proc("pdftk", in, "dump_data_utf8").call().out.text().split("\n")
        val bookmarks: Seq[Seq[Seq[String]]] = lines
          .filter(_.startsWith("Bookmark"))
          .mkString("\n")
          .split("BookmarkBegin")
          .map: xs =>
            xs.split("\n")
              .filter(_.nonEmpty)
              .map(_.replace("Bookmark", ""))
              .map(_.split(":", 2).map(_.trim).filter(_.nonEmpty).toSeq) // split only the leading "Field:"
              .toSeq
          .toSeq

        case class Title(level: String, heading: String, page: String)
        val titles: Seq[Title] =
          bookmarks
            .map: ref =>
              val titleInfo = ref.find(pair => pair.lift(0) == Some("Title"))
              val pageInfo = ref.find(pair => pair.lift(0) == Some("PageNumber"))
              val levelInfo = ref.find(pair => pair.lift(0) == Some("Level"))
              (levelInfo, titleInfo, pageInfo) match
                case (Some(l), Some(t), Some(p))
                    if l.length > 1 && t.length > 1 && p.length > 1 =>
                  Some(Title(l(1), t(1).removeStuff, p(1)))
                case _ => None
            .flatten
            .sortBy(t => (t.page.toIntOption, t.level.toIntOption))

        // 2) .toc: heading -> (section number, printed page)
        val toc: Map[String, (String, Int)] =
          if os.exists(tocFile) then tocInfo(os.read(tocFile)) else Map.empty
        println(s"Parsed ${toc.size} numbered headings from ${tocFile.last}")

        // 3) join: (heading, number, printedPage, physicalPage)
        //    number = "" and printedPage = physical when no .toc match.
        case class Info(heading: String, number: String, printed: Int, physical: Int):
          def esc(x: String): String = x.replace("\\", "\\\\").replace("\"", "\\\"")
          def show: String = s"""("${esc(heading)}", "${esc(number)}", $printed, $physical)"""
        val infos: Seq[Info] =
          titles.flatMap: t =>
            t.page.toIntOption.map: physical =>
              toc.get(t.heading) match
                case Some((number, printed)) =>
                  Info(t.heading, number, if printed >= 0 then printed else physical, physical)
                case None => Info(t.heading, "", physical, physical)

        val generatedCode =
          s"""|package shared
              |
              |  /** Generated by introprog plan/FindHeadings.scala:
              |    * ($valName: heading, sectionNumber, printedPage, physicalPage).
              |    * sectionNumber is "" if unnumbered; printedPage == physicalPage if no .toc match. */
              |  lazy val $valName: Seq[(String, String, Int, Int)] = Seq(
              |${infos.map(_.show).mkString("    ", ",\n    ", ",\n    ")}
              |  )
              |""".stripMargin
        println(s"Saving: $out")
        os.write.over(out, generatedCode)
        if os.exists(dest) then
          println(s"Saving: $dest/$source")
          os.write.over(dest / source, generatedCode)
        else
          println(
            Console.YELLOW + s"Cannot copy file to missing dir $dest \n  clone bjornregnell/muntabot" + Console.RESET
          )
      } match
        case util.Failure(exception) =>
          println(Console.RED + s"Failed to generate $valName: $exception" + Console.RESET)
        case util.Success(_) =>
          println(Console.GREEN + s"OK! Successful $valName generation done!" + Console.RESET)
          println(Console.YELLOW + "TODO: Rebuild with muntabot/publish.sh" + Console.RESET)

  /** SV compendium (always) + EN mirror (when built). Both editions emit the same
    * tuple shape so muntabot links can join sv-heading -> number -> en-page. */
  def apply(): Unit =
    generate("compendium", "compendium.pdf", "headings", "headings-GENERATED.scala")
    generate("compendium-en", "compendium-en.pdf", "headingsEn", "headings-En-GENERATED.scala")
