//> using scala 3.3.4

// SwedishScore — decide whether a piece of text is untranslated SWEDISH, deterministically, with no
// model. Built for two consumers that currently each carry their own heuristic:
//   (a) the class 3+4 cache purge (introprog#960/#962): which key==value rows are stale Swedish fallbacks
//   (b) the CI prose-leaks ratchet: how much Swedish is left in the English PDFs
// and it generalises the 10-word list already proven in scratch/swedish-scan.scala.
//
// THE DESIGN CONSTRAINT, and everything below follows from it: the two errors are NOT symmetric, AND the
// asymmetry POINTS THE OPPOSITE WAY for the two existing consumers. This is why a single boolean
// `isSwedish` cannot serve both, and why this returns a graded verdict instead:
//
//   PURGING a cache row (#960 classes 3+4, PR #962)   -> PRECISION-first.
//     miss           = stale row kept = still Swedish = status quo. Cheap.
//     false positive = a correct row deleted, re-translated, possibly WORSE than the correctly-identical
//                      term it replaced. Expensive, and irreversible without a git revert.
//
//   GATING what gets sent to the model (Code.swedishish) -> RECALL-first.
//     miss           = Swedish string never translated = a PERMANENT leak. Expensive.
//     false positive = one wasted model call on already-English text. Nearly free.
//
// So: ONE shared word list and ONE scorer, but DIFFERENT thresholds per consumer. A purge acts only on
// `Swedish`; a translation gate should act on `Swedish` OR `Weak` (and may want the content words too).
// That split is the whole reason `verdict` is three-valued rather than a boolean.
//
// NB `Code.swedishish` is declared the single source of truth in build.sbt, and its word list has grown
// reactively with CONTENT words (definierar, likhet, heltal, funkar...) added as leaks were found. Those
// are right for a recall-first gate and wrong for a precision-first purge — which is exactly the tension
// above. This file does NOT change Code.swedishish; unifying them is a separate, deliberate step.
//
// Why not a big Swedish dictionary: the rows at risk are exactly those where key == value, and words are
// identical in both languages precisely when they are SHARED (program, data, text, index, system, int).
// A big list contains those and would purge correct rows -- the very failure this is meant to prevent.
//
// PURE except `load`. No dependencies, so it runs under scala-cli or compiles into the sbt project.

import java.nio.file.{Files, Path, Paths}
import scala.jdk.CollectionConverters.*

