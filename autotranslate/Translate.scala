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
  var SelectedModel: Name = "qwen2.5:7b" // <-- change this to switch model (var: --modeltest overrides it)

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
  def codeCacheFile(root: os.Path): os.Path = autotranslateDir(root) / "translate-code-cache.tsv"

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
  // Overrides live in Overrides.scala — typed, compile-checked, keyed by CLEAN Swedish (see
  // translateBlock). The cache is a generated TSV (idempotency store), self-healing on load.
  val overrides: Map[String, String] = Overrides.overridingTranslations
  private val cache = mutable.LinkedHashMap[String, String]()
  // separate idempotency store for CODE prose (comments / string literals), kept apart from the LaTeX
  // cache so the LaTeX self-heal/validate (which rejects {}, $, …) doesn't churn legitimate code text.
  private val codeCache = mutable.LinkedHashMap[String, String]()

  // Reversible TSV escaping: backslash, newline AND tab must round-trip. The old scheme mapped only
  // newline↔`\n`, so a key/value containing a LITERAL `\n` or a TAB (dense code units: REPL blocks,
  // serverLoop, Sobel matrix, the lab table) was mangled or split on reload → the unit's recomputed key
  // never matched the stored one → a model call EVERY `--all` run. Escape `\` FIRST on write; decode in a
  // single scan so `\\n` stays backslash+n (not a newline). Self-migrating: legacy single-`\` LaTeX
  // (`\Emph`) survives via the unknown-escape pass-through; the few legacy literal-`\n`/tab entries
  // re-translate once and re-save under the new scheme.
  private def enc(s: String): String =
    s.replace("\\", "\\\\").replace("\n", "\\n").replace("\t", "\\t")
  private def dec(s: String): String =
    val sb = StringBuilder(); var i = 0
    while i < s.length do
      if s(i) == '\\' && i + 1 < s.length then
        s(i + 1) match
          case 'n'  => sb += '\n'; i += 2
          case 't'  => sb += '\t'; i += 2
          case '\\' => sb += '\\'; i += 2
          case c    => sb += '\\'; sb += c; i += 2 // unknown escape (e.g. legacy single-`\` LaTeX) — keep both
      else { sb += s(i); i += 1 }
    sb.toString

  private def readTsv(f: os.Path, into: mutable.LinkedHashMap[String, String]): Unit =
    if os.exists(f) then
      for line <- os.read.lines(f) if line.contains("\t") do
        val Array(sv, en) = line.split("\t", 2)
        into(dec(sv)) = dec(en)

  private def writeTsv(f: os.Path, m: mutable.LinkedHashMap[String, String]): Unit =
    val body = m.toSeq.sortBy(_._1).map((sv, en) => s"${enc(sv)}\t${enc(en)}").mkString("\n")
    os.write.over(f, if body.isEmpty then "" else body + "\n")

  def cacheSize: Int = cache.size

  /** Load the cache, dropping any entry that fails current validation (self-heals after guards get
    * stricter) — dropped keys get re-translated under the new guards on the next run. */
  def loadCache(root: os.Path): Unit =
    readTsv(cacheFile(root), cache)
    // self-heal step 1: re-apply deterministic post-processing (quote-normalize + term enforcement,
    // e.g. grundtyp's "primitive"->"basic") to stored values — fixes cached entries without --clean.
    var fixed = 0
    for (sv, en) <- cache.toList do
      val n = enforceTerms(sv, normalize(en)); if n != en then { cache(sv) = n; fixed += 1 }
    // self-heal step 2: drop entries still failing validation (re-translated under current guards)
    // Do NOT drop guard-flagged entries: the committed cache built clean (820pp compendium-en), so they
    // are build-safe — the guards false-positive on dense units (too-long heuristic, placeholder reorder/
    // merged-content). Dropping forced a model call EVERY `--all` run with no quality gain (re-translation
    // fails identically). Keep them; fresh model output is still validated via checkOut at translate time.
    val bad = cache.iterator.filterNot((sv, en) => validate(sv, en)).size
    if fixed > 0 then println(s"  [cache] normalized $fixed entries")
    if bad > 0 then println(s"  [cache] kept $bad guard-flagged entries (trusting committed cache; not re-translating)")
    // code prose store: self-heal with the code validator (drops poisoned entries, e.g. masked $vars
    // or dropped interpolations cached before the guards existed → re-translated next run).
    readTsv(codeCacheFile(root), codeCache)
    // drop invalid entries (poisoned masking etc.) AND stale Swedish fallbacks (cached sv->sv that still
    // has åäö = the model gave up earlier). Unlike the big LaTeX cache, the code corpus is small and the
    // mirror is committed once, so retrying give-ups each run is worth the quality.
    val badCode = codeCache.iterator.filter((sv, en) =>
      codeReason(sv, enforceTerms(sv, en)).isDefined || (sv == en && sv.exists("åäöÅÄÖ".contains))
    ).map(_._1).toList
    badCode.foreach(codeCache.remove)
    if badCode.nonEmpty then println(s"  [code-cache] dropped ${badCode.size} invalid/stale entries (re-translate under current guards)")
  def saveCache(root: os.Path): Unit =
    writeTsv(cacheFile(root), cache); writeTsv(codeCacheFile(root), codeCache)

  /** `--retry-fallbacks`: drop LaTeX-cache entries that are Swedish fallbacks — en == sv AND the text
    * still contains åäö (the model gave up, usually a placeholder drop on a dense slide unit). They get
    * re-translated on this run (ideally on a stronger backend / after segmentation improvements). The
    * good translations stay cached (no re-translation). Returns how many were dropped. */
  def dropSwedishFallbacks(): Int =
    val drop = cache.iterator.filter((sv, en) => sv == en && sv.exists("åäöÅÄÖ".contains)).map(_._1).toList
    drop.foreach(cache.remove)
    drop.size

  /** Non-destructive A/B: set `model`, sample the first `n` current Swedish fallbacks (en==sv with åäö)
    * from the cache, re-translate each, and count how many now PASS validation (recover to English).
    * Does NOT save the cache — use to compare backends before committing to a full --retry-fallbacks run.
    * The sample is deterministic (cache insertion order), so the same units are tried across models. */
  def modeltest(root: os.Path, model: String, n: Int): Unit =
    SelectedModel = model
    init(root) // loads cache + resolves backend with the chosen model (no disk write)
    val fb = cache.iterator.filter((sv, en) => sv == en && sv.exists("åäöÅÄÖ".contains)).map(_._1).toVector
    val sample = fb.take(n)
    val log = collection.mutable.ArrayBuffer[String](s"modeltest: model=$model, backend=${backend.label} — sampling ${sample.size} of ${fb.size} Swedish fallbacks")
    var recovered = 0
    for (sv, i) <- sample.zipWithIndex do
      modelTranslate(sv) match
        case Some(en) if en != sv => recovered += 1; log += f"  [OK ${i + 1}%2d] ${sv.take(55)}  ->  ${en.take(55)}"
        case _                    => log += f"  [-- ${i + 1}%2d] ${sv.take(80)}"
    log += s"=== $model recovered $recovered / ${sample.size} sampled fallbacks ==="
    val out = root / "autotranslate" / "scratch" / s"modeltest-${model.replace(":", "-").replace("/", "-")}.txt"
    os.write.over(out, log.mkString("\n") + "\n")
    log.foreach(println)
    println(s"(report written to $out)")

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
  var overrideHits = 0

  // ---------- override-suggestion capture (--dump-overrides) ----------
  // When on, every unit that resolves to the MODEL tier (not override/authoritative/cache) is recorded
  // with its CLEAN Swedish key (the form Overrides.scala uses) + the model's English, so the misses that
  // keep a `--all` run from being 0-model-calls can be curated into Overrides.scala. Pure diagnostics.
  var captureSuggestions = false
  var dumpFallbacks = false // --sweep-fallbacks: record SLIDE units whose result is still Swedish
  var currentLabel = ""
  val suggestions = mutable.ArrayBuffer[(String, String, String, String)]() // (kind, label, cleanSv, en)

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

  /** Source-aware TERM enforcement (deterministic; safe to re-apply to cached entries on load).
    * "grundtyp" (basic type) is a beginner-friendly term and must NOT become "primitive type" — that
    * is a JVM term of art (e.g. String is NOT a JVM primitive). The source uses "primitiv" separately
    * (~50×) for genuine primitives, so we only rewrite units whose Swedish has grundtyp and NOT
    * primitiv. Extend this map as more such pairs are found. */
  def enforceTerms(sv: String, en: String): String =
    val low = sv.toLowerCase
    if low.contains("grundtyp") && !low.contains("primitiv")
    then en.replace("primitive", "basic").replace("Primitive", "Basic")
    else en

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
    // STRICT placeholder structure (order-sensitive). A relaxation that allowed reordering was tried
    // and reverted: it recovered only ~292 units (most complex-unit fallbacks are genuine placeholder
    // DROPs, not reorders) and let a mangled unit through into a build break — a bad trade vs the
    // build-safety priority. Keep strict.
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
    val cleaned = enforceTerms(sv, normalize(out)) // build-safe + term post-processing, then validate
    invalidReason(sv, cleaned) match
      case None         => Some(cleaned)
      case Some(reason) => println(s"  [fallback] $reason, kept Swedish for: ${sv.take(60)}"); None

  /** Validator for CODE prose (comments / string literals): NO LaTeX placeholder/specials guards
    * (code legitimately has {}, $, …), and NO quote-normalization (a `"` must stay literal). Instead
    * reject anything that would break the code: an introduced `"` or newline. */
  private val interpRe = raw"\$$\{[^}]*\}|\$$[A-Za-z_][A-Za-z0-9_]*".r
  private val escapeRe = raw"\\.".r
  private val placeholderLeakRe = raw"__C\d".r // masking-placeholder leak (e.g. __C0__) from an old code path
  /** Pure validity check for translated CODE prose (no printing) — used by both the validator and the
    * code-cache self-heal. `cleaned` is the post-enforceTerms candidate. */
  private def codeReason(sv: String, cleaned: String): Option[String] =
    // string-interpolation tokens ($x, ${...}) must survive verbatim, else the value is silently lost
    val svInterps = interpRe.findAllIn(sv).toSet
    // escape sequences (\n, \t, \", \\, …) must survive too — dropping a \n silently changes I/O
    val svEscapes = escapeRe.findAllIn(sv).toList
    if cleaned.isEmpty then Some("empty")
    else if cleaned.length > sv.length * 4 + 80 then Some("too long")
    else if looksLikeLeak(cleaned) then Some("leak")
    else if placeholderLeakRe.findFirstIn(cleaned).isDefined && placeholderLeakRe.findFirstIn(sv).isEmpty then Some("placeholder leak")
    // reject only delimiters the source didn't have (a single-line "..." or // breaks; a multi-line
    // """...""" legitimately keeps its newlines and inner quotes, so those are fine if sv had them).
    else if cleaned.contains('"') && !sv.contains('"') then Some("introduced quote")
    else if cleaned.contains('\n') && !sv.contains('\n') then Some("introduced newline")
    else if svInterps.exists(t => !cleaned.contains(t)) then Some("dropped interpolation")
    else if svEscapes.distinct.exists(e => escapeRe.findAllIn(cleaned).count(_ == e) < svEscapes.count(_ == e)) then Some("dropped escape")
    else if cleaned.exists(c => isForeignLetter(c) && !sv.contains(c)) then Some("foreign-script")
    else None

  private def checkOutCode(sv: String, out: String): Option[String] =
    val cleaned = enforceTerms(sv, out)
    codeReason(sv, cleaned) match
      case None => Some(cleaned)
      case Some(r) => println(s"  [code-fallback] $r: ${sv.take(50)}"); None

  /** Translate one unit via the resolved backend; None on failure/offline (caller keeps Swedish).
    * temperature 0 + fixed seed on both backends ⇒ reproducible. `check` validates the raw output. */
  def modelTranslate(sv: String, check: (String, String) => Option[String] = checkOut): Option[String] = backend match
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
        case Some(r) => check(sv, try ujson.read(r)("message")("content").str.trim catch case _: Throwable => "")
        case None    => println(s"  [fallback] local Ollama error for: ${sv.take(50)}"); None
    case Backend.Modly =>
      // modly /generate is single-prompt (Ollama /api/generate); combine instructions + masked text
      val prompt = buildSystem(sv) + "\n\nSwedish:\n" + sv + "\n\nEnglish:"
      val payload = ujson.Obj("prompt" -> prompt, "temperature" -> 0, "seed" -> Seed)
      httpPost(s"$ModlyUrl/generate", ujson.write(payload), 600000) match
        case Some(r) => check(sv, try ujson.read(r)("response").str.trim catch case _: Throwable => "")
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

  /** Translate CODE prose (a comment / string-literal body) — preserves leading/trailing whitespace,
    * uses the code-safe validator + a separate code cache. Passed to Code.translate as `tr`. */
  def translatePlain(sv: String): String =
    val lead = sv.length - sv.dropWhile(_.isWhitespace).length
    val trail = sv.length - sv.reverse.dropWhile(_.isWhitespace).length
    if lead + trail >= sv.length then sv
    else
      val core = sv.substring(lead, sv.length - trail)
      val willModel = captureSuggestions &&
        !overrides.contains(core) && !authoritative.contains(core) && !codeCache.contains(core)
      val en = overrides.get(core).orElse(authoritative.get(core)).orElse(codeCache.get(core)).getOrElse {
        modelTranslate(core, checkOutCode) match
          case Some(t) => modelCalls += 1; codeCache(core) = t; noteCacheAdd(); t
          case None    => fallbacks += 1; codeCache(core) = core; noteCacheAdd(); core
      }
      if willModel then suggestions += (("code", currentLabel, core, en))
      sv.substring(0, lead) + en + sv.substring(sv.length - trail)

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
  private def translateBlock(b: String, spans: IndexedSeq[String], collapseEmph: Boolean = false): String =
    val lead = leadLen(b)
    val trail = trailStart(b)
    if trail <= lead then b // all whitespace/placeholders, no prose
    else
      val core = b.substring(lead, trail)
      if !Latex.hasText(core) then b
      else
        // CONTRIBUTOR OVERRIDE (highest precedence): match the unit's CLEAN (unmasked) Swedish, so
        // overrides.tsv keys are plain Swedish exactly as in the source — no internal __C0__ forms.
        // The override value is used verbatim (the contributor writes natural English incl. any LaTeX),
        // so it also works for units that contain inline commands.
        val clean = Latex.restore(core, spans).trim
        overrides.get(clean) match
          case Some(en) => overrideHits += 1; b.substring(0, lead) + en + b.substring(trail)
          case None     =>
            val willModel = captureSuggestions && sourceOf(core) == "model"
            val en = translate(core)
            if willModel then suggestions += (("tex", currentLabel, clean, Latex.restore(en, spans).trim))
            // TIER-2 RETRY: the exposed-emphasis translation fell back (still Swedish) on a unit WITH
            // inline emphasis → re-translate with emphasis collapsed to single placeholders (child-
            // translated). Tier-1 (exposed) keeps in-context grammar for the units that succeed; only
            // the placeholder-dense fallbacks pay the isolated-emphasis cost.
            if !collapseEmph && Code.swedishish(Latex.restore(en, spans)) && Latex.hasEmphArg(clean) then
              b.substring(0, lead) + translateRegion(Latex.restore(core, spans), currentLabel, collapseEmph = true) + b.substring(trail)
            else
              if dumpFallbacks && currentLabel.startsWith("slides/") && Code.swedishish(Latex.restore(en, spans)) then
                suggestions += (("sv-fallback", currentLabel, clean, "")) // slide unit still Swedish after retry
              b.substring(0, lead) + en + b.substring(trail)

  /** Translate one region (mask -> segment -> translate prose blocks -> restore). */
  private def translateRegion(region: String, label: String, collapseEmph: Boolean = false): String =
    currentLabel = label
    val (masked, spans, itemIdx) = Latex.mask(region, stripEng = true, collapseEmph = collapseEmph)
    val (blocks, seps) = Latex.segmentMasked(masked, itemIdx)
    val translated = Array.from[String](blocks)
    for k <- blocks.indices do
      if Latex.hasText(blocks(k)) then
        translated(k) = translateBlock(blocks(k), spans, collapseEmph)
        doneUnits += 1
        printBar(label, force = false)
    val sb = StringBuilder()
    for k <- blocks.indices do
      sb ++= translated(k); if k < seps.size then sb ++= seps(k)
    // When emphasis was collapsed (tier-2), translate each \Emph/\Alert/... span's inner text as a child
    // unit. No-op under tier-1 (exposed) masking, where emphasis isn't a whole-command span.
    val finalSpans = if !collapseEmph then spans
      else spans.map(sp => Latex.reEmphArg(sp, child => translateRegion(child, label, collapseEmph = true)))
    Latex.restore(sb.toString, finalSpans)

  /** Translate a .tex body. For a MAIN document, ONLY the matter between \begin{document} and
    * \end{document} is translated: the preamble (\documentclass, \usepackage[..]{..}, \geometry,
    * lengths, macro setup, ...) is pure LaTeX, not prose, and translating it mangles the build
    * (\usepackage[swedish]{babel} -> "\usepackage Scala{babel}" -> File `S.sty' not found). Any
    * post-\end{document} matter is preserved too. \input fragments (no \begin{document}) translate
    * whole — that is where the real content lives. */
  def translateTex(body: String, label: String = ""): String =
    val bTok = "\\begin{document}"
    val bi = body.indexOf(bTok)
    if bi < 0 then translateRegion(body, label) // \input fragment
    else
      val bodyStart = bi + bTok.length
      val ei = body.lastIndexOf("\\end{document}")
      if ei < bodyStart then
        body.substring(0, bodyStart) + translateRegion(body.substring(bodyStart), label)
      else
        body.substring(0, bodyStart) + translateRegion(body.substring(bodyStart, ei), label) + body.substring(ei)

  // ---------- lifecycle ----------
  /** Load cache and make sure the model is ready (called before mirror translation).
    * Overrides need no loading — they are compiled in (Overrides.overridingTranslations). */
  def init(root: os.Path, withModel: Boolean = true): Unit =
    modelCalls = 0; fallbacks = 0; overrideHits = 0; sinceSave = 0
    saveRoot = Some(root) // enable incremental cache flushing
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

  /** Quick, MODEL-FREE progress metric for the Overrides ralph-loop (see handover-to-codex.md): count the
    * DISTINCT Swedish-looking lines remaining in the generated `-en` mirrors, reusing `Code.swedishish`
    * (the same åäö + Swedish-stop-word notion the translator uses to decide what to translate). Scans the
    * already-generated `-en` .tex (run after a `--all` pass); prints the corpus total + the worst files so
    * the loop can target them and watch the number fall. */
  def checkHowMuchSwedishLeft(root: os.Path): Unit =
    val all = mutable.LinkedHashSet[String]()      // distinct Swedish-looking lines
    val allLines = mutable.LinkedHashSet[String]() // distinct non-empty lines (the % denominator)
    val perFile = mutable.ArrayBuffer[(String, Int)]()
    for dir <- Seq("slides-en", "compendium-en"); d = root / dir if os.exists(d)
        f <- os.walk(d) if os.isFile(f) && Set("tex", "scala", "java")(f.ext) // incl. CODE files (human-fix at source)
    do
      val lines = os.read.lines(f).iterator.map(_.trim).filter(_.nonEmpty).toVector
      val sw = lines.filter(Code.swedishish).distinct
      if sw.nonEmpty then perFile += ((f.relativeTo(root).toString, sw.size))
      all ++= sw; allLines ++= lines
    val total = allLines.size
    val pct = if total == 0 then 0.0 else all.size * 100.0 / total
    if all.isEmpty then println(s"  swedish-left: 0% — fully English ✅ ($total distinct lines, ${perFile.size} files)")
    else println(f"  swedish-left: $pct%.1f%% Swedish — ${all.size}/$total distinct lines across ${perFile.size} -en files")
    perFile.sortBy(-_._2).take(20).foreach((p, c) => println(f"    $c%4d  $p"))

  /** Insourced PDF Swedish scan (was scratch/pdf-swedish.scala): pdftotext → the fraction of DISTINCT
    * non-empty lines that `Code.swedishish` flags. The *En build tasks call this so they ALWAYS report
    * how close to 0% we are. Prints a clear `0% — fully English ✅` or `X.X% Swedish ⚠`; writes the full
    * Swedish-line list next to the PDF (swedish-<name>.txt) for inspection. Fail-safe (no pdftotext → skip). */
  def pdfSwedish(root: os.Path, pdfStr: String): Unit =
    val pdf = os.Path(pdfStr, root)
    if !os.exists(pdf) then { println(s"  [swedish] no PDF at $pdf"); return }
    // pdftotext is OPTIONAL: if the binary is missing OR exits non-zero, just WARN (with install hint) and
    // skip the report — never throw, so a `--pdf-swedish` / *En build is NEVER killed by a missing tool.
    val res =
      try os.proc("pdftotext", "-layout", pdf.toString, "-").call(check = false)
      catch case _: Throwable =>
        println("  [swedish] WARNING: 'pdftotext' not found — install it for the Swedish-% report " +
          "(e.g. `sudo apt install poppler-utils`); skipping (build NOT affected).")
        return
    if res.exitCode != 0 then
      println(s"  [swedish] WARNING: pdftotext failed (exit ${res.exitCode}) on ${pdf.last} — " +
        "install/repair poppler-utils; skipping the Swedish-% report (build NOT affected).")
      return
    val lines = res.out.text().linesIterator.map(_.trim).filter(_.nonEmpty).toVector.distinct
    val sw = lines.filter(Code.swedishish)
    val pct = if lines.isEmpty then 0.0 else sw.size * 100.0 / lines.size
    val outFile = pdf / os.up / s"swedish-${pdf.baseName}.txt"
    os.write.over(outFile, if sw.isEmpty then "" else sw.mkString("\n") + "\n")
    if sw.isEmpty then { println(s"  [swedish] ${pdf.last}: 0% — fully English ✅ (${lines.size} lines)"); os.remove(outFile) }
    else
      println(f"  [swedish] ${pdf.last}: $pct%.1f%% Swedish — ${sw.size}/${lines.size} distinct lines ⚠")
      // Accepted residual is mostly Swedish in CODE examples (identifiers/strings). List the lines a human
      // can fix at the source (grep the source tree for them): full list written next to the PDF.
      println(s"            lines to fix (incl. code) -> ${outFile}")

  /** P0 self-test: exercise the precedence + fallback on a few plain sentences. */
  def selftest(root: os.Path): Unit =
    println(s"autotranslate --selftest: seed=$Seed")
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
