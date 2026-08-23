//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8
//> using file ../CodeGlossary.scala
//> using file ../Latex.scala

// COMPILE GATE for the mirror's example-code translation (#947/#948 + the personExample s-interp fix).
// "Only compilable code should pass": every example .scala/.java that CodeGlossary.renderCodeIds rewrites
// must still COMPILE after the identifier rename. For each CHANGED file we compile the rendered version
// standalone; if it fails but the ORIGINAL compiled standalone, that's a translation-introduced REGRESSION
// (e.g. an s-interpolation `s"${p.namn}"` left pointing at a renamed field) — the gate exits non-zero.
//
// Files that don't compile standalone on their own — cross-file imports (vego1Test imports exempelVego1),
// external deps (shapes/SimpleDrawingWindow need a drawing lib), or a name that must match the filename —
// are SKIPPED (can't be gated in isolation; their definitions are gated via the files that DO stand alone).
// Model-free: comments/strings don't affect compilation, so compiling renderCodeIds output is sufficient.
//
// Also a CONTENT LEAK-CHECK: every ratified code-STRING (CodeGlossary.codeStr, e.g. the animal-sound
// onomatopoeia) must be translated wherever the mirror's inline-.tex pass runs renderCodeIds. A quoted Swedish
// key surviving in a rendered Scala-code env / inline \code fails the gate — so a ratified sound can't regress
// to Swedish, and a NEW sound added to a .tex but not to codeStr is caught.
//
// Plus a PHASE-1 INLINE .tex COMPILE GATE (#951): the same regression rule applied to the display Scala-code
// envs (Code/CodeSmall/lstlisting) the mirror rewrites in .tex — skipping \ifswedish-clamped code. And a
// PHASE-2 REPL GATE: REPL* transcripts split on `scala>` prompts, inputs concatenated into an object and
// compiled under the same regression rule (SV/EN classification is symmetric, so a mis-split only skips).
//
// Plus a PHASE-3 CROSS-ENV SESSION GATE (#951): a recovery pass over the phase-1/2 skips, which are mostly
// bodies that reference names defined in a NEIGHBOURING env. Each is retried with the smallest window of
// preceding same-session envs whose SWEDISH program compiles — the window is chosen on the source side, so it
// is identical for both languages — and a hand-clamped context env contributes its \ifswedish branch to the
// Swedish compile and its \else branch to the English one. That pairing is what catches a clamped DEFINITION
// and an auto-translated REFERENCE that disagree on a name. See those sections below.
//
//   scala-cli run autotranslate/scratch/verify-mirror-examples.scala -- [introprog-root] [--list] [--report F]
//     introprog-root  repo root to scan (optional; defaults to ".")
//     --list          also print the per-body/-transcript OK/skip breakdown for phases 1-3
//     --report <file> also WRITE the whole report to <file> — a CI artifact, and readable when a
//                     run-and-verify driver (`tt verify`) captures stdout instead of echoing it
//   (exit 0 = clean, exit 1 = at least one regression or an untranslated ratified code-string)

