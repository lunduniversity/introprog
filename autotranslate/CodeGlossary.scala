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
    "Akademiker" -> "Academic", "Forskare" -> "Researcher", "IckeAkademiker" -> "NonAcademic",
    // lowercase val-name forms (Task 5 REPL: `val akademiker = new Academic(...)`) — same words as the classes
    // above, just lowercased per Scala val-naming convention — BR-ratified 2026-07-21.
    "akademiker" -> "academic", "forskare" -> "researcher",
    // one-off example identifiers surfaced by the #944 coverage sweep — BR-ratified 2026-07-19 (#942).
    "Examinerad" -> "Graduated",                                       // trait Graduated { val title: String }
    "kastaTärningTillsAllaUtfallUtomEtt" -> "rollDieUntilAllOutcomesExceptOne",
    "BaklängesHandler" -> "BackwardsHandler",
    // lect-w11-context's covariant-enum demo (`enum Toalett[+T]`): a toilet's occupancy, so Occupied/Vacant as
    // on a lavatory sign. GLOBAL, not perFileId: unlike Djur/Bur there is no context conflict to scope around
    // — the three tokens occur nowhere else in slides/ or compendium/, and the surrounding `enum`/`case` are
    // Scala keywords, which a token-exact rename cannot touch (no glossary key is a keyword).
    // Proposed 2026-08-30, pending BR ratification.
    "Toalett" -> "Toilet", "Upptagen" -> "Occupied", "Ledig" -> "Vacant",
    // NB: the ANIMAL cluster (Djur/Ko/Gris/Häst/väsnas/skapaDjur/bondgård) is NOT global — it lives in
    // `perFileId` scoped to w10-inheritance-exercise. `Djur` also occurs in lect-w11-context's generics demo
    // (class Katt/Hund extends Djur) where Katt/Hund aren't in the glossary; a global Djur->Animal rendered
    // `class Katt extends Animal` (mixed). w11 now carries its OWN scoped cluster (with Katt/Hund/Robothund),
    // so the fix for a mixed render is a per-file entry, never promoting this cluster to global.
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

  // WHOLE-literal code-string translations, applied by renderCodeIds INSIDE string literals (both the mirror's
  // inline-.tex pass and example .scala go through renderCodeIds). EXACT full-content match only — never a
  // substring — so nothing half-translates (the failure mode that keeps `str` out of renderCodeIds). Use for
  // ratified string DATA that must be English but isn't reachable via Code.translate/Overrides (inline .tex
  // code never runs Code.translate). Keyed on the literal's inner text (no quotes).
  val codeStr: Map[String, String] = Map(
    // ANIMAL cluster onomatopoeia (w10-inheritance-exercise Task 3) — BR-ratified 2026-07-20: English sounds.
    "Muuuuuuu" -> "Mooooo", "Nöffnöff" -> "Oink", "Gnääääägg" -> "Neigh",
    // PERSON: the researcher's title value (Task 5 REPL: new Researcher(..., "Doktorand")) — BR-ratified 2026-07-21.
    "Doktorand" -> "PhD student",
    // lect-w11-context string DATA (proposed 2026-08-30, pending BR ratification). Exact whole-literal keys,
    // so "hej" here can only match a literal that IS "hej" (`trait Y { val y = "hej" }`) and can never
    // misfire inside a longer string the way a `str` substring would.
    "hej" -> "hi",
    "$this har behandlat $x" -> "$this has treated $x", // s-interpolated: only the prose between $refs differs
    "syrlig" -> "tart",                                 // Tomat's taste in the intersection-type demo
    // the given-value literal names its own object, so the clamped name has to appear inside the string too:
    // without this the mirror read `given aGivenString: String = "a given value in EnNamnrymd"` (English
    // prose, Swedish object name) even though the object above it had become ANamespace.
    // ("tagen för givet" needs no entry: code-cache row 1236 already renders it "taken for granted".)
    "ett givet värde i EnNamnrymd" -> "a given value in ANamespace",
    "vattning" -> "watery",                             // Gurka's taste; source looks like a typo for "vattnig"
  )

  private val tok: Regex = "[A-Za-zÅÄÖåäö_][A-Za-z0-9ÅÄÖåäö_]*".r
  // `extra` (a per-file override map) wins over the global id, so a file with a context conflict can deviate.
  private def renameAll(span: String, extra: Map[String, String]): String =
    tok.replaceAllIn(span, m => Regex.quoteReplacement(extra.getOrElse(m.matched, id.getOrElse(m.matched, m.matched))))

  /** id rename + `str` replace over the WHOLE span (incl. inside string literals). CONTEXT-SCOPED: only for
    * the slide clamps (apply-b0d), where the `str` list is ratified per file. NOT for corpus-wide use —
    * `str` is a bare substring replace that half-translates partial-coverage strings and misfires on short
    * keys ("Städa toaletter" -> "Clean toaletter", "hej" in "hejsan"). */
  def render(span: String): String =
    renameAll(str.foldLeft(span) { case (acc, (sv, en)) => acc.replace(sv, en) }, Map.empty)

  /** Token-exact IDENTIFIER rename applied ONLY in code regions; //, /* */ comments and char literals are copied
    * VERBATIM. String literals are copied verbatim TOO, except: a WHOLE-literal `codeStr` exact match is
    * translated, and s/f-interpolation bodies get identifier renames. For mirrored example .scala/.java, other
    * Swedish strings/comments are left for Code.translate + Overrides (a knowledge string like "Skållas."->
    * "Blanched." resolves via Overrides; nothing half-translates). No bare-substring `str` replace here. */
  def renderCodeIds(s: String, extraId: Map[String, String] = Map.empty): String =
    val out = new StringBuilder; val code = new StringBuilder; var i = 0; val n = s.length
    def flush(): Unit = if code.nonEmpty then { out ++= renameAll(code.toString, extraId); code.clear() }
    def copyChar(): Unit = // copy a '...' char literal verbatim (handles \-escapes); never interpolated
      out += s(i); i += 1
      while i < n && s(i) != '\'' do { if s(i)=='\\' && i+1 < n then { out += s(i); i += 1 }; if i < n then { out += s(i); i += 1 } }
      if i < n then { out += s(i); i += 1 }
    // an interpolator prefix (s"…"/f"…"/raw"…") is any identifier char immediately before the opening quote.
    def isInterp: Boolean = out.nonEmpty && (out.last.isLetterOrDigit || out.last == '_')
    // rename identifiers inside an s/f-interpolator body (no quotes): ${…} and $ident are CODE references,
    // renamed via renameAll; `$$` (escaped literal $) and plain text stay verbatim; `\x` escapes are copied
    // as-is (so a `\$` never triggers interpolation).
    def renameInterp(body: String): String =
      val b = new StringBuilder; var j = 0; val m = body.length
      while j < m do
        val c = body(j)
        if c == '\\' && j+1 < m then { b += c; j += 1; b += body(j); j += 1 }
        else if c == '$' && j+1 < m && body(j+1) == '$' then { b ++= "$$"; j += 2 }
        else if c == '$' && j+1 < m && body(j+1) == '{' then
          b ++= "${"; j += 2; val st = j; var d = 1
          while j < m && d > 0 do { val ch = body(j); if ch=='{' then d += 1 else if ch=='}' then d -= 1; if d > 0 then j += 1 }
          b ++= renameAll(body.substring(st, j), extraId); if j < m then { b += '}'; j += 1 }
        else if c == '$' && j+1 < m && (body(j+1).isLetter || body(j+1) == '_') then
          b += '$'; j += 1; val st = j
          while j < m && (body(j).isLetterOrDigit || body(j) == '_') do j += 1
          b ++= renameAll(body.substring(st, j), extraId)
        else { b += c; j += 1 }
      b.toString
    // copy a "…" or \"\"\"…\"\"\" literal. Precedence: (1) a WHOLE-literal `codeStr` exact match wins (ratified
    // string DATA, e.g. the animal sounds); (2) inside an s/f-interpolator, RENAME identifiers in ${…}/$ident
    // (leaving them Swedish breaks compilation, e.g. s"${p.namn}" after namn->name); (3) otherwise verbatim.
    def copyStr(triple: Boolean, interp: Boolean): Unit =
      val open = if triple then "\"\"\"" else "\""
      out ++= open; i += open.length
      val start = i
      def atClose: Boolean = if triple then i+2 < n && s(i)=='"' && s(i+1)=='"' && s(i+2)=='"' else i < n && s(i)=='"'
      while i < n && !atClose do { if !triple && s(i)=='\\' && i+1 < n then i += 2 else i += 1 }
      val rawInner = s.substring(start, i)                 // literal content, verbatim (escapes preserved)
      codeStr.get(rawInner) match
        case Some(en) => out ++= en
        case None     => out ++= (if interp then renameInterp(rawInner) else rawInner)
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

  // ---- per-file deviations for the mirror's inline-.tex Scala-code pass (#944/#947) ----
  // A mirror-relative path (e.g. "compendium/modules/w10-kojo-lab.tex") containing a key gets that map
  // MERGED OVER the global id (override wins). Use for a context conflict — e.g. a colour enum where `Färg`
  // must be `Colour` (not the global `Färg->Suit` for cards). Slide files with hand `\ifswedish` clamps
  // need nothing here (their clamps are masked verbatim, so the mirror pass leaves them alone).
  val perFileId: Map[String, Map[String, String]] = Map(
    // e.g. "w01-kojo" -> Map("Färg" -> "Colour", "Röd" -> "Red", "Svart" -> "Black")
    // ANIMAL cluster — scoped here (NOT global) so `Djur` doesn't bleed into files whose subclasses aren't
    // glossary'd and would render mixed code (lect-w11-context has its own entry below; lect-w06-equals still
    // has unclamped case class Hund/Katt and is deliberately left uniformly Swedish).
    // Task 3's own Fyle-bird example in this same file is hand-clamped (\ifswedish), so the mirror leaves it
    // alone (Latex.ifswedishRanges); only Task 3's unclamped REPL/Code envs get these. BR-agreed 2026-07-20.
    "w10-inheritance-exercise" -> Map(
      "Djur" -> "Animal", "Ko" -> "Cow", "Gris" -> "Pig", "Häst" -> "Horse",
      "väsnas" -> "makeNoise", "skapaDjur" -> "createAnimal", "bondgård" -> "farm",
    ),
    // lect-w10-extends type-casting section (10.1.28/10.1.30): placeholder type-names in the inline
    // \code{isInstanceOf[..]} / \code{asInstanceOf[..]} examples. Scoped here (NOT global) because `Typ` is a
    // common token — a global Typ->Type would bleed into e.g. lect-wjava's \code{Klassnamn<Typ>} (mixed).
    // BR-ratified 2026-07-24: EnVissTyp ("en viss typ") -> SomeType; Typ -> Type.
    "lect-w10-extends" -> Map(
      "EnVissTyp" -> "SomeType", "Typ" -> "Type",
      // placeholder code-file names (10.1.26): KlassensNamn (capitalised, = the public class's name) ->
      // ClassName; bastypensNamn (lowercase initial per the "multi-content file -> lowercase" rule) -> baseTypeName.
      "KlassensNamn" -> "ClassName", "bastypensNamn" -> "baseTypeName",
    ),
    // lect-w11-context: every Scala identifier in this lecture's demos is a throwaway example name, so the
    // whole file is clamp-only work -- no prose depends on the Swedish spellings. Scoped (NOT global) for the
    // same reason as the w10 cluster: `Bur`/`Typ`-style short tokens and `Djur` would bleed into files whose
    // subclasses are NOT glossary'd and render mixed code (e.g. lect-w06-equals also has case class Hund/Katt
    // and must stay uniformly Swedish until it is clamped too). Matching is per identifier TOKEN, so the
    // compounds each need their own entry -- `Robothund` does not inherit from `Hund`, and `kattBur` does
    // not inherit from `Bur`. Proposed 2026-08-30, pending BR ratification.
    // The comment text of these same demos is clamped in Overrides.scala ("lect-w11-context" section), NOT
    // here: renderCodeIds rewrites identifier tokens only, and `str` is deliberately kept out of it, so a
    // comment naming a clamped type is an Overrides whole-unit match. The committed caches need no hand-edit
    // for w11 -- an earlier fix to the translate-code-cache.tsv cell was moved to Overrides because every
    // model-less run rewrites that file (writeTsv, unless --cache-only) and undoes it.
    "lect-w11-context" -> Map(
      // upper/lower type-bound demo (class Djur/Katt/Hund/Robothund + testUpperBound/testLowerBound)
      "Djur" -> "Animal", "Katt" -> "Cat", "Hund" -> "Dog", "Robothund" -> "RobotDog",
      // variance demo: Bur = cage (co-variant container), Veterinär = the contra-variant consumer
      "Bur" -> "Cage", "Veterinär" -> "Vet",
      "bytTill" -> "replaceWith", "släppUt" -> "letOut",
      // the val names in the REPL transcripts; two spellings of the same value (djurbur / djurBur) both occur
      "djur" -> "animal", "katt" -> "cat", "djurbur" -> "animalCage", "djurBur" -> "animalCage",
      "kattBur" -> "catCage", "kattveterinär" -> "catVet",
      // self-type demo (11.x): "är denna typ", plus the trait it is mixed into ("min trait")
      "ÄrDennaTyp" -> "IsThisType", "MinTrait" -> "MyTrait",
      // given/namespace demo: "en namnrymd" / "en given sträng" / "en annan namnrymd"
      "EnNamnrymd" -> "ANamespace", "enGivenSträng" -> "aGivenString",
      "EnAnnanNamnrymd" -> "AnotherNamespace",
      // `framkalla` -> elicit, NOT summon: the very next slide introduces Predef's `summon` as "a generic
      // variant of the method framkalla", so clamping it to `summon` would make that sentence tautological
      // and put two `summon`s in the same demo. elicit keeps the Swedish pun's pedagogy intact.
      "framkalla" -> "elicit",
      // type-class demo: "använd parser"
      "användParser" -> "useParser",
      // (the covariant-enum demo's Toalett/Upptagen/Ledig live in the GLOBAL id map above — no context
      //  conflict to scope around, so this file no longer needs them)
      // contravariance demo: the vet TREATS an animal
      "behandla" -> "treat",
      // self-type / mixin demo ("kan skalas", "vikt efter skalning"); Grönsak+vikt are already global
      "KanSkalas" -> "CanBePeeled", "viktEfterSkalning" -> "weightAfterPeeling",
      // cyclic self-type demo. NOTE: the val `tulipanaros` is deliberately NOT clamped -- it is the Swedish
      // portmanteau the slide links out to (sv.wikipedia.org/wiki/Tulipanaros), i.e. the joke itself, not an
      // example identifier. Flagged for BR: clamp it too if the English deck should drop the reference.
      "Tulpan" -> "Tulip", "Ros" -> "Rose",
      // intersection-type demo ("har smak"); the `smak` -> taste rename is already global
      "HarSmak" -> "HasTaste",
      // `hej` also appears UNQUOTED in REPL echo (`= Pair(hej,42)`, `x: Int | String = hej`), where codeStr
      // cannot see it -- codeStr needs a whole quoted literal. As an id-map token it is renamed in code
      // regions only, so the quoted `val y = "hej"` still goes through codeStr. Both spellings covered.
      "hej" -> "hi",
    ),
  )
  // Mirror-relative path substrings whose inline Scala-code envs SKIP the renderCodeIds pass entirely.
  val optOut: Set[String] = Set()
  /** Per-file override map for a mirror-relative path (empty = just the global id applies). */
  def overridesFor(relPath: String): Map[String, String] =
    perFileId.iterator.collect { case (k, m) if relPath.contains(k) => m }.flatten.toMap
  def isOptedOut(relPath: String): Boolean = optOut.exists(relPath.contains)
