//> using scala 3.8.4
//> using dep com.lihaoyi::os-lib:0.11.8
//> using file ../Latex.scala
//> using file ../CodeGlossary.scala
//> using file ../Code.scala

/** invariants -- check RELATIONS between places, not the Swedish-ness of strings.
  *
  * The Swedish percentage can fall to zero while the book gets worse: a transcript whose input was
  * translated and whose echo was not is 100% English and factually false. So this tool checks
  * invariants that must hold BETWEEN two places, and reports violations as pairs.
  *
  * Implemented here (the verifiable core):
  *   I4 KEY LIVENESS   every code-cache key must be PRODUCIBLE by the current pipeline. Ratifying an
  *                     identifier rewrites the literal that forms the key, so the old key dies and the
  *                     English under it is stranded -- which is how `Du hittade talet $hemlis ...` went
  *                     back to Swedish between the 2026-10-14 and 10-30 builds without any gate moving.
  *   I6 TYPED FAILURE  a row with sv == en means "I gave up", but is indistinguishable from a string
  *                     that is legitimately identical (`println("hi")`). Failures are laundered into
  *                     successes. We separate them using the pipeline's OWN `Code.swedishish`.
  *   V5 MUTATION       `--mutate sv=en` simulates ratifying a name and reports how many keys that KILLS.
  *                     This validates the checker without waiting for a real defect -- the discipline
  *                     that was missing when the pdf oracle reported 81 fixed and 7 were.
  *
  *   I8 KEYWORD        an identifier rename whose VALUE is a Scala reserved word emits code that does
  *                     not compile, silently, far from the entry that caused it.
  *
  * DESIGN RULE: never reimplement the build's region logic. `Code.translate` takes the translate
  * function as a parameter, so we harvest the REAL keys by passing a collector that returns its input
  * unchanged. Any reimplementation would drift from the pipeline it audits, and a reachability check
  * that lies is worse than none.
  *
  * ---------------------------------------------------------------------------------------------
  * USING IT AS A GATE
  *
  *   scala-cli run autotranslate/scratch/invariants.scala --main-class invariants -- --root .
  *
  * Exit 0 = clean, exit 1 = a FAILING invariant, exit 2 = the selftest itself broke. Needs no model,
  * no backend and no built pdf; it reads the committed sources and caches, so it is safe in CI and
  * takes seconds. Add it beside the other ratchets in .github/workflows/main.yml.
  *
  * WHAT FAILS THE RUN, and what only reports:
  *   FAILS   I4 stranded-by-rename  -- an active regression: correct English under a dead key
  *   FAILS   I8 global keyword collision -- emits uncompilable code
  *   reports I4 obsolete            -- clutter, not a defect; source content is simply gone
  *   reports I6 disguised failures  -- debt to work down, not a reason to block a PR
  *   reports I8 scoped collision    -- scoping bounds the hazard rather than removing it
  *
  * This is deliberately NOT a ratchet with a committed baseline number. A stranded key is a REGRESSION
  * -- it means correct English stopped being applied -- so the right threshold is zero, not "no worse
  * than last time". The obsolete and disguised counts are the ones that behave like debt, and those do
  * not fail.
  *
  * FLAGS
  *   --root <dir>     repo root (default `.`)
  *   --list           print every violation rather than the first 12
  *   --selftest       replay a known historical defect; exit 2 if the detector no longer catches it
  *   --mutate sv=en   simulate ratifying a name, report how many live cache rows it would kill
  *
  * VALIDATING A CHANGE TO THIS FILE: a checker that has only ever reported zero is untested. Both
  * detectors here were validated by INJECTION -- re-adding the historical `$hemlis` row made I4 report
  * exactly one stranded key, and a synthetic `zzTest -> object` made I8 report one global collision and
  * exit 1. Do the same for anything added: make it fire on a planted defect before trusting its zero.
  */

import java.nio.file.{Files, Path}
import scala.jdk.CollectionConverters.*
import scala.util.matching.Regex