object SwedishScore:

  /** How confident we are that a text is untranslated Swedish. Only `Swedish` is safe to act on. */
  enum Verdict:
    case Swedish, Weak, NotSwedish

  /** The evidence behind a verdict. Kept in the result so a caller can print WHY a row was chosen —
    * a purge that cannot explain itself is one nobody can review. */
  final case class Score(
      tokens: Int,        // total word tokens
      sv: Int,            // Swedish function-word hits
      en: Int,            // English function-word hits
      svLetters: Int,     // tokens containing å, ä or ö
      suffixes: Int,      // tokens with a distinctively Swedish ending
  ):
    def strong: Int = sv + svLetters
    def explain: String =
      s"tokens=$tokens sv=$sv en=$en svLetters=$svLetters suffixes=$suffixes"

  final case class Lists(swedish: Set[String], english: Set[String])

  /** Swedish definite/plural/adjective endings. Deliberately narrow: these fire on `klassen`,
    * `metoden`, `objekten`, `variabeln` — the Swedish words that carry NO å/ä/ö and would otherwise be
    * invisible. They are WEAK evidence only, never enough on their own. */
  private val SwedishSuffixes: Vector[String] =
    Vector("arna", "orna", "erna", "andet", "ningen", "ningar", "heten", "heter", "ligt", "aste")

  private val WordRx = "[\\p{L}\\p{Nd}_]+".r

  /** PURE: word tokens, lowercased. Underscores and digits stay attached so `my_var` is ONE token and
    * cannot masquerade as the function word it contains. */
  def tokenize(text: String): Vector[String] =
    WordRx.findAllIn(text.toLowerCase).toVector

  /** PURE: read a word-list file's content into a set. `#` comments and blank lines ignored. */
  def parseList(content: String): Set[String] =
    content.linesIterator
      .map(_.trim)
      .filter(l => l.nonEmpty && !l.startsWith("#"))
      .map(_.toLowerCase)
      .toSet

  /** EFFECTFUL: load both lists from a directory (default: the autotranslate dir holding this file). */
  def load(dir: Path): Lists =
    def read(name: String): Set[String] =
      val p = dir.resolve(name)
      if !Files.isRegularFile(p) then
        throw IllegalStateException(s"word list not found: $p")
      parseList(String(Files.readAllBytes(p), "UTF-8"))
    Lists(read("swedish-function-words.txt"), read("english-function-words.txt"))

  /** PURE: count the evidence in `text`. */
  def score(text: String, lists: Lists): Score =
    val toks = tokenize(text)
    Score(
      tokens = toks.size,
      sv = toks.count(lists.swedish.contains),
      en = toks.count(lists.english.contains),
      svLetters = toks.count(t => t.exists(c => "åäöÅÄÖ".contains(c))),
      suffixes = toks.count(t => t.length >= 6 && SwedishSuffixes.exists(t.endsWith)),
    )

  /** How much English evidence it takes to OVERRULE the Swedish evidence. English must DOMINATE, not
    * merely lead. Tuned against two real rows from translate-cache.tsv that pull in opposite directions:
    *
    *   "Följande guide beskriver de sista stegen under rubriken 'Adding a new SSH key...'"
    *      strong=2 en=3  -> Swedish. Swedish prose quoting an English title. A strict `sv >= en` test
    *                       called this NotSwedish, which was simply wrong.
    *   "You can use this work if you respect this LICENSE: CC BY-SA 4.0 ... Copyright ..."
    *      strong=1 en=4  -> NotSwedish. English licence text whose ONLY Swedish signal is one å/ä/ö,
    *                       almost certainly a name in the copyright line. A bare å/ä/ö test purges the
    *                       LICENCE ROW; this margin is what stops that.
    */
  val EnglishOverrulesFactor = 2

  /** PURE: the verdict, precision-first.
    *
    *  - `Swedish`    strong Swedish evidence (a function word or a Swedish letter), and English does not
    *                 DOMINATE it. The margin protects both mixed cases: English prose quoting a Swedish
    *                 identifier, and Swedish prose quoting an English title.
    *  - `Weak`       only suffix evidence, and nothing pulling the other way. Real Swedish often lands
    *                 here when it is a single word like `klassen`. Surfaced for review, NOT purged.
    *  - `NotSwedish` no evidence, or English dominates.
    */
  def verdict(s: Score): Verdict =
    if s.strong > 0 && s.en <= EnglishOverrulesFactor * s.strong then Verdict.Swedish
    else if s.strong == 0 && s.en == 0 && s.suffixes > 0 then Verdict.Weak
    else Verdict.NotSwedish

  /** PURE: the convenience predicate the purge should use. Weak deliberately counts as false — a caller
    * that wants the review pile asks for the verdict, so widening the net is always a deliberate act. */
  def isSwedish(text: String, lists: Lists): Boolean = verdict(score(text, lists)) == Verdict.Swedish

  /** PURE: the whole judgement for one cache row, which is the shape #962 needs.
    * A row is a stale Swedish fallback when the translation never happened (key == value) AND the text
    * is confidently Swedish. Both halves are required; either alone is not enough. */
  def isStaleSwedishFallback(key: String, value: String, lists: Lists): Boolean =
    key == value && isSwedish(value, lists)

  // ---- CLI, for eyeballing the lists against real data -------------------------------------------
  //   scala-cli run SwedishScore.scala -- --dir <autotranslate> --text "Detta är en klass"
  //   scala-cli run SwedishScore.scala -- --dir <autotranslate> --tsv translate-cache.tsv
  // The --tsv mode reports the counts per verdict WITHOUT touching the file.
  // NB the entry point is NOT `swedishScore`: a @main whose name differs from the enclosing object only
  // in case generates a class that collides with it on case-insensitive filesystems (macOS, Windows).
  @main def swedishScoreCli(args: String*): Unit =
    val a = args.toList
    def opt(name: String): Option[String] =
      val i = a.indexOf(name); if i >= 0 && i + 1 < a.length then Some(a(i + 1)) else None
    val dir = Paths.get(opt("--dir").getOrElse(".")).toAbsolutePath
    val lists = load(dir)
    println(s"loaded ${lists.swedish.size} swedish + ${lists.english.size} english function words")

    opt("--text").foreach: t =>
      val s = score(t, lists)
      println(s"${verdict(s)}  (${s.explain})  <- $t")

    opt("--tsv").foreach: f =>
      val path = Paths.get(f)
      val rows = Files.readAllLines(path).asScala.toVector
        .map(_.split("\t", -1)).filter(_.length >= 2)
      val keyEq = rows.filter(r => r(0) == r(1))
      val byVerdict = keyEq.groupBy(r => verdict(score(r(1), lists))).view.mapValues(_.size).toMap
      println(s"$f: ${rows.size} rows, ${keyEq.size} with key == value")
      Verdict.values.foreach(v => println(s"  $v: ${byVerdict.getOrElse(v, 0)}"))
      println(s"  -> would purge ${byVerdict.getOrElse(Verdict.Swedish, 0)} (Swedish only; Weak is the review pile)")
