# autotranslate/scratch — driver + helper scripts

Purpose-built `scala-cli` drivers for the introprog **autotranslate (AT)** work: build the English mirror,
scan for residual Swedish, and cut releases. They exist to run the `autotranslateProject` and the LaTeX builds
**from the introprog root without a `cd`** — you pass the repo root as an argument instead.

## The one convention: absolute paths, no `cd`

Every script takes the **introprog root** and runs from there. Invoke with an **absolute path**; never `cd` into a
subdir first. This is the whole reason these thin drivers exist.

```
scala-cli run autotranslate/scratch/at.scala -- <introprog-root> <AT-args…>
# e.g.
scala-cli run autotranslate/scratch/at.scala -- /abs/introprog --workspace-en --only irritext
```

`at.scala` runs `sbt --client "autotranslateProject/run <args>"` in the given root (it buffers and prints the
output at completion — no live progress mid-run), so it is the way to invoke **every AT flag** below without a `cd`.

## Scripts

| Script | What it does |
|---|---|
| `at.scala <root> <args…>` | Thin driver for `autotranslateProject/run` — the entry point for all AT flags below. |
| `slides-en.scala <root>` | Build every EN slide deck via `pdfSlidesEn`; reports per-deck PASS/FAIL + total. |
| `build-en.scala` / `build-compendiums.scala` | EN (and SV+EN) compendium builds, with an error + Swedish scan. |
| `build-release.scala` | Release build; `--en-only` skips the invariant SV artifacts. |
| `swedish-scan.scala` | Swedish-density scan of a **built PDF** (replaces the removed `pdf-swedish.scala`). |
| `sweep-keys.scala` | List translation candidates (e.g. `… multi` for multi-line units). |
| `verify.scala` / `verify-step2.scala` | Post-build verification steps. |
| `compose-release-notes.scala` | Assemble release notes. |
| `typo-scan.scala` | Typo scan. |

(Other `*.scala` here are one-off analysis/scratch tools; the ones above are the maintained entry points.)

## AT flags (passed through `at.scala` to `autotranslateProject/run`)

`--all` · `--only <substr>` · `--retry-fallbacks` · `--model <name>` · `--prose-leaks [file]` ·
`--prose-leaks-dump` · `--en-only` · `--dryrun` · `--clean` · `--latextest` · `--workspace-en`

- **`--all`** is safe + deterministic: a cache-hot run makes ~1 model call and takes a few seconds. Regeneration
  churn is near-zero when the cache is warm.
- **`--prose-leaks [file]`** regenerates the residual-Swedish corpus (`prose-leaks-corpus.txt`).

## Models (only needed to REGENERATE, not for cache-hot builds)

Regeneration calls a local model server (BR's setup: `http://bjornyx.local:8080`, "modly"):
`qwen2.5:7b` (default) · `gemma2:9b` (dense) · `aya-expanse:8b`. A **cache-hot** build needs no model — the
translation cache carries prior results; only genuinely-uncached units hit the model.

## Key source (`autotranslate/`)

`Main.scala` (the mirror: `enChrome` + `moduleNames` + `chromePhrases`, `setEnglishFlags`, `rewriteInputs`,
`redirectListings`, arg dispatch) · `Translate.scala` · `Code.scala` (`swedishish`, public) ·
`Overrides.scala` (cache overrides) · `Latex.scala` (`maskWhole`, `segmentMasked`).

## Conventions & guardrails

- **Push on every commit** (the build box runs hot). Use bare `git -C <abs-path> …` — no `cd` / `&&`.
- **Do not track the generated EN side** — `compendium-en/`, `slides-en/`, `plan-*-en` (.tex + PDF) are
  gitignored and regenerate from the cache. `cover-en` and `workspace-en` are **static committed mirrors**, not
  autotranslated.
- **The `\ifswedish{sv}\else{en}\fi` clamp keeps the SV edition byte-identical** — after any clamp, verify the SV
  build is unchanged.

## Do not touch

- **Legacy `compendium/{assignments,exercises,lectures,solutions}.tex`** — live `build`/CI targets and the
  `%!TEX root` for exercise modules. Do not delete.
- **`w01_kojo` workspace-en** — an English-Kojo dependency special case, deferred.
