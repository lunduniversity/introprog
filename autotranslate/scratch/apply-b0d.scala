//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8
//> using file ../Latex.scala
//> using file ../Code.scala

// Fused B0+D applier (case (a): inline \code + verbatim code ENVS in .tex). For each code unit, compute the
// English via a ratified cluster glossary (identifier rename + string/comment replace), and clamp it as
// `\ifswedish<sv>\else<en>\fi` IFF the English is fully non-Swedish (auto-gate: a unit only partially covered
// by the glossary stays unclamped/Swedish — so no Swedish ever leaks into an \else branch). The clamp makes
// the SWEDISH build byte-identical; only the en branch changes. Reuses Latex.mask (locate) + the span-
// transform/restore round-trip + Code.swedishish (gate). DRY by default; --write applies.
//   scala-cli run autotranslate/scratch/apply-b0d.scala -- <file.tex> [--write]

import scala.util.matching.Regex

object Glossary:
  // VEGO cluster — ratified 2026-06-30 (vegomatch.scala + vego1-4 + w06-patterns), compile-verified.
  // whole-identifier rename (token-exact). longest keys are fine; token replace is exact-match, not substring.
  val id: Map[String, String] = Map(
    "Grönsak" -> "Vegetable", "grönsak" -> "vegetable", "Grönsaker" -> "Vegetables", "grönsaker" -> "vegetables",
    "Gurka" -> "Cucumber", "gurka" -> "cucumber", "ingenGurka" -> "noCucumber",
    "Tomat" -> "Tomato", "tomat" -> "tomato",
    "vikt" -> "weight", "ärRutten" -> "isRotten", "rutten" -> "rotten",
    "slumpvikt" -> "randomWeight", "slumprutten" -> "randomRotten",
    "slumpgurka" -> "randomCucumber", "slumptomat" -> "randomTomato", "slumpgrönsak" -> "randomVegetable",
    "slumpbroccoli" -> "randomBroccoli", // exercise variant (w06): Broccoli stays (English), slump-prefix -> random
    "Broccoli" -> "Broccoli", // identity: keep, but listed so a half-Swedish 'slumpbroccoli' can't slip the gate
    "ärÄtvärd" -> "isWorthEating", "skörd" -> "harvest", "ätvärda" -> "worthEating", "ärÄtbar" -> "isEdible",
    "anonymGrönsak" -> "anonymousVegetable",
    "skalningsmetod" -> "peelingMethod", "skalfaktor" -> "peelFactor", "ärSkalad" -> "isPeeled", "skala" -> "peel",
    // VEGO plural inflections used as val-names in REPL examples (lect-w10-extends).
    "gurkor" -> "cucumbers", "vikter" -> "weights",
    // GENERIC OO example (lect-w10-extends) — placeholder type names in the arv/subtyp illustration:
    // `trait Bastyp` with `class Subtyp1/Subtyp2`. NOT a domain cluster; distinct from the prose words
    // "bastyp"/"subtyp" (concepts, translated as prose). render() only touches code spans, so these
    // rename the CODE identifiers only, never the prose term.
    "Bastyp" -> "BaseType", "Subtyp1" -> "Subtype1", "Subtyp2" -> "Subtype2",
    // CARDS cluster (w06-patterns) — BR-RATIFIED 2026-06-30 (sameColourSuit + Färg->Suit confirmed).
    // NOTE: Färg->Suit is CARDS-CONTEXT-ONLY; a kojo/colour file needs Färg->Colour (apply per-context).
    "Färg" -> "Suit", "Kortlek" -> "Deck",
    "Spader" -> "Spades", "Hjärter" -> "Hearts", "Ruter" -> "Diamonds", "Klöver" -> "Clubs",
    // parafärg = short for parallellfärg = the same-COLOUR suit (Spades<->Clubs black, Hearts<->Diamonds red).
    // No standard English card term exists (verified: bridge pairs suits by colour but has no single word),
    // so use the clearest self-documenting name. NOT "partnerSuit" (collides with bridge 'partner' = player).
    "parafärg" -> "sameColourSuit", "parallellFärg" -> "sameColourSuit",
  )
  // string / comment inner text (longest first so a prefix doesn't pre-empt). exact substring replace.
  val str: Seq[(String, String)] = Seq(
    "Antal skördade grönsaker: " -> "Number of harvested vegetables: ",
    "Antal ätvärda grönsaker:  " -> "Number of vegetables worth eating:  ",
    "gurka är gott ibland..." -> "cucumber is tasty sometimes...",
    "Skalas med skalare." -> "Peeled with a peeler.",
    "Skållas." -> "Blanched.",  // BR: culinary term for peeling tomatoes (skålla = blanch), not "scald"
  ).sortBy(-_._1.length)

  private val tok: Regex = "[A-Za-zÅÄÖåäö_][A-Za-z0-9ÅÄÖåäö_]*".r
  def render(span: String): String =
    val s1 = str.foldLeft(span) { case (acc, (sv, en)) => acc.replace(sv, en) } // strings first
    tok.replaceAllIn(s1, m => Regex.quoteReplacement(id.getOrElse(m.matched, m.matched)))
  def touches(span: String): Boolean =
    tok.findAllIn(span).exists(id.contains) || str.exists((sv, _) => span.contains(sv))

