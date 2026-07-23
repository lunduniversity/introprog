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
// envs (Code/CodeSmall/lstlisting) the mirror rewrites in .tex — skipping \ifswedish-clamped code and deferring
// REPL transcripts to phase 2. See that section below.
//
//   scala-cli run autotranslate/scratch/verify-mirror-examples.scala -- <introprog-root>
//   (exit 0 = clean, exit 1 = at least one regression or an untranslated ratified code-string)

@main def verifyMirrorExamples(args: String*): Unit =
  val rootStr = args.find(a => !a.startsWith("--")).getOrElse(".")
  val listMode = args.contains("--list")   // also print the per-body phase-1 classification (file:line (env))
  def lineOf(s: String, pos: Int): Int = s.substring(0, pos).count(_ == '\n') + 1
  val root = os.Path(rootStr, os.pwd)
  val dir = root / "compendium" / "examples"   // workspace/ is served by the hand-maintained workspace-en
  val files =
    if !os.exists(dir) then Seq.empty[os.Path]
    else os.walk(dir).filter(f => os.isFile(f) && (f.ext == "scala" || f.ext == "java")).sortBy(_.toString)
  def compiles(code: String, name: String): Boolean =
    val tmp = os.temp.dir(prefix = "mirror-gate")          // keep the original filename (Java needs it)
    os.write.over(tmp / name, code)
    os.proc("scala-cli", "compile", "--server=false", (tmp / name).toString)
      .call(check = false, mergeErrIntoOut = true).exitCode == 0
  var checked = 0; var skipped = 0
  val regressions = collection.mutable.ArrayBuffer[String]()
  for f <- files do
    val src = os.read(f)
    val en = CodeGlossary.renderCodeIds(src)
    if en != src then                                       // only rewritten files can regress
      if compiles(en, f.last) then checked += 1
      else if compiles(src, f.last) then
        regressions += f.relativeTo(root).toString
        println(s"  REGRESSION: ${f.relativeTo(root)} — compiles in Swedish but NOT after rename")
      else skipped += 1                                     // not self-contained (imports/deps/@main) — not gatable here
  println(s"\n=== mirror example compile gate: $checked ok, $skipped skipped (not standalone), ${regressions.size} REGRESSIONS ===")
  if regressions.nonEmpty then println("FAIL — translation broke compilation in: " + regressions.mkString(", "))
  else println("PASS — every rewritten, self-contained example still compiles.")

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
  for f <- texFiles do
    val tex = os.read(f)
    def scan(body: String): Unit =
      val en = CodeGlossary.renderCodeIds(body)
      for k <- CodeGlossary.codeStr.keys if en.contains("\"" + k + "\"") do
        leaks += s"""${f.relativeTo(root)}: ratified code-string "$k" left untranslated"""
    for m <- envRe.findAllMatchIn(tex) do scan(m.group(2))
    for m <- inlineRe.findAllMatchIn(tex) do scan(m.group(2))
  println(s"\n=== inline .tex code-string leak-check: ${texFiles.size} .tex scanned, ${leaks.size} LEAKS ===")
  if leaks.nonEmpty then leaks.foreach(l => println("  LEAK: " + l))
  else println("PASS — every ratified code-string is translated in inline .tex code regions.")

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
  for f <- texFiles do
    val tex = os.read(f)
    val rel = f.relativeTo(root).toString
    val extraId = CodeGlossary.overridesFor(rel)
    if !CodeGlossary.isOptedOut(rel) then
      val clampRanges = Latex.ifswedishRanges(tex)
      def clamped(pos: Int): Boolean = clampRanges.exists((a, b) => pos >= a && pos < b)
      for m <- envRe.findAllMatchIn(tex) if !clamped(m.start) do
        val env = m.group(1)
        val body = if env == "lstlisting" then stripLstOpt(m.group(2)) else m.group(2)
        val en = CodeGlossary.renderCodeIds(body, extraId)
        if en != body then                                   // only REWRITTEN bodies are gate-relevant
          if env.startsWith("REPL") then replDeferred += 1   // rewritten transcripts -> phase 2
          else if !phase1Envs(env) then nonCode += 1         // Trace/Output — not compilable Scala
          else if compiles(en, "Inline.scala") then { inChecked += 1; inOk += s"$rel:${lineOf(tex, m.start)} ($env)" }
          else if compiles(body, "Inline.scala") then
            inRegressions += s"$rel ($env)"
            println(s"  INLINE REGRESSION: $rel ($env) — compiles in Swedish but NOT after rename")
          else { inSkipped += 1; inSkip += s"$rel:${lineOf(tex, m.start)} ($env)" }
  println(s"\n=== inline .tex compile gate (phase 1): $inChecked ok, $inSkipped skipped (not standalone), " +
    s"$replDeferred REPL deferred to phase 2, $nonCode Trace/Output not gated, ${inRegressions.size} REGRESSIONS ===")
  if inRegressions.nonEmpty then println("FAIL — inline translation broke compilation in: " + inRegressions.mkString(", "))
  else println("PASS — every rewritten, self-contained inline Scala-code env still compiles.")
  if listMode then
    println(s"\n--- phase-1 OK (${inOk.size}) — rewritten inline envs that compile after rename ---")
    inOk.foreach(s => println(s"  ok   $s"))
    println(s"--- phase-1 SKIPPED (${inSkip.size}) — rewritten inline envs that compile in NEITHER language (not standalone) ---")
    inSkip.foreach(s => println(s"  skip $s"))

  if regressions.nonEmpty || leaks.nonEmpty || inRegressions.nonEmpty then sys.exit(1)
