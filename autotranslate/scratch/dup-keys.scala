//> using scala 3.3.6
// Report DUPLICATE keys in Overrides.scala / CodeGlossary.scala, with the line of each occurrence.
//
// The header of Overrides.scala claims "Duplicate keys are a compile error (Map literal)". They are
// NOT: a Scala `Map` literal silently keeps the LAST pair, so a second entry for an existing key
// replaces the first with no warning anywhere. That is how `"alternativ" -> "selection"` -- the right
// term in the sekvens/alternativ/repetition triad -- turned into "alternative" with the build green.
//
// SCOPE: plain "..." keys only. Triple-quoted keys are skipped: they carry LaTeX and newlines, a
// regex over them mis-pairs (the first version of this tool tried, and silently missed `topptyp`).
// Concept terms and other short keys are the ones that collide, and those are all plain strings.
//
// run: scala-cli run autotranslate/scratch/dup-keys.scala -- autotranslate/Overrides.scala

@main def dupKeys(files: String*): Unit =
  // a plain key, possibly with its `->` on the next line; `(?<!")` and `(?!")` keep """ out
  val keyRe = """(?<!")"((?:[^"\\\n]|\\.)*)"(?!")\s*(?:\r?\n\s*)?->""".r
  for file <- files do
    // Blank out `// ...` comments first, keeping the line count: several comments QUOTE an entry
    // ("`\"Grupplaboration\" -> ...` is RULED"), and counting those as a second entry is a false
    // duplicate. A `//` inside a string (a URL) is left alone by the quote-parity test.
    val src = scala.io.Source.fromFile(file, "UTF-8").getLines().map { line =>
      val cut = Iterator.iterate(line.indexOf("//"))(i => line.indexOf("//", i + 1)).takeWhile(_ >= 0)
        .find(i => line.take(i).count(_ == '"') % 2 == 0)
      cut.fold(line)(line.take)
    }.mkString("\n")
    // NB the parens: `->` and `+` share a precedence level, so `k -> n + 1` parses as `(k -> n) + 1`,
    // which is string concatenation on a tuple and compiles to a baffling Any.
    val hits = keyRe.findAllMatchIn(src)
      .map(m => (m.group(1), src.take(m.start).count(_ == '\n') + 1)).toVector
    val dups = hits.groupBy(_._1).filter(_._2.size > 1).toVector.sortBy(_._1)
    println(s"$file: ${hits.size} plain-string keys, ${dups.size} duplicated")
    for (k, occurrences) <- dups do
      println(s"DUP\t$k\tlines ${occurrences.map(_._2).mkString(", ")} -- the LAST one wins")
