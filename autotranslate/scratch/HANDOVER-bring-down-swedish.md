# Handover: bring down Swedish in the English mirror (by adding to Overrides.scala)

Run your agent **from this introprog repo root** (so plain `git`/`sbt`/`scala-cli` work, no `cd`). Read this
whole file, then `autotranslate/{Main,Translate,Overrides,Latex,Code}.scala` to orient, then start the loop.

## Goal & policy
Drive **reader-facing (prose) Swedish** in the English mirror toward 0% by ADDING entries to
`autotranslate/Overrides.scala`. You (the agent) translate far better than the local model, so a
hand-authored override both KILLS Swedish and beats the model fallback. **Prioritize killing Swedish over
perfect English** — this is a beta English edition; a slightly stiff English line beats a Swedish one.

**Swedish inside CODE is ACCEPTED residual** — identifiers (`gurklängd`, `slumpgrönsak`) and string
literals (`"Hej på dej"`, `s"Dubbla värdet av $x är…"`) inside `\begin{Code}`/listings or `.scala`/`.java`
examples. Do NOT try to override code. The `*En` build tasks already report where it is (see below) for a
later human source pass. Focus overrides on PROSE: bullets, headings, sentences, study advice, goals.

## How the autotranslator works (just enough)
Precedence: **OVERRIDE > authoritative(glossary) > cache > model**. `Overrides.overridingTranslations` is a
`Map[Swedish,English]`:
- **KEY** = the CLEAN Swedish of ONE translation unit, restored + trimmed, EXACTLY as it appears (copy it
  verbatim from the dump — see step 2). Plain Swedish, not the internal `__C0__` form.
- **VALUE** = the English you want, used VERBATIM (may contain LaTeX like `\Emph{...}`/`\code{...}`).
- Use `"""triple quotes"""` (backslashes literal — `\Emph` is safe). Only use `"..."` (with real `\n`/`\t`)
  when the key contains an actual newline/tab (rare; block-comment units).
- Duplicate key = compile error (good — catches clashes).
A full `--all` run is **model-free and deterministic** (cache complete → `model calls: 0`). Adding an
override only changes that unit; re-running `--all` re-applies all overrides.

## The loop (one iteration)
Let `ROOT=/home/bjornr/git/hub/lunduniversity/introprog` (your cwd).
1. **Pick a target** from the per-file list (also lists code files):
   `scala-cli run autotranslate/scratch/at.scala -- $ROOT --swedish-left`
   Start with the biggest PROSE offenders, e.g. `compendium-en/modules/w09-setmap-exercise`,
   `w10-inheritance-exercise`, `w06-patterns-exercise`, `slides-en/body/lect-w05-classes`.
2. **Ground truth = the rendered PDF**, NOT `--sweep-fallbacks` (it OVER-reports — flags already-English
   units; see the caveat below). Use the build auto-report, or:
   `scala-cli run autotranslate/scratch/at.scala -- $ROOT --pdf-swedish slides-en/lect-w01-en.pdf`
   It prints `X% Swedish` (or `0% — fully English ✅`) and writes `swedish-<deck>.txt` = the real lines.
3. **Find each Swedish prose unit in the SOURCE** (`compendium/modules/*.tex` or `slides/body/*.tex`; slide
   goals etc. are `\input` from `../compendium/` which the translator now auto-redirects to the `-en`
   mirror). Locate with the typed tool (absolute dir; one bare command):
   `tt text grepr $ROOT/compendium tex '<distinctive Swedish phrase>'`
4. **Append to `autotranslate/Overrides.scala`**: `cleanSwedishKey -> "good English"`. Copy the KEY
   verbatim from the dump. Keep inline `\Emph{}`/`\Alert{}`/`\code{}` in BOTH key and value.
5. **Regenerate + verify:**
   `scala-cli run autotranslate/scratch/at.scala -- $ROOT --all --model gemma2:9b`  (must stay `model calls: 0`)
   `scala-cli run autotranslate/scratch/slides-en.scala -- $ROOT`  (must be `19 PASS, 0 FAIL`)
   `scala-cli run autotranslate/scratch/at.scala -- $ROOT --swedish-left`  (confirm the % dropped)
6. **Commit + push** (plain git — cwd is introprog): `git commit autotranslate/Overrides.scala -m "…" ; git push`
7. Repeat.

## Exact-key gotchas (the #1 cause of "my override didn't apply")
- The key must equal the unit's clean Swedish exactly. The `SV |…` line in the dump IS that string — copy
  it (turn each `⏎` into a real newline only for `"..."`-style keys).
