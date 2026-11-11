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
  * DESIGN RULE: never reimplement the build's region logic. `Code.translate` takes the translate
  * function as a parameter, so we harvest the REAL keys by passing a collector that returns its input
  * unchanged. Any reimplementation would drift from the pipeline it audits, and a reachability check
  * that lies is worse than none.
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
