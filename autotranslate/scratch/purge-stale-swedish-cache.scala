//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8

// Purge STALE Swedish->Swedish fallback rows from the mirror's translation caches, so a subsequent
// MODEL-ENABLED run re-translates them into real English. A fallback row has an identical Swedish key and
// value (key == value) — it was "translated" when no model backend was reachable and then cached as its own
// Swedish, so it renders Swedish forever (a cache HIT on the frozen Swedish), even in a model-enabled build.
// Purging it turns the unit back into a cache MISS, which the model then translates. (See issue #960, classes 3+4.)
//
// !!! DO NOT COMMIT A PURGED CACHE WITHOUT A PAIRED MODEL RUN !!!
// Purge + re-translate is ATOMIC and must happen on a MODEL-ENABLED machine. The purged units become cache
// MISSES; with no model reachable a miss falls back to Swedish, so:
//   * the compendium-en.pdf looks the SAME (those units were already Swedish as stale fallbacks), BUT
//   * live fallbacks jump from ~9 to ~684, which BREAKS the cache-only CI prose-leaks ratchet
//     (baseline = the live-measured 9, commit 8978ed62), and leaves the committed cache worse for model-free builds.
// Correct workflow (all before committing):  --write  ->  run --all WITH Ollama/modly  ->  review  ->  commit.
// If no model is reachable, do nothing here. (Alternative to a targeted purge: `run --all --clean` drops the whole
// cache and re-translates everything with the model — simpler but ~15k rows instead of the ~684 stale ones.)
//
// SAFE by construction: only rows where BOTH columns are equal AND the text is Swedish are removed; a row whose
// key != value (a real translation) is never touched. Detects Swedish by å/ä/ö OR a whole-word Swedish function
// word (och/är/… — catches diacritic-free Swedish like "Typerna … och … är … typen"). Over-detection at worst
// re-translates a row that was already fine; under-detection just leaves a known leak. DRY RUN by default.
//
//   scala-cli run autotranslate/scratch/purge-stale-swedish-cache.scala -- .           # report only (safe anywhere)
//   scala-cli run autotranslate/scratch/purge-stale-swedish-cache.scala -- . --write   # apply — ONLY as step 1 of the
//                                                                                       # atomic workflow above
// Then, with Ollama/modly reachable:
//   sbt "autotranslateProject/run --all"    # re-translates the purged units
//   git diff autotranslate/translate-*cache.tsv   # review the new English, then commit

import scala.util.matching.Regex

@main def purgeStaleSwedishCache(args: String*): Unit =
  val root  = os.Path(args.find(a => !a.startsWith("--")).getOrElse("."), os.pwd)
  val write = args.contains("--write")
  val caches = Seq("autotranslate/translate-cache.tsv", "autotranslate/translate-code-cache.tsv")

  // distinctive Swedish tokens — none is an English word, so on the key==value subset a match means Swedish.
  val swWord: Regex =
    raw"(?i)\b(och|är|för|till|inte|eller|att|som|kan|ska|med|även|denna|detta|dessa|vilket|respektive|samt|används|görs|finns|samma|typen|typerna|subtyp|subtyper|bastyp|supertyp|värde|sträng|heltal)\b".r
  def isSwedish(s: String): Boolean =
    s.exists(c => "åäöÅÄÖ".contains(c)) || swWord.findFirstIn(s).isDefined

  var grandStale = 0
  for c <- caches do
    val f = root / os.SubPath(c)
    if !os.exists(f) then println(s"SKIP (missing): $c")
    else
      val lines = os.read.lines(f).toVector
      val (stale, keep) = lines.partition { l =>
        val a = l.split("\t", -1)
        a.length == 2 && a(0).nonEmpty && a(0) == a(1) && isSwedish(a(0))
      }
      grandStale += stale.size
      println(s"\n$c")
      println(s"  ${lines.size} rows; ${stale.size} stale Swedish key==value fallbacks to purge")
      stale.take(6).foreach(l => println("    purge: " + l.split("\t", -1).head.take(78)))
      if stale.size > 6 then println(s"    … +${stale.size - 6} more")
      if write then
        os.write.over(f, keep.mkString("\n") + (if keep.nonEmpty then "\n" else ""))
        println(s"  WROTE ${keep.size} rows back (removed ${stale.size}).")

  println(s"\n=== ${if write then "PURGED" else "would purge"} $grandStale stale Swedish fallback row(s) across ${caches.size} caches ===")
  if !write then println("(dry run — re-run with --write to apply, then run the mirror WITH the model and commit the refreshed caches)")
  else println("Next: run `sbt \"autotranslateProject/run --all\"` with Ollama/modly reachable, then review + commit the new translations.")
