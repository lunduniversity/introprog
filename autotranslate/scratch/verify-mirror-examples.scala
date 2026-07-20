//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8
//> using file ../CodeGlossary.scala

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
//   scala-cli run autotranslate/scratch/verify-mirror-examples.scala -- <introprog-root>
//   (exit 0 = clean, exit 1 = at least one regression or an untranslated ratified code-string)

@main def verifyMirrorExamples(rootStr: String): Unit =
  val root = os.Path(rootStr, os.pwd)
  val dir = root / "compendium" / "examples"   // workspace/ is served by the hand-maintained workspace-en
  val files = os.walk(dir).filter(f => os.isFile(f) && (f.ext == "scala" || f.ext == "java"))
    .sortBy(_.toString)
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

  if regressions.nonEmpty || leaks.nonEmpty then sys.exit(1)
