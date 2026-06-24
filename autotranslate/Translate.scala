import scala.collection.mutable

/** Translation engine for the introprog English mirror (P0 scaffolding).
  *
  * Determinism & safety (ported from the muntabot pattern):
  *  - precedence: OVERRIDE > AUTHORITATIVE(glossary) > CACHE > MODEL;
  *  - a committed cache (autotranslate/translate-cache.tsv) makes runs idempotent — the model runs
  *    only on cache misses, so a clean re-run needs no Ollama at all;
  *  - temperature 0 + fixed seed => reproducible first-time translations;
  *  - any model failure (or no Ollama) falls back to the Swedish source — never a broken build.
  *
  * P0 translates PLAIN sentences (no LaTeX masking yet — that arrives in P1 with the tokenizer).
  */
object Translate:

  // ---------- model config (swap by editing SelectedModel) ----------
  val Model1: String = "qwen2.5:3b" // small, fast, ~2GB; low RAM; baseline quality
  val Model2: String = "qwen2.5:7b" // better quality; needs more RAM/CPU
  // add translation-tuned candidates here as Model3, ...
  val SelectedModel: String = Model1 // <-- change this line to switch model

  val Seed = 42
  val OllamaChat = "http://localhost:11434/api/chat"

  // Inference threads: llama.cpp wants PHYSICAL cores (hyperthreads hurt — benchmarked: 4 beats 8
  // on an i7-4790K). Detect physical cores from /proc/cpuinfo; fall back to logical/2; override below.
  val ThreadsOverride: Option[Int] = None

  private def physicalCores: Int =
    val logical = Runtime.getRuntime.availableProcessors
    try
      val info = os.read(os.Path("/proc/cpuinfo"))
      val pairs = info.split("\n\n").flatMap { block =>
        val pid = block.linesIterator.find(_.startsWith("physical id")).map(_.split(":").last.trim)
        val cid = block.linesIterator.find(_.startsWith("core id")).map(_.split(":").last.trim)
        for p <- pid; c <- cid yield (p, c)
      }.toSet
      if pairs.nonEmpty then pairs.size else math.max(1, logical / 2)
    catch case _: Throwable => math.max(1, logical / 2)

  lazy val numThreads: Int = ThreadsOverride.getOrElse(physicalCores)

  // Save the cache to disk every this many new model translations (so a kill never loses much).
  val SaveEvery = 20

  def autotranslateDir(root: os.Path): os.Path = root / "autotranslate"
  def cacheFile(root: os.Path): os.Path = autotranslateDir(root) / "translate-cache.tsv"
  def overridesFile(root: os.Path): os.Path = autotranslateDir(root) / "overrides.tsv"

  // ---------- authoritative data from glossary (typed, no parsing) ----------
  lazy val concepts: Seq[glossary.explain.Concept] = glossary.explain.allConcepts

  /** sv -> en term pairs, injected into the model prompt as a glossary. */
  lazy val termPairs: Map[String, String] =
    concepts.iterator.filter(c => c.sv.nonEmpty && c.en.nonEmpty).map(c => c.sv -> c.en).toMap

  /** Full-sentence authoritative translations: the glossary explanations (short + long). */
  lazy val authoritative: Map[String, String] =
    val m = mutable.LinkedHashMap[String, String]()
    for c <- concepts do
      if c.svShortExplanation.nonEmpty && c.enShortExplanation.nonEmpty then m(c.svShortExplanation) = c.enShortExplanation
      if c.svLongExplanation.nonEmpty && c.enLongExplanation.nonEmpty then m(c.svLongExplanation) = c.enLongExplanation
    m.toMap

  // ---------- reviewed overrides (always win) + cache (idempotency) ----------
  private val overrides = mutable.LinkedHashMap[String, String]()
  private val cache = mutable.LinkedHashMap[String, String]()

  private def readTsv(f: os.Path, into: mutable.LinkedHashMap[String, String]): Unit =
    if os.exists(f) then
      for line <- os.read.lines(f) if line.contains("\t") do
        val Array(sv, en) = line.split("\t", 2)
        into(sv.replace("\\n", "\n")) = en.replace("\\n", "\n")

  private def writeTsv(f: os.Path, m: mutable.LinkedHashMap[String, String]): Unit =
    val body = m.toSeq.sortBy(_._1)
      .map((sv, en) => s"${sv.replace("\n", "\\n")}\t${en.replace("\n", "\\n")}").mkString("\n")
    os.write.over(f, if body.isEmpty then "" else body + "\n")

  def cacheSize: Int = cache.size
  def loadCache(root: os.Path): Unit = readTsv(cacheFile(root), cache)
  def saveCache(root: os.Path): Unit = writeTsv(cacheFile(root), cache)
  def loadOverrides(root: os.Path): Unit = readTsv(overridesFile(root), overrides)

  // ---------- Ollama process management ----------
  var ollamaOk = false

  private def proc(args: String*): (Int, String) =
    try
      val r = os.proc(args).call(check = false, stderr = os.Pipe)
      (r.exitCode, r.out.text())
    catch case _: Throwable => (-1, "")

  /** Check ollama is installed and SelectedModel is pulled; pull it if missing. Warn, never crash. */
  def ensureModel(): Unit =
    val (vc, vout) = proc("ollama", "--version")
    if vc != 0 then
      println("  [warn] ollama not found on PATH — translations will fall back to Swedish.")
      ollamaOk = false
    else
      println(s"  ollama: ${vout.trim}")
      val (_, listed) = proc("ollama", "list")
      val present = listed.linesIterator.drop(1).map(_.takeWhile(!_.isWhitespace).trim).contains(SelectedModel)
      if present then
        println(s"  model $SelectedModel is available.")
        ollamaOk = true
      else
        println(s"  [warn] model $SelectedModel not pulled — running `ollama pull $SelectedModel` (may take a while)...")
        val pull = try os.proc("ollama", "pull", SelectedModel).call(check = false, stdout = os.Inherit, stderr = os.Inherit).exitCode
        catch case _: Throwable => -1
        ollamaOk = pull == 0
        if !ollamaOk then println("  [warn] pull failed — translations will fall back to Swedish.")

  // ---------- model call ----------
  var modelCalls = 0
  var fallbacks = 0

  /** Authoritative term pairs whose Swedish term occurs in `t`, formatted for the prompt. */
  def glossaryFor(t: String): String =
    val low = t.toLowerCase
    termPairs.collect { case (sv, en) if sv.length >= 4 && low.contains(sv.toLowerCase) => s"$sv = $en" }
      .toSeq.sorted.take(40).mkString("; ")

  /** Translate one Swedish unit (plain sentence OR masked LaTeX segment) via Ollama.
    * Returns None on any failure or if a `__CN__` placeholder was dropped (caller falls back). */
  def ollamaTranslate(sv: String): Option[String] =
    if !ollamaOk then return None
    val needSeq = Latex.placeholderSeq(sv) // ORDERED — output must keep the same placeholder sequence
    val needAdj = Latex.placeholderAdjacency(sv) // newline-before/after each placeholder must be preserved
    val gloss = glossaryFor(sv)
    val system =
      "You are a precise Swedish-to-English translator for an introductory Scala programming course. " +
        "Translate the Swedish to natural English. Keep numbers, symbols and code unchanged. " +
        "Keep every placeholder token of the form __C0__, __C1__, ... EXACTLY as-is and in place. " +
        "Output ONLY the translation: no quotes, no notes, no extra text." +
        (if gloss.nonEmpty then s" Use these official term translations where they occur: $gloss." else "")
    val payload = ujson.Obj(
      "model" -> SelectedModel,
      "messages" -> ujson.Arr(
        ujson.Obj("role" -> "system", "content" -> system),
        ujson.Obj("role" -> "user", "content" -> sv)
      ),
      "stream" -> false,
      "options" -> ujson.Obj("seed" -> Seed, "temperature" -> 0, "num_thread" -> numThreads, "num_ctx" -> 2048)
    )
    try
      val resp = requests.post(OllamaChat, data = ujson.write(payload),
        headers = Map("Content-Type" -> "application/json"), readTimeout = 600000, connectTimeout = 10000)
      val out = ujson.read(resp.text())("message")("content").str.trim
      val orderOk = Latex.placeholderSeq(out) == needSeq // same placeholders, same ORDER (no reorder/drop/dup)
      // model must NOT invent LaTeX specials in prose (all were masked) — catches stray braces etc.
      val prose = Latex.stripPlaceholders(out)
      val cleanProse = !prose.exists(c => "{}\\$%&#^_~".contains(c))
      // model must NOT change newline-adjacency of any placeholder (collapsing a newline around a
      // %comment / \end{} / \includegraphics breaks LaTeX) — catches the newline-collapse class.
      val adjOk = Latex.placeholderAdjacency(out) == needAdj
      val reason =
        if !orderOk then "placeholder reorder/drop"
        else if !cleanProse then "introduced LaTeX special"
        else if !adjOk then "collapsed newline around placeholder"
        else "suspicious output"
      if out.nonEmpty && orderOk && cleanProse && adjOk && out.length <= sv.length * 4 + 80 then Some(out)
      else { println(s"  [fallback] $reason, kept Swedish for: ${sv.take(60)}"); None }
    catch case e: Throwable => { println(s"  [fallback] ollama error (${e.getClass.getSimpleName}) for: ${sv.take(50)}"); None }

  // incremental cache persistence (set in init): flush every SaveEvery model translations.
  private var saveRoot: Option[os.Path] = None
  private var sinceSave = 0

  private def noteCacheAdd(): Unit =
    sinceSave += 1
    if sinceSave >= SaveEvery then { saveRoot.foreach(saveCache); sinceSave = 0 }

  /** sv -> en with precedence OVERRIDE > AUTHORITATIVE > CACHE > MODEL; Swedish on fallback. */
  def translate(sv: String): String =
    if sv.isEmpty then ""
    else overrides.get(sv).orElse(authoritative.get(sv)).orElse(cache.get(sv)).getOrElse {
      ollamaTranslate(sv) match
        case Some(en) => modelCalls += 1; cache(sv) = en; noteCacheAdd(); en // cache only real model results
        case None     => fallbacks += 1; sv                                   // do NOT cache fallbacks
    }

  /** Report which tier a string resolves from (for diagnostics). */
  def sourceOf(sv: String): String =
    if overrides.contains(sv) then "override"
    else if authoritative.contains(sv) then "authoritative"
    else if cache.contains(sv) then "cache"
    else "model"

  // ---------- progress bar (global across the whole run) ----------
  private var startMs = 0L
  private var lastPrintMs = 0L
  var totalUnits = 0
  var doneUnits = 0

  /** Call once before translating, with the total translatable block count across all files. */
  def beginProgress(total: Int): Unit =
    totalUnits = total; doneUnits = 0; startMs = System.currentTimeMillis; lastPrintMs = 0L

  private def fmt(ms: Long): String = { val s = ms / 1000; f"${s / 60}%dm${s % 60}%02ds" }

  /** Throttled single-line progress bar (carriage-return, in-place). */
  def printBar(label: String, force: Boolean): Unit =
    val now = System.currentTimeMillis
    if force || now - lastPrintMs >= 800 then
      lastPrintMs = now
      val frac = if totalUnits > 0 then doneUnits.toDouble / totalUnits else 0.0
      val w = 24; val n = (frac * w).toInt
      val bar = "#" * n + "-" * (w - n)
      val elapsed = now - startMs
      val eta = if doneUnits > 0 then ((elapsed.toDouble / doneUnits) * (totalUnits - doneUnits)).toLong else 0L
      print(f"\r  [$bar] ${(frac * 100).toInt}%3d%%  $doneUnits/$totalUnits  model=$modelCalls cache=${cache.size} fb=$fallbacks  ${fmt(elapsed)} ETA ${fmt(eta)}  $label        ")
      System.out.flush()

  /** Translate a whole .tex body: mask → segment (paragraphs) → translate each masked segment
    * (skip pure-markup segments) → rejoin → restore. \Eng{...} is stripped on the en side.
    * Updates the global progress bar so long runs show %/counts/ETA. */
  private val placeRe = raw"__C\d+__".r

  /** Length of the leading run of [whitespace | placeholder] (structural prefix the model must not touch). */
  private def leadLen(b: String): Int =
    var i = 0; var changed = true
    while changed do
      changed = false
      while i < b.length && b(i).isWhitespace do { i += 1; changed = true }
      placeRe.findPrefixMatchOf(b.substring(i)) match
        case Some(m) => i += m.end; changed = true
        case None    => ()
    i

  /** Start index of the trailing run of [whitespace | placeholder] (structural suffix to preserve). */
  private def trailStart(b: String): Int =
    var i = b.length; var changed = true
    while changed do
      changed = false
      while i > 0 && b(i - 1).isWhitespace do { i -= 1; changed = true }
      placeRe.findAllMatchIn(b.substring(0, i)).toList.lastOption match
        case Some(m) if m.end == i => i = m.start; changed = true
        case _                     => ()
    i

  /** Translate a segment, keeping ALL boundary structure deterministic: peel the leading and
    * trailing runs of whitespace+placeholders (e.g. `\item`, `\end{itemize}`, and the NEWLINES
    * between them — beamer fragile frames need `\end{...}` at line start) and translate only the
    * prose core. The model thus cannot move structural tokens or alter the whitespace around them. */
  private def translateBlock(b: String): String =
    val lead = leadLen(b)
    val trail = trailStart(b)
    if trail <= lead then b // all whitespace/placeholders, no prose
    else
      val core = b.substring(lead, trail)
      if Latex.hasText(core) then b.substring(0, lead) + translate(core) + b.substring(trail) else b

  def translateTex(body: String, label: String = ""): String =
    val (masked, spans, itemIdx) = Latex.mask(body, stripEng = true)
    val (blocks, seps) = Latex.segmentMasked(masked, itemIdx)
    val translated = Array.from[String](blocks)
    for k <- blocks.indices do
      if Latex.hasText(blocks(k)) then
        translated(k) = translateBlock(blocks(k))
        doneUnits += 1
        printBar(label, force = false)
    val sb = StringBuilder()
    for k <- blocks.indices do
      sb ++= translated(k); if k < seps.size then sb ++= seps(k)
    Latex.restore(sb.toString, spans)

  // ---------- lifecycle ----------
  /** Load cache + overrides and make sure the model is ready (called before mirror translation). */
  def init(root: os.Path, withModel: Boolean = true): Unit =
    modelCalls = 0; fallbacks = 0; sinceSave = 0
    saveRoot = Some(root) // enable incremental cache flushing
    loadOverrides(root)
    loadCache(root)
    if withModel then ensureModel()
    else { ollamaOk = false; println("  [dryrun] model disabled — all units kept Swedish (pipeline structural test)") }
    println(s"  translate init: concepts=${concepts.size} authoritative=${authoritative.size} " +
      s"overrides=${overrides.size} cache=${cache.size} model=$SelectedModel threads=$numThreads ollamaOk=$ollamaOk")

  // ---------- commands ----------
  def clean(root: os.Path): Unit =
    val f = cacheFile(root)
    if os.exists(f) then { os.remove(f); println(s"autotranslate --clean: removed $f (next run re-translates from scratch)") }
    else println(s"autotranslate --clean: no cache to remove at $f")

  /** P0 self-test: exercise the precedence + fallback on a few plain sentences. */
  def selftest(root: os.Path): Unit =
    println(s"autotranslate --selftest: model=$SelectedModel seed=$Seed")
    loadOverrides(root)
    loadCache(root)
    ensureModel()
    println(s"  concepts: ${concepts.size}, term pairs: ${termPairs.size}, authoritative sentences: ${authoritative.size}, " +
      s"overrides: ${overrides.size}, cache: ${cache.size}")

    val authSample = concepts.find(_.svLongExplanation.nonEmpty)
    val samples: Seq[String] = Seq(
      authSample.map(_.svLongExplanation).getOrElse("En klass är en mall för objekt."),
      "En klass är en mall som beskriver struktur och beteende.",
      "Detta är en enkel mening om programmering i Scala."
    )
    println("\n  --- translations ---")
    for sv <- samples do
      val tier = sourceOf(sv)
      val en = translate(sv)
      println(s"\n  [$tier] SV: ${sv.take(110)}${if sv.length > 110 then "…" else ""}")
      println(s"          EN: ${en.take(110)}${if en.length > 110 then "…" else ""}")

    saveCache(root)
    println(s"\n  done. model calls: $modelCalls, fallbacks: $fallbacks, cache now: ${cache.size}")
    if modelCalls == 0 then println("  (no model calls — everything came from authoritative/override/cache)")
