import scala.util.matching.Regex

object CodeGlossary:
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
    // wrapper object names of the vego example programs (compendium/examples/.../vego1-4.scala).
    "exempelVego1" -> "exampleVego1", "exempelVego2" -> "exampleVego2",
    "exempelVego3" -> "exampleVego3", "exempelVego4" -> "exampleVego4",
    // VEGO plural inflections used as val-names in REPL examples (lect-w10-extends).
    "gurkor" -> "cucumbers", "vikter" -> "weights",
    // GENERIC OO example (lect-w10-extends) — placeholder type names in the arv/subtyp illustration:
    // `trait Bastyp` with `class Subtyp1/Subtyp2`. NOT a domain cluster; distinct from the prose words
    // "bastyp"/"subtyp" (concepts, translated as prose). render() only touches code spans, so these
    // rename the CODE identifiers only, never the prose term.
    "Bastyp" -> "BaseType", "Subtyp1" -> "Subtype1", "Subtyp2" -> "Subtype2",
    // lect-w10-extends demo identifiers (ratified in session, pending BR confirmation): encapsulation + inheritance examples.
    // gEllerT = "Gurka eller Tomat" -> cOrT (Cucumber or Tomato); minHemlis/avslöjad = secret/reveal demo.
    "gEllerT" -> "cOrT", "minHemlis" -> "mySecret", "vårHemlis" -> "ourSecret", "avslöjad" -> "revealed",
    "pris" -> "price", "alternativ" -> "alternative", "StorGurkan" -> "BigCucumber",
    // CARDS cluster (w06-patterns) — BR-RATIFIED 2026-06-30 (sameColourSuit + Färg->Suit confirmed).
    // NOTE: Färg->Suit is CARDS-CONTEXT-ONLY; a kojo/colour file needs Färg->Colour (apply per-context).
    "Färg" -> "Suit", "Kortlek" -> "Deck",
    "Spader" -> "Spades", "Hjärter" -> "Hearts", "Ruter" -> "Diamonds", "Klöver" -> "Clubs",
    // parafärg = short for parallellfärg = the same-COLOUR suit (Spades<->Clubs black, Hearts<->Diamonds red).
    // No standard English card term exists (verified: bridge pairs suits by colour but has no single word),
    // so use the clearest self-documenting name. NOT "partnerSuit" (collides with bridge 'partner' = player).
    "parafärg" -> "sameColourSuit", "parallellFärg" -> "sameColourSuit",
    // lect-w06-matching (pattern-matching lecture) identifiers — ratified in session, pending BR confirmation.
    "smak" -> "taste", "lök" -> "onion", "testa" -> "test", "visa" -> "show", "svans" -> "tail",
    "livetsMening" -> "meaningOfLife", "LivetsMening" -> "MeaningOfLife", "ärLivetsMening" -> "isMeaningOfLife",
    "ärLivetsMeningBuggig" -> "isMeaningOfLifeBuggy", "ärLivetsMeningBackTicks" -> "isMeaningOfLifeBackTicks",
    "svar" -> "answer", "värdeAttUndersöka" -> "valueToExamine",
    "mönster1" -> "pattern1", "mönster2" -> "pattern2", "mönster3" -> "pattern3", "mönsterN" -> "patternN",
    "resultat1" -> "result1", "resultat2" -> "result2", "resultat3" -> "result3", "resultatN" -> "resultN",
    // MAYBE cluster (lect-w10-override, an Option-analog demo) — BR-ratified 2026-07-19 (#942).
    // Just/Empty avoids shadowing scala.Nothing and Option.Some/None; Empty echoes Optional.empty().
    "Kanske" -> "Maybe", "Någon" -> "Just", "Ingen" -> "Empty",
    // PERSON cluster (personExample*.scala + lect-w10-override) — BR-ratified 2026-07-19 (#942).
    "namn" -> "name", "ålder" -> "age", "universitet" -> "university", "titel" -> "title",
    "Akademiker" -> "Academic", "Forskare" -> "Researcher",
    // one-off example identifiers surfaced by the #944 coverage sweep — BR-ratified 2026-07-19 (#942).
    "Examinerad" -> "Graduated",                                       // trait Graduated { val title: String }
    "kastaTärningTillsAllaUtfallUtomEtt" -> "rollDieUntilAllOutcomesExceptOne",
    "BaklängesHandler" -> "BackwardsHandler",
  )
  // string / comment inner text (longest first so a prefix doesn't pre-empt). exact substring replace.
  val str: Seq[(String, String)] = Seq(
    "gurka är gott ibland..." -> "cucumber is tasty sometimes...",
    // NB: the vego example-file knowledge strings (Skållas.->Blanched., Skalas med skalare., Antal
    // skördade/ätvärda grönsaker:) moved to Overrides.scala — whole-unit match, resolved via Code.translate
    // for the mirrored example files (PR #943 review). Keep only slide-clamp result strings here.
    // lect-w10-extends output/string data (ratified in session, pending BR confirmation). Applied everywhere in a span, so they
    // cover both the string literal and the REPL echo (e.g. `println(" Städa Städa")` + `... Städa Städa`).
    "Det går precis lika bra med selleri!" -> "Celery works just as well!",
    "kan kanske också funka med en betongpelare" -> "could maybe also work with a concrete pillar",
    "Error: p är ej student; program saknas" -> "Error: p is not a student; program missing",
    "Städa" -> "Clean", "Prata" -> "Talk", "hej" -> "hi",
    // lect-w06-matching result/prompt strings — ratified in session, pending BR confirmation. `$`-interpolation fragments kept;
    // sortBy(-length) ensures longer strings (e.g. "inte gott :(") replace before shorter ("gott").
    "livets mening är funnen: " -> "the meaning of life is found: ",
    "en rutten gurka som väger " -> "a rotten cucumber weighing ",
    "exakt två grönsaker: " -> "exactly two vegetables: ",
    "exakt en grönsak: " -> "exactly one vegetable: ",
    " och sedan svansen: " -> " and then the tail: ",
    "smakar bakvänt: " -> "tastes backwards: ",
    "tom grönsaksvektor" -> "empty vegetable vector",
    "okänd grönsak: " -> "unknown vegetable: ",
    "fattas mening: " -> "missing meaning: ",
    "Ange en grönsak" -> "Enter a vegetable",
    "ganska gott..." -> "quite tasty...",
    "mindre gott..." -> "less tasty...",
    "gott ibland!" -> "tasty sometimes!",
    "inte gott :(" -> "not tasty :(",
    "gott, väger " -> "tasty, weighs ",
    "först en " -> "first one ",
    "jättegott!" -> "very tasty!",
    "inte gott" -> "not tasty",
    "gott!" -> "tasty!",
    "inte " -> "not ",
    " är " -> " is ",
    "gott" -> "tasty",
  ).sortBy(-_._1.length)

  private val tok: Regex = "[A-Za-zÅÄÖåäö_][A-Za-z0-9ÅÄÖåäö_]*".r
  private def renameAll(span: String): String =
    tok.replaceAllIn(span, m => Regex.quoteReplacement(id.getOrElse(m.matched, m.matched)))

  /** id rename + `str` replace over the WHOLE span (incl. inside string literals). CONTEXT-SCOPED: only for
    * the slide clamps (apply-b0d), where the `str` list is ratified per file. NOT for corpus-wide use —
    * `str` is a bare substring replace that half-translates partial-coverage strings and misfires on short
    * keys ("Städa toaletter" -> "Clean toaletter", "hej" in "hejsan"). */
  def render(span: String): String =
    renameAll(str.foldLeft(span) { case (acc, (sv, en)) => acc.replace(sv, en) })

  /** Token-exact IDENTIFIER rename applied ONLY in code regions; string/char literals and //, /* */ comments
    * are copied VERBATIM. For mirrored example .scala/.java: identifiers get renamed, but strings/comments
    * are left untouched so Code.translate + Overrides handle them on their natural Swedish (a knowledge
    * string like "Skållas."->"Blanched." resolves via Overrides; nothing half-translates). No `str` here. */
  def renderCodeIds(s: String): String =
    val out = new StringBuilder; val code = new StringBuilder; var i = 0; val n = s.length
    def flush(): Unit = if code.nonEmpty then { out ++= renameAll(code.toString); code.clear() }
    def copyChar(): Unit = // copy a '...' char literal verbatim (handles \-escapes); never interpolated
      out += s(i); i += 1
      while i < n && s(i) != '\'' do { if s(i)=='\\' && i+1 < n then { out += s(i); i += 1 }; if i < n then { out += s(i); i += 1 } }
      if i < n then { out += s(i); i += 1 }
    // an interpolator prefix (s"…"/f"…"/raw"…") is any identifier char immediately before the opening quote.
    def isInterp: Boolean = out.nonEmpty && (out.last.isLetterOrDigit || out.last == '_')
    // copy a "…" or \"\"\"…\"\"\" literal. Inside an s/f-interpolator, RENAME identifiers in ${…} and $ident
    // — they are CODE references, and leaving them Swedish breaks compilation (e.g. s"${p.namn}" after
    // namn->name -> "value namn is not a member"). Plain text stays verbatim; non-interpolated strings
    // are copied byte-for-byte.
    def copyStr(triple: Boolean, interp: Boolean): Unit =
      if triple then { out ++= "\"\"\""; i += 3 } else { out += '"'; i += 1 }
      def atClose: Boolean = if triple then i+2 < n && s(i)=='"' && s(i+1)=='"' && s(i+2)=='"' else i < n && s(i)=='"'
      while i < n && !atClose do
        val c = s(i)
        if !triple && c == '\\' && i+1 < n then { out += c; i += 1; if i < n then { out += s(i); i += 1 } }
        else if interp && c == '$' && i+1 < n && s(i+1) == '$' then { out ++= "$$"; i += 2 } // escaped literal $
        else if interp && c == '$' && i+1 < n && s(i+1) == '{' then
          out ++= "${"; i += 2; val st = i; var d = 1
          while i < n && d > 0 do { val ch = s(i); if ch=='{' then d += 1 else if ch=='}' then d -= 1; if d > 0 then i += 1 }
          out ++= renameAll(s.substring(st, i)); if i < n then { out += '}'; i += 1 }
        else if interp && c == '$' && i+1 < n && (s(i+1).isLetter || s(i+1)=='_') then
          out += '$'; i += 1; val st = i
          while i < n && (s(i).isLetterOrDigit || s(i)=='_') do i += 1
          out ++= renameAll(s.substring(st, i))
        else { out += c; i += 1 }
      if triple then { if i+2 < n then { out ++= "\"\"\""; i += 3 } else while i < n do { out += s(i); i += 1 } }
      else if i < n then { out += '"'; i += 1 }
    while i < n do
      val c = s(i)
      if c == '/' && i+1 < n && s(i+1) == '/' then { flush(); while i < n && s(i) != '\n' do { out += s(i); i += 1 } }
      else if c == '/' && i+1 < n && s(i+1) == '*' then
        flush(); out ++= "/*"; i += 2
        while i+1 < n && !(s(i)=='*'&&s(i+1)=='/') do { out += s(i); i += 1 }
        if i+1 < n then { out ++= "*/"; i += 2 } else while i < n do { out += s(i); i += 1 }
      else if c == '"' && i+2 < n && s(i+1)=='"' && s(i+2)=='"' then { flush(); copyStr(true, isInterp) }
      else if c == '"' then { flush(); copyStr(false, isInterp) }
      else if c == '\'' then { flush(); copyChar() }
      else { code += c; i += 1 }
    flush()
    out.toString
  def touches(span: String): Boolean =
    tok.findAllIn(span).exists(id.contains) || str.exists((sv, _) => span.contains(sv))
