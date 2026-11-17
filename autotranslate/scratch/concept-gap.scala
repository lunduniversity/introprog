//> using scala 3.3.6
// Audit the module-plan concept column against the two maps that translate it.
//
// `plan.Main` resolves each comma-separated term through the AUTHORITATIVE glossary and leaves the
// rest Swedish; `Main.enConcepts` finishes those from Overrides. This tool reports the two ways that
// can go wrong without any gate noticing:
//
//   UNCOVERED  a term neither map translates -- it renders SWEDISH in the English table. Most of the
//              list is Scala code (`val`, `flatMap`, `java.nio.file`) where verbatim IS the English,
//              so read the report, do not just count it.
//   REDUNDANT  an Overrides key the glossary already covers. Harmless but misleading: the glossary
//              wins, so editing the override changes nothing.
//
// A key that matches NO plan term at all is caught by the override-orphan gate instead, which is why
// this tool no longer tries to: against the full Overrides file every prose key would look like one.
//
// run: scala-cli run glossary/concepts.scala autotranslate/scratch/concept-gap.scala -- autotranslate/Overrides.scala

@main def conceptGap(table: String): Unit =
  val conceptEn: Map[String, String] =
    glossary.explain.allConcepts.filter(_.en.nonEmpty).map(c => c.sv.trim -> c.en.trim).toMap
  val lines = scala.io.Source.fromFile("plan/module-plan-generated.tex", "UTF-8").getLines().toVector
  val terms = lines.flatMap: line =>
    val cols = line.split('&').toVector
    if cols.size < 3 then Vector.empty
    else cols(2).replace("\\\\", "").split(',').toVector.map(_.trim).filter(_.nonEmpty)
  val all = terms.distinct.toSet
  val missing = all.filterNot(conceptEn.contains)

  // KEYS ONLY, read from the whole file at once: an entry may put its `->` on the NEXT line and may
  // carry a trailing `// comment`, so a per-line `key -> value` regex silently misses entries and
  // reports them as uncovered. The first version of this tool did exactly that.
  val src = scala.io.Source.fromFile(table, "UTF-8").mkString
  val keyRe = """"((?:[^"\\\n]|\\.)*)"\s*(?:\r?\n\s*)?->""".r
  val keys = keyRe.findAllMatchIn(src).map(_.group(1)).toSet

  val uncovered = (missing -- keys).toVector.sorted
  val redundant = (keys & all & conceptEn.keySet).toVector.sorted
  println(s"plan terms: ${all.size}; glossary covers ${all.size - missing.size}; " +
    s"${keys.size} plain-string keys in ${table.split('/').last} " +
    s"(triple-quoted ones are not concept terms and are not read); " +
    s"uncovered: ${uncovered.size}; redundant: ${redundant.size}")
  uncovered.foreach(t => println(s"UNCOVERED\t$t"))
  redundant.foreach(t => println(s"REDUNDANT\t$t"))
