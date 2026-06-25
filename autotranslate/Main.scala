import java.util.regex.Matcher

/** autotranslate — (re-)creates the English-side mirror of the Swedish source.
  *
  * For now the .tex CONTENT is copied AS-IS (still Swedish); only the file names
  * get an -en suffix (X.tex -> X-en.tex). The directory structure under
  * compendium/ and slides/ is mirrored into compendium-en/ and slides-en/.
  * Actual translation will be wired in later, step by step.
  *
  * To make the mirror BUILDABLE we also:
  *   - rewrite LOCAL \input{X.tex}/\include{X.tex} targets to X-en.tex, so a
  *     mirrored file pulls in its mirrored neighbours (refs starting with ../
  *     or / are left alone — they reach siblings like ../glossary, ../img);
  *   - copy non-.tex assets as-is (same name): .cls, images, the scala/java
  *     files under examples, etc., skipping build artifacts and tooling dirs.
  *
  * Run from the introprog root with:  sbt --client autotranslate
  */
object Main:
  val mirrors = Seq("compendium" -> "compendium-en", "slides" -> "slides-en")

  /** File extensions that are build artifacts and are never mirrored. */
  val skipExt = Set(
    "aux", "log", "out", "nav", "snm", "vrb", "toc", "gz", "fdb_latexmk",
    "fls", "dvi", "xdv", "pdf", "class", "bbl", "blg", "idx", "ilg", "ind"
  )

  /** Path segments whose whole subtree is never mirrored. */
  val skipSeg = Set("target", ".git", "old", "bin", ".bloop", ".metals", ".scala-build", ".bsp")

  private val inputRef = raw"\\(input|include)\{([^}]*)\}".r

  /** Rewrite \input/\include targets to point at the English mirror:
    *  - LOCAL X.tex -> X-en.tex
    *  - ../slides/<p>.tex -> ../slides-en/<p>-en.tex  (compendium chapters pull translated slide
    *    bodies via the Slide environment; without this the chapters stay Swedish)
    *  - other ../ and / refs (../glossary, ../plan, ../img, ../contributors) left ALONE — their
    *    English mirrors don't exist yet (e.g. explanations-generated-en.tex is a later phase). */
  def rewriteInputs(tex: String): String =
    inputRef.replaceAllIn(
      tex,
      m =>
        val t = m.group(2)
        val nt =
          if t.startsWith("../slides/") && t.endsWith(".tex") then
            "../slides-en/" + t.stripPrefix("../slides/").dropRight(4) + "-en.tex"
          else if t.startsWith("..") || t.startsWith("/") then t
          else if t.endsWith(".tex") then t.dropRight(4) + "-en.tex"
          else t + "-en"
        Matcher.quoteReplacement(s"\\${m.group(1)}{$nt}")
    )

  /** Rewrite a MAIN document's preamble for the English build (the preamble is otherwise translation-
    * protected). Two things:
    *   1. CHROME: switch babel to english, so LaTeX-generated labels are English (Contents / Chapter /
    *      Figure / Table / Appendix) and `\today` is an English date. babel is loaded in the main .tex
    *      preamble (not the .cls). The `\addto\captionsswedish` patch is retargeted to `captionsenglish`
    *      so it doesn't reference an undefined caption list under english babel.
    *   2. inject `\swedishfalse` right after `\begin{document}`, so `\ifswedish...\fi` Swedish-only
    *      blocks are skipped on the English side.
    * Gated on a first-line `\documentclass`: only real main docs, never an \input fragment (a slide
    * body may contain `\begin{document}`/`\documentclass` as VERBATIM example text in a LaTeX lecture). */
  def setEnglishFlags(tex: String): String =
    val firstReal = tex.linesIterator.map(_.trim).find(l => l.nonEmpty && !l.startsWith("%"))
    if !firstReal.exists(_.startsWith("\\documentclass")) then tex
    else
      // TITLE PAGE: the cover text lives in \title/\author/\date in the (translation-protected)
      // preamble, so translate the known cover phrases here. Distinctive strings that only occur on
      // the cover of a main doc — safe to replace document-wide.
      val coverPhrases = Seq(
        "Introduktion till programmering med Scala" -> "Introduction to Programming with Scala",
        "Föreläsningar \\& uppgifter"               -> "Lectures \\& exercises",
        "för läsning på skärm"                      -> "for reading on screen",
        "Laborationer och projekt"                  -> "Labs and projects",
        "Kompileringsdatum"                         -> "Compilation date",
        "Datavetenskap, LTH"                        -> "Computer Science, LTH",
        "Lunds universitet"                         -> "Lund University",
        "Övningar"                                  -> "Exercises", // exercises-doc cover subtitle
      )
      val t1 = coverPhrases.foldLeft(
        tex
          .replace("\\usepackage[swedish]{babel}", "\\usepackage[english]{babel}")
          .replace("\\addto\\captionsswedish", "\\addto\\captionsenglish")
      ) { case (s, (sv, en)) => s.replace(sv, en) }
      val marker = "\\begin{document}"
      val idx = t1.indexOf(marker)
      if idx < 0 then t1
      else
        val cut = idx + marker.length
        t1.substring(0, cut) +
          "\n\\swedishfalse % autotranslate: English build excludes \\ifswedish (Swedish-only) blocks" +
          t1.substring(cut)

  def main(args: Array[String]): Unit =
    // Same idea as glossary/Main.scala's inputPrefix hack (here with os-lib):
    // works whether run from the introprog root or from the autotranslate sub-dir.
    val root = if os.pwd.last == "autotranslate" then os.pwd / os.up else os.pwd
    require(
      os.exists(root / "compendium"),
      s"cannot find 'compendium' dir from $root (run in introprog root or autotranslate/)"
    )
    // arg parsing
    def argVal(flag: String): Option[String] =
      val i = args.indexOf(flag); if i >= 0 && i + 1 < args.length then Some(args(i + 1)) else None
    val only = argVal("--only")              // translate only files whose name contains this substring
    val all = args.contains("--all")          // translate all files
    val dryrun = args.contains("--dryrun")    // run the full pipeline with the MODEL DISABLED (all Swedish)
    val doTranslate = all || only.isDefined || dryrun // default (none): copy as-is, no Ollama

    if args.contains("--selftest") then Translate.selftest(root)
    else if args.contains("--clean") then Translate.clean(root)
    else if args.contains("--latextest") then latextest(root, only)
    else mirror(root, doTranslate, only, dryrun)

  /** (Re-)create compendium-en/ and slides-en/ (copy + -en rename + \input rewrite + assets).
    * If doTranslate, .tex bodies matching `only` (or all) are run through Translate.translateTex. */
  def mirror(root: os.Path, doTranslate: Boolean, only: Option[String], dryrun: Boolean = false): Unit =
    def selected(f: os.Path): Boolean = only.forall(s => f.last.contains(s))
    if doTranslate then
      Translate.init(root, withModel = !dryrun)
      // pre-pass (model-free): count translatable blocks across selected files to size the bar
      var total = 0
      for (src, _) <- mirrors; f <- os.walk(root / src)
        if os.isFile(f) && f.ext == "tex" && !f.relativeTo(root / src).segments.exists(skipSeg) && selected(f)
      do total += Latex.countTextBlocks(os.read(f))
      Translate.beginProgress(total)
      println(s"  translating ~$total blocks across selected files...")
    for (src, dst) <- mirrors do
      val srcDir = root / src
      val dstDir = root / dst
      if os.exists(dstDir) then os.remove.all(dstDir) // clean re-create
      var nTex = 0
      var nTrans = 0
      var nAsset = 0
      for f <- os.walk(srcDir) if os.isFile(f) do
        val rel = f.relativeTo(srcDir)
        if rel.segments.exists(skipSeg) || skipExt(f.ext) then () // artifact: skip
        else if f.ext == "tex" then
          val target = dstDir / rel / os.up / s"${f.baseName}-en.tex"
          os.makeDir.all(target / os.up)
          val translate = doTranslate && selected(f)
          val body = os.read(f)
          val outBody = if translate then { nTrans += 1; Translate.translateTex(body, rel.toString) } else body
          os.write.over(target, setEnglishFlags(rewriteInputs(outBody)))
          nTex += 1
        else
          val target = dstDir / rel
          os.makeDir.all(target / os.up)
          os.copy.over(f, target)
          nAsset += 1
      println(s"autotranslate: $src -> $dst  ($nTex .tex [$nTrans translated], $nAsset assets copied)")
    if doTranslate then
      Translate.printBar("done", force = true); println()
      Translate.saveCache(root)
      println(s"  done. model calls: ${Translate.modelCalls}, fallbacks: ${Translate.fallbacks}, overrides: ${Translate.overrideHits}, cache: ${Translate.cacheSize}")

  /** Tokenizer safety net: verify restore(mask(x)) == x exactly (with \Eng kept) on selected files. */
  def latextest(root: os.Path, only: Option[String]): Unit =
    val pat = only.getOrElse("lect-w01")
    var pass = 0; var fail = 0
    for (src, _) <- mirrors do
      for f <- os.walk(root / src) if os.isFile(f) && f.ext == "tex" && f.last.contains(pat) do
        val orig = os.read(f)
        val (masked, spans, _) = Latex.mask(orig, stripEng = false)
        val round = Latex.restore(masked, spans)
        if round == orig then pass += 1
        else
          fail += 1
          val k = orig.zip(round).indexWhere((a, b) => a != b)
          val at = if k < 0 then math.min(orig.length, round.length) else k
          println(s"  [FAIL] ${f.relativeTo(root)} differs at offset $at")
          println(s"    orig : ...${orig.slice(at - 20, at + 40).replace("\n", "\\n")}...")
          println(s"    round: ...${round.slice(at - 20, at + 40).replace("\n", "\\n")}...")
    println(s"latextest (pattern '$pat'): round-trip PASS=$pass FAIL=$fail")
