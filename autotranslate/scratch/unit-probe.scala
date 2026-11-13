//> using scala 3.8.4
//> using jvm 21
//> using file ../Latex.scala

// PROBE for the unit-splitter fix (SM286c): for each given .tex file, mask + segment exactly like
// Translate.translateRegion does, then print every unit whose restored text matches the given regex —
// showing the unit's CORE (what the model sees / the cache key base), its CLEAN form (the override
// key translateBlock computes today), and its CLEAN-FULL form (core + trailing placeholder run
// restored). Run BEFORE and AFTER a splitter change to see exactly which units move.
//   scala-cli run autotranslate/scratch/unit-probe.scala -- <regex> <file.tex>...

private val placeRe = raw"__C\d+__".r

// replicas of Translate.scala's private leadLen/trailStart (keep in sync when probing)
def leadLen(b: String): Int =
  var i = 0; var changed = true
  while changed do
    changed = false
    while i < b.length && b(i).isWhitespace do { i += 1; changed = true }
    placeRe.findPrefixMatchOf(b.substring(i)) match
      case Some(m) => i += m.end; changed = true
      case None    => ()
  i

def trailStart(b: String): Int =
  var i = b.length; var changed = true
  while changed do
    changed = false
    while i > 0 && b(i - 1).isWhitespace do { i -= 1; changed = true }
    placeRe.findAllMatchIn(b.substring(0, i)).toList.lastOption match
      case Some(m) if m.end == i => i = m.start; changed = true
      case _                     => ()
  i

def show(s: String): String = s.replace("\n", "⏎")

/** Shorten for overview lines only, and say so: an ellipsis marks every cut. */
def cut(s: String, max: Int = 160): String = if s.length <= max then s else s.take(max) + " …[cut]"

@main def unitProbe(pattern: String, files: String*): Unit =
  val re = pattern.r
  for file <- files do
    val src = String(java.nio.file.Files.readAllBytes(java.nio.file.Path.of(file)), "UTF-8")
    val (masked, spans, itemIdx) = Latex.mask(src, stripEng = true)
    val (blocks, _) = Latex.segmentMasked(masked, itemIdx, Latex.separatorIdx(spans))
    val textBlocks = blocks.count(Latex.hasText)
    println(s"=== $file: ${blocks.size} blocks, $textBlocks with text")
    for (b, k) <- blocks.zipWithIndex if Latex.hasText(b) do
      val restored = Latex.restore(b, spans)
      if re.findFirstIn(restored).isDefined then
        val lead = leadLen(b); val trail = trailStart(b)
        if trail <= lead then println(s"  [block $k] MATCH but no prose core: ${show(cut(restored))}")
        else
          val core = b.substring(lead, trail)
          val clean = Latex.restore(core, spans).trim
          val cleanFull = Latex.restore(b.substring(lead), spans).trim
          println(s"  [block $k]")
          // clean/cleanFull are printed WHOLE (never truncated): `clean` is the override key, and a
          // truncated key that looks complete never matches (#1013). The length makes a cut visible.
          println(s"    core:      ${show(cut(core))}")
          println(s"    clean:     ${show(clean)}  (${clean.length} chars)")
          println(s"    cleanFull: ${show(cleanFull)}  (${cleanFull.length} chars)")
