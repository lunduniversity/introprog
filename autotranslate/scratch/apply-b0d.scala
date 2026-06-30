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
    // CARDS cluster (w06-patterns) — PROVISIONAL, needs BR ratification (esp. parafärg).
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

  /** returns (out, clamped, skippedResidualSwedish) */
  def apply(src: String): (String, Seq[(String, String)], Seq[String]) =
    val (masked, spans, _) = Latex.mask(src, stripEng = false)
    val clamped = collection.mutable.ArrayBuffer[(String, String)]()
    val skipped = collection.mutable.ArrayBuffer[String]()
    val spans2 = spans.map { s =>
      val k = kind(s)
      if k.isEmpty then s
      else
        val c = content(s, k)
        val candidate = Code.swedishish(c) || Glossary.touches(s)
        if !candidate then s
        else
          val en = Glossary.render(s)
          if en != s && !Code.swedishish(Glossary.render(c)) then
            clamped += ((c.replace("\n", "⏎").take(70), content(en, k).replace("\n", "⏎").take(70)))
            clamp(s, en, k)
          else
            if Code.swedishish(c) then skipped += c.replace("\n", "⏎").take(70) // wanted but not fully covered
            s
    }
    (Latex.restore(masked, spans2), clamped.toSeq, skipped.toSeq)

@main def run(args: String*): Unit =
  val file = os.Path(args.head, os.pwd)
  val write = args.contains("--write")
  val src = os.read(file)
  val (out, clamped, skipped) = Apply(src)

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
  report ++= s"self-validation: round-trip=$roundTrips brace=$braceOk ifswedishDelta=$ifswDelta (expect=${clamped.size}) -> ${if valOk then "OK" else "CHECK"}\n\n"
  report ++= "--- clamped units (sv -> en) ---\n"
  for (sv, en) <- clamped do { report ++= s"  SV: $sv\n"; report ++= s"  EN: $en\n" }
  report ++= "\n--- skipped (Swedish code units NOT in glossary — left as-is) ---\n"
  for c <- skipped do report ++= s"  $c\n"
  val reportPath = file / os.up / os.up / os.up / "autotranslate" / "scratch" / "apply-b0d-report.txt"
  os.write.over(reportPath, report.toString)
  if write then { os.write.over(file, out); println(s"[--write] applied ${clamped.size} clamps to $file") }
  println(s"B0+D ${if write then "WRITE" else "dry-run"}: clamped=${clamped.size} skipped=${skipped.size} validation=${if valOk then "OK" else "CHECK"} -> report: ${reportPath.relativeTo(os.pwd)}")
