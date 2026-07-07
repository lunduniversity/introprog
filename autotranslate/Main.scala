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
  // NOTE: workspace-en is NOT an auto-regenerated mirror — it is a COMMITTED, compile-verified, hand/
  // once-translated English copy of workspace/ (runnable lab code, rarely changes). The autotranslator
  // only (a) redirects listing paths to it (redirectListings) and (b) warns on drift (checkWorkspaceDrift).

  /** File extensions that are build artifacts and are never mirrored. */
  val skipExt = Set(
    "aux", "log", "out", "nav", "snm", "vrb", "toc", "gz", "fdb_latexmk",
    "fls", "dvi", "xdv", "pdf", "class", "bbl", "blg", "idx", "ilg", "ind"
  )

  /** Path segments whose whole subtree is never mirrored. */
  val skipSeg = Set("target", ".git", "old", "bin", ".bloop", ".metals", ".scala-build", ".bsp")

  private val inputRef = raw"\\(input|include)\{([^}]*)\}".r
  // \externaldocument (xr/xr-hyper cross-doc refs) carries NO .tex suffix; retarget local basenames to -en.
  private val externalDocRef = raw"\\externaldocument(\[[^\]]*\])?\{([^}]*)\}".r

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
          else if t.endsWith("plan/overview-generated.tex") || t.endsWith("plan/module-plan-generated.tex") then
            // plan-generated tables (Swedish) with -en variants: overview-generated-en (chrome) and
            // module-plan-generated-en (plan emits en concepts; mirror() enChrome's module names).
            // Matches both ../plan/ (slides) and ../../plan/ (cover).
            t.dropRight(4) + "-en.tex"
          else if t.startsWith("../compendium/") then
            // slides \input shared compendium MODULE content directly (e.g. ../compendium/modules/
            // wNN-…-goals[.tex]) — redirect to the translated mirror so that content is English in slides
            // too (compendium-en mirrors every module + builds clean). With/without a .tex suffix.
            val b = t.stripPrefix("../compendium/")
            "../compendium-en/" + (if b.endsWith(".tex") then b.dropRight(4) else b) + "-en.tex"
          else if t.endsWith("global-constants.tex") then
            // global-constants.tex lives at compendium/ root and IS mirrored (global-constants-en.tex),
            // but is \input via `../` from a subdir (cover/), so the `..` catch-all below would leave it
            // pointing at a non-existent compendium-en/global-constants.tex. Redirect to the -en mirror.
            t.dropRight(4) + "-en.tex"
          else if t.startsWith("..") || t.startsWith("/") then t
          else if t.endsWith(".tex") then t.dropRight(4) + "-en.tex"
          else t + "-en"
        Matcher.quoteReplacement(s"\\${m.group(1)}{$nt}")
    )

  /** Retarget \externaldocument{X} (xr/xr-hyper cross-doc refs — e.g. the compendium1<->compendium2 print
    * pair) to the -en mirror's basename. Without this the EN doc looks for the SV-named .aux inside
    * compendium-en/ (which isn't there), so every cross-VOLUME \ref resolves to ?? in the printed PDF.
    * Local basenames only; leaves ../ and / paths (and already-en names) alone. */
  def rewriteExternalDocs(tex: String): String =
    externalDocRef.replaceAllIn(
      tex,
      m =>
        val pre = Option(m.group(1)).getOrElse("")
        val t = m.group(2)
        val nt = if t.startsWith("..") || t.startsWith("/") || t.endsWith("-en") then t else t + "-en"
        Matcher.quoteReplacement(s"\\externaldocument$pre{$nt}")
    )

  /** Redirect `\...inputlisting{...}` example-code paths to the translated mirrors (Option B), so the
    * English docs include English-comment/-string code: `../workspace/`→`../workspace-en/` and
    * `../compendium/examples/`→`../compendium-en/examples/`. These path strings only occur in code
    * listing args, so a plain replace is safe. */
  def redirectListings(tex: String): String =
    tex.replace("../workspace/", "../workspace-en/")
      .replace("../compendium/examples/", "../compendium-en/examples/")

  /** workspace-en is a COMMITTED, hand-maintained English copy of workspace/. Surface DRIFT: warn if
    * workspace/ has files (lab code etc.) that workspace-en/ is missing, so the English listings don't
    * silently fall back to / break on a missing input when new lab code is added. */
  def checkWorkspaceDrift(root: os.Path): Unit =
    val ws = root / "workspace"; val wsen = root / "workspace-en"
    if os.exists(ws) && os.exists(wsen) then
      val missing = os.walk(ws).filter(f => os.isFile(f)
        && !f.relativeTo(ws).segments.exists(skipSeg) && !skipExt(f.ext)
        && !os.exists(wsen / f.relativeTo(ws)))
      if missing.nonEmpty then
        println(s"  [DRIFT] workspace-en is missing ${missing.size} file(s) present in workspace/ — translate + commit:")
        missing.take(20).foreach(f => println(s"    ${f.relativeTo(ws)}"))
    else if os.exists(ws) then println("  [DRIFT] workspace-en/ does not exist yet — create the committed English mirror.")

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
  /** Deterministic English-side rewrite of SLIDE/PLAN CHROME that lives inside maskWhole macros
    * (`\title`, `\date`, `\newcommand{\veckotema}`) or masked plan-table args — so it never becomes a
    * translation unit and neither the model nor Overrides.scala can reach it. PURE literal replaces
    * (no regex), applied to every mirrored .tex AFTER translation; the Swedish source is untouched.
    * Distinctive multi-word phrases → safe to replace document-wide. `moduleNames` is the SINGLE source
    * of truth for week-module titles, reused by `\veckotema` themes, per-week overview lines, and (via
    * the plan-table -en variant) the full-course overview table. */
  val moduleNames: Seq[(String, String)] = Seq(
    // longest-first where one phrase is a prefix of another (substring-safe replace order):
    "Nästlade och generiska strukturer, typsättning" -> "Nested and generic structures, typesetting",
    "Nästlade och generiska strukturer"              -> "Nested and generic structures",
    "Mängder och tabeller, versionshantering"        -> "Sets and tables, version control",
    "Mängder och tabeller"                           -> "Sets and tables",
    "Program, kontrollstrukturer, maskinkod"         -> "Programs, control structures, machine code",
    "Program och kontrollstrukturer"                 -> "Programs and control structures",
    "Funktioner och abstraktion"                     -> "Functions and abstraction",
    "Objekt och inkapsling"                          -> "Objects and encapsulation",
    "Klasser och datamodellering"                    -> "Classes and data modelling",
    "Mönster och felhantering"                       -> "Patterns and error handling",
    "Sekvenser och enumerationer"                    -> "Sequences and enumerations",
    "Arv och komposition"                            -> "Inheritance and composition",
    "Varians och kontextparametrar"                  -> "Variance and context parameters",
    "Fördjupning, Projekt"                           -> "In-depth study, Project",
    "Introduktion"                                   -> "Introduction",
    "Repetition"                                     -> "Revision",
    "Avslutning"                                     -> "Conclusion",
  )

  /** Fixed slide boilerplate chrome (title-page / running header-footer / overview-table headers). */
  val chromePhrases: Seq[(String, String)] = Seq(
    "Introduktion till programmering med Scala" -> "Introduction to Programming with Scala",
    "Föreläsning i ''Programmering, grundkurs''" -> "Lecture in ''Introduction to programming''",
    "Programmering, grundkurs"                   -> "Introduction to programming", // official course name (\subtitle, after footer phrase)
    "senast uppdaterad:"                         -> "last updated:",
    "Vecka \\vecka"                              -> "Week \\vecka",   // frame title: "Vecka N." -> "Week N."
    "Kompilerad den"                             -> "Compiled on",
    "Modul "                                     -> "Module ",        // overview-table label (per-week line)
    "Övn "                                       -> "Exc ",           // exercise column (abbrev)
    "Labb "                                      -> "Lab ",
    "{Modul}"                                    -> "{Module}",       // full-course plan-table header
    "{Övn}"                                      -> "{Exc}",
    // "resurstid" is the course-admin term for scheduled, TA-staffed sessions in booked computer rooms
    // (parallel to "Labs"). The model rendered it literally as "Resource Time(s)" — wrong/cryptic in
    // English. Deterministic term fix to "Tutorial(s)" everywhere (plural handled by the trailing 's';
    // verified all "resource time" occurrences are this term, none legitimate). See \label{section:tutorials}.
    "Resource Time"                              -> "Tutorial",
    "Resource time"                              -> "Tutorial",
    "resource time"                              -> "tutorial",
    // inline hint labels `\emph{Ledtråd.}` / `\emph{Ledtråd:}` in exercise modules — the model leaves the
    // short label Swedish. Brace-delimited so we don't touch lowercase prose "ledtråd" (= a clue). (The
    // bold `\hint` macro label is clamped in compendium.cls instead.)
    "{Ledtråd.}"                                 -> "{Hint.}",
    "{Ledtråd:}"                                 -> "{Hint:}",
  )

  def enChrome(tex: String): String =
    (moduleNames ++ chromePhrases).foldLeft(tex) { case (s, (sv, en)) => s.replace(sv, en) }

  def setEnglishFlags(tex: String): String =
    val firstReal = tex.linesIterator.map(_.trim).find(l => l.nonEmpty && !l.startsWith("%"))
    if !firstReal.exists(_.startsWith("\\documentclass")) then tex
    else
      // TITLE PAGE: the cover text lives in \title/\author/\date in the (translation-protected)
      // preamble, so translate the known cover phrases here. Distinctive strings that only occur on
      // the cover of a main doc — safe to replace document-wide.
      val coverPhrases = Seq(
        "Introduktion till programmering"           -> "Introduction to Programming",
        "Kompendium 1"                              -> "Compendium 1",   // cover: \title volume label
        "Kompendium 2"                              -> "Compendium 2",
        "Första läsperioden"                        -> "First reading period",  // cover subtitle (": Module 1 -- 7")
        "Andra läsperioden"                         -> "Second reading period",
        "Detta kompendium"                          -> "This compendium",
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
    val retryFallbacks = args.contains("--retry-fallbacks") // drop Swedish fallbacks from cache + re-translate
    val dumpOverrides = args.contains("--dump-overrides") // record every MODEL-tier unit (clean sv + en) for curating Overrides.scala
    val sweepFallbacks = args.contains("--sweep-fallbacks") // record every SLIDE unit whose result is still Swedish
    if dumpOverrides then Translate.captureSuggestions = true
    if sweepFallbacks then Translate.dumpFallbacks = true
    argVal("--model").foreach(m => Translate.SelectedModel = m) // override the model for this run
    val doTranslate = all || only.isDefined || dryrun || retryFallbacks || dumpOverrides || sweepFallbacks // default (none): copy as-is, no Ollama

    if args.contains("--swedish-left") then Translate.checkHowMuchSwedishLeft(root) // corpus progress metric (%)
    else if args.contains("--swedish-lines") then Translate.dumpSwedishProse(root, argVal("--swedish-lines").get) // prose lines of ONE file
    else if args.contains("--prose-leaks-dump") then Translate.proseLeakDump(root) // corpus-wide leak LINES (for categorization)
    else if args.contains("--prose-leaks") then // TRUE fixable prose leaks (inline code spans masked): one file, or corpus priority list if no file
      argVal("--prose-leaks") match
        case Some(rel) => Translate.dumpProseLeaks(root, rel)
        case None      => Translate.proseLeakCorpus(root)
    else if args.contains("--prose-swedish") then Translate.proseSwedish(root) // SM018 prose-only Swedish gauge: leak% + tagged [leak]/[allowed]/[glossary] dump
    else if args.contains("--pdf-swedish") then Translate.pdfSwedish(root, argVal("--pdf-swedish").get) // per-PDF % (insourced)
    else if args.contains("--selftest") then Translate.selftest(root)
    else if args.contains("--clean") then Translate.clean(root)
    else if args.contains("--latextest") then latextest(root, only)
    else if args.contains("--codetest") then // translate ONE .scala/.java file's comments+strings, print it
      Translate.init(root); println(Code.translate(os.read(os.Path(argVal("--codetest").get, root)), Translate.translatePlain))
    else if args.contains("--codeenvtest") then // HD2 hybrid: translate code-ENV comments/strings in ONE .tex
      Translate.init(root)
      val f = os.Path(argVal("--codeenvtest").get, root)
      val out = Translate.translateCodeEnvBodies(os.read(f))
      val outPath = root / "autotranslate" / "scratch" / "codeenvtest-out.tex"
      os.write.over(outPath, out)
      Translate.saveCache(root)
      println(s"--codeenvtest: code-env comments/strings translated -> ${outPath.relativeTo(root)} (diff vs $f to review)")
    else if args.contains("--workspace-en") then translateWorkspaceEn(root, only)
    else if args.contains("--modeltest") then // A/B a model on a sample of fallbacks (non-destructive)
      Translate.modeltest(root, argVal("--modeltest").getOrElse(Translate.SelectedModel),
        argVal("--n").flatMap(_.toIntOption).getOrElse(30))
    else
      mirror(root, doTranslate, only, dryrun, retryFallbacks)
      if dumpOverrides || sweepFallbacks then writeOverrideSuggestions(root)

  /** Write the captured MODEL-tier units (clean Swedish + the model's English) to a review file, so the
    * misses that keep `--all` from being 0-model-calls can be curated into Overrides.scala. TSV-ish:
    * one unit per block — kind, label, then sv/en with newlines shown as ⏎ for readability. */
  def writeOverrideSuggestions(root: os.Path): Unit =
    val s = Translate.suggestions.toSeq
    val out = root / "autotranslate" / "scratch" / "override-suggestions.txt"
    def vis(x: String): String = x.replace("\n", "⏎")
    val body = s.zipWithIndex.map { case ((kind, label, sv, en), i) =>
      s"[${i + 1}] $kind | $label\n  SV |${vis(sv)}\n  EN |${vis(en)}"
    }.mkString("\n\n")
    os.write.over(out, s"=== ${s.size} model-tier units (curate into Overrides.scala) ===\n\n$body\n")
    println(s"  --dump-overrides: ${s.size} model-tier units -> $out")

  /** ONE-TIME (re-)translate the COMMITTED workspace-en mirror IN PLACE: comments + Swedish string
    * literals in every .scala/.java, keeping ALL code (identifiers, operators, and `//> using`
    * directives) verbatim. workspace code is English-identifier already, so this is the whole of the
    * "full" translation. Review the diff + commit the result; afterwards workspace-en is hand-maintained. */
  def translateWorkspaceEn(root: os.Path, only: Option[String]): Unit =
    val wsen = root / "workspace-en"
    require(os.exists(wsen), s"no workspace-en at $wsen — run mk-workspace-en first")
    Translate.init(root)
    // w01_kojo is a DEFERRED special case: Kojo is a kids' lib/app and English users use the English
    // Kojo (different dep + different exports), so its Swedish Kojo identifiers need hand-mapping via
    // Appendix A table A.1 ("More about Kojo"). Skip the generic pass; handle it once examples are done.
    val deferredDirs = Set("w01_kojo")
    var n = 0; var changed = 0
    for f <- os.walk(wsen)
      if os.isFile(f) && (f.ext == "scala" || f.ext == "java")
        && !f.relativeTo(wsen).segments.exists(skipSeg)
        && !f.relativeTo(wsen).segments.exists(deferredDirs)
        && only.forall(s => f.last.contains(s))
    do
      val src = os.read(f); val out = Code.translate(src, Translate.translatePlain)
      if out != src then { os.write.over(f, out); changed += 1 }
      n += 1
    Translate.saveCache(root)
    println(s"workspace-en: $n code files processed, $changed changed")
    checkWorkspaceDrift(root)

  /** (Re-)create compendium-en/ and slides-en/ (copy + -en rename + \input rewrite + assets).
    * If doTranslate, .tex bodies matching `only` (or all) are run through Translate.translateTex. */
  def mirror(root: os.Path, doTranslate: Boolean, only: Option[String], dryrun: Boolean = false,
      retryFallbacks: Boolean = false): Unit =
    def selected(f: os.Path): Boolean = only.forall(s => f.last.contains(s))
    if doTranslate then
      Translate.init(root, withModel = !dryrun)
      if retryFallbacks then
        val n = Translate.dropSwedishFallbacks()
        println(s"  --retry-fallbacks: dropped $n Swedish fallback cache entries → re-translating them")
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
          // HD2 hybrid: after prose translation, translate comments/strings inside inline code envs too
          // (identifiers stay verbatim — the source-side \ifswedish id-glossary clamps handle those).
          val outBody =
            if translate then { nTrans += 1; Translate.translateCodeEnvBodies(Translate.translateTex(body, s"$src/$rel")) }
            else body
          os.write.over(target, setEnglishFlags(enChrome(redirectListings(rewriteExternalDocs(rewriteInputs(outBody))))))
          nTex += 1
        else
          val target = dstDir / rel
          os.makeDir.all(target / os.up)
          // A .cls (e.g. the slides' lecturenotes.cls) loads babel — switch the EN copy to english so
          // the slide chrome (figure/table captions, dates) is English. The Swedish source .cls is
          // untouched, so the Swedish slides build unchanged.
          if f.ext == "cls" then
            os.write.over(target, os.read(f).replace("\\usepackage[swedish]{babel}", "\\usepackage[english]{babel}"))
          else if doTranslate && (f.ext == "scala" || f.ext == "java") then
            // Option B: translate comments + Swedish string literals; keep all code (identifiers) verbatim.
            os.write.over(target, Code.translate(os.read(f), Translate.translatePlain))
          else os.copy.over(f, target)
          nAsset += 1
      println(s"autotranslate: $src -> $dst  ($nTex .tex [$nTrans translated], $nAsset assets copied)")
    // full-course overview table lives in plan/ (not a mirror); slides/cover pull it via ../plan/... which
    // rewriteInputs now redirects to overview-generated-en.tex. Generate it with the deterministic chrome
    // map (module names + headers) — single source of truth, regenerate after the Swedish plan changes.
    val planSv = root / "plan" / "overview-generated.tex"
    if os.exists(planSv) then
      os.write.over(root / "plan" / "overview-generated-en.tex", enChrome(os.read(planSv)))
      println("autotranslate: plan/overview-generated.tex -> plan/overview-generated-en.tex (chrome)")
    // module-plan-generated-en.tex is emitted by the PLAN project (concept terms translated via glossary),
    // but with module names left SWEDISH — translate them here with the SAME enChrome/moduleNames the
    // overview-en uses, so module titles are identical across both tables. enChrome is a literal sv->en
    // replace, so running it on the (partly-English) file is a safe in-place no-op for already-English text.
    val modulePlanEn = root / "plan" / "module-plan-generated-en.tex"
    if os.exists(modulePlanEn) then
      os.write.over(modulePlanEn, enChrome(os.read(modulePlanEn)))
      println("autotranslate: plan/module-plan-generated-en.tex (module names -> chrome)")
    if doTranslate then
      Translate.printBar("done", force = true); println()
      Translate.saveCache(root)
      println(s"  done. model calls: ${Translate.modelCalls}, fallbacks: ${Translate.fallbacks}, overrides: ${Translate.overrideHits}, cache: ${Translate.cacheSize}")
      checkWorkspaceDrift(root) // committed workspace-en must keep up with workspace/

  /** Tokenizer safety net: verify restore(mask(x)) == x exactly (with \Eng kept) AND that segmentation
    * is lossless (blocks ++ seps interleaved == masked) on selected files. */
  def latextest(root: os.Path, only: Option[String]): Unit =
    val pat = only.getOrElse("lect-w01")
    var pass = 0; var fail = 0; var segFail = 0
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
        // segmentation must be lossless: interleaving blocks and seps reproduces the masked text
        val (mk2, spans2, itemIdx) = Latex.mask(orig, stripEng = true)
        val (blocks, seps) = Latex.segmentMasked(mk2, itemIdx, Latex.separatorIdx(spans2))
        val rejoined = blocks.zipAll(seps, "", "").map((b, s) => b + s).mkString
        if rejoined != mk2 then { segFail += 1; println(s"  [SEG-FAIL] ${f.relativeTo(root)} (blocks++seps != masked)") }
    println(s"latextest (pattern '$pat'): round-trip PASS=$pass FAIL=$fail, segmentation FAIL=$segFail")
