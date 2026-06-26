/** Option B: translate PROSE inside Scala/Java example code — comments (all) and string literals
  * (only Swedish-ish ones) — while leaving ALL code (identifiers, keywords, operators) verbatim, so
  * the mirror still compiles. A small hand-rolled lexer (NOT a parser): it walks the source and only
  * ever hands the TEXT of a comment or string to `tr`; everything else is copied byte-for-byte.
  *
  * Identifiers (e.g. `Grönsak`, `vikt`) stay Swedish — that is Option D's job. This is the safe,
  * compile-preserving first step. See notes/at-code-examples-swedish-investigation.md. */
object Code:
  private def swedishish(s: String): Boolean = s.exists("åäöÅÄÖ".contains)

  /** Translate comments + Swedish-ish string content in `src` via `tr`; keep all code verbatim.
    * `tr` should be effect-free w.r.t. code safety: it must NOT introduce a `"` or newline (the caller
    * — Translate.translatePlain — guarantees this), so string/line-comment delimiters stay intact. */
  def translate(src: String, tr: String => String): String =
    val sb = StringBuilder(); val n = src.length; var i = 0
    // translate inner text of a comment (always, if it has letters) or string (only if Swedish-ish)
    def piece(s: String, isComment: Boolean): String =
      if s.exists(_.isLetter) && (isComment || swedishish(s)) then tr(s) else s
    while i < n do
      val c = src(i)
      if c == '/' && i + 1 < n && src(i + 1) == '/' then            // line comment
        val e = src.indexOf('\n', i); val end = if e < 0 then n else e
        sb ++= "//"; sb ++= piece(src.substring(i + 2, end), true); i = end
      else if c == '/' && i + 1 < n && src(i + 1) == '*' then       // block comment
        val cl = src.indexOf("*/", i + 2); val inner = src.substring(i + 2, if cl < 0 then n else cl)
        sb ++= "/*"; sb ++= piece(inner, true); if cl >= 0 then sb ++= "*/"
        i = if cl < 0 then n else cl + 2
      else if c == '"' && i + 2 < n && src(i + 1) == '"' && src(i + 2) == '"' then  // raw string """..."""
        val cl = src.indexOf("\"\"\"", i + 3); val inner = src.substring(i + 3, if cl < 0 then n else cl)
        sb ++= "\"\"\""; sb ++= piece(inner, false); if cl >= 0 then sb ++= "\"\"\""
        i = if cl < 0 then n else cl + 3
      else if c == '"' then                                         // string "..."
        var j = i + 1
        while j < n && src(j) != '"' do { if src(j) == '\\' then j += 1; j += 1 }
        sb ++= "\""; sb ++= piece(src.substring(i + 1, math.min(j, n)), false); if j < n then sb ++= "\""
        i = math.min(j + 1, n)
      else if c == '\'' then                                        // char literal — skip verbatim
        var j = i + 1
        while j < n && src(j) != '\'' do { if src(j) == '\\' then j += 1; j += 1 }
        sb ++= src.substring(i, math.min(j + 1, n)); i = math.min(j + 1, n)
      else { sb += c; i += 1 }
    sb.toString
