//> using scala 3.8.4
//> using dep com.lihaoyi::os-lib:0.11.8

// Confirmation-safe extractor for the override grind: print the clean SV override keys for units in a
// given file (substring match) from scratch/override-suggestions.txt — no grep-pipe chains / for-loops.
// By default prints only SINGLE-LINE keys (no ⏎, the override-friendly ones); pass "all" as 3rd arg for
// every key. Usage:
//   scala-cli run autotranslate/scratch/sweep-keys.scala -- <introprog-root> <file-substring> [all]
@main def run(root: String, fileSubstr: String, rest: String*): Unit =
  val mode = if rest.contains("all") then "all" else "single"
  val lines = os.read.lines(os.Path(root) / "autotranslate" / "scratch" / "override-suggestions.txt")
  var i = 0
  var n = 0
  while i < lines.length - 1 do
    val l = lines(i)
    if l.contains("sv-fallback |") && l.contains(fileSubstr) then
      val sv = lines(i + 1)
      val mark = sv.indexOf("SV |")
      if mark >= 0 then
        val key = sv.substring(mark + 4)
        if mode == "all" || !key.contains("⏎") then { println(key); n += 1 }
    i += 1
  println(s"--- $n keys for '$fileSubstr' (${if mode == "all" then "all" else "single-line only"}) ---")