- **Trailing-structure peel:** a trailing inline command's closing `}` (and trailing `\item`/`\\`) is peeled
  as structure, so a key may legitimately END MID-COMMAND (e.g. `… \Emph{Lära genom att göra!` with no
  closing brace). That's correct — it's auto-restored. Trust the dump.
- If an override "didn't take", re-run step 2 — if the unit still shows Swedish in the PDF, your key differs;
  diff it against the dump char by char.

## ⚠ `--sweep-fallbacks` OVER-REPORTS
It exists (`--all --sweep-fallbacks` → writes `autotranslate/scratch/override-suggestions.txt` with clean
keys), but after the two-tier emphasis fix its check uses pre-child-translation spans, so it flags many
units that are ALREADY English. Treat it as *candidates only*; confirm against the rendered PDF
(`--pdf-swedish`) before overriding. **Optional first task that makes the whole loop trustworthy:** fix
`Translate.translateBlock`'s `dumpFallbacks` capture to test the post-`finalSpans` restore.

## Priorities / don'ts
- Most-seen first: `w01`, `simple/intro`, then the biggest files from `--swedish-left`.
- **Do NOT** hand-override `slides/generated/wNN-overview-generated.tex` (concept-term checklist tables);
  fix those at the glossary / `plan` generator instead.
- A redirect now flows `../compendium/` module content into slides, so a compendium-module override counts
  double (compendium-en AND slides). Prefer overriding shared module files.
- **Pathological dense LaTeX that won't yield to a clean override:** don't fight it — append it to
  `notes/swedish-source-to-simplify.md` and ask BR to simplify the SWEDISH source. BR has offered.
- Stop when the count plateaus on the hard tail / code residual.

## Tools (in `autotranslate/scratch/`, tracked; run with `scala-cli run … -- $ROOT …`)
- `at.scala $ROOT <flags>` — runs the autotranslate project. Flags: `--all`, `--only <substr>`,
  `--swedish-left` (corpus %, per-file incl. code files), `--pdf-swedish <pdf>` (per-PDF %, ground truth),
  `--sweep-fallbacks` (candidate dump), `--dump-overrides`, `--model <name>`.
- `slides-en.scala $ROOT` — builds all 19 decks, PASS/FAIL each (each also auto-reports its Swedish %).
- `build-deck.scala $ROOT <deck>` — build one deck. `build-en.scala` — compendium-en + density.
- The `*En` sbt tasks (`pdfCompendiumEn`, `pdfSlidesEn`) auto-report `X% Swedish` after each PDF and print
  the path to `swedish-<deck>.txt` (the lines to fix, incl. code). `pdftotext` missing → warns, never fails.

## Working discipline (genscalator)
Adopt the method at **https://codeberg.org/bjornregnell/genscalator**:
- Use the typed `tt` toolbox (`tt text grepr/match/count/freq/cols`) instead of raw `grep`/`awk`/`cut`
  (safe-by-design, no confirmation prompts). `tt text grepr` needs an ABSOLUTE dir.
- **One bare command per shell call** — no `cd`, no `&&`/`;` chains, no `> log; echo` scaffolding.
- Log any confirmation-friction event to genscalator's `research/wr-data/` (gated — write only, BR reviews).

## sbt gotcha
`sbt --client` does NOT auto-reload `build.sbt` (it reuses a long-running server). After editing
`build.sbt`, run `sbt --client reload` (or `sbt --client shutdown`) before your next task, or you'll run a
stale task. The `*En` Swedish-% report prints ONE `[swedish] …` line at the very END of the build output
(after all the pdflatex spam) — grep for `[swedish]` if you miss it.

## Commit rules
- Plain `git` (cwd is introprog). **NO agent/Claude/Codex credit** in commit messages. **Push every commit.**
- Generated `compendium-en/`, `slides-en/`, `plan/*-en` are **gitignored** — never commit them. Commit
  `autotranslate/Overrides.scala` (+ `translate-cache.tsv`/`translate-code-cache.tsv` only if they changed).

## Current state (2026-06-27)
- `--all` is deterministic, **0 model calls**. Two-tier inline-emphasis masking + `../compendium`→`-en`
  redirect are in place. w01 ≈ **10.9%** Swedish; corpus ≈ **14.7%** (`--swedish-left`). All 19 decks build
  clean. Slides ship to fileadmin (BR runs `publish.sh`); the GitHub release holds ONLY the compendium PDFs
  — do NOT add slide PDFs there.

## Your first move
Read the files above, run `--swedish-left`, pick the largest PROSE file, do iteration 1, and **show your
plan before committing**.