@main def verifyMirrorExamples(args: String*): Unit =
  val positional = args.filter(a => !a.startsWith("--"))
  val listMode = args.contains("--list")   // also print the per-body phase-1 classification (file:line (env))
  // --report <file>: also WRITE the report. The console is not always readable — a CI job keeps a build
  // artifact, and a run-and-verify driver (`tt verify`) captures stdout rather than echoing it — so the tool
  // emits its own file instead of relying on a shell redirect at the call site.
  val reportPath = args.indexOf("--report") match
    case i if i >= 0 && i + 1 < args.size => Some(args(i + 1))
    case _                                => None
  val rootStr = positional.find(p => !reportPath.contains(p)).getOrElse(".")
  // Appended line by line, not dumped at the end: this gate takes tens of minutes (a fresh compiler per
  // snippet), so a report that only lands on exit is useless for watching a run — and a run-and-verify driver
  // captures stdout, leaving the console blank too.
  val reportFile = reportPath.map(p => os.Path(p, os.pwd))
  reportFile.foreach(p => os.write.over(p, "", createFolders = true))
  def say(s: String): Unit =
    println(s)
    reportFile.foreach(p => os.write.append(p, s + "\n"))
  def lineOf(s: String, pos: Int): Int = s.substring(0, pos).count(_ == '\n') + 1
  val root = os.Path(rootStr, os.pwd)
  val dir = root / "compendium" / "examples"   // workspace/ is served by the hand-maintained workspace-en
  val files =
    if !os.exists(dir) then Seq.empty[os.Path]
    else os.walk(dir).filter(f => os.isFile(f) && (f.ext == "scala" || f.ext == "java")).sortBy(_.toString)
  /** Compile one snippet standalone. Uses the persistent bloop daemon: ~2s warm against ~14s for the cold
    * JVM + compiler that `--server=false` pays PER SNIPPET, and this gate runs hundreds of them. Isolation is
    * unchanged — still a fresh directory per snippet, so no snippet can see another's definitions; only the
    * DAEMON is reused.
    *
    * `isolated = true` forces the cold path. Used to CONFIRM a would-be regression: a wedged or OOM'd bloop
    * fails compiles for reasons that have nothing to do with the translation, and a failed English compile is
    * exactly what this gate reads as a REGRESSION that breaks the build. Confirming only on that rare path
    * keeps the speedup while making every build-failing verdict come from a clean compiler. */
  def compiles(code: String, name: String, isolated: Boolean = false): Boolean =
    val tmp = os.temp.dir(prefix = "mirror-gate")          // keep the original filename (Java needs it)
    os.write.over(tmp / name, code)
    val server = if isolated then Seq("--server=false") else Seq.empty
    os.proc("scala-cli", "compile", server, (tmp / name).toString)
      .call(check = false, mergeErrIntoOut = true).exitCode == 0
  var checked = 0; var skipped = 0
  val regressions = collection.mutable.ArrayBuffer[String]()
  for f <- files do
    val src = os.read(f)
    val en = CodeGlossary.renderCodeIds(src)
    if en != src then                                       // only rewritten files can regress
      if compiles(en, f.last) then checked += 1
      else if compiles(src, f.last) then
        if compiles(en, f.last, isolated = true) then checked += 1   // daemon hiccup, not a translation break
        else
          regressions += f.relativeTo(root).toString
          say(s"  REGRESSION: ${f.relativeTo(root)} — compiles in Swedish but NOT after rename")
      else skipped += 1                                     // not self-contained (imports/deps/@main) — not gatable here
  say(s"\n=== mirror example compile gate: $checked ok, $skipped skipped (not standalone), ${regressions.size} REGRESSIONS ===")
  if regressions.nonEmpty then say("FAIL — translation broke compilation in: " + regressions.mkString(", "))
  else say("PASS — every rewritten, self-contained example still compiles.")

  // ---- CONTENT LEAK-CHECK: ratified code-STRINGS (CodeGlossary.codeStr, e.g. the animal sounds) must actually
  // be translated wherever the mirror's inline-.tex pass runs renderCodeIds. A quoted Swedish key surviving in a
  // rendered Scala-code env or inline \code is an untranslated ratified string — fail. Mirrors Translate's
  // scalaCodeEnvs + inlineCodeRe so it gates exactly what the mirror translates (this is why the sounds can't
  // silently regress to Swedish; it also catches a NEW sound added to a .tex but not to codeStr).
  val scalaCodeEnvs = Set("Code", "CodeSmall", "REPL", "REPLnonum", "REPLsmall", "lstlisting", "Trace", "Output")
  val envRe = ("(?s)\\\\begin\\{(" + scalaCodeEnvs.mkString("|") + ")\\}(.*?)\\\\end\\{\\1\\}").r
  val inlineRe = raw"\\(code|jcode|lstinline)\{([^}]*)\}".r
  val texFiles = Seq("compendium", "slides").map(root / _).filter(os.exists)
    .flatMap(d => os.walk(d)).filter(f => os.isFile(f) && f.ext == "tex").sortBy(_.toString)
  val leaks = collection.mutable.ArrayBuffer[String]()
  // NB (#958.6): the per-file setup (read tex, rel, extraId, opt-out, clampRanges) recurs in the two gates below.
  // Kept as deliberate duplication — this is a scratch tool and the three loops differ enough (leak-check needs
  // no clamp/extraId) that a shared higher-order helper would add more indirection than it removes.
  for f <- texFiles do
    val tex = os.read(f)
    def scan(body: String): Unit =
      val en = CodeGlossary.renderCodeIds(body)
      for k <- CodeGlossary.codeStr.keys if en.contains("\"" + k + "\"") do
        leaks += s"""${f.relativeTo(root)}: ratified code-string "$k" left untranslated"""
    for m <- envRe.findAllMatchIn(tex) do scan(m.group(2))
    for m <- inlineRe.findAllMatchIn(tex) do scan(m.group(2))
  say(s"\n=== inline .tex code-string leak-check: ${texFiles.size} .tex scanned, ${leaks.size} LEAKS ===")
  if leaks.nonEmpty then leaks.foreach(l => say("  LEAK: " + l))
  else say("PASS — every ratified code-string is translated in inline .tex code regions.")

  // ---- PHASE-1 INLINE .tex COMPILE GATE (#951) ----
  // Gate the display Scala-code envs the mirror actually rewrites. For each Code/CodeSmall/lstlisting body that
  // is NOT inside an \ifswedish clamp (the mirror leaves clamped code alone, via Latex.ifswedishRanges) and that
  // renderCodeIds changes, compile the Swedish body AND the rendered English body. REGRESSION = Swedish compiles
  // but English does NOT. Skip if BOTH fail — many inline bodies aren't standalone-compilable (neighbour context
  // / signature-only / script style), and self-skipping those beats false alarms. REPL* transcripts are DEFERRED
  // to phase 2 (they need splitting on `scala>` prompts); Trace/Output aren't Scala. Uses the SAME per-file
  // overrides as the mirror (renderCodeIds(body, extraId)) so per-file-scoped clusters (e.g. ANIMAL) are gated.
  val phase1Envs = Set("Code", "CodeSmall", "lstlisting")
  def stripLstOpt(body: String): String =                    // \begin{lstlisting}[opts]: the [opts] can trail on line 1
    body.replaceFirst("(?s)\\A[ \\t]*\\[[^\\]]*\\]", "")
  var inChecked = 0; var inSkipped = 0; var replDeferred = 0; var nonCode = 0
  val inRegressions = collection.mutable.ArrayBuffer[String]()
  val inOk = collection.mutable.ArrayBuffer[String]()      // file:line (env) of each OK body (for --list)
  val inSkip = collection.mutable.ArrayBuffer[String]()    // file:line (env) of each skipped body (for --list)
  // PHASE-3 RETRY QUEUE: every rewritten body that compiled in NEITHER language standalone. Those are not
  // (usually) broken — they reference names defined in a NEIGHBOURING env — so phase 3 re-tries exactly these
  // with cross-env context. Filled by phases 1 and 2; "no scala> input" transcripts are deliberately NOT
  // queued (there is nothing to compile, so context cannot help).
  val pending = collection.mutable.ArrayBuffer[(file: os.Path, rel: String, pos: Int, env: String)]()
  for f <- texFiles do
    val tex = os.read(f)
    val rel = f.relativeTo(root).toString
    val extraId = CodeGlossary.overridesFor(rel)
    if !CodeGlossary.isOptedOut(rel) then
      val clampRanges = Latex.ifswedishRanges(tex)
      def clamped(pos: Int): Boolean = clampRanges.exists(r => pos >= r.start && pos < r.end)
      for m <- envRe.findAllMatchIn(tex) if !clamped(m.start) do
        val env = m.group(1)
        val body = if env == "lstlisting" then stripLstOpt(m.group(2)) else m.group(2)
        val en = CodeGlossary.renderCodeIds(body, extraId)
        if en != body then                                   // only REWRITTEN bodies are gate-relevant
          if env.startsWith("REPL") then replDeferred += 1   // rewritten transcripts -> phase 2
          else if !phase1Envs(env) then nonCode += 1         // Trace/Output — not compilable Scala
          else if compiles(en, "Inline.scala") then { inChecked += 1; inOk += s"$rel:${lineOf(tex, m.start)} ($env)" }
          else if compiles(body, "Inline.scala") then
            if compiles(en, "Inline.scala", isolated = true) then
              inChecked += 1; inOk += s"$rel:${lineOf(tex, m.start)} ($env)"  // daemon hiccup, not a break
            else
              inRegressions += s"$rel:${lineOf(tex, m.start)} ($env)"
              say(s"  INLINE REGRESSION: $rel:${lineOf(tex, m.start)} ($env) — compiles in Swedish but NOT after rename")
          else
            inSkipped += 1; inSkip += s"$rel:${lineOf(tex, m.start)} ($env)"
            pending += ((file = f, rel = rel, pos = m.start, env = env))
  say(s"\n=== inline .tex compile gate (phase 1): $inChecked ok, $inSkipped skipped (not standalone), " +
    s"$replDeferred REPL deferred to phase 2, $nonCode Trace/Output not gated, ${inRegressions.size} REGRESSIONS ===")
  if inRegressions.nonEmpty then say("FAIL — inline translation broke compilation in: " + inRegressions.mkString(", "))
  else say("PASS — every rewritten, self-contained inline Scala-code env still compiles.")
  if listMode then
    say(s"\n--- phase-1 OK (${inOk.size}) — rewritten inline envs that compile after rename ---")
    inOk.foreach(s => say(s"  ok   $s"))
    say(s"--- phase-1 SKIPPED (${inSkip.size}) — rewritten inline envs that compile in NEITHER language (not standalone) ---")
    inSkip.foreach(s => say(s"  skip $s"))

  // ---- PHASE-2 INLINE REPL COMPILE GATE (#951) ----
  // REPL transcripts: split on `scala>` prompts (prompt-stripped input + its indented continuation lines; OUTPUT
  // lines — result echoes `val x: T = …`, `resN`, error `|`/`-- Error`, blanks — are dropped), concatenate a
  // transcript's inputs into one `object` body and compile SV vs EN with the phase-1 regression rule. Line
  // classification is IDENTICAL for the SV and EN transcripts (they have parallel structure), so a mis-split can
  // only SKIP a transcript — never cause a false regression. Many transcripts reference defs from a neighbouring
  // Code env and so compile in neither language (self-skip). Clamp-aware + changed-only + per-file overrides.
  val replEnvs = Set("REPL", "REPLnonum", "REPLsmall")
  val promptLineRe = raw"^[ \t]*scala>[ ]?(.*)".r
  val replOutRe = raw"^[ \t]*(val res\d|res\d+:|[|]|\d+ *[|]|-- (Error|Warning)|<console>|\^).*".r
  def replProgram(bodyStr: String): String =
    val prog = collection.mutable.ArrayBuffer[String]()
    var inInput = false
    for line <- bodyStr.split("\n", -1) do
      promptLineRe.findFirstMatchIn(line) match
        case Some(pm) => prog += pm.group(1); inInput = true          // `scala> …` input line (prompt stripped)
        case None =>
          val t = line.trim
          val isOutput = t.isEmpty || replOutRe.findFirstMatchIn(line).isDefined ||
            t.matches("(?U)(val|var)\\s+\\w+:.*=.*")                  // result-echo `val x: T = …`; (?U) so \w
                                                                      // matches åäö — else `var räknaLäte:` (SV)
                                                                      // and `var callCount:` (EN) classify
                                                                      // differently, breaking SV/EN symmetry (#958)
          if inInput && !isOutput then prog += line                  // continuation of a multi-line input
          else inInput = false
    prog.mkString("\n")
  def wrapObj(prog: String): String =
    "object ReplWrap:\n" + prog.split("\n", -1).map("  " + _).mkString("\n")
  var rpChecked = 0; var rpSkipped = 0
  val rpRegressions = collection.mutable.ArrayBuffer[String]()
  val rpOk = collection.mutable.ArrayBuffer[String]()      // file:line of each OK transcript (for --list)
  val rpSkip = collection.mutable.ArrayBuffer[String]()    // file:line of each skipped transcript (for --list)
  for f <- texFiles do
    val tex = os.read(f)
    val rel = f.relativeTo(root).toString
    val extraId = CodeGlossary.overridesFor(rel)
    if !CodeGlossary.isOptedOut(rel) then
      val clampRanges = Latex.ifswedishRanges(tex)
      def clamped(pos: Int): Boolean = clampRanges.exists(r => pos >= r.start && pos < r.end)
      for m <- envRe.findAllMatchIn(tex) if replEnvs(m.group(1)) && !clamped(m.start) do
        val body = stripLstOpt(m.group(2))                           // strip a leading [numbers=none] optional arg
        val en = CodeGlossary.renderCodeIds(body, extraId)
        if en != body then
          val loc = s"$rel:${lineOf(tex, m.start)}"
          val enProg = replProgram(en)
          if enProg.trim.isEmpty then
            rpSkipped += 1; rpSkip += s"$loc (no scala> input)"
            // Phase 2's verdict stands unchanged — but queue it for phase 3 anyway: its continuation-aware
            // reader routinely finds input this one dropped (a multi-line entry typed behind `|` prompts),
            // and a transcript with no input to phase 2 is exactly the one with nothing to wrap in an object.
            pending += ((file = f, rel = rel, pos = m.start, env = m.group(1)))
          else if compiles(wrapObj(enProg), "ReplWrap.scala") then { rpChecked += 1; rpOk += loc }
          else if compiles(wrapObj(replProgram(body)), "ReplWrap.scala") then
            if compiles(wrapObj(enProg), "ReplWrap.scala", isolated = true) then
              rpChecked += 1; rpOk += loc                              // daemon hiccup, not a translation break
            else
              rpRegressions += loc
              say(s"  REPL REGRESSION: $loc — transcript compiles in Swedish but NOT after rename")
          else
            rpSkipped += 1; rpSkip += loc
            pending += ((file = f, rel = rel, pos = m.start, env = m.group(1)))
  say(s"\n=== inline .tex REPL compile gate (phase 2): $rpChecked ok, $rpSkipped skipped (not standalone), ${rpRegressions.size} REGRESSIONS ===")
  if rpRegressions.nonEmpty then say("FAIL — REPL translation broke compilation in: " + rpRegressions.mkString(", "))
  else say("PASS — every rewritten, self-contained REPL transcript still compiles.")
  if listMode then
    say(s"\n--- phase-2 OK (${rpOk.size}) — rewritten REPL transcripts that compile after rename ---")
    rpOk.foreach(s => say(s"  ok   $s"))
    say(s"--- phase-2 SKIPPED (${rpSkip.size}) — REPL transcripts that compile in NEITHER language / no input ---")
    rpSkip.foreach(s => say(s"  skip $s"))

  // ---- PHASE-3 CROSS-ENV SESSION CONTEXT (#951) ----
  // A RECOVERY pass over the phase-1/2 skips. Those bodies compile in neither language not because they are
  // broken but because they reference names DEFINED IN A NEIGHBOURING ENV. Phase 3 supplies that context and
  // re-applies the phase-1 regression rule, so a body that was "not gatable" becomes either OK or a REGRESSION.
  //
  // SESSION — the span from the nearest preceding structural boundary (\Task / \WHAT / a sectioning command /
  // \begin{Slide} / \QUESTBEGIN / \BasicTasks…) up to the target. Deliberately COARSE: \Subtask and \SOLUTION
  // are NOT boundaries, because a solution's code routinely references the hierarchy printed in its own TASK
  // statement (w10-inheritance-exercise Task 5: the solution's `val person = new Person(…)` needs the
  // `class Person` env from before \SOLUTION). A boundary only ever SHRINKS a session, so an over-eager one
  // costs coverage, never correctness.
  //
  // WINDOW — the k context units immediately preceding the target, for the SMALLEST k whose SWEDISH program
  // compiles. Growing k from 1 is what makes progressive REDEFINITION safe: successive subtask solutions
  // redefine the same types, and the smallest working window picks the definitions actually in scope instead
  // of stacking duplicates into an ambiguous-reference error. k is chosen on the SOURCE side ALONE, so the
  // unit selection is IDENTICAL for Swedish and English — a mis-grouped boundary or an unlucky window can
  // only SKIP, never fabricate a regression.
  //
  // CONTEXT UNIT — normally one code env, but a whole \ifswedish…\else…\fi clamp collapses to ONE unit whose
  // Swedish text is its \ifswedish branch and whose English text is its \else branch (empty for a Swedish-only
  // clamp, exactly as pdflatex renders it). Collapsing is what keeps the unit LIST language-independent while
  // the two branches hold different code — and it is the pairing that lets phase 3 catch the failure it exists
  // for: a HAND-CLAMPED definition and an AUTO-TRANSLATED reference that disagree on a name. Unclamped units
  // are rendered through renderCodeIds (the mirror translates them); clamped ones are used verbatim (it doesn't).
  //
  // COST GUARD — a static pre-filter drops a target whose session offers no definition the body even mentions
  // (library / cross-file / expression-only cases, the bulk of the skips). Without it every unrecoverable
  // target would burn MaxWindow compiles.
  //
  // Phase 3 also retries the transcripts phase 2 filed as "no scala> input", because its own reader (replInput
  // below) recovers the multi-line entries phase 2's drops. For those, k=0 — the body alone, no context — is a
  // real possibility and is tried first.
  val phase3Envs = Set("Code", "CodeSmall", "lstlisting", "REPL", "REPLnonum", "REPLsmall")
  val MaxWindow = 4                                  // context units tried before a target is given up on
  val boundaryRe = raw"\\(Task|WHAT|QUESTBEGIN|BasicTasks|ExtraTasks|AdvancedTasks|part|chapter|ChapterUnnum|subsubsection|subsection|section|Subsection|Section|begin\{Slide)".r
  val idc = "A-Za-zÅÄÖåäö_"                          // Scala identifier chars, Swedish letters included
  val defRe = (raw"(?m)^[ \t]*(?:@\w+[ \t]+)*(?:(?:final|sealed|abstract|implicit|private|protected|open|case|" +
    raw"lazy|override|inline|transparent)[ \t]+)*(?:class|trait|object|enum|def|val|var|type|given)[ \t]+" +
    s"([$idc][${idc}0-9]*)").r
  val tokRe = s"[$idc][${idc}0-9]*".r
  // PHASE-3 TRANSCRIPT READER. Deliberately SEPARATE from `replProgram` above, which phases 1-2 keep using
  // byte for byte — their counts and verdicts must not move.
  //
  // `     | rest of the entry` is the Scala 3 REPL CONTINUATION prompt, i.e. INPUT. But a bare `|` is ALSO the
  // gutter of a compiler diagnostic (`8 |  namn: String,`, `  |  ^`), which is output. `replProgram` settles
  // that ambiguity by calling both output, so a multi-line entry truncates to NOTHING — a whole class
  // hierarchy typed at one prompt disappears, and the transcript is filed under "no scala> input". Harmless
  // for phases 1-2 (it only ever skips) but fatal for the phase-3 idea, which is precisely to wrap that code
  // in a compilable object: there is nothing left to wrap.
  //
  // Position disambiguates what a single line cannot: the gutter only ever follows a `-- Error` / `-- Warning`
  // / `<console>` header, and the next `scala>` closes the diagnostic. Purely structural, so it classifies the
  // SV and EN transcripts identically — the #958 symmetry property holds and a mis-split can still only SKIP.
  // Scala-2-style output carrying neither a header nor a `res`/`val` echo would read as input; that transcript
  // then fails to compile and is skipped, costing coverage rather than correctness.
  val contLineRe = raw"^[ \t]*\|(.*)".r
  val diagStartRe = raw"^[ \t]*(-- (Error|Warning)|<console>).*".r
  val ctxOutRe = raw"^[ \t]*(val res\d|res\d+:|\d+ *[|]|\^).*".r
  def replInput(bodyStr: String): String =
    val prog = collection.mutable.ArrayBuffer[String]()
    var inInput = false
    var inDiag = false
    for line <- bodyStr.split("\n", -1) do
      promptLineRe.findFirstMatchIn(line) match
        case Some(pm) => prog += pm.group(1); inInput = true; inDiag = false  // `scala> …` (prompt stripped)
        case None =>
          val t = line.trim
          if diagStartRe.findFirstMatchIn(line).isDefined then { inDiag = true; inInput = false }
          else if inDiag then ()                                    // rest of the diagnostic block is output
          else if t.isEmpty then inInput = false
          else contLineRe.findFirstMatchIn(line) match
            // Strip the ONE space the REPL prints after `|`, so the entry's own indentation survives intact
            // (`|   namn:` -> `  namn:`) and the reconstructed entry is valid indented Scala.
            case Some(cm) if inInput => prog += cm.group(1).stripPrefix(" ")
            case _ =>
              val isOutput = ctxOutRe.findFirstMatchIn(line).isDefined ||
                t.matches("(?U)(val|var)\\s+\\w+:.*=.*")             // result-echo; (?U) so \w matches åäö (#958)
              if inInput && !isOutput then prog += line              // continuation of a multi-line entry
              else inInput = false
    prog.mkString("\n")
  /** Code an env contributes: a REPL transcript collapses to its INPUT lines, anything else is the body.
    * `stripLstOpt` first, for both the `\begin{lstlisting}[opts]` and `\begin{REPL}[numbers=none]` forms. */
  def envCode(env: String, rawBody: String): String =
    val b = stripLstOpt(rawBody)
    if env.startsWith("REPL") then replInput(b) else b
  def wrapSession(pieces: Seq[String]): String =
    "object Session:\n" + pieces.mkString("\n").split("\n", -1).map("  " + _).mkString("\n")
  /** Does the `\begin` at `pos` sit on a %-COMMENTED .tex line? Whole tasks are parked by commenting every
    * line, and their envs would otherwise enter a session as `% …` garbage that compiles in no language. */
  def isCommentedOut(tex: String, pos: Int): Boolean =
    var i = tex.lastIndexOf('\n', pos - 1) + 1
    var found = false
    while i < pos && !found do
      if tex(i) == '\\' then i += 2 else if tex(i) == '%' then found = true else i += 1
    found

  var s3Ok = 0; var s3Unresolved = 0; var s3NoCtx = 0; var s3NoInput = 0
  val s3Regressions = collection.mutable.ArrayBuffer[String]()
  val s3Recovered = collection.mutable.ArrayBuffer[String]()   // file:line (env) k=<n> of each recovery (--list)
  for (rel, items) <- pending.groupBy(_.rel).toSeq.sortBy(_._1) do
    val f = items.head.file
    val tex = os.read(f)
    val extraId = CodeGlossary.overridesFor(rel)
    val boundaries = boundaryRe.findAllMatchIn(tex).map(_.start).toVector
    val branches = Latex.ifswedishBranches(tex)
    def clampIdx(pos: Int): Int = branches.indexWhere(b => pos >= b.start && pos < b.end)
    val envs = envRe.findAllMatchIn(tex).toVector
    val bodyAt = envs.iterator.map(m => m.start -> m).toMap
    val ctxEnvs = envs.filter(m => phase3Envs(m.group(1)) && !isCommentedOut(tex, m.start))
      .map(m => (pos = m.start, env = m.group(1), body = m.group(2)))
    // Collapse each clamp's envs into one unit; every other env is its own unit.
    val units = collection.mutable.ArrayBuffer[(pos: Int, sv: String, en: String)]()
    var q = 0
    while q < ctxEnvs.size do
      val ci = clampIdx(ctxEnvs(q).pos)
      if ci < 0 then
        val sv = envCode(ctxEnvs(q).env, ctxEnvs(q).body)
        units += ((pos = ctxEnvs(q).pos, sv = sv, en = CodeGlossary.renderCodeIds(sv, extraId)))
        q += 1
      else
        val b = branches(ci)
        val grp = ctxEnvs.drop(q).takeWhile(e => clampIdx(e.pos) == ci)
        val sv = grp.filter(_.pos < b.elseAt).map(e => envCode(e.env, e.body)).mkString("\n")
        val en = grp.filter(_.pos >= b.elseAt).map(e => envCode(e.env, e.body)).mkString("\n")
        units += ((pos = grp.head.pos, sv = sv, en = en))
        q += grp.size
    for it <- items.sortBy(_.pos) do
      val m = bodyAt(it.pos)
      val raw = stripLstOpt(m.group(2))
      val svBody = envCode(it.env, m.group(2))
      val enBody = CodeGlossary.renderCodeIds(svBody, extraId)
      val sessStart = boundaries.filter(_ <= it.pos).lastOption.getOrElse(0)
      val before = units.filter(u => u.pos >= sessStart && u.pos < it.pos).toVector
      val loc = s"$rel:${lineOf(tex, it.pos)} (${it.env})"
      val refs = tokRe.findAllIn(svBody).toSet
      // Cheap static gate: does anything in this session DEFINE a name this body mentions?
      val offersDef = before.exists(u => defRe.findAllMatchIn(u.sv).exists(d => refs.contains(d.group(1))))
      // k=0 (the body ALONE) is worth a compile only when the phase-3 reader recovered input that phase 2's
      // reader threw away — otherwise phases 1-2 already proved the body fails standalone. For a non-REPL env
      // the two readers agree, so this is false and no compile is wasted.
      val readerGained = it.env.startsWith("REPL") && svBody != replProgram(raw)
      if svBody.trim.isEmpty then s3NoInput += 1
      else if !offersDef && !readerGained then s3NoCtx += 1
      else
        var k = if readerGained then 0 else 1
        var chosen = -1
        val maxK = if offersDef then math.min(MaxWindow, before.size) else 0
        while chosen < 0 && k <= maxK do
          if compiles(wrapSession(before.takeRight(k).map(_.sv) :+ svBody), "Session.scala") then chosen = k
          else k += 1
        if chosen < 0 then s3Unresolved += 1
        else
          val win = before.takeRight(chosen)
          val enSession = wrapSession(win.map(_.en).filter(_.trim.nonEmpty) :+ enBody)
          if compiles(enSession, "Session.scala") then { s3Ok += 1; s3Recovered += s"$loc k=$chosen" }
          else if compiles(enSession, "Session.scala", isolated = true) then
            s3Ok += 1; s3Recovered += s"$loc k=$chosen"                // daemon hiccup, not a translation break
          else
            s3Regressions += loc
            say(s"  SESSION REGRESSION: $loc — compiles in Swedish WITH its session context but NOT after rename")
  say(s"\n=== cross-env session gate (phase 3): $s3Ok recovered, $s3NoCtx no context offered, " +
    s"$s3NoInput no code at all, $s3Unresolved still unresolved (window <= $MaxWindow), " +
    s"${s3Regressions.size} REGRESSIONS ===")
  if s3Regressions.nonEmpty then say("FAIL — cross-env translation broke compilation in: " + s3Regressions.mkString(", "))
  else say(s"PASS — every body recoverable from its session context still compiles after rename.")
  if listMode then
    say(s"\n--- phase-3 RECOVERED (${s3Recovered.size}) — phase-1/2 skips now gated, with the window size used ---")
    s3Recovered.foreach(s => say(s"  ok   $s"))

  if regressions.nonEmpty || leaks.nonEmpty || inRegressions.nonEmpty || rpRegressions.nonEmpty ||
    s3Regressions.nonEmpty then sys.exit(1)
