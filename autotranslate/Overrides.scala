/** Manual translation overrides — the HIGHEST-precedence layer of the autotranslator.
  *
  * An entry here beats BOTH the model cache AND a fresh model translation, so this is THE place to
  * correct bad English. Because it is ordinary Scala, the compiler validates every entry: a
  * malformed line fails the BUILD, not silently at run time (unlike a hand-edited .tsv where a
  * tab turned into spaces just vanishes).
  *
  * ── HOW TO ADD A FIX ────────────────────────────────────────────────────────────────────────
  *   1. Spot the bad English in compendium-en.pdf or slides-en.
  *   2. Find the matching Swedish in the SOURCE (compendium/ or slides/): the text of one
  *      translation unit — a paragraph, the text of a `\item` bullet, or a heading's text.
  *   3. Add a line to the map below:   swedishKey -> englishValue
  *   4. Rebuild:  sbt "autotranslateProject/run --all" ; sbt pdfCompendiumEn
  *      Your line now wins over the model.
  *
  * ── STRINGS & BACKSLASHES (LaTeX!) ──────────────────────────────────────────────────────────
  *   Prefer PLAIN triple-quoted strings — backslashes are LITERAL, no escaping, and (verified on
  *   Scala 3.8) there is no `\u` gotcha, so even `\underline`/`\usepackage` are safe:
  *         """Se \Emph{rutan}""" -> """See the \Emph{box}"""
  *   A normal "..." string needs EVERY backslash doubled:        "\\Emph{x}"
  *   An s"""...""" interpolator ALSO needs doubled backslashes (the `s` processes escapes even in
  *   triple quotes) — so avoid `s"..."` here unless you genuinely need to inject a value.
  *
  * ── KEY / VALUE ─────────────────────────────────────────────────────────────────────────────
  *   KEY   = the CLEAN Swedish of ONE unit, exactly as in the source, trimmed. Plain Swedish —
  *           NOT the internal __C0__ placeholder form.
  *   VALUE = the English you want, used verbatim (may contain LaTeX commands).
  *   Duplicate keys are a compile error (Map literal) — good, it stops accidental clashes.
  */
object Overrides:
  type Swedish = String
  type English = String

  val overridingTranslations: Map[Swedish, English] = Map(
    // ── week 1: standalone concept words the model mistranslated ────────────────────────────
    "alternativ"          -> "selection",
    "repetition"          -> "repetition",
    "identifierare"       -> "identifier",
    "slumptal"            -> "random number",
    "dod: operativsystem" -> "dod: operating system",
  )