// ALLOWLIST leak gate (BR-approved 2026-07-01). SOUND by construction: a clamped unit's English must contain
// NO token that isn't recognized English / Scala / glossary. Anything unknown is treated as SUSPECT SWEDISH and
// the unit stays Swedish (logged as a fall-through), so ALL diacritic-free Swedish is caught -- namn, smak, Fyle,
// KanSkalas -- INCLUDING invented pun-words that a blocklist or even a full Swedish dictionary can't. Cost: it
// over-skips genuinely-English-but-unlisted tokens (SAFE -- they just stay Swedish and surface in the fall-through
// report, which drives what to allowlist/glossary next). Contrast a blocklist (`namn` still leaked). English =
// /usr/share/dict/{american,british}-english; the report's fall-through vocabulary is the key review artefact.
object Allow:
  private def loadDict(p: String): Set[String] =
    val f = os.Path(p)
    if os.exists(f) then os.read.lines(f).iterator.map(_.trim.toLowerCase).filter(_.nonEmpty).toSet else Set.empty
  lazy val english: Set[String] =
    loadDict("/usr/share/dict/american-english") ++ loadDict("/usr/share/dict/british-english")
  // Scala keywords + common stdlib/abbreviations NOT in an English dictionary (English words are covered above):
  val scalaProg: Set[String] = Set(
    "def", "val", "var", "class", "trait", "object", "case", "match", "if", "then", "else", "for", "while", "do",
    "yield", "extends", "with", "given", "using", "import", "export", "package", "new", "this", "super", "sealed",
    "enum", "type", "lazy", "implicit", "override", "final", "private", "protected", "abstract", "null", "true",
    "false", "return", "throw", "try", "catch", "finally", "end", "derives", "opaque", "transparent", "inline",
    "erased", "infix", "as", "scala", "repl", "int", "str", "seq", "nil", "res", "args", "arg", "obj", "ctx",
    "idx", "tmp", "impl", "init", "len", "expr", "stmt", "env", "cfg", "config", "param", "params", "ptr", "ref",
    "num", "io", "os", "jvm", "sbt", "api", "cli", "url", "uri", "http", "https", "json", "html", "css", "xml",
    "ok", "todo", "fixme", "uuid", "foo", "bar", "baz", "qux", "std", "stdin", "stdout", "stderr", "util", "utils",
    "lib", "src", "dir", "msg", "err", "fn", "cb", "req", "resp", "elem", "elems", "acc", "iter", "prev", "curr",
    "vararg", "varargs", "lhs", "rhs", "xs", "ys", "gs", "kv", "toint", "tostring", "hashcode", "eq", "ne",
    // java/lib package parts + math abbreviations (false positives seen in the fall-through report):
    "java", "awt", "swing", "lang", "nio", "sql", "rgb", "nom", "denom", "div", "mul", "rem", "mod", "gcd",
    "lcm", "abs", "sqrt", "pow", "sin", "cos", "tan", "arr", "vec", "buf", "idx", "pos", "dim", "px",
    // Scala stdlib + English words the dict misses, and all-lowercase LaTeX listing/font option keys
    // captured from code-env optional args (\begin{Code}[basicstyle=...\ttfamily\selectfont]) — never Swedish:
    "println", "instantiated", "basicstyle", "ttfamily", "selectfont", "numberstyle",
  )
  private def subwords(s: String): Iterator[String] =
    "[A-Za-zÅÄÖåäö_]+".r.findAllIn(s).iterator
      .flatMap(_.split("(?<=[a-zåäö])(?=[A-ZÅÄÖ])"))       // split camelCase so KanSkalas -> kan, skalas
      .map(_.toLowerCase.filter(_.isLetter)).filter(_.nonEmpty)
  lazy val glossaryEn: Set[String] =
    (Glossary.id.values.iterator.flatMap(subwords) ++ Glossary.str.iterator.map(_._2).flatMap(subwords)).toSet
  def allowed(w: String): Boolean = w.length <= 2 || scalaProg(w) || glossaryEn(w) || english(w)
  /** distinct lowercased subwords in `en` that are NOT recognized = suspect Swedish / fall-through. */
  def suspects(en: String): Seq[String] = subwords(en).filterNot(allowed).toSeq.distinct
  def isClean(en: String): Boolean = !subwords(en).exists(w => !allowed(w))

