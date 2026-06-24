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
  val verbatimEnvs = Set("Code", "REPL", "verbatim", "Verbatim", "lstlisting", "comment")
  // environments whose mandatory {title} arg is a PROSE heading to translate (the slide title shows
  // as a level-3 heading in the compendium). \begin{env} + optional [..] is masked; {title} is not.
  val titleEnvs = Set("Slide", "SlideExtra", "SlideSimple")
  // \item and its introprog aliases (\ii \is \di = \item in the .cls) — treated as list-item
  // boundaries so \ii{...} bullet lists split into SMALL per-item units (reliably translatable)
  // instead of one giant placeholder-dense block that falls back to Swedish.
  // (\ti \ts = \textit are handled by the default scan = translate-arg, no special case needed.)
  val itemCmds = Set("item", "ii", "is", "di")
  // \Eng{...} renders "(eng. term)" — redundant in English → removed on the en side.
  // Commands masked WHOLE (command + args) — args are NON-prose and must NOT be translated.
  val maskWhole = Set(
    // non-prose args (refs / labels / urls / dimensions / colours):
    "ref", "pageref", "eqref", "autoref", "nameref", "label", "cite", "input", "include",
    "includegraphics", "scalainputlisting", "javainputlisting", "inputgraphics", "hypertarget",
    "hyperlink", "url", "href", "index", "vspace", "hspace", "vskip", "hskip", "fontsize",
    "setlength", "selectfont", "color", "textcolor", "colorbox", "raisebox", "includepdf",
    "SlideFontSize", "marginnote", "reversemarginpar",
    "texttt", "title", "author", "date", "textsuperscript", // code / proper-noun / symbol — keep verbatim
    "renewcommand", "newcommand", "providecommand", // macro (re)definitions — never translate the body
    // custom introprog macros whose args delegate to NON-prose targets (paths/refs/labels/code/
    // numbers/\def) — audited from compendium.cls + slides/*.cls. Masked WHOLE so the model can't
    // translate/mangle the path/label/id (\SlideImg{title}{PATH} was the empty-filename build break).
    "SlideImg", "file", "Key", "StudyTheory", "DoExercise", "Exercise", "ExerciseSolution",
    "Lab", "Teamlab", "Assignment", "WHAT", "Size", "stars", "setnextsection", "difficulty",
    "ExeRow", "LabRow", "Vecka" // defined in .tex files (plan tables / week links) — non-prose args
  )

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

  /** if s(k)=='{' return index after the matching '}' (brace-balanced); else k. */
  private def skipGroup(s: String, k: Int): Int =
    if k >= s.length || s(k) != '{' then k
    else
      var depth = 0; var j = k
      var done = false
      while j < s.length && !done do
        s(j) match
          case '\\' => j += 1 // skip escaped next char
          case '{'  => depth += 1
          case '}'  => depth -= 1; if depth == 0 then done = true
          case _    =>
        j += 1
      j

  /** if s(k)=='[' return index after the matching ']'; else k. */
  private def skipOptional(s: String, k: Int): Int =
    if k >= s.length || s(k) != '[' then k
    else { val c = s.indexOf(']', k + 1); if c < 0 then s.length else c + 1 }

  /** read a {name} group's content starting at k (k at '{'); return (content, idxAfter). */
  private def groupContent(s: String, k: Int): (String, Int) =
    if k >= s.length || s(k) != '{' then ("", k)
    else { val end = skipGroup(s, k); (s.substring(k + 1, end - 1), end) }

  /** Mask the source. Returns (maskedText, spans, itemIdx) where itemIdx is the set of placeholder
    * indices that are `\item` markers (used to split bullet lists into per-item translation units).
    * With stripEng=true, `\Eng{...}` is removed. */
  def mask(text: String, stripEng: Boolean): (String, IndexedSeq[String], Set[Int]) =
    val out = StringBuilder()
    val spans = mutable.ArrayBuffer[String]()
    val itemIdx = mutable.Set[Int]()
    def protect(span: String): Unit = { out ++= s"__C${spans.size}__"; spans += span }
    val n = text.length
    var i = 0
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
                // mask \begin{env} + optional [..]; leave the mandatory {title} for translate-arg
                val k = skipOptional(text, j2)
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

  /** Split masked text into segments on blank lines AND before each `\item` marker, keeping the
    * separators for EXACT rejoin (blocks ++ seps interleaved == s). Splitting per-item keeps
    * bullet-list translation units small (few placeholders) — faster and far fewer model failures.
    * Safe because mask() has already absorbed blank lines inside verbatim/Code/comment blocks. */
  def segmentMasked(s: String, itemIdx: Set[Int]): (IndexedSeq[String], IndexedSeq[String]) =
    val blankSep = raw"\n[ \t]*(?:\n[ \t]*)+".r
    // boundary events as (start, end); blank-line seps have width, item boundaries are zero-width.
    val events = mutable.ArrayBuffer[(Int, Int)]()
    for m <- blankSep.findAllMatchIn(s) do events += ((m.start, m.end))
    for m <- Place.findAllMatchIn(s) if itemIdx.contains(m.group(1).toInt) && m.start > 0 do
      events += ((m.start, m.start)) // cut just before the \item placeholder
    val sorted = events.distinct.sortBy(_._1)
    val blocks = mutable.ArrayBuffer[String](); val seps = mutable.ArrayBuffer[String]()
    var last = 0
    for (a, b) <- sorted if a >= last do { blocks += s.substring(last, a); seps += s.substring(a, b); last = b }
    blocks += s.substring(last)
    (blocks.toVector, seps.toVector)

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
    val (masked, _, itemIdx) = mask(body, stripEng = true)
    segmentMasked(masked, itemIdx)._1.count(hasText)
