//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8

// One-off sweep: bare `\fi ` followed by a word letter GLUES in the rendered PDF because TeX
// consumes the space after the control word \fi (specimens: "Suitis", "Suitagain", "tomatoin",
// "Gurkaär" — BOTH editions). apply-b0d emits `\fi{}` since 1a024804; this repairs the clamps
// emitted BEFORE that fix (and hand-written ones) in the shared sources: `\fi word` -> `\fi{} word`.
// Prints per-file replacement counts; changes nothing else. Run bare with the introprog root:
//   scala-cli run autotranslate/scratch/fix-fi-glue.scala -- /abs/introprog
@main def fixFiGlue(rootStr: String): Unit =
  val root = os.Path(rootStr)
  val files = Seq(
    root / "compendium" / "modules" / "w06-patterns-exercise.tex",
    root / "compendium" / "modules" / "w10-inheritance-exercise.tex",
    root / "compendium" / "prechapters" / "preface.tex",
    root / "slides" / "body" / "lect-wjava-body.tex",
  )
  val glue = """\\fi (?=[A-Za-zÅÄÖåäö])""".r
  var total = 0
  for f <- files do
    val before = os.read(f)
    val n = glue.findAllIn(before).size
    if n > 0 then
      val after = glue.replaceAllIn(before, java.util.regex.Matcher.quoteReplacement("\\fi{} "))
      os.write.over(f, after)
    println(s"  ${f.relativeTo(root)}: $n replaced")
    total += n
  println(s"total: $total bare-fi glue sites repaired")