object Apply:
  val inlineRe: Regex = raw"(?s)^\\(code|jcode|lstinline|verb)\*?(.)".r
  val envRe: Regex = raw"(?s)^\\begin\{(Code|CodeSmall|REPL|REPLnonum|REPLsmall|lstlisting|verbatim|Verbatim)\}(.*)\\end\{".r
  def inlineInner(span: String): String =
    val k = span.indexWhere(c => !c.isLetter && c != '\\', 1)
    if k < 0 || k + 1 >= span.length then "" else span.substring(k + 1, span.length - 1)
  def kind(span: String): String =
    if inlineRe.findPrefixMatchOf(span).isDefined then "inline"
    else if envRe.findPrefixMatchOf(span).isDefined then "env" else ""
  def content(span: String, k: String): String = k match
    case "inline" => inlineInner(span)
    case "env"    => envRe.findPrefixMatchOf(span).map(_.group(2)).getOrElse("")
    case _        => ""
  def clamp(sv: String, en: String, k: String): String = k match
    case "inline" => s"\\ifswedish$sv\\else$en\\fi"
    case "env"    => s"\\ifswedish\n$sv\n\\else\n$en\n\\fi"
    case _        => sv

  // Strip //-comments and /* */ comments (only) for the leak GATE, but KEEP string/char literals. Comment
  // Swedish is translated DOWNSTREAM by translateCodeEnvBodies inside the \else branch (see Translate.scala:
  // "composes with apply-b0d clamps: it also translates the comments inside an \else"), so it must NOT block
  // an identifier clamp -- this fixes the w10-extends false-skip where a Swedish comment
  // `// implementation saknas, inget =` skipped a fully-glossaried trait/case-class block.
  // Strings are KEPT deliberately: render() renames identifier tokens even INSIDE a string literal, so a
  // Swedish word used as string DATA (e.g. the n-gram demo Vector("fem","gurkor","aer",...)) gets half-
  // renamed to "cucumbers" while "fem"/"aer" stay Swedish. Keeping strings in the gate lets that residual
  // Swedish block the clamp (a skipped unit outputs the ORIGINAL, discarding the partial render). String-
  // aware so a `//` inside a string literal is not mistaken for a comment.
  def stripComments(s: String): String =
    val b = new StringBuilder; var i = 0; val n = s.length
    def copyDelimited(q: Char): Unit = // append a "..." or '...' literal verbatim (handles \-escapes)
      b += s(i); i += 1
      while i < n && s(i) != q do { if s(i)=='\\' && i+1 < n then { b += s(i); i += 1 }; if i < n then { b += s(i); i += 1 } }
      if i < n then { b += s(i); i += 1 }
    while i < n do
      val c = s(i)
      if c == '/' && i+1 < n && s(i+1) == '/' then { while i < n && s(i) != '\n' do i += 1 }
      else if c == '/' && i+1 < n && s(i+1) == '*' then { i += 2; while i+1 < n && !(s(i)=='*'&&s(i+1)=='/') do i += 1; i += 2 }
      else if c == '"' && i+2 < n && s(i+1)=='"' && s(i+2)=='"' then
        b ++= "\"\"\""; i += 3
        while i+2 < n && !(s(i)=='"'&&s(i+1)=='"'&&s(i+2)=='"') do { b += s(i); i += 1 }
        if i+2 < n then { b ++= "\"\"\""; i += 3 } else while i < n do { b += s(i); i += 1 }
      else if c == '"' then copyDelimited('"')
      else if c == '\'' then copyDelimited('\'')
      else { b += c; i += 1 }
    b.toString

  /** returns (out, clamped, skipped[preview, suspects], fallthroughVocab[token, count desc]).
    * A candidate unit is CLAMPED only if its rendered English is allowlist-clean (no suspect Swedish token);
    * otherwise it stays Swedish and its suspect tokens are logged (the fall-through BR wants tracked). */
  def apply(src: String): (String, Seq[(String, String)], Seq[(String, Seq[String])], Seq[(String, Int)]) =
    val (masked, spans, _) = Latex.mask(src, stripEng = false)
    val clamped = collection.mutable.ArrayBuffer[(String, String)]()
    val skipped = collection.mutable.ArrayBuffer[(String, Seq[String])]()
    val fall = collection.mutable.Map[String, Int]().withDefaultValue(0)
    val spans2 = spans.map { s =>
      val k = kind(s)
      if k.isEmpty then s
      else
        val c = content(s, k)
        val candidate = Code.swedishish(c) || Glossary.touches(s)
        if !candidate then s
        else
          val en = Glossary.render(s)
          val enBody = Glossary.render(c)
          val gateBody = stripComments(enBody) // gate ignores comments (handled downstream); strings kept
          if en != s && Allow.isClean(gateBody) then
            clamped += ((c.replace("\n", "⏎").take(70), content(en, k).replace("\n", "⏎").take(70)))
            clamp(s, en, k)
          else
            val susp = Allow.suspects(gateBody)
            if susp.nonEmpty || Code.swedishish(c) then
              skipped += ((c.replace("\n", "⏎").take(70), susp))
              susp.foreach(w => fall(w) += 1)
            s
    }
    val fallVocab = fall.toSeq.sortBy { case (w, n) => (-n, w) }
    (Latex.restore(masked, spans2), clamped.toSeq, skipped.toSeq, fallVocab)

