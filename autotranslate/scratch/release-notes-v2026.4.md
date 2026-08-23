## What's new since v2026.3

Refreshes the **beta English autotranslation** and rebuilds every compendium with multi-pass pdflatex so tables
of contents and cross-references resolve. The Swedish compendium is unchanged (byte-identical source).

**Highlights**
- **English print-pair cross-references now resolve.** `compendium1-en.pdf` and `compendium2-en.pdf` previously
  rendered `??` for every reference pointing across the two print volumes (appendices, exercises, chapters),
  because the autotranslation copied the `\externaldocument{…}` (`xr`/`xr-hyper`) cross-document link verbatim
  instead of retargeting it to the `-en` mirror. Fixed — the English print pair now cross-references correctly.
- **Cleaner English mirror.** Prose-leak gauge improvements, recovery of ~50 previously-Swedish prose lines,
  dense-tabular row/bullet splitting (about −15 % residual Swedish), and build-safety guards (reject
  model-introduced LaTeX environments and non-ASCII).
- **Localised culture content for the English edition.** The Fyle (Scanian) bird-taxonomy exercises in w10 are
  re-themed to a Welsh village for English readers, and the w10 inheritance-terminology table is Englished — all
  via fine-grained `\ifswedish` clamps, so the Swedish edition stays byte-identical.

**Assets:** the 6 compendium PDFs — Swedish `compendium` / `compendium1` / `compendium2` plus the English `-en`
variants. Slide PDFs are published on the course fileadmin site, not attached here.

**Full Changelog:** https://github.com/lunduniversity/introprog/compare/v2026.3...v2026.4

### Full commit list (19 commits, v2026.3..HEAD)

- autotranslate: retarget externaldocument cross-doc refs to the -en mirror so the EN print pair resolves its cross-volume references (were dangling appendix refs in compendium1-en); add a 3-pass EN compendium build script (0be79041)
- w10 exercise: anglicize the Fyle bird tasks for the EN edition (Welsh-village re-theme) via fine-grained ifswedish clamps (232d17c7)
- Revert "w10 exercise: anglicize the Fyle bird taxonomy for the EN edition via big ifswedish clamp (task 1)" (c93601f5)
- Revert "w10 exercise: anglicize final/Simkråga (task 2) + Pjodd (task 4) bird tasks via ifswedish clamps" (935b4d91)
- w10 exercise: anglicize final/Simkråga (task 2) + Pjodd (task 4) bird tasks via ifswedish clamps (4fd59dc3)
- w10 exercise: anglicize the Fyle bird taxonomy for the EN edition via big ifswedish clamp (task 1) (576c16a6)
- autotranslate: drop 'monster' from Code.swedishWords (English homograph) (7e97af05)
- autotranslate: prose-leak gauge — strip standalone runs of aao incl. UPPERCASE (4eaa4db7)
- autotranslate/scratch: add build-compendiums.scala (all SV+EN compendium PDFs, 2 passes) (13d7ac62)
- autotranslate: skip LaTeX %-comment lines in code-env translation (Code.translate skipTexComments) (6c205c74)
- autotranslate: add --prose-leaks-dump corpus-wide leak-line report (a5720d57)
- autotranslate/scratch: add --en-only flag to build-release (skip invariant SV artifacts) (88fdce08)
- autotranslate: split dense tabular rows/bullets into per-cell units + reject introduced non-ASCII (EN mirror -15% Swedish, build-safe) (5e175a24)
- autotranslate: reject model-introduced LaTeX environments (build-safety guard) (87fd8012)
- AT scratch: preserve reusable orchestration scripts + v2026.3 release notes (23146b62)
- AT: \ifswedish-clamp a disabled w04 slide that flakily broke the EN compendium build (3a90ac23)
- AT: clamp w10 inheritance-terminology table with \ifswedish\else English table (cb0efe9c)
- AT: retry Swedish fallbacks on GPU model - recover ~50 prose lines, clean 233 dead cache entries (aacc79a9)
- AT: add --prose-leaks discriminator (true fixable prose vs deferred code identifiers) (882298cd)
