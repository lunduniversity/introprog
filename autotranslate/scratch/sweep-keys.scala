//> using scala 3.8.4
//> using dep com.lihaoyi::os-lib:0.11.8

// Confirmation-safe extractor for the override grind: print the clean SV override keys for units in a
// given file (substring match) from scratch/override-suggestions.txt — no grep-pipe chains / for-loops.
// Modes (3rd arg): "single" (default) = only single-line keys (override-friendly); "all" = every key;
// "multi" = only MULTI-LINE units (the \ifswedish-clamp candidates), with ⏎ rendered as real newlines and
// a separator, for readability. Usage:
//   scala-cli run autotranslate/scratch/sweep-keys.scala -- <introprog-root> <file-substring> [single|all|multi]
@main def run(root: String, fileSubstr: String, rest: String*): Unit =
  val mode = if rest.contains("all") then "all" else if rest.contains("multi") then "multi" else "single"
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
        val multi = key.contains("⏎")
        val show = mode match { case "all" => true; case "multi" => multi; case _ => !multi }
        if show then
          n += 1
          if mode == "multi" then println(s"\n[$n] ${key.replace("⏎", "\n")}\n---")
          else println(key)
    i += 1
  println(s"--- $n keys for '$fileSubstr' (mode=$mode) ---")
