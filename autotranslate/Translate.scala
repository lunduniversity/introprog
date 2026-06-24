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

  // ---------- model catalogue ----------
  type Name = String
  case class Model(name: Name, description: String)

  val catalogue: Map[Name, Model] = Seq(
    Model("qwen2.5:3b",   "Qwen2.5 3B — small & fast; baseline sv→en quality (runs on CPU)"),
    Model("qwen2.5:7b",   "Qwen2.5 7B — general multilingual, good sv→en; fits a 6GB GPU"),
    Model("qwen2.5:14b",  "Qwen2.5 14B — higher quality; needs >6GB VRAM or a strong CPU"),
    Model("gemma2:9b",    "Gemma2 9B — strong European/Nordic prose"),
    Model("gemma3:latest","Gemma3 4B — multilingual, small & fast"),
    Model("aya23:35b",    "Aya23 35B — top translation quality; GPU-class (~20GB), won't fit a 6GB card")
  ).map(m => m.name -> m).toMap

  /** The model to use — a catalogue key. Whether it is served by the modly model server (GPU) or the
    * local CPU Ollama is resolved at runtime: modly is preferred if reachable AND has this model. */
  val SelectedModel: Name = "qwen2.5:7b" // <-- change this to switch model

  val Seed = 42
  val ModlyUrl = "http://bjornyx.local:8080" // GPU model server (modly) on the LAN
  val OllamaChat = "http://localhost:11434/api/chat" // local CPU Ollama (this box)
  val OllamaTags = "http://localhost:11434/api/tags"

  /** Where model calls go. Resolved at init; printed so it's always clear which is in use. */
  enum Backend(val label: String):
    case Modly extends Backend(s"modly model server ($ModlyUrl)")
    case Local   extends Backend(s"local CPU via Ollama (this box, $OllamaChat)")
    case Offline extends Backend("NO backend reachable — translations fall back to Swedish")

  // Inference threads: llama.cpp wants PHYSICAL cores (hyperthreads hurt — benchmarked: 4 beats 8
  // on an i7-4790K). Detect physical cores from /proc/cpuinfo; fall back to logical/2; override below.
  val ThreadsOverride: Option[Int] = Some(3) // leave a core for the OS — gentler heat after the crash

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

  /** Load the cache, dropping any entry that fails current validation (self-heals after guards get
    * stricter) — dropped keys get re-translated under the new guards on the next run. */
  def loadCache(root: os.Path): Unit =
    readTsv(cacheFile(root), cache)
    // self-heal step 1: re-apply deterministic normalization (e.g. " -> ``'') to stored values
    var fixed = 0
    for (sv, en) <- cache.toList do
      val n = normalize(en); if n != en then { cache(sv) = n; fixed += 1 }
    // self-heal step 2: drop entries still failing validation (re-translated under current guards)
    val bad = cache.iterator.filterNot((sv, en) => validate(sv, en)).map(_._1).toList
    bad.foreach(cache.remove)
    if fixed > 0 then println(s"  [cache] normalized $fixed entries")
    if bad.nonEmpty then println(s"  [cache] dropped ${bad.size} invalid entries (re-translate under current guards)")
  def saveCache(root: os.Path): Unit = writeTsv(cacheFile(root), cache)
  def loadOverrides(root: os.Path): Unit = readTsv(overridesFile(root), overrides)

  // ---------- backend: modly model server (GPU) preferred, else local CPU Ollama ----------
  var backend: Backend = Backend.Offline

  private def httpGet(url: String, timeoutMs: Int): Option[String] =
    try Some(requests.get(url, readTimeout = timeoutMs, connectTimeout = timeoutMs).text())
    catch case _: Throwable => None

  private def httpPost(url: String, body: String, timeoutMs: Int): Option[String] =
    try Some(requests.post(url, data = body, headers = Map("Content-Type" -> "application/json"),
      readTimeout = timeoutMs, connectTimeout = timeoutMs).text())
    catch case _: Throwable => None

  /** Models reported by the modly server; None if modly is unreachable. */
  def modlyModels(): Option[List[String]] =
    httpGet(s"$ModlyUrl/models", 3000).flatMap { s =>
      try Some(ujson.read(s)("models").arr.map(_.str).toList) catch case _: Throwable => None
    }

  /** Models in the local Ollama (via /api/tags); Nil if unreachable. */
  def localModels(): List[String] =
    httpGet(OllamaTags, 3000).flatMap { s =>
      try Some(ujson.read(s)("models").arr.map(_("name").str).toList) catch case _: Throwable => None
    }.getOrElse(Nil)

  /** Resolve which backend serves SelectedModel and PRINT model + description + backend.
    * Prefers modly (GPU) if reachable AND it has the model; else local Ollama; else offline. */
  def resolveBackend(): Unit =
    val m = catalogue.getOrElse(SelectedModel, Model(SelectedModel, "(uncatalogued model)"))
    val mm = modlyModels()
    backend =
      if mm.exists(_.contains(SelectedModel)) then Backend.Modly
      else if localModels().contains(SelectedModel) then Backend.Local
      else Backend.Offline
    if backend == Backend.Modly then
      // tell modly the active model + deterministic defaults once (used by /generate)
      httpPost(s"$ModlyUrl/set-model",
        ujson.write(ujson.Obj("model" -> SelectedModel, "temperature" -> 0, "seed" -> Seed)), 120000)
    println(s"  model:   ${m.name} — ${m.description}")
    println(s"  backend: ${backend.label}")
    println(s"  modly: ${mm.map(ms => s"UP (${ms.size} models)").getOrElse("down/unreachable")} ($ModlyUrl)")
    if backend == Backend.Offline then
      println(s"  [warn] '$SelectedModel' is on neither modly nor local Ollama — keeping Swedish.")

  // ---------- model call ----------
  var modelCalls = 0
  var fallbacks = 0

  /** Authoritative term pairs whose Swedish term occurs in `t`, formatted for the prompt. */
  def glossaryFor(t: String): String =
    val low = t.toLowerCase
    termPairs.collect { case (sv, en) if sv.length >= 4 && low.contains(sv.toLowerCase) => s"$sv = $en" }
      .toSeq.sorted.take(40).mkString("; ")

  // LaTeX special chars that were all masked away — the model must not invent them in prose.
  val Specials: String = "{}\\$%&#^_~"

  /** Post-process raw model output into build-safe, house-style LaTeX prose. Deterministic (no model
    * call), so it is safe to also re-apply to cached entries on load (self-healing).
    *
    * Converts straight ASCII double-quotes "x" to directional ``x'': the compendium loads
    * babel[swedish], where " is an ACTIVE shorthand character. A stray " (e.g. the model's "scal")
    * makes pdflatex die with "! Incomplete \iffalse" deep inside babel's shorthand expansion. The
    * Swedish source never uses " (house style is ``...''), so qwen's " is the sole source. */
  def normalize(en: String): String =
    if !en.contains('"') then en
    else
      val sb = StringBuilder(); var open = true
      en.foreach: c =>
        if c == '"' then { sb ++= (if open then "``" else "''"); open = !open }
        else sb += c
      sb.toString

  /** qwen sometimes ignores "output ONLY the translation" and emits meta-commentary such as
    * `Note: The term "dator" was translated to "computer" as per the official translation provided.`
    * These phrases never occur in real course prose, so treat them as a failed unit (keep Swedish). */
  private val leakMarkers = List("was translated", "official translation", "translation provided")
  private def looksLikeLeak(en: String): Boolean =
    val low = en.toLowerCase
    leakMarkers.exists(low.contains)

  /** All structural guards a translated unit must satisfy. Applied to BOTH fresh model output and
    * cached entries on load, so the cache self-heals when the guards get stricter. */
  def validate(sv: String, en: String): Boolean = invalidReason(sv, en).isEmpty

  /** None if `en` is a structurally-safe translation of masked `sv`; else the first failing reason. */
  def invalidReason(sv: String, en: String): Option[String] =
    if en.isEmpty then Some("empty")
    else if en.length > sv.length * 4 + 80 then Some("too long")
    else if looksLikeLeak(en) then Some("model meta-comment leak")
    else if Latex.placeholderSeq(en) != Latex.placeholderSeq(sv) then Some("placeholder reorder/drop")
    else if Latex.placeholderAdjacency(en) != Latex.placeholderAdjacency(sv) then Some("collapsed newline around placeholder")
    else if Latex.placeholderGapText(en) != Latex.placeholderGapText(sv) then Some("merged content between placeholders")
    else if Latex.stripPlaceholders(en).exists(c => Specials.contains(c)) then Some("introduced LaTeX special")
    else if Latex.stripPlaceholders(en).exists(c => isForeignLetter(c) && !sv.contains(c)) then Some("foreign-script char (e.g. CJK)")
    else None

  /** A letter from a non-Latin script (e.g. CJK, Cyrillic) — qwen sometimes leaks these; pdflatex
    * can't typeset undefined Unicode. Swedish åäö are Latin script, so they're allowed. */
  private def isForeignLetter(c: Char): Boolean =
    c.isLetter && Character.UnicodeScript.of(c.toInt) != Character.UnicodeScript.LATIN

  /** System instructions (+ glossary) shared by both backends. */
  private def buildSystem(sv: String): String =
    val gloss = glossaryFor(sv)
    "You are a precise Swedish-to-English translator for an introductory Scala programming course. " +
      "Translate the Swedish to natural English. Keep numbers, symbols and code unchanged. " +
      "Keep every placeholder token of the form __C0__, __C1__, ... EXACTLY as-is and in place. " +
      "Output ONLY the translation: no quotes, no notes, no extra text." +
      (if gloss.nonEmpty then s" Use these official term translations where they occur: $gloss." else "")

  private def checkOut(sv: String, out: String): Option[String] =
    val cleaned = normalize(out) // build-safe post-processing (e.g. " -> ``''), then validate
    invalidReason(sv, cleaned) match
      case None         => Some(cleaned)
      case Some(reason) => println(s"  [fallback] $reason, kept Swedish for: ${sv.take(60)}"); None

  /** Translate one unit via the resolved backend; None on failure/offline (caller keeps Swedish).
    * temperature 0 + fixed seed on both backends ⇒ reproducible. */
  def modelTranslate(sv: String): Option[String] = backend match
    case Backend.Offline => None
    case Backend.Local =>
      val payload = ujson.Obj(
        "model" -> SelectedModel,
        "messages" -> ujson.Arr(
          ujson.Obj("role" -> "system", "content" -> buildSystem(sv)),
          ujson.Obj("role" -> "user", "content" -> sv)),
        "stream" -> false,
        "options" -> ujson.Obj("seed" -> Seed, "temperature" -> 0, "num_thread" -> numThreads, "num_ctx" -> 2048))
      httpPost(OllamaChat, ujson.write(payload), 600000) match
        case Some(r) => checkOut(sv, try ujson.read(r)("message")("content").str.trim catch case _: Throwable => "")
        case None    => println(s"  [fallback] local Ollama error for: ${sv.take(50)}"); None
    case Backend.Modly =>
      // modly /generate is single-prompt (Ollama /api/generate); combine instructions + masked text
      val prompt = buildSystem(sv) + "\n\nSwedish:\n" + sv + "\n\nEnglish:"
      val payload = ujson.Obj("prompt" -> prompt, "temperature" -> 0, "seed" -> Seed)
      httpPost(s"$ModlyUrl/generate", ujson.write(payload), 600000) match
        case Some(r) => checkOut(sv, try ujson.read(r)("response").str.trim catch case _: Throwable => "")
        case None    => println(s"  [fallback] modly error for: ${sv.take(50)}"); None

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
      modelTranslate(sv) match
        case Some(en) => modelCalls += 1; cache(sv) = en; noteCacheAdd(); en
        // cache the Swedish fallback too, so hopeless 3B units aren't re-tried every run (a masking
        // change gives a NEW key so it still retries; `--clean` retries all for a quality pass).
        case None => fallbacks += 1; cache(sv) = sv; noteCacheAdd(); sv
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
    if withModel then resolveBackend()
    else { backend = Backend.Offline; println("  [dryrun] backend disabled — all units kept Swedish (pipeline structural test)") }
    println(s"  translate init: concepts=${concepts.size} authoritative=${authoritative.size} " +
      s"overrides=${overrides.size} cache=${cache.size} threads=$numThreads")

  // ---------- commands ----------
  def clean(root: os.Path): Unit =
    val f = cacheFile(root)
    if os.exists(f) then { os.remove(f); println(s"autotranslate --clean: removed $f (next run re-translates from scratch)") }
    else println(s"autotranslate --clean: no cache to remove at $f")

  /** P0 self-test: exercise the precedence + fallback on a few plain sentences. */
  def selftest(root: os.Path): Unit =
    println(s"autotranslate --selftest: seed=$Seed")
    loadOverrides(root)
    loadCache(root)
    resolveBackend()
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
