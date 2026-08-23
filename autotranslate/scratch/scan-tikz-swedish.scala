//> using scala 3.8.4
//> using dep com.lihaoyi::os-lib:0.11.4

// Scope the "Swedish text inside tikz/pgf figures" pass. These envs are masked-whole by the
// autotranslator (never translated), so their node text stays Swedish on the English side; the only
// fix is a source \ifswedish <sv>\else <en>\fi clamp inside each node. This lists every non-comment
// line that sits inside a tikz/pgf env AND contains åäöÅÄÖ, with a per-file tally. Output ->
// scratch/tikz-swedish.txt; summary to stdout. Run: scala-cli run scan-tikz-swedish.scala -- <introprog>

@main def run(root: String): Unit =
  val base = os.Path(root)
  val dirs = Seq("slides", "compendium", "plan").map(base / _).filter(os.exists)
  val beginRe = raw"\\begin\{(tikzpicture|pgfpicture|tikzcd|forest)\}".r
  val endRe = raw"\\end\{(tikzpicture|pgfpicture|tikzcd|forest)\}".r
  // åäö catches most Swedish; but tikz labels are often short Swedish words with NO diacritic
  // (Kompilator, Maskinkod, Indata-enhet, Minne, Virtuell…) — add a CS-diagram Swedish-term list so the
  // scope is complete. False positives are fine (reviewed by hand); misses are the real risk.
  val sweRe = ("(?i)(" +
    raw"[åäöÅÄÖ]" + "|" +
    raw"\b(kompilator|maskinkod|maskinspr|k(ä|ae)llkod|indata|utdata|enhet|minne|h(å|aa)rdvara|" +
    raw"h(ö|oe)gniv|virtuell|programmeringsspr|bytekod|niv(å|aa)|spr(å|aa)k|(ö|oe)vers(ä|ae)tt|" +
    raw"konkret|abstrakt|specifik|generell|datorn|raden|kolumn|vänster|höger|sats|uttryck)\w*" +
    ")").r
  val perFile = scala.collection.mutable.LinkedHashMap[os.RelPath, Int]()
  val out = scala.collection.mutable.ArrayBuffer[String]()
  var total = 0
  for dir <- dirs; f <- os.walk(dir).sorted if f.ext == "tex" do
    val rel = f.relativeTo(base)
    var depth = 0
    var count = 0
    for line <- os.read.lines(f) do
      val opens = beginRe.findAllIn(line).size
      val closes = endRe.findAllIn(line).size
      val inside = depth > 0 || opens > 0
      if inside && !line.trim.startsWith("%") && sweRe.findFirstIn(line).isDefined then
        count += 1
        out += s"$rel: ${line.trim}"
      depth = math.max(0, depth + opens - closes)
    if count > 0 then { perFile(rel) = count; total += count }
  os.write.over(base / "autotranslate" / "scratch" / "tikz-swedish.txt", out.mkString("\n") + "\n")
  println(s"=== $total Swedish lines inside tikz/pgf/forest envs, ${perFile.size} files (full list -> scratch/tikz-swedish.txt) ===")
  perFile.toSeq.sortBy(-_._2).foreach((rel, c) => println(f"$c%4d  $rel"))
