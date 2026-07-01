//> using scala 3.8.4
//> using dep com.lihaoyi::os-lib:0.11.8

// Compose the v2026.3 GitHub release notes: a short human summary + the FULL commit list from
// git log v2026.2..v2026.3 (gh --generate-notes only emits the compare link since the work was
// direct commits, no PRs). Writes an in-repo .md (Bash-visible) for `gh release edit --notes-file`.
//   scala-cli run autotranslate/scratch/compose-release-notes.scala -- /home/bjornr/git/hub/lunduniversity/introprog <out.md>
@main def composeReleaseNotes(root: String, out: String): Unit =
  val commits = os.proc("git", "-C", root, "log", "--pretty=format:- %s (%h)", "v2026.2..v2026.3")
    .call().out.text().trim
  val n = commits.linesIterator.size
  val summary =
    s"""## What's new since v2026.2
       |
       |Refreshes the Swedish compendium and the **beta English autotranslation**, rebuilt with
       |multi-pass pdflatex so tables of contents and cross-references resolve correctly.
       |
       |**Highlights**
       |- English autotranslation now also translates **in-code comments and strings** in code examples
       |  (HD2 hybrid), guarded by a sound **allowlist leak-gate** that flags any residual Swedish rather
       |  than silently passing it.
       |- Added English **print-split** compendia: `compendium1-en.pdf` and `compendium2-en.pdf`
       |  (alongside the screen-optimized `compendium-en.pdf`).
       |- Numerous hand-authored prose-translation overrides across lectures, exercises, and labs;
       |  assorted heading/macro leak fixes in `compendium.cls`.
       |
       |**Assets:** the 6 compendium PDFs (Swedish `compendium`/`compendium1`/`compendium2` + English
       |`-en` variants). Slide PDFs are published on the course fileadmin site, not attached here.
       |
       |**Full Changelog:** https://github.com/lunduniversity/introprog/compare/v2026.2...v2026.3
       |
       |### Full commit list ($n commits, v2026.2..v2026.3)
       |
       |""".stripMargin
  os.write.over(os.Path(out), summary + commits + "\n")
  println(s"wrote $out ($n commits)")