@main def run(args: String*): Unit =
  val file = os.Path(args.head, os.pwd)
  val write = args.contains("--write")
  val src = os.read(file)
  val (out, clamped, skipped, fallVocab) = Apply(src)

  // structural self-validation (same as b0.scala)
  val (m1, s1, _) = Latex.mask(out, stripEng = false)
  val roundTrips = Latex.restore(m1, s1) == out
  def braces(s: String) = s.count(_ == '{') - s.count(_ == '}')
  val braceOk = braces(out) == braces(src)
  def ifsw(s: String) = Latex.mask(s, stripEng = false)._2.count(_.startsWith("\\ifswedish"))
  val ifswDelta = ifsw(out) - ifsw(src)

  val valOk = roundTrips && braceOk && ifswDelta == clamped.size
  // instrumentation-by-default: write the full detail to a report file (Read it) so the tool is never
  // wrapped in shell (no `> file`, no `2>/dev/null`, no `echo`). stdout stays a short summary.
  val report = new StringBuilder
  report ++= s"=== fused B0+D ${if write then "WRITE" else "dry-run"}: $file ===\n"
  report ++= s"clamped (translated) units: ${clamped.size}   |   skipped (not fully covered): ${skipped.size}\n"
  report ++= s"self-validation: round-trip=$roundTrips brace=$braceOk ifswedishDelta=$ifswDelta (expect=${clamped.size}) -> ${if valOk then "OK" else "CHECK"}\n"
  report ++= s"fall-through vocabulary (distinct suspect Swedish tokens left untranslated): ${fallVocab.size}\n\n"
  // The KEY review artefact (BR): every suspect token the allowlist gate left Swedish, most-frequent first.
  // Real Swedish (namn, smak, fyle...) -> add to glossary; missed English/prog token -> add to Allow.scalaProg.
  report ++= "=== FALL-THROUGH VOCABULARY (token × count — what is still Swedish) ===\n"
  for (w, n) <- fallVocab do report ++= f"  ${n}%3d  $w\n"
  report ++= "\n--- clamped units (sv -> en) ---\n"
  for (sv, en) <- clamped do { report ++= s"  SV: $sv\n"; report ++= s"  EN: $en\n" }
  report ++= "\n--- skipped candidate units (blocking suspect tokens shown) ---\n"
  for (c, susp) <- skipped do { report ++= s"  UNIT: $c\n"; report ++= s"    suspects: ${susp.mkString(", ")}\n" }
  val reportPath = file / os.up / os.up / os.up / "autotranslate" / "scratch" / "apply-b0d-report.txt"
  os.write.over(reportPath, report.toString)
  if write then { os.write.over(file, out); println(s"[--write] applied ${clamped.size} clamps to $file") }
  println(s"B0+D ${if write then "WRITE" else "dry-run"}: clamped=${clamped.size} skipped=${skipped.size} fallthrough=${fallVocab.size} validation=${if valOk then "OK" else "CHECK"} -> report: ${reportPath.relativeTo(os.pwd)}")