object Inv:
  /** Same construction as Translate.codeEnvs (private there, but built from a PUBLIC Latex value). */
  val codeEnvs: Set[String] = Latex.verbatimEnvs ++ Set("Trace", "Output")

  /** Mirrors Translate.scalaCodeEnvs, which is private. Copied sets rot, so `checkEnvSet` re-reads the
    * real definition from source and reports disagreement instead of silently diverging. */
  val scalaCodeEnvs: Set[String] =
    Set("Code", "CodeSmall", "REPL", "REPLnonum", "REPLsmall", "lstlisting", "Trace", "Output")

  def checkEnvSet(root: Path): Option[String] =
    val f = root.resolve("autotranslate/Translate.scala")
    if !Files.exists(f) then Some("Translate.scala not found - cannot verify scalaCodeEnvs")
    else
      val src = Files.readString(f)
      raw"""private val scalaCodeEnvs = Set\(([^)]*)\)""".r.findFirstMatchIn(src) match
        case None => Some("could not locate scalaCodeEnvs in Translate.scala")
        case Some(m) =>
          val real = m.group(1).split(",").map(_.trim.stripPrefix("\"").stripSuffix("\"")).filter(_.nonEmpty).toSet
          if real == scalaCodeEnvs then None
          else Some(s"scalaCodeEnvs DRIFTED: build has ${real.diff(scalaCodeEnvs)} extra, we have ${scalaCodeEnvs.diff(real)} extra")

  final case class Row(sv: String, en: String, line: Int)

  def readCache(p: Path): Vector[Row] =
    if !Files.exists(p) then Vector.empty
    else Files.readAllLines(p).asScala.toVector.zipWithIndex.flatMap: (l, i) =>
      l.split("\t", -1) match
        case Array(sv, en, _*) if sv.nonEmpty => Some(Row(sv, en, i + 1))
        case _                                => None

  def texFiles(root: Path): Vector[Path] =
    Vector("compendium", "slides").map(root.resolve).filter(Files.isDirectory(_)).flatMap: d =>
      Files.walk(d).iterator.asScala.filter(f => Files.isRegularFile(f) && f.toString.endsWith(".tex")).toVector

  def scalaFiles(root: Path): Vector[Path] =
    Vector("compendium/examples", "workspace").map(root.resolve).filter(Files.isDirectory(_)).flatMap: d =>
      Files.walk(d).iterator.asScala.filter(f => Files.isRegularFile(f) && f.toString.endsWith(".scala")).toVector

  /** Harvest every piece the pipeline WOULD ask to translate, by running the real lexer with a collector.
    * `extraRatified` simulates a further ratification batch (the V5 mutation). */
  def producibleKeys(root: Path, extraRatified: Map[String, String]): Set[String] =
    val out = collection.mutable.Set.empty[String]
    def collect(s: String): String = { out += s; out += s.trim; s }
    val envAlt = codeEnvs.toSeq.map(java.util.regex.Pattern.quote).mkString("|")
    val re = ("(?s)(\\\\begin\\{(" + envAlt + ")\\})(.*?)(\\\\end\\{\\2\\})").r

    for f <- texFiles(root) do
      val rel = root.relativize(f).toString
      val text = Files.readString(f)
      val clamps = Latex.ifswedishRanges(text)
      def clamped(pos: Int) = clamps.exists(r => pos >= r.start && pos < r.end)
      val extraId = CodeGlossary.overridesFor(rel) ++ extraRatified
      val extraStr = CodeGlossary.codeStrFor(rel)
      val doIds = !CodeGlossary.isOptedOut(rel)
      for m <- re.findAllMatchIn(text) if !clamped(m.start) do
        val raw = m.group(3)
        val body = if doIds && scalaCodeEnvs(m.group(2)) then CodeGlossary.renderCodeIds(raw, extraId, extraStr) else raw
        Code.translate(body, collect, skipTexComments = true)

    // mirrored example files: Main.scala renders ids over the WHOLE file, then Code.translate
    for f <- scalaFiles(root) do
      val rel = root.relativize(f).toString
      val extraId = CodeGlossary.overridesFor(rel) ++ extraRatified
      val extraStr = CodeGlossary.codeStrFor(rel)
      Code.translate(CodeGlossary.renderCodeIds(Files.readString(f), extraId, extraStr), collect)

    out.toSet

  // ---- I9 support: ratified domain terms, and prose lines paired with the mirror ---------------

  /** The mirror file for a source .tex, or None. `compendium/modules/x.tex` -> `compendium-en/
    * modules/x-en.tex`. Only exists after an autotranslate run; I9 skips silently without it. */
  def mirrorOf(root: Path, f: Path): Option[Path] =
    val rel = root.relativize(f).toString
    val mapped = rel match
      case r if r.startsWith("compendium/") => Some("compendium-en/" + r.stripPrefix("compendium/"))
      case r if r.startsWith("slides/")     => Some("slides-en/" + r.stripPrefix("slides/"))
      case _                                => None
    mapped
      .map(m => root.resolve(m.stripSuffix(".tex") + "-en.tex"))
      .filter(Files.isRegularFile(_))

  /** Line numbers (1-based) that fall INSIDE a code environment. Prose is everything else. Uses the
    * SAME env regex construction as `producibleKeys`, so the two cannot drift apart. */
  def codeEnvLines(text: String): Set[Int] =
    val envAlt = codeEnvs.toSeq.map(java.util.regex.Pattern.quote).mkString("|")
    val re = ("(?s)(\\\\begin\\{(" + envAlt + ")\\})(.*?)(\\\\end\\{\\2\\})").r
    val out = collection.mutable.Set.empty[Int]
    for m <- re.findAllMatchIn(text) do
      val from = text.take(m.start).count(_ == '\n') + 1
      val to   = text.take(m.end).count(_ == '\n') + 1
      out ++= (from to to)
    out.toSet

  /** A ratified DOMAIN TERM: a CodeGlossary identifier entry that names a thing, reduced to a Swedish
    * stem and the English root the prose is then obliged to use.
    *
    * Only entries whose English value is a single noun survive: `Mullvaden -> TheMole` yields
    * (mullvad, mole), but `ärMullvadsmat -> isMoleFood` is a predicate, not a name for a thing, and is
    * dropped. The Swedish stem has its definite/plural ending removed so that `mullvaden`,
    * `blockmullvadar` and `mullvader` all match the one entry. */
  def domainTerms(root: Path): Vector[(String, String, String)] =
    val svSuffixes = Vector("arna", "erna", "orna", "en", "et", "ar", "er", "or", "na", "n", "a")
    val articles = Set("the", "a", "an")
    // BR's canonical list, the same one SwedishScore uses. `Ingen`, `Någon` and `Kanske` are the MAYBE
    // cluster's case names -- capitalised, so they pass the identifier test, but they are also three of
    // the commonest words in Swedish, and prose uses them in their everyday sense with no obligation to
    // the glossary. They alone produced 145 of this check's first 220 hits. Reusing the repo's own list
    // rather than a stoplist invented here, so the two cannot disagree.
    val functionWords: Set[String] =
      val p = root.resolve("autotranslate/swedish-function-words.txt")
      if Files.isRegularFile(p) then
        Files.readAllLines(p).asScala.map(_.trim.toLowerCase).filter(_.nonEmpty).toSet
      else Set.empty
    def enRoot(en: String): Option[String] =
      if !en.forall(_.isLetter) then None
      else
        val toks = en.split("(?=[A-Z])").toVector.filter(_.nonEmpty).map(_.toLowerCase)
        val body = if toks.length > 1 && articles(toks.head) then toks.tail else toks
        // singularise: `Grönsaker -> Vegetables` must still match prose that writes "vegetable"
        val one = body.headOption.map(w => if w.length > 5 && w.endsWith("s") then w.dropRight(1) else w)
        one.filter(_ => body.length == 1).filter(_.length >= 4)
    def svStem(sv: String): Option[String] =
      val low = sv.toLowerCase
      if !low.forall(c => c.isLetter) then None
      else
        val cut = svSuffixes.find(s => low.endsWith(s) && low.length - s.length >= 4)
          .map(s => low.dropRight(s.length)).getOrElse(low)
        Option.when(cut.length >= 4)(cut)
    val scoped = CodeGlossary.perFileId.toVector.flatMap((f, m) => m.toVector.map((sv, en) => (sv, en, f)))
    (CodeGlossary.id.toVector.map((sv, en) => (sv, en, "global")) ++ scoped)
      // ONLY CAPITALISED KEYS. A lowercase entry is usually an ordinary Swedish word that happens to be
      // used as a val name -- `sant`, `ingen`, `antal`, `någon` -- and prose uses those thousands of
      // times in their everyday sense, with no obligation to the glossary at all. Including them made
      // the first run of this check report 1053 violations, of which the visible ones were all noise
      // (`sant` matched inside `intressant`). A capitalised identifier names a TYPE or a singleton,
      // i.e. a thing in the domain, which is exactly what the prose has to keep calling by one name.
      .filter((sv, _, _) => sv.headOption.exists(_.isUpper) && !functionWords(sv.toLowerCase))
      .flatMap: (sv, en, where) =>
      for
        stem <- svStem(sv)
        root <- enRoot(en)
        // a rename that changes nothing of substance cannot be contradicted by prose
        if !stem.contains(root) && !root.contains(stem)
      yield (stem, root, where)
    .distinctBy((stem, root, _) => (stem, root))

