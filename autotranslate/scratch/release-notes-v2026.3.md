## What's new since v2026.2

Refreshes the Swedish compendium and the **beta English autotranslation**, rebuilt with
multi-pass pdflatex so tables of contents and cross-references resolve correctly.

**Highlights**
- English autotranslation now also translates **in-code comments and strings** in code examples
  (HD2 hybrid), guarded by a sound **allowlist leak-gate** that flags any residual Swedish rather
  than silently passing it.
- Added English **print-split** compendia: `compendium1-en.pdf` and `compendium2-en.pdf`
  (alongside the screen-optimized `compendium-en.pdf`).
- Numerous hand-authored prose-translation overrides across lectures, exercises, and labs;
  assorted heading/macro leak fixes in `compendium.cls`.

**Assets:** the 6 compendium PDFs (Swedish `compendium`/`compendium1`/`compendium2` + English
`-en` variants). Slide PDFs are published on the course fileadmin site, not attached here.

**Full Changelog:** https://github.com/lunduniversity/introprog/compare/v2026.2...v2026.3

### Full commit list (67 commits, v2026.2..v2026.3)

- AT: wire HD2 hybrid into Main.mirror + build-safe non-ASCII guard - corpus-wide code-env translation, both compendium EN green (207e6029)
- AT: extend swedishWords with diacritic-free comment words (definierar/likhet/ger/alltid/referens...) so the HD2 hybrid catches them - lect-w06-matching now 9/9 code comments translated (d0dd8eaf)
- AT: HD2 hybrid - translateCodeEnvBodies (Code.translate on inline code-env comments/strings) + --codeenvtest (79533428)
- AT: allowlist - add java/lib package parts + math abbrevs (awt/div/nom/denom/...) from fall-through review (60dfbdfe)
- AT: allowlist leak gate for apply-b0d (BR-approved) + fall-through vocabulary report (7c4cc24e)
- AT: draft animals-cluster glossary for BR ratification (w10-inheritance + w11 slides) (8a574aa3)
- AT: stricter apply-b0d leak gate (camelCase split + Swedish stems) - necessary, not sufficient (d0fc596a)
- AT: bilingual vego code in w10-inheritance-exercise (33 \ifswedish clamps) (666820d8)
- AT: harden swedishish gate against diacritic-free Swedish in code comments (724bf2d2)
- AT: mark cards cluster BR-RATIFIED (sameColourSuit, Färg->Suit); note Färg->Suit is cards-context-only (2707bcaf)
- AT: cards parafärg en->sameColourSuit (was partnerSuit) (4e98d188)
- AT: fused B0+D cards cluster — w06-patterns now fully bilingual code (8.2%->7.9% Swedish) (f05afb1d)
- AT: fused B0+D vego cluster — bilingual code clamps in w06-patterns (sv byte-identical, en shows English) (b1b94d0d)
- autotranslate: fix cover leaks (Kompendium/laesperioden) + clamp 2.1.4 vector slide (4ff93a0a)
- 2 more w01-intro-exercise prose overrides (innermost call, plain-text operator variants) (23f07afc)
- 3 more w06-patterns-exercise prose overrides (parafärg, vector match, Gurka equals) (c6dda048)
- 7 w12-extra-exercise prose overrides (concurrency, threads, atomicity, Akka/Future) (427bd9f0)
- 6 w10-inheritance-lab prose overrides (snake-game group lab: monsters, scoring, requirements) (e72d20a9)
- 11 w01-intro-lab prose overrides (terminal commands + Kojo square/grid abstractions) (27644808)
- 3 w02-programs-exercise prose overrides (github code, top-level statements, rock-paper-scissors) (c2770e71)
- 4 lect-w09-collections prose overrides + README modly link (95b4a7c7)
- 9 w03-functions-exercise prose overrides (anonymous functions, lambda, HOF, call-by-value) (326d5d9a)
- fix 3 more compendium.cls macro leaks: DoExercise, ReadTheLab, Lösn. uppg. solution label (191bf78b)
- fix Mål/Förberedelser/Kontrollfragor heading leaks (compendium.cls) + README: glossary ground-truth, Eng/Emph/Alert, deps (ee3e69ad)
- lect-w10-override: 3 ifswedish prose clamps (ADT slide intros) (9f549cc5)
- gauge: exclude ALL \ifswedish SV interiors (inline + multi-line) + report clamp count (c918c0ed)
- lect-w02-codestruct: 6 ifswedish prose clamps (multi-line tail, between code blocks) (4b29500d)
- lect-w01-intro: 10 \ifswedish prose clamps (multi-line tail) + sweep-keys multi mode (d518a60f)
- 10 w04-objects-exercise prose overrides (Underjorden/Mole-Worm exercise, dialogs, do-while) (64b8f797)
- 9 lect-w04-objects slide prose overrides (methods, state, packages, lazy, jar) (2772fb13)
- 20 lect-w10-extends slide prose overrides (inheritance, traits, composition) (6cdc7b27)
- 42 slide prose overrides: lect-wjava-body (13), lect-w01-about (14), lect-w05-classes (15) (410e5b39)
- 5 lect-w10-override slide prose overrides (override keyword, product/sum types) (9795e825)
- 7 lect-w02-codestruct slide prose overrides + scratch/sweep-keys.scala (key extractor) (e855df80)
- 11 w07/w10 exercise prose overrides + scratch/sbt-task.scala (confirmation-safe sbt runner) (2c1d9999)
- translate 'Ledtråd'->'Hint' (\hint macro + inline labels) + 10 w06-patterns prose overrides (e7ba56be)
- translate \difficulty 'Svårighet:' label (compendium.cls) + 8 w01-intro prose overrides (84113e80)
- README: document placeholder-normalized cache (§5 gotcha + §7.7) — edits no longer cascade (c0e20895)
- autotranslate: placeholder-normalized cache keys — body \ifswedish clamps no longer cascade (8ef61e9e)
- compendium.cls: hard-space (~) after \fi in clamped exercise/lab headings (d8f963ba)
- translate exercise/lab/task headings (compendium.cls) + clamp w01 De Morgan / expr-or-stmt / dod-delen (85551803)
- autotranslate: w01 literals table + VM-not-JVM + En-variabel + w07 overrides; model-call diagnostic (7364f89f)
- autotranslate/README: sbt run args + Swedish-only build/CI note + reviewer deep-dive (5227849f)
- autotranslate: fix garbled language list (proper-noun overrides) + resurstid -> Tutorials (506a1ffa)
- tikz figures: clamp Swedish prose nodes with \ifswedish for the English build (935754a3)
- autotranslate: 13 hand-authored overrides for lect-w02-codestruct slide prose (6e2ad13b)
- autotranslate: 10 hand-authored overrides for w06-patterns-exercise prose (6d817b27)
- autotranslate: --sweep-fallbacks captures ALL files (reliable override keys) + 12 w01-intro-exercise overrides (9ba259c4)
- autotranslate: --swedish-left gauge skips \ifswedish SV branches + exlatex/envi (f679945b)
- autotranslate: fix compendium build break + recover dod:latex prose (translateTex gate, env-masking, \n\n guard) (02457f5a)
- gitignore: plan/*-generated-en.tex (covers module-plan-generated-en, regenerated by gen+enChrome) (6d5cda06)
- autotranslate: regenerate cache under verbatim-env masking fix (restores 0-model-call) (0ad03e39)
- vscode: enable metals.startMcpServer (Metals MCP for compile/diagnostics) (f9b8767e)
- autotranslate+plan: verbatim-env masking fix, honest progress, module-plan-en, prose-gauge tools (45f07124)
- add conditional exclusion of two slides (d89b84e9)
- autotranslate: add --swedish-lines <file> to dump fixable Swedish prose of one -en file (spotting tool) (d45b3b49)
- autotranslate: de-noise --swedish-left to count fixable PROSE only (7299fa31)
- autotranslate: use official course name 'Introduction to programming' (not 'Programming, introductory course') (7106018c)
- autotranslate: redirect ../global-constants.tex to -en mirror + add pdfCompendium1En/2En tasks (bd2467ee)
- fix typo (93888925)
- refactor; lift title into global constants (398b9edc)
- fix translation Uppgift -> Task (b3b2738f)
- include explanation translations (f627a33a)
- improve translation of front matters (dddf59ce)
- add new chromePhrase (fa3e886e)
- scratch handover: note sbt --client reload gotcha + where the [swedish] report prints (8c8b5815)
- autotranslate: fix 'Om' headings mistranslated as 'If' — Part 'About the course' + section 'About your learning' (ec582b3b)
