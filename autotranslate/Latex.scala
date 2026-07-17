import scala.collection.mutable
import scala.util.matching.Regex
import java.util.regex.Matcher

/** Hand-rolled, brace-balanced LaTeX masking scanner (substitute-in-place).
  *
  * `mask` walks the source left-to-right with a brace-depth counter and replaces everything the
  * model must NOT touch with `__C<n>__` placeholders, leaving only translatable prose between them.
  * `restore` puts the original spans back. Invariant (with stripEng=false):
  *     restore(mask(x)._1, mask(x)._2) == x      // round-trips exactly (verified by --latextest)
  *
  * NOT a parser, NOT regex over the whole document — just a scanner that LOCATES spans. See
  * notes/introprog-autotranslator-plan.md ("the crux"). P1 masks soft wrappers (\Emph, \section…)
  * OPAQUELY (their text stays Swedish); P2 will recurse into them to translate the argument.
  */
object Latex:

  private val Place: Regex = raw"__C(\d+)__".r

  // \code/\jcode/\lstinline/\verb: all DELIMITER-based verbatim (see memory note). \verb is a LaTeX
  // builtin (not \newcommand) used with /, |, + delimiters here — content must be masked verbatim.
  val verbatimInline = Set("code", "jcode", "lstinline", "lstinline*", "verb", "verb*")
  // environments whose whole body is verbatim/non-prose → masked as one block.
  // NB: ALL code/REPL variants must be here. A missing one (e.g. CodeSmall) is NOT masked verbatim, so its
  // Scala `$`-interpolation (`$x`, `${...}`) leaks into the `$…$` math handler → mis-paired `$` swallow a
  // huge prose region as one span (an 11k-char span hid whole \Subtask sentences from translation).
  val verbatimEnvs = Set("Code", "CodeSmall", "REPL", "REPLnonum", "REPLsmall",
    "verbatim", "Verbatim", "lstlisting", "comment",
    // exlatex/envi: custom LaTeX-EXAMPLE environments (the dod:latex lecture shows raw LaTeX source). Not
    // masking them leaks $/\ and the model mangles the example macros. Example source stays verbatim.
    "exlatex", "envi",
    // algorithm (algorithm2e): pseudocode with keyword macros (\SetKwInOut{Input}{Indata}, \Input, \While)
    // — NOT prose. Translating it mangles the macros (\SetKwInOut{Input}{Indata} -> {Input }{ In data } so
    // \Input is never defined -> build break). It was ACCIDENTALLY masked before by a $-runaway from the
    // (then-unmasked) CodeSmall leak; masking CodeSmall exposed it. Mask whole; pseudocode stays Swedish.
    "algorithm",
    // diagram environments: coordinates / node NAMES / options / \foreach vars are NOT prose and
    // translating them breaks the build (a node ref (Subtyp) -> (Subtype) => "No shape named ...").
    // Node TEXT labels stay Swedish — an accepted trade-off for build safety.
    "tikzpicture", "pgfpicture", "tikzcd", "forest")
  // environments whose mandatory {title} arg is a PROSE heading to translate (the slide title shows
  // as a level-3 heading in the compendium). \begin{env} + optional [..] is masked; {title} is not.
  val titleEnvs = Set("Slide", "SlideExtra", "SlideSimple")
  // \item and its introprog aliases (\ii \is \di = \item in the .cls) — treated as list-item
  // boundaries so \ii{...} bullet lists split into SMALL per-item units (reliably translatable)
  // instead of one giant placeholder-dense block that falls back to Swedish.
  // (\ti \ts = \textit are handled by the default scan = translate-arg, no special case needed.)
  val itemCmds = Set("item", "ii", "is", "di")
  // sectioning commands: like titleEnvs, the {arg} is a prose heading that becomes its OWN translation
  // unit (so it translates in isolation AND plain-heading override keys match even when body follows
  // with no blank line). Command + optional [short title] masked; {heading} left for translate-arg.
  val headingCmds = Set("part", "chapter", "section", "subsection", "subsubsection",
    "Section", "Subsection", "paragraph", "ChapterUnnum",
    "WHAT") // \WHAT{desc} = a task title (\def\what{\emph{#1}}); its arg is PROSE -> translate as own unit
  // \Eng{...} renders "(eng. term)" — redundant in English → removed on the en side.
  // Commands masked WHOLE (command + args) — args are NON-prose and must NOT be translated.
  val maskWhole = Set(
    // non-prose args (refs / labels / urls / dimensions / colours):
    "ref", "pageref", "eqref", "autoref", "nameref", "label", "cite", "input", "include", "hyphenation",
    "includegraphics", "scalainputlisting", "javainputlisting", "lstinputlisting", "inputgraphics", "hypertarget",
    "hyperlink", "url", "href", "index", "vspace", "hspace", "vskip", "hskip", "fontsize",
    "setlength", "selectfont", "color", "textcolor", "colorbox", "raisebox", "includepdf",
    "SlideFontSize", "marginnote", "reversemarginpar",
    "texttt", "title", "author", "date", "textsuperscript", // code / proper-noun / symbol — keep verbatim
    "renewcommand", "newcommand", "providecommand", // macro (re)definitions — never translate the body
    // custom introprog macros whose args delegate to NON-prose targets (paths/refs/labels/code/
    // numbers/\def) — audited from compendium.cls + slides/*.cls. Masked WHOLE so the model can't
    // translate/mangle the path/label/id (\SlideImg{title}{PATH} was the empty-filename build break).
    "SlideImg", "file", "Key", "StudyTheory", "DoExercise", "Exercise", "ExerciseSolution",
    "Lab", "Teamlab", "Assignment", "Size", "stars", "setnextsection", "difficulty",
    "ExeRow", "LabRow", "Vecka" // defined in .tex files (plan tables / week links) — non-prose args
  )

  // Inline emphasis wrappers: prose args we DO translate, but mask the WHOLE `\cmd{arg}` as ONE
  // placeholder (not command + `{` + exposed-text + `}` = 3) so a multi-emphasis bullet is not
  // placeholder-dense — the dominant cause of model placeholder drop/reorder → Swedish fallback. The
  // arg is translated as a CHILD unit (see reEmphArg + Translate.translateRegion), so emphasized words
  // still become English (often a glossary hit), in isolation rather than mid-sentence.
  val emphArg = Set("Emph", "Alert", "emph", "textbf", "textit", "textsf", "underline", "sout")

  // TRANSLATE-ARG: these prose-wrapping commands are deliberately NOT in maskWhole. The default scan
  // masks the command + its braces as placeholders and leaves the ARGUMENT TEXT translatable, so
  // emphasized words AND headings become English (e.g. \Subsection{Om programmering} -> \Subsection{About programming}):
  //   \Emph \Alert \emph \textbf \textit \textsf \underline \sout
  //   \section \subsection \subsubsection \Subsection \Section \chapter \paragraph \caption \footnote
  // commands taking 2 mandatory args.
  val argCount = Map(
    "href" -> 2, "textcolor" -> 2, "hyperlink" -> 2, "fontsize" -> 2, "setlength" -> 2,
    "SlideImg" -> 2, "SlideFontSize" -> 2, "DoExercise" -> 2, "difficulty" -> 2,
    "renewcommand" -> 2, "newcommand" -> 2, "providecommand" -> 2
  )

  private def isCmdLetter(c: Char): Boolean = c.isLetter

  /** if s(k)=='{' (optionally after whitespace) return index after the matching '}'; else k.
    * Tolerating whitespace lets a command pick up an argument on the NEXT line, e.g.
    * `\scalainputlisting\n{path}` — otherwise the path leaks out as translatable prose and the
    * model mangles it (Snake.scala -> "Snake(.scala)" => Listings "File not found"). */
  private def skipGroup(s: String, k: Int): Int =
    var p = k
    while p < s.length && s(p).isWhitespace do p += 1
    if p >= s.length || s(p) != '{' then k
    else
      var depth = 0; var j = p
      var done = false
      while j < s.length && !done do
        s(j) match
          case '\\' => j += 1 // skip escaped next char
          case '{'  => depth += 1
          case '}'  => depth -= 1; if depth == 0 then done = true
          case _    =>
        j += 1
      j

  /** if s(k)=='[' (optionally after whitespace) return index after the matching ']'; else k. */
  private def skipOptional(s: String, k: Int): Int =
    var p = k
    while p < s.length && s(p).isWhitespace do p += 1
    if p >= s.length || s(p) != '[' then k
    else { val c = s.indexOf(']', p + 1); if c < 0 then s.length else c + 1 }

  /** read a {name} group's content starting at k (k at '{'); return (content, idxAfter). */
  private def groupContent(s: String, k: Int): (String, Int) =
    if k >= s.length || s(k) != '{' then ("", k)
    else { val end = skipGroup(s, k); (s.substring(k + 1, end - 1), end) }

  /** From index `from` (just after a `\ifX`), return the index just after its matching `\fi`,
    * nesting-aware: a nested `\if...` opens a level, `\fi` closes one, `\else` is irrelevant.
    * `\iff` (the amsmath relation `\Longleftrightarrow`, not a TeX conditional) is NOT counted.
    * Used to mask a whole `\ifswedish...\fi` block verbatim. */
  private def matchFi(s: String, from: Int): Int =
    var i = from; var depth = 1; val n = s.length
    while i < n && depth > 0 do
      if s(i) == '\\' && i + 1 < n && isCmdLetter(s(i + 1)) then
        var j = i + 1
        while j < n && isCmdLetter(s(j)) do j += 1
        val nm = s.substring(i + 1, j)
        if nm == "fi" then depth -= 1
        else if nm.startsWith("if") && nm != "iff" then depth += 1
        i = j
      else i += 1
    i

  /** Mask the source. Returns (maskedText, spans, itemIdx) where itemIdx is the set of placeholder
    * indices that are `\item` markers (used to split bullet lists into per-item translation units).
    * With stripEng=true, `\Eng{...}` is removed. */
  def mask(text: String, stripEng: Boolean, collapseEmph: Boolean = false): (String, IndexedSeq[String], Set[Int]) =
    val out = StringBuilder()
    val spans = mutable.ArrayBuffer[String]()
    val itemIdx = mutable.Set[Int]()
    def protect(span: String): Unit = { out ++= s"__C${spans.size}__"; spans += span }
    val n = text.length
    var i = 0
    var titleCloseAt = -1 // position of the `}` closing a Slide title (titleEnvs) — made a segment
                          // boundary so the title becomes its OWN translation unit (clean override keys)
    while i < n do
      val c = text(i)
      if c == '%' then
        var j = i; while j < n && text(j) != '\n' do j += 1
        protect(text.substring(i, j)); i = j // newline stays as text (paragraph structure)
      else if c == '$' then
        if i + 1 < n && text(i + 1) == '$' then
          val cl = text.indexOf("$$", i + 2); val e = if cl < 0 then n else cl + 2
          protect(text.substring(i, e)); i = e
        else
          val cl = text.indexOf('$', i + 1); val e = if cl < 0 then n else cl + 1
          protect(text.substring(i, e)); i = e
      else if "{}&~#^_".contains(c) then
        // grouping brace, tabular column separator (&), non-breaking space (~), and other bare LaTeX
        // specials → mask as a placeholder so they're preserved verbatim, order-validated, and kept
        // OUT of the prose (otherwise a tabular row's '&' trips the no-specials guard → Swedish).
        // A Slide title's closing `}` is marked as a segment boundary so the title is its own unit.
        if c == '}' && i == titleCloseAt then { itemIdx += spans.size; titleCloseAt = -1 }
        protect(c.toString); i += 1
      else if c == '\\' then
        if i + 1 >= n then { out += c; i += 1 }
        else if !isCmdLetter(text(i + 1)) then
          val sym = text(i + 1)
          if sym == '[' then { val cl = text.indexOf("\\]", i + 2); val e = if cl < 0 then n else cl + 2; protect(text.substring(i, e)); i = e }
          else if sym == '(' then { val cl = text.indexOf("\\)", i + 2); val e = if cl < 0 then n else cl + 2; protect(text.substring(i, e)); i = e }
          else { protect(text.substring(i, i + 2)); i += 2 } // \\ \{ \% \$ \, etc.
        else
          var j = i + 1
          while j < n && isCmdLetter(text(j)) do j += 1
          if j < n && text(j) == '*' then j += 1
          val name = text.substring(i + 1, j)
          name match
            case "begin" =>
              val (env, j2) = groupContent(text, j)
              if verbatimEnvs.contains(env) then
                val endTok = s"\\end{$env}"
                val cl = text.indexOf(endTok, j2); val e = if cl < 0 then n else cl + endTok.length
                protect(text.substring(i, e)); i = e
              else if titleEnvs.contains(env) then
                // mask \begin{env} + optional [..]; leave the mandatory {title} for translate-arg.
                // Mark the title's closing `}` as a segment boundary (titleCloseAt) so the title is
                // translated as its OWN unit — better quality AND lets overrides.tsv-style keys be the
                // plain title text even when slide body content follows with no blank line.
                val k = skipOptional(text, j2)
                if k < n && text(k) == '{' then titleCloseAt = skipGroup(text, k) - 1
                protect(text.substring(i, k)); i = k
              else
                var k = j2 // mask \begin{env} + any immediate env args [..]/{..}
                var moved = true
                while moved do
                  val k2 = skipGroup(text, skipOptional(text, k))
                  moved = k2 != k; k = k2
                protect(text.substring(i, k)); i = k
            case "end" =>
              val (_, j2) = groupContent(text, j); protect(text.substring(i, j2)); i = j2
            case "Eng" =>
              val k = skipGroup(text, skipOptional(text, j))
              if stripEng then i = k // remove entirely
              else { protect(text.substring(i, k)); i = k }
            case "ifswedish" =>
              // Swedish-only block: mask the whole \ifswedish...\fi VERBATIM so its content is never
              // translated. The English build sets \swedishfalse (injected by Main.setEnglishFlags),
              // so pdflatex skips it — and conditional skip-scanning never expands the content, making
              // this a robust escape hatch for build-breaking constructs. An optional \else branch
              // (English replacement) rides along verbatim and shows on the English side.
              // Absorb a trailing `{}` (the space-guard emitted by inline clamps, since `\fi` is a control
              // word and TeX would swallow a following space -> "Xoch"). Keeping it INSIDE this one span
              // means an inline clamp masks to a SINGLE placeholder exactly like the bare \code{} it
              // replaced, so a clamped prose unit keeps the same cache key (stays a model-free cache hit).
              var e = matchFi(text, j)
              if e + 1 < n && text(e) == '{' && text(e + 1) == '}' then e += 2
              protect(text.substring(i, e)); i = e
            case nm if verbatimInline.contains(nm) =>
              var k = j
              if nm == "lstinline" then k = skipOptional(text, k)
              if k < n then
                val d = text(k)
                val cl = if d == '{' then text.indexOf('}', k + 1) else text.indexOf(d, k + 1)
                val e = if cl < 0 then n else cl + 1
                protect(text.substring(i, e)); i = e
              else { protect(text.substring(i, k)); i = k }
            case nm if maskWhole.contains(nm) =>
              val argc = argCount.getOrElse(nm, 1)
              var k = j
              for _ <- 1 to argc do k = skipGroup(text, skipOptional(text, k))
              protect(text.substring(i, k)); i = k
            case nm if itemCmds.contains(nm) => // \item / \ii / \is / \di — list-item boundary
              val k = skipOptional(text, j); itemIdx += spans.size; protect(text.substring(i, k)); i = k
            case nm if headingCmds.contains(nm) => // \section/\subsection/... — heading is its OWN unit
              // mask the command + optional [short title]; leave {heading} for translate-arg, and mark
              // its closing `}` as a segment boundary (same mechanism as Slide titles, titleCloseAt).
              val k = skipOptional(text, j)
              if k < n && text(k) == '{' then titleCloseAt = skipGroup(text, k) - 1
              protect(text.substring(i, k)); i = k
            case nm if collapseEmph && emphArg.contains(nm) => // FALLBACK masking: collapse \cmd{arg} to
              val k = skipGroup(text, j)         // ONE placeholder (arg child-translated via reEmphArg).
              protect(text.substring(i, k)); i = k // Only when collapseEmph — default exposes the arg
              // (translated in-context for better grammar); see Translate.translateBlock's tier-2 retry.
            case _ =>
              protect(text.substring(i, j)); i = j // zero-arg / font / unknown control word
      else { out += c; i += 1 }
    (out.toString, spans.toVector, itemIdx.toSet)

  /** Restore placeholders (single pass; unknown indices left as-is).
    * Self-corrects the "control word glued to a following letter" case (model dropped the space, e.g.
    * `\what`+`What` → `\whatWhat` → undefined control sequence): if the span is a command ending in a
    * letter and the next char is a letter, insert a space. No-op on valid input (a control word is
    * never followed by a letter in real LaTeX), so the round-trip invariant holds. */
  def restore(masked: String, spans: IndexedSeq[String]): String =
    Place.replaceAllIn(masked, m =>
      val idx = m.group(1).toInt
      val span = if idx < spans.size then spans(idx) else m.matched
      val nextIsLetter = m.end < masked.length && masked.charAt(m.end).isLetter
      val fixed = if nextIsLetter && span.length >= 2 && span(0) == '\\' && span.last.isLetter then span + " " else span
      Matcher.quoteReplacement(fixed)
    )

  /** ── Cache-key stability (placeholder normalization) ──────────────────────────────────────────
    * A unit's masked form embeds GLOBAL placeholder numbers (`__C57__`) assigned sequentially across the
    * whole file. Any upstream edit that adds/removes a placeholder (e.g. inserting an `\ifswedish` clamp)
    * shifts every downstream number — so if the translate cache is keyed on the masked form, every
    * downstream unit in the file gets a new key and re-translates (a costly cascade). These helpers
    * renumber placeholders to LOCAL order-of-appearance (`__C0__`, `__C1__`, …) so the cache key is
    * position-independent. `normalize` is idempotent (already-local text is unchanged), so the committed
    * cache can be migrated in place on load with no model calls. */

  /** Renumber `masked`'s placeholders to local order-of-appearance. Returns the normalized text and
    * `local2global` (index = local number, value = original global number). */
  def normalize(masked: String): (String, IndexedSeq[Int]) =
    val g2l = scala.collection.mutable.LinkedHashMap[Int, Int]()
    val out = Place.replaceAllIn(masked, m =>
      val g = m.group(1).toInt
      val l = g2l.getOrElseUpdate(g, g2l.size)
      Matcher.quoteReplacement(s"__C${l}__"))
    (out, g2l.keysIterator.toIndexedSeq)

  /** Map a normalized string's local placeholders back to this unit's global numbers (so `restore` can
    * then swap in the unit's spans). Unknown locals are left as-is. */
  def denormalize(norm: String, local2global: IndexedSeq[Int]): String =
    Place.replaceAllIn(norm, m =>
      val l = m.group(1).toInt
      Matcher.quoteReplacement(s"__C${if l >= 0 && l < local2global.size then local2global(l) else l}__"))

  /** Renumber a string's global placeholders to local via `global2local` (for normalizing model OUTPUT or
    * migrating a cached value). Unmapped globals are left as-is. */
  def globalToLocal(s: String, global2local: Map[Int, Int]): String =
    Place.replaceAllIn(s, m =>
      val g = m.group(1).toInt
      Matcher.quoteReplacement(s"__C${global2local.getOrElse(g, g)}__"))

  /** If `span` is a whole inline-emphasis command `\cmd{arg}` (cmd in emphArg), return it with `arg`
    * replaced by `tr(arg)` — the child-translated English; else return `span` unchanged. Lets
    * Translate.translateRegion translate emphasized text AFTER the surrounding prose, so a dense bullet
    * sees one placeholder per emphasis instead of three. */
  /** True if `s` contains an inline-emphasis command (\Emph{…}/\Alert{…}/…) — gates the tier-2 retry. */
  def hasEmphArg(s: String): Boolean = emphArg.exists(c => s.contains(s"\\$c{"))

  def reEmphArg(span: String, tr: String => String): String =
    if span.length < 2 || span(0) != '\\' then span
    else
      var j = 1
      while j < span.length && span(j).isLetter do j += 1
      if !emphArg.contains(span.substring(1, j)) then span
      else
        var open = j
        while open < span.length && span(open).isWhitespace do open += 1
        if open >= span.length || span(open) != '{' then span
        else
          val end = skipGroup(span, open) // index after the matching '}'
          span.substring(0, open + 1) + tr(span.substring(open + 1, end - 1)) + span.substring(end - 1)

  /** Split masked text into segments on blank lines AND before each `\item` marker, keeping the
    * separators for EXACT rejoin (blocks ++ seps interleaved == s). Splitting per-item keeps
    * bullet-list translation units small (few placeholders) — faster and far fewer model failures.
    * Safe because mask() has already absorbed blank lines inside verbatim/Code/comment blocks. */
  def segmentMasked(s: String, itemIdx: Set[Int], sepIdx: Set[Int] = Set.empty): (IndexedSeq[String], IndexedSeq[String]) =
    val blankSep = raw"\n[ \t]*(?:\n[ \t]*)+".r
    // boundary events as (start, end); blank-line seps have width, item boundaries are zero-width.
    val events = mutable.ArrayBuffer[(Int, Int)]()
    for m <- blankSep.findAllMatchIn(s) do events += ((m.start, m.end))
    for m <- Place.findAllMatchIn(s) if itemIdx.contains(m.group(1).toInt) && m.start > 0 do
      events += ((m.start, m.start)) // cut just before the \item placeholder
    // Sentence-split PLACEHOLDER-DENSE blocks: a unit with many inline commands (\code/\Emph/...) is
    // where the model DROPs a placeholder (the dominant fallback). Splitting such a block at sentence
    // boundaries gives each sentence fewer placeholders ⇒ far fewer drops. Only dense base blocks are
    // split (light ones keep full context). All cuts preserve the blocks++seps==s invariant.
    val baseSorted = events.distinct.sortBy(_._1)
    var p = 0
    for (a, b) <- baseSorted if a >= p do
      sentenceCuts(s, p, a).foreach(events += _)
      separatorCuts(s, p, a, sepIdx).foreach(events += _)
      p = b
    sentenceCuts(s, p, s.length).foreach(events += _)
    separatorCuts(s, p, s.length, sepIdx).foreach(events += _)
    val sorted = events.distinct.sortBy(_._1)
    val blocks = mutable.ArrayBuffer[String](); val seps = mutable.ArrayBuffer[String]()
    var last = 0
    for (a, b) <- sorted if a >= last do { blocks += s.substring(last, a); seps += s.substring(a, b); last = b }
    blocks += s.substring(last)
    (blocks.toVector, seps.toVector)

  private val DenseThreshold = 3 // placeholders in a base block above which we sentence-split it
  private val sentenceAbbrev = // lowercased word before a '.' that is NOT a sentence end (Swedish)
    Set("etc", "dvs", "osv", "jfr", "kap", "fig", "resp", "obs", "ung", "ev", "bl", "ex", "ca", "ssk")

  /** Sentence-boundary cut events within s[a,b), but ONLY if that block is placeholder-dense. A cut is
    * (afterPunct, afterWhitespace): the `.`/`!`/`?` stays with its sentence, the whitespace is the sep.
    * Conservative: the word before the dot must be >=3 letters and not a known abbreviation, and the
    * next sentence must start with an uppercase letter or a placeholder — so decimals (3.14), ellipses,
    * and abbreviations (t.ex.) are not split. */
  private def sentenceCuts(s: String, a: Int, b: Int): Seq[(Int, Int)] =
    if Place.findAllMatchIn(s.substring(a, b)).size < DenseThreshold then Nil
    else
      val cuts = mutable.ArrayBuffer[(Int, Int)]()
      var i = a
      while i < b - 1 do
        val c = s(i)
        if (c == '.' || c == '!' || c == '?') && s(i + 1).isWhitespace && sentenceEndsAt(s, i, a) then
          var j = i + 1
          while j < b && s(j).isWhitespace do j += 1
          val nextOk = j < b && (s(j).isUpper || s(j) == '_')
          if nextOk && i + 1 > a then { cuts += ((i + 1, j)); i = j } else i += 1
        else i += 1
      cuts.toVector

  /** Is the punctuation at index `i` a real sentence end (not an abbreviation/decimal)? */
  private def sentenceEndsAt(s: String, i: Int, lo: Int): Boolean =
    if s(i) != '.' then true // '!' and '?' are unambiguous ends
    else
      var k = i // back-scan the letter run before the dot
      while k > lo && s(k - 1).isLetter do k -= 1
      val word = s.substring(k, i)
      word.length >= 3 && !sentenceAbbrev.contains(word.toLowerCase)

  /** Structural-separator cut events within s[a,b): zero-width cuts just before each `&` / `\\` separator
    * placeholder, but ONLY if the block is placeholder-dense. Turns a dense one-unit tabular row (e.g. a
    * generated quiz matching row `term & n & \leadsto & L & def`) into per-cell units so the model no
    * longer drops/reorders the row's many placeholders. Density-gated so light tables stay whole (their
    * whole-row translations in the cache are untouched). Cuts preserve the blocks++seps==s invariant. */
  private def separatorCuts(s: String, a: Int, b: Int, sepIdx: Set[Int]): Seq[(Int, Int)] =
    if sepIdx.isEmpty || Place.findAllMatchIn(s.substring(a, b)).size < DenseThreshold then Nil
    else
      Place.findAllMatchIn(s.substring(a, b))
        .filter(m => sepIdx.contains(m.group(1).toInt))
        .map(m => (a + m.start, a + m.start)) // zero-width cut just before the separator placeholder
        .filter((cut, _) => cut > a)          // never cut at the block start
        .toVector

  /** Placeholder indices that mask a structural separator — `&` (tabular column) or `\\` (row break).
    * Pass to segmentMasked so dense tabular rows split into per-cell translation units. */
  def separatorIdx(spans: IndexedSeq[String]): Set[Int] =
    spans.iterator.zipWithIndex.collect { case (sp, i) if sp == "&" || sp == "\\\\" => i }.toSet

  /** Split on blank lines only (no item splitting). */
  def splitParas(s: String): (IndexedSeq[String], IndexedSeq[String]) = segmentMasked(s, Set.empty)

  /** Placeholders present in a (masked) segment. */
  def placeholders(s: String): Set[String] = Place.findAllIn(s).toSet

  /** Placeholders in ORDER of appearance (for order-validation: structure must not be reordered). */
  def placeholderSeq(s: String): Seq[String] = Place.findAllIn(s).toVector

  /** For each placeholder, whether a newline sits immediately before / after it. The model must not
    * change this (collapsing a newline around `%comment`/`\end{}`/`\includegraphics` breaks LaTeX). */
  def placeholderAdjacency(s: String): Seq[(Boolean, Boolean)] =
    Place.findAllMatchIn(s).map { m =>
      (m.start > 0 && s(m.start - 1) == '\n', m.end < s.length && s(m.end) == '\n')
    }.toVector

  /** Remove all `__Cn__` placeholders, leaving only the surrounding prose. */
  def stripPlaceholders(s: String): String = Place.replaceAllIn(s, " ")

  /** For each gap around/between placeholders, whether it has any NON-WHITESPACE content.
    * The model must preserve this pattern: it must not MERGE a text gap to empty (e.g. two address
    * lines → one, clustering `\\` into `\\\\`) NOR inject junk into an empty gap (e.g. commas between
    * the placeholders of `\renewcommand{\arraystretch}{1.75}` → `\renewcommand,{,...`). */
  def placeholderGapText(s: String): Seq[Boolean] =
    Place.pattern.split(s, -1).toVector.map(_.exists(!_.isWhitespace))

  /** Does the segment contain translatable prose (a letter outside any placeholder)? */
  def hasText(s: String): Boolean = Place.replaceAllIn(s, "").exists(_.isLetter)

  /** Count translatable blocks in a body (cheap, model-free) — used to size the progress bar. */
  def countTextBlocks(body: String): Int =
    val (masked, spans, itemIdx) = mask(body, stripEng = true)
    segmentMasked(masked, itemIdx, separatorIdx(spans))._1.count(hasText)
