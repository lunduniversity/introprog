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

  def esc(x: String): String = x.replace("\\", "\\\\").replace("\"", "\\\"")

  /** One generated row: (heading, sectionNumber, printedPage, physicalPage).
    * `heading` is the pdftk BOOKMARK title, not the `.toc` title -- see
    * `writeTranslateMap` for why that distinction decides a whole feature. */
  case class Info(heading: String, number: String, printed: Int, physical: Int):
    def show: String = s"""("${esc(heading)}", "${esc(number)}", $printed, $physical)"""

  /** Generate one edition's headings file from its PDF + `.toc`: pdftk bookmarks
    * (title -> physical page) joined with the `.toc` (heading -> section number +
    * printed page). `valName` is the emitted `lazy val` — SV: `headings`; EN:
    * `headingsEn` — with the SAME tuple shape for both editions, so muntabot can
    * join a Swedish heading to its English page via the language-independent
    * section number. Skips cleanly (SV build unaffected) when the PDF is absent,
    * so the EN pass stays dormant until `sbt pdfCompendiumEn` has built it.
    *
    * ⚠ THIS WRITES ONLY INTO `target/` — it does NOT touch muntabot (2026-08-21). Copying
    * into a sibling clone used to happen here, guarded by nothing but `os.exists(dest)`,
    * which meant a plain `sbt gen` silently dirtied a repo the user never asked to touch
    * on ANY box that had muntabot cloned. And the content is only as good as THAT box's
    * PDFs, since the page numbers come from whatever `compendium.pdf` is on disk, so an
    * automatic copy could quietly publish numbers from a stale build. The copy is now an
    * explicit opt-in step: `sbt syncMuntabot`. Keep generation and distribution separate.
    *
    * ⚠ The EN file (`headingsEn`) is still NOT synced: nothing in muntabot consumes it.
    * What muntabot DOES consume is `heading-translate-GENERATED.scala`, built from these
    * two tables by `writeTranslateMap` below and shipped by `syncMuntabot`. Until
    * 2026-08-22 muntabot generated that map itself with a local ollama run; it no longer
    * does, so there is now exactly ONE writer. Keep it that way. */
  def generate(subdir: String, pdfName: String, valName: String, source: String): Seq[Info] =
    val wd = os.pwd / subdir
    val in = wd / pdfName
    val tocFile = wd / pdfName.replace(".pdf", ".toc")
    if !os.exists(in) then
      println(
        Console.YELLOW + s"Skipping $valName: no $in (build it first, e.g. sbt pdfCompendiumEn)" + Console.RESET
      )
      Seq.empty
    else
      util.Try {
        val out = os.pwd / "target" / source
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
        infos
      } match
        case util.Failure(exception) =>
          println(Console.RED + s"Failed to generate $valName: $exception" + Console.RESET)
          Seq.empty
        case util.Success(infos) =>
          println(Console.GREEN + s"OK! Successful $valName generation done!" + Console.RESET)
          infos

  /** Is this edition's built `.toc` older than the `.tex` sources it was built from?
    *
    * The skew this catches is invisible by construction: `autotranslate` regenerates the English
    * `.tex` sources AND clears the built pdf/`.toc`, and `pdfCompendiumEn` rebuilds them
    * afterwards. Run `gen` in between and the heading map is joined against the PREVIOUS English
    * edition -- a map that is complete, well-formed and plausible, describing a book that is no
    * longer on disk. Nothing downstream can tell. So compare mtimes and SAY it. */
  def editionSkew(subdir: String, pdfName: String): Option[String] =
    val wd = os.pwd / subdir
    val toc = wd / pdfName.replace(".pdf", ".toc")
    if !os.exists(wd) || !os.exists(toc) then None
    else
      val tocTime = os.mtime(toc)
      val newer = os.list(wd).filter(p => p.ext == "tex" && os.mtime(p) > tocTime)
      if newer.isEmpty then None
      else Some(
        s"${newer.size} .tex source(s) in $subdir/ are NEWER than ${toc.last}" +
          s" (e.g. ${newer.take(3).map(_.last).mkString(", ")})"
      )

  /** The sv -> en DISPLAY map muntabot shows in English mode (`headingTranslateSvEn`),
    * joined on section number from the two tables generated above.
    *
    * ⚠ WHY THE JOIN IS OVER THE GENERATED TABLES AND NOT OVER THE TWO `.toc` FILES:
    * the keys must be pdftk BOOKMARK titles, because that is what muntabot looks up
    * (`Compendium.infoOf` is keyed by this table's first column, and `shownHeading`
    * is called with the same string). Joining the `.toc` files would key on
    * `cleanTocTitle` output instead -- a different string domain -- and every lookup
    * would silently MISS, degrading to Swedish with nothing to notice it. Measured
    * 2026-08-22: all 160 of muntabot's then-current keys are bookmark titles and
    * none reached its fallback branch.
    *
    * WHY THIS EXISTS AT ALL: the same Swedish heading used to be translated TWICE by
    * two independent systems that could silently disagree -- here, and by muntabot's
    * own ollama run in `auto-translate.sc`. At the switchover, of the 157 headings
    * both systems translated, 85 DISAGREED. This side wins because it is not a
    * translation at all: it is what the English compendium actually prints, so every
    * Overrides entry improves muntabot's labels for free.
    *
    * Headings with no English counterpart KEEP THEIR SWEDISH (BR's call) and are
    * COUNTED in the printout, so drift stays visible rather than silent. */
  def writeTranslateMap(sv: Seq[Info], en: Seq[Info], source: String): Unit =
    val out = os.pwd / "target" / source
    if en.isEmpty then
      println(
        Console.YELLOW + s"Skipping $source: no English headings (build compendium-en.pdf first)." +
          "\n  NOT writing an empty map: that would silently revert every English label to Swedish." +
          Console.RESET
      )
    else
      // Section numbers repeat in these tables (pdftk yields two bookmark hits for some
      // headings), so do NOT assume uniqueness. Reversing before `toMap` makes the FIRST
      // (lowest-page) occurrence win, matching muntabot's `Compendium.infoOf`.
      val enByNumber: Map[String, String] =
        en.filter(_.number.nonEmpty).reverse.map(i => i.number -> i.heading).toMap
      val joined: Map[String, String] =
        sv.filter(_.number.nonEmpty).reverse
          .flatMap(i => enByNumber.get(i.number).map(e => i.heading -> e))
          .toMap
      val pairs = joined.toSeq.sortBy(_._1)
      val keptSwedish = sv.map(_.heading).distinct.count(!joined.contains(_))
      val identical = pairs.count((s, e) => s == e)
      val entries = pairs.map((s, e) => s"""    "${esc(s)}" -> "${esc(e)}"""").mkString(",\n")
      val generatedCode =
        s"""|package shared
            |
            |  /** English DISPLAY titles for the Swedish compendium headings muntabot links to,
            |    * keyed by the heading text in `headings`. Links still open the Swedish
            |    * compendium.pdf; only the shown text is translated.
            |    *
            |    * GENERATED by introprog plan/FindHeadings.scala by joining compendium.toc and
            |    * compendium-en.toc on section number -- NOT translated by a model. A heading
            |    * with no English counterpart is absent here and stays Swedish at the call site.
            |    * Do not edit: fix the English compendium instead. */
            |  lazy val headingTranslateSvEn: Map[String, String] = Map(
            |$entries
            |  )
            |""".stripMargin
      println(s"Saving: $out")
      os.write.over(out, generatedCode)
      println(
        Console.GREEN + s"OK! headingTranslateSvEn: ${pairs.size} joined, " +
          s"$keptSwedish kept Swedish (no English counterpart), " +
          s"$identical identical to Swedish" + Console.RESET
      )

  /** SV compendium (always) + EN mirror (when built). Both editions emit the same
    * tuple shape so muntabot links can join sv-heading -> number -> en-page.
    * Both land in `target/` only; distribution to muntabot is `sbt syncMuntabot`. */
  def apply(): Unit =
    val sv = generate("compendium", "compendium.pdf", "headings", "headings-GENERATED.scala")
    val en = generate("compendium-en", "compendium-en.pdf", "headingsEn", "headings-En-GENERATED.scala")
    writeTranslateMap(sv, en, "heading-translate-GENERATED.scala")
    editionSkew("compendium-en", "compendium-en.pdf").foreach: msg =>
      println(
        Console.YELLOW + s"WARNING -- English edition skew: $msg." + Console.RESET +
          "\n  The heading map just written describes the PREVIOUS English edition." +
          "\n  Fix:  sbt pdfCompendiumEn  ->  sbt gen  ->  sbt syncMuntabot"
      )
    println(Console.YELLOW + "Headings generated into target/." + Console.RESET)
    println("  To update a local muntabot clone:  sbt syncMuntabot")
    println("  (page numbers come from THIS box's compendium.pdf -- rebuild it first if unsure)")
