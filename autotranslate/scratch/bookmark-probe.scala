//> using scala 3.3.4
//> using dep com.lihaoyi::os-lib:0.11.8

/** How many pdftk BOOKMARKS does each compendium PDF carry?
  *
  * WHY THIS EXISTS: `FindHeadings.writeTranslateMap` joins the Swedish and English heading
  * tables on section number, and both tables come from pdftk bookmarks -- so the map is
  * capped by whichever edition has FEWER. The two editions should be within a handful of
  * each other; a large gap means one of them is thin, and a thin edition silently halves
  * muntabot's English labels rather than failing anything.
  *
  * HOW AN EDITION GOES THIN: hyperref embeds bookmarks from the `.out` file left by the
  * PREVIOUS run, and the Swedish tasks run pdflatex ONCE by design (build.sbt:257-259,
  * because compendium/ keeps its .aux/.toc across builds). A build that aborted under
  * -halt-on-error leaves a TRUNCATED .out, and the next single pass embeds only those few.
  * Measured 2026-09-11: compendium.pdf 127 bookmarks vs compendium-en.pdf 982, five days
  * after the Swedish build had been failing. Re-running `sbt pdf` fixed it (982 vs 982).
  *
  * Usage:
  *   scala-cli run autotranslate/scratch/bookmark-probe.scala -- \
  *     compendium/compendium.pdf compendium-en/compendium-en.pdf
  */
@main def run(pdfs: String*): Unit =
  if pdfs.isEmpty then
    println("usage: bookmark-probe.scala -- <file.pdf>...")
  else
    val counts =
      for p <- pdfs yield
        val path = os.Path(p, os.pwd)
        if !os.exists(path) then
          println(s"$p: MISSING -- build it first")
          p -> -1
        else
          val res = os.proc("pdftk", path, "dump_data_utf8").call(check = false)
          if res.exitCode != 0 then
            println(s"$p: pdftk FAILED with exit ${res.exitCode}")
            p -> -1
          else
            val n = res.out.text().linesIterator.count(_.startsWith("BookmarkTitle:"))
            println(f"$p%-44s $n%5d bookmarks")
            p -> n
    val good = counts.map(_._2).filter(_ >= 0)
    if good.size > 1 then
      val (lo, hi) = (good.min, good.max)
      if hi - lo > hi / 10 then
        println(s"\n  WARNING: editions disagree by ${hi - lo} bookmarks ($lo vs $hi).")
        println("  The sv->en heading map is capped by the THINNER one. Rebuild it:")
        println("  run `sbt pdf` again so a second pass reads a complete .out, then `sbt gen`.")
      else println(s"\n  editions agree within ${hi - lo} bookmark(s) -- the join is not capped by a thin edition.")
