//> using scala 3.3.6
// Report DUPLICATE keys in Overrides.scala / CodeGlossary.scala, with the line of each occurrence.
//
// WHY: the header of Overrides.scala used to claim "Duplicate keys are a compile error (Map literal)".
// They are NOT. A Scala `Map` literal silently keeps the LAST pair, so a second entry for an existing
// key replaces the first with no warning from anywhere -- including when the two disagree. That is how
// `"alternativ" -> "selection"`, the right term in the sekvens/alternativ/repetition triad, became
// "alternative" with every gate green: a duplicate leaves the KEY SET unchanged, so neither the
// override-orphan count nor the fallback count can move.
//
// CodeGlossary needs more than a flat scan: the same word legitimately appears in `id` AND in
// `codeStr` (the documented two-mechanism pattern -- `id` reaches the bare token a REPL echoes,
// `codeStr` reaches the quoted literal), and a per-file scope name legitimately appears in both
// `perFileId` and `perFileCodeStr`. Only a repeat within ONE map is a bug. So keys are grouped by
// their enclosing `val <name>` and, inside the nested per-file maps, by their enclosing file scope.
//
// SCOPE: plain "..." keys only. Triple-quoted keys carry LaTeX and newlines, and a regex over them
// mis-pairs -- the first version of this tool tried and silently missed `topptyp`.
//
// run: scala-cli run autotranslate/scratch/dup-keys.scala -- autotranslate/Overrides.scala autotranslate/CodeGlossary.scala

@main def dupKeys(files: String*): Unit =
  val sectionRe = """^\s*(?:val|lazy val)\s+(\w+)\s*:""".r
  // one regex for both: a key followed by `-> Map(` OPENS a per-file scope, anything else is an entry.
  // They share a line in the common case -- `"lect-w04-objects" -> Map("grön" -> "green"),` -- so the
  // matches of a line must be walked left to right, not classified line-at-a-time.
  // `(?<!")` / `(?!")` keep TRIPLE-quoted keys out: without them `"""X""" ->` matches the last two
  // quotes of the closing `"""` as an EMPTY key, and every such entry collapses into one fake duplicate.
  val entryRe   = """(?<!")"((?:[^"\\\n]|\\.)*)"(?!")\s*->\s*(Map\()?""".r
  var bad = 0
  for file <- files do
    var section = "<top>"
    var scope   = ""
    var depth   = 0 // brace depth at which the current scope's inner Map opened
    val hits = scala.collection.mutable.ArrayBuffer.empty[(String, String, Int)]
    var open = 0
    for (raw, n) <- scala.io.Source.fromFile(file, "UTF-8").getLines().zipWithIndex do
      // blank out `// ...`: several comments QUOTE an entry, and counting those is a false duplicate.
      // A `//` inside a string (a URL) is left alone by the quote-parity test.
      val line = Iterator.iterate(raw.indexOf("//"))(i => raw.indexOf("//", i + 1)).takeWhile(_ >= 0)
        .find(i => raw.take(i).count(_ == '"') % 2 == 0).fold(raw)(raw.take)
      sectionRe.findFirstMatchIn(line).foreach { m => section = m.group(1); scope = "" }
      for m <- entryRe.findAllMatchIn(line) do
        if m.group(2) != null then { scope = m.group(1); depth = open + line.take(m.start).count(_ == '(') - line.take(m.start).count(_ == ')') }
        else hits += ((s"$section${if scope.isEmpty then "" else s"/$scope"}", m.group(1), n + 1))
      open += line.count(_ == '(') - line.count(_ == ')')
      if scope.nonEmpty && open <= depth then scope = ""
    val dups = hits.groupBy(h => (h._1, h._2)).filter(_._2.size > 1).toVector.sortBy(_._1)
    bad += dups.size
    println(s"$file: ${hits.size} plain-string keys, ${dups.size} duplicated within one map")
    for ((where, k), occurrences) <- dups do
      println(s"DUP\t$where\t$k\tlines ${occurrences.map(_._3).mkString(", ")} -- the LAST one wins")
  if bad > 0 then System.exit(1)