@main def invariants(args: String*): Unit =
  def opt(n: String, d: String): String =
    val i = args.indexOf(s"--$n"); if i >= 0 && i + 1 < args.length then args(i + 1) else d

  val root = Path.of(opt("root", ".")).toAbsolutePath.normalize
  val cachePath = root.resolve("autotranslate/translate-code-cache.tsv")
  val verbose = args.contains("--list")

  println(s"invariants over $root")

  // ---- guard: our copy of a private set must match the build's -------------------------------
  Inv.checkEnvSet(root) match
    case Some(msg) => println(s"  [guard] $msg"); println("  -> reachability results below may be wrong")
    case None      => println("  [guard] scalaCodeEnvs agrees with Translate.scala")

  val rows = Inv.readCache(cachePath)
  println(s"  code-cache rows: ${rows.size}")

  val mutation = opt("mutate", "")
  val extra =
    if mutation.isEmpty then Map.empty[String, String]
    else mutation.split("=", 2) match
      case Array(sv, en) => Map(sv -> en)
      case _             => println(s"  [warn] --mutate wants sv=en, got '$mutation'"); Map.empty

  val keys = Inv.producibleKeys(root, extra)
  println(s"  producible keys harvested: ${keys.size}")

  // ---- I4: key liveness ----------------------------------------------------------------------
  val dead = rows.filterNot(r => keys.contains(r.sv))

  // A dead key splits into two populations that look identical and matter completely differently.
  // STRANDED BY RENAME: applying the id map to the key yields a key that IS producible -- which is
  //   precisely what ratification did to the literal that formed it. The English under the old key is
  //   correct and now unreachable, and the live key typically holds a give-up row. ACTIVE REGRESSION.
  // OBSOLETE: the key renames to nothing producible, i.e. the source content is simply gone (edited or
  //   deleted upstream). Harmless clutter.
  // Conflating them is what made the first run of this tool claim 211 stranded rows when a hand census
  // of the same corpus found 4. Never report "not producible" as if it meant "broken".
  def renamesInto(sv: String): Option[String] =
    val renamed = CodeGlossary.renderCodeIds(sv, extra, Map.empty)
    if renamed != sv && keys.contains(renamed) then Some(renamed) else None

  val stranded = dead.filter(r => Code.swedishish(r.sv) && r.sv != r.en).flatMap: r =>
    renamesInto(r.sv).map(live => (r, live))
  val obsolete = dead.size - stranded.size

  println()
  println(s"=== I4 key liveness: ${dead.size} of ${rows.size} cache rows are not producible ===")
  println(s"    ${stranded.size} STRANDED BY RENAME -- active regressions: correct English under a key")
  println(s"      that ratification killed, while the live key usually holds a give-up row")
  println(s"    ${obsolete} merely OBSOLETE -- source content gone upstream; clutter, not a defect")
  for (r, live) <- (if verbose then stranded else stranded.take(12)) do
    println(f"      line ${r.line}%5d  ${r.sv.take(58)}")
    println(f"            now keyed:  ${live.take(58)}")
  if !verbose && stranded.size > 12 then println(s"      ... ${stranded.size - 12} more (--list for all)")

  // ---- I6: typed failure ---------------------------------------------------------------------
  val identical = rows.filter(r => r.sv == r.en)
  val disguised = identical.filter(r => Code.swedishish(r.sv))
  println()
  println(s"=== I6 typed failure: ${identical.size} rows store sv == en ===")
  println(s"    ${disguised.size} of them are DISGUISED FAILURES -- the pipeline's own Code.swedishish")
  println(s"    says the source is Swedish, so an identical 'translation' means the model gave up.")
  println(s"    ${identical.size - disguised.size} are plausibly identical in both languages (println(\"hi\")).")
  for r <- (if verbose then disguised else disguised.take(12)) do
    println(f"      line ${r.line}%5d  ${r.sv.take(62)}")
  if !verbose && disguised.size > 12 then println(s"      ... ${disguised.size - 12} more (--list for all)")

  // ---- I8: keyword collision -------------------------------------------------------------------
  // An identifier rename whose VALUE is a Scala reserved word emits code that does not compile, and it
  // does so silently: the entry looks correct, and the breakage appears wherever the Swedish name is
  // used, not where the entry was written. `sant -> true` and `falskt -> false` were both global when
  // this check was added (2026-11-11) -- harmless only because neither word happened to be used as a
  // declaration anywhere in the corpus. A future `val sant = ...` would have produced `val true = ...`.
  // Scoping to perFileId does not remove the hazard, it bounds it, so scoped hits are reported too.
  val reserved = Set("abstract", "case", "catch", "class", "def", "do", "else", "enum", "export",
    "extends", "false", "final", "finally", "for", "given", "if", "implicit", "import", "lazy", "match",
    "new", "null", "object", "override", "package", "private", "protected", "return", "sealed", "super",
    "then", "throw", "trait", "true", "try", "type", "val", "var", "while", "with", "yield")
  val softKw = Set("as", "derives", "end", "extension", "infix", "inline", "opaque", "open",
    "transparent", "using")
  val globalHits = CodeGlossary.id.filter((_, en) => reserved(en) || softKw(en)).toVector
  val scopedHits = CodeGlossary.perFileId.toVector.flatMap: (file, m) =>
    m.filter((_, en) => reserved(en) || softKw(en)).toVector.map((sv, en) => (file, sv, en))
  println()
  println(s"=== I8 keyword collision: ${globalHits.size} global, ${scopedHits.size} scoped ===")
  println("    a rename whose value is a Scala keyword emits uncompilable code, far from its entry")
  for (sv, en) <- globalHits do
    println(f"      GLOBAL  $sv%-22s -> $en   ${if reserved(en) then "[RESERVED]" else "[soft]"}")
  for (file, sv, en) <- scopedHits do
    println(f"      scoped  $sv%-22s -> $en   in $file")

  // ---- I9: prose term drift --------------------------------------------------------------------
  // CodeGlossary ratifies a Swedish name to an English one for CODE. The same word also appears in the
  // PROSE around that code, and prose is translated by the model, which picks a synonym freely and
  // differently each time. So the book ends up calling one thing several things, and no existing gate
  // can see it: every rendering is fluent English and none is Swedish.
  //
  // Found by this check on 2026-11-22, all in compendium/modules/w06-patterns-lab.tex, where the glossary
  // says Mullvaden -> TheMole and Masken -> TheWorm, and BR's own hand-clamped caption on line 37 says
  // "block worms" and "block moles": the model-translated prose in the SAME FILE says "block masks"
  // (mask read as the English word, not Swedish for worm) and "block sediment" for a mole.
  //
  // The comparison is line-for-line against the mirror, which preserves line numbering. A line the
  // mirror left byte-identical is SKIPPED: that is either untranslated or the Swedish branch of a
  // clamp, which is I4/I6 territory, not drift.
  val terms = Inv.domainTerms(root)
  // Precompiled ONCE. Building these per line per term per file turned a seconds-long check into
  // minutes; the regex is the same every time, only the line changes.
  val rootPat: Map[String, java.util.regex.Pattern] =
    terms.map((_, r, _) => r -> java.util.regex.Pattern.compile(
      // FREE PREFIX, boundary only at the END: the English compounds are written solid
      // ("blockworm", "blockmole"), so requiring \b before the root missed exactly the drift this
      // check exists for. Requiring it AFTER still rejects "suitable" for the root "suit".
      "[a-zA-Z]*" + java.util.regex.Pattern.quote(r) + "(s|es|d|ed|ing|er|ers|ion|ions)?\\b")).toMap
  val stemPat: Map[String, java.util.regex.Pattern] =
    terms.map((st, _, _) => st -> java.util.regex.Pattern.compile(
      "(?i)[a-zåäö]*" + java.util.regex.Pattern.quote(st) + "(arna|erna|orna|en|et|ar|er|or|na|n|a)?\\b")).toMap
  val mirrorFiles = Inv.texFiles(root).flatMap(f => Inv.mirrorOf(root, f).map(f -> _))
  case class Drift(file: String, line: Int, stem: String, root: String, sv: String, en: String)
  val drift = collection.mutable.ArrayBuffer.empty[Drift]
  // `old-*` modules are kept in the tree but \input by nothing, so nothing in them reaches a reader.
  for (src, mir) <- mirrorFiles if !src.getFileName.toString.startsWith("old-") do
    val svText = Files.readString(src)
    val svLines = svText.linesIterator.toVector
    val enLines = Files.readString(mir).linesIterator.toVector
    if svLines.length == enLines.length then // differing length => no sound line correspondence
      val inCode = Inv.codeEnvLines(svText)
      val enLower = enLines.map(_.toLowerCase)
      val svLower = svText.toLowerCase
      val enWhole = enLower.mkString("\n")
      // PRUNE PER FILE before touching a single line. Of 26 terms, a given file mentions one or two;
      // deciding that with two whole-text scans turns an O(files x lines x terms) walk into an
      // O(files x terms + files x lines) one. Without this the check took minutes instead of seconds.
      val live = terms.filter: (stem, rootWord, _) =>
        svLower.contains(stem) && rootPat(rootWord).matcher(enWhole).find()
      // The term must already be ESTABLISHED in this file's English: at least two other lines use the
      // ratified root. That turns the check into "this file calls one thing two things", which is the
      // defect the reader actually meets, and it drops every term the file merely happens to mention.
      // WORD BOUNDARY, not substring: `contains("suit")` is true of "suitable", which is how a cards
      // cluster term came to look established in a Kojo COLOUR lab and produced five false positives.
      def established(rootWord: String): Boolean =
        enLower.count(l => rootPat(rootWord).matcher(l).find()) >= 2
      for
        i <- svLines.indices
        if !inCode(i + 1) && svLines(i) != enLines(i)
        // three things that look like prose to a line scanner and are not:
        //   a commented-out line is not in the book at all
        //   a one-line \ifswedish clamp carries BOTH languages, so the Swedish half always "fails"
        //   a URL or \href target is an address, not a word anyone translates
        if !svLines(i).trim.startsWith("%")
        if !svLines(i).contains("\\ifswedish") && !svLines(i).contains("\\href")
        if !svLines(i).contains("http")
        (stem, rootWord, _) <- live
        if established(rootWord)
        // `[a-zåäö]*` before the stem lets COMPOUNDS match (blockmullvadar); the suffix group keeps
        // `maskin` from matching the stem `mask`.
        if stemPat(stem).matcher(svLines(i)).find()
        if !rootPat(rootWord).matcher(enLines(i).toLowerCase).find()
      do drift += Drift(root.relativize(src).toString, i + 1, stem, rootWord, svLines(i).trim, enLines(i).trim)
  println()
  println(s"=== I9 prose term drift: ${drift.size} prose line(s) contradict a ratified term ===")
  println(s"    ${terms.size} CodeGlossary entries name a thing and so constrain the prose around it")
  println(s"    compared ${mirrorFiles.size} file(s) line-for-line against the mirror")
  for d <- (if verbose then drift.toVector else drift.toVector.take(12)) do
    println(f"      ${d.file}:${d.line}  ${d.stem} -> ${d.root}")
    println(f"            en: ${d.en.take(96)}")
  if !verbose && drift.size > 12 then println(s"      ... ${drift.size - 12} more (--list for all)")

  // `--todo <file>` APPENDS findings in the shape compendium-todo already uses, so the reading log and
  // the automated checks share one queue. Any future check can emit the same block.
  //
  // A finding is useless in that log unless it carries the STUDY GUIDE's own numbering: the log is
  // organised by `6.3.5`, not by source path, and a block of `file:line` headers does not sort or read
  // with the rest of it. `--pdftext <file>` is a `pdftotext` dump of the built English pdf, and that is
  // where the printed numbers live -- they exist nowhere in the source, being assigned by LaTeX.
  //
  // In the dump a subsection number sits ALONE on its line, with the title following a blank line:
  //     6.3.2
  //
  //     Background
  // so carrying the last such number forward gives the section in force at any point. The text is then
  // matched against a flattened copy, because pdftotext wraps prose mid-sentence and the source line
  // and the printed line share no line breaks at all.
  val todoOut = opt("todo", "")
  if todoOut.nonEmpty && drift.nonEmpty then
    val pdfText = opt("pdftext", "")
    // (offset into `flat`, section in force) and the flattened text itself
    val (flat, marks): (String, Vector[(Int, String)]) =
      if pdfText.isEmpty || !Files.isRegularFile(Path.of(pdfText)) then ("", Vector.empty)
      else
        val secRe = """^\s*(\d+\.\d+(?:\.\d+)?|[A-Z]\.\d+(?:\.\d+)?)\s*$""".r
        val sb = StringBuilder()
        val ms = Vector.newBuilder[(Int, String)]
        for line <- Files.readAllLines(Path.of(pdfText)).asScala do
          secRe.findFirstMatchIn(line) match
            case Some(m) => ms += ((sb.length, m.group(1)))
            // Normalise BEFORE appending. Collapsing whitespace on the finished string would move
            // every character, while the offsets in `ms` were recorded against the UN-collapsed text,
            // so each lookup drifted backwards -- far enough to report 6.2.2 for a line in 6.3.2.
            case None    => sb ++= line.trim.replaceAll("\\s+", " "); sb += ' '
        (sb.toString, ms.result())
    /** LaTeX out, so the source line can be compared with what the pdf printed. */
    def plain(s: String): String =
      var t = s
      t = t.replaceAll("""\\(ref|pageref|label|cite|index|footnote)\{[^}]*\}""", " ")
      for _ <- 1 to 4 do t = t.replaceAll("""\\[A-Za-z@]+\s*\{([^{}]*)\}""", "$1")
      t = t.replaceAll("""\\[A-Za-z@]+""", " ").replaceAll("""[{}$\\]""", " ")
      t.replaceAll("\\s+", " ").trim
    /** The printed section a finding sits in, or None when the text cannot be located. */
    def sectionOf(en: String): Option[String] =
      if flat.isEmpty then None
      else
        val p = plain(en)
        // EVERY token, including one- and two-letter ones. Dropping the short words while joining
        // the rest with a non-letter separator makes the probe unmatchable the moment a short word
        // sits between two kept ones -- "develop a game" -- which is what sank the whole w06 lab.
        val words = p.split("[^A-Za-zÅÄÖåäö]+").filter(_.nonEmpty)
        // a 6-word window, not the whole line: the mirror has moved on since the pdf was built, and a
        // single changed word would otherwise lose the whole finding
        // Match a SEQUENCE OF WORDS, not literal text. A literal indexOf fails on anything the two
        // sides punctuate differently -- `\what~In` leaves a stray tilde, quotes differ, pdftotext
        // hyphenates -- and that alone cost three quarters of the recall. Letters only, any gap.
        val probe = words.take(8).map(java.util.regex.Pattern.quote).mkString("[^A-Za-z]+")
        val at =
          if words.length < 5 then -1
          else probe.r.findFirstMatchIn(flat).map(_.start).getOrElse(-1)
        if at < 0 then None else marks.takeWhile(_._1 <= at).lastOption.map(_._2)
    val stamp = java.time.LocalDate.now.toString
    val sb = StringBuilder()
    sb ++= s"\n# I9 prose term drift (invariants.scala, $stamp): prose contradicts a ratified CodeGlossary term\n"
    val located = drift.toVector.map(d => (sectionOf(d.en), d))
    val bySection = located.collect { case (Some(s), d) => (s, d) }
      // sort 2.10 after 2.9, and keep lettered appendices after numbered chapters
      .groupBy(_._1).toVector.sortBy: (s, _) =>
        val parts = s.split("[.]").toVector
        val lead = parts.head.toIntOption.getOrElse(1000 + parts.head.headOption.map(_.toInt).getOrElse(0))
        (lead, parts.lift(1).flatMap(_.toIntOption).getOrElse(0), parts.lift(2).flatMap(_.toIntOption).getOrElse(0))
    for (sec, rows) <- bySection do
      sb ++= s"$sec\n"
      // one line can contradict two or three terms at once (mullvad AND mask AND underjord on the
      // same sentence); the reader wants the line once
      for (_, d) <- rows.sortBy(_._2.line).distinctBy(_._2.en) do sb ++= s"${d.en}\n"
    // Two DIFFERENT reasons a finding has no number, kept apart because they mean opposite things.
    // A slides line is not in a compendium pdf at all and never will be -- nothing to fix. Anything
    // else means the text moved since the pdf was built, which is a signal to rebuild: pass a
    // `--pdftext` dumped from the SAME tree as the mirror, or most findings land here.
    val (slidesOnly, unplaced) =
      located.collect { case (None, d) => d }.partition(_.file.startsWith("slides/"))
    if slidesOnly.nonEmpty then
      sb ++= s"# slides, so not in the compendium pdf (${slidesOnly.size}) -- source position only\n"
      for d <- slidesOnly.sortBy(d => (d.file, d.line)) do sb ++= s"${d.file}:${d.line}\n${d.en}\n"
    if unplaced.nonEmpty then
      sb ++= s"# not found in the pdf (${unplaced.size}) -- mirror newer than the pdf? rebuild and re-run\n"
      for d <- unplaced.sortBy(d => (d.file, d.line)) do sb ++= s"${d.file}:${d.line}\n${d.en}\n"
    Files.writeString(Path.of(todoOut), sb.toString,
      java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND)
    println(s"    appended ${drift.size} finding(s) to $todoOut " +
            s"(${located.count(_._1.isDefined)} with a section number)")

  // ---- V3: frozen ground truth ---------------------------------------------------------------
  // A checker that has only ever reported zero is untested. This replays a REAL historical defect:
  // `hemlis -> secret` (#993) stranded the guessing-game translation, and the English compendium
  // printed Swedish output from 2026-10-30 until it was repaired. The fixture asserts that such a key
  // is still RECOGNISED as stranded. If this fails, either the detector broke or the live key moved --
  // both worth knowing, neither safe to ignore.
  if args.contains("--selftest") then
    val fixture = "Du hittade talet $hemlis på $loop gissningar :)"
    val live = CodeGlossary.renderCodeIds(fixture, Map.empty, Map.empty)
    println()
    println("=== V3 selftest: replay of the hemlis->secret regression ===")
    val renamed = live != fixture
    val producible = keys.contains(live)
    println(s"    fixture renames:        $renamed  ($live)")
    println(s"    renamed key producible: $producible")
    if renamed && producible then println("    PASS -- a row under the old key would be flagged STRANDED")
    else
      println("    FAIL -- this defect would now go undetected")
      System.exit(2)

  // ---- V5: mutation --------------------------------------------------------------------------
  if mutation.nonEmpty then
    val base = Inv.producibleKeys(root, Map.empty)
    val killed = rows.count(r => base.contains(r.sv) && !keys.contains(r.sv))
    println()
    println(s"=== V5 mutation '$mutation' ===")
    println(s"    ratifying this name would KILL $killed currently-live cache row(s).")
    if killed == 0 then
      println("    0 means either the name appears in no cache key, or the checker is blind.")
      println("    Validate with a name you KNOW occurs in a key before trusting a 0 elsewhere.")

  println()
  println(s"summary: ${stranded.size} stranded by rename, ${disguised.size} disguised failures")
  if stranded.nonEmpty || globalHits.nonEmpty then System.exit(1)
