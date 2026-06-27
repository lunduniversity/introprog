//> using scala 3.8.4
//> using dep com.lihaoyi::os-lib:0.11.8

// List all (brace-balanced) usages of a macro across the Swedish source, to judge whether its arg is
// PROSE (translate-arg) or NON-prose (maskWhole). Run from introprog/:
//   scala-cli run autotranslate/scratch/find-macro.scala -- WHAT

@main def run(macroName: String): Unit =
  val dirs = List(os.pwd / "compendium", os.pwd / "slides")
  val files = dirs.flatMap(d => os.walk(d)).filter(p => p.ext == "tex" && os.isFile(p))
  val open = s"\\$macroName{"
  var n = 0
  for f <- files do
    val t = os.read(f)
    var i = t.indexOf(open)
    while i >= 0 do
      // brace-balanced extract of the argument
      var depth = 0; var j = i + open.length - 1; var done = false
      while j < t.length && !done do
        t(j) match
          case '{' => depth += 1
          case '}' => depth -= 1; if depth == 0 then done = true
          case _   =>
        j += 1
      val arg = t.substring(i + open.length, j - 1)
      n += 1
      if n <= 25 then println(f"  ${f.last}%-34s ${arg.take(70).replace("\n", " ")}")
      i = t.indexOf(open, j)
  println(s"--- \\$macroName: $n usages ---")
