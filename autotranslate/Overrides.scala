/** Manual translation overrides — the HIGHEST-precedence layer of the autotranslator.
  *
  * An entry here beats BOTH the model cache AND a fresh model translation, so this is THE place to
  * correct bad English. Because it is ordinary Scala, the compiler validates every entry: a
  * malformed line fails the BUILD, not silently at run time (unlike a hand-edited .tsv where a
  * tab turned into spaces just vanishes).
  *
  * ── HOW TO ADD A FIX ────────────────────────────────────────────────────────────────────────
  *   1. Spot the bad English in compendium-en.pdf or slides-en.
  *   2. Find the matching Swedish in the SOURCE (compendium/ or slides/): the text of one
  *      translation unit — a paragraph, the text of a `\item` bullet, or a heading's text.
  *   3. Add a line to the map below:   swedishKey -> englishValue
  *   4. Rebuild:  sbt "autotranslateProject/run --all" ; sbt pdfCompendiumEn
  *      Your line now wins over the model.
  *
  * ── STRINGS & BACKSLASHES (LaTeX!) ──────────────────────────────────────────────────────────
  *   Prefer PLAIN triple-quoted strings — backslashes are LITERAL, no escaping, and (verified on
  *   Scala 3.8) there is no `\u` gotcha, so even `\underline`/`\usepackage` are safe:
  *         """Se \Emph{rutan}""" -> """See the \Emph{box}"""
  *   A normal "..." string needs EVERY backslash doubled:        "\\Emph{x}"
  *   An s"""...""" interpolator ALSO needs doubled backslashes (the `s` processes escapes even in
  *   triple quotes) — so avoid `s"..."` here unless you genuinely need to inject a value.
  *
  * ── KEY / VALUE ─────────────────────────────────────────────────────────────────────────────
  *   KEY   = the CLEAN Swedish of ONE unit, exactly as in the source, trimmed. Plain Swedish —
  *           NOT the internal __C0__ placeholder form.
  *   VALUE = the English you want, used verbatim (may contain LaTeX commands).
  *   Duplicate keys are a compile error (Map literal) — good, it stops accidental clashes.
  */
object Overrides:
  type Swedish = String
  type English = String

  val overridingTranslations: Map[Swedish, English] = Map(
    // ── week 1: standalone concept words the model mistranslated ────────────────────────────
    "alternativ"          -> "selection",
    "repetition"          -> "repetition",
    "identifierare"       -> "identifier",
    "slumptal"            -> "random number",
    "dod: operativsystem" -> "dod: operating system",
    "Denna kurs behandlar de tre första." -> "This course covers the first three.", // paradigms slide; model fallback kept Swedish
    // "How do you learn" slide (lect-w01-about): inline \Alert/\Emph split the text into a placeholder-
    // dense unit the model mangles (placeholder reorder/drop → Swedish fallback); override the clean unit.
    // NB: the trailing `}` (closing \Emph) is peeled as structure, so the matched unit ends at "göra!"
    // with an UNBALANCED \Emph{ — key + value omit the closing brace (auto-restored as trailing structure).
    """Genom praktiskt \Alert{eget arbete}: \Emph{Lära genom att göra!""" ->
      """Through practical \Alert{own work}: \Emph{Learning by doing!""",

    // ── slide titles: clear qwen errors (false friends, conflations, bad compounds) ──────────
    "Veckoöversikt"                 -> "Weekly overview",            // qwen: "WeekOverview"
    "Vem går pgk?"                  -> "Who takes pgk?",             // "gå en kurs" = take a course
    "Kursmoment --- varför?"        -> "Course components --- why?", // "moment" = component, not "moment"
    "Laborationer"                  -> "Labs",                       // qwen: "Exercises" (clashes w/ Övningar)
    "Grundtypernas omfång"          -> "Range of the basic types", // "omfång"=range (NOT scope); grundtyp=basic (NOT JVM "primitive")
    "Grundtyper i Scala och primitiva typer Java" -> "Basic types in Scala and primitive types in Java", // mixed unit: model swapped the two terms
    "Grundtyper i Scala"            -> "Basic types in Scala", // grundtyp = basic
    "Funktion, argument, parameter" -> "Function, argument, parameter", // qwen lower-cased "function"
    "Kommandotolk (shell, ''skal'') och terminal" -> "Command interpreter (shell) and terminal", // "skal"->"scal"
    """Historik förstaspråk på D \& C vid LTH""" -> """History of the first programming language at D \& C at LTH""",

    // ── slide titles w04–w13: false friends, a factual howler, bad compounds ────────────────
    "Bogo sort"                               -> "Bogo sort",                 // a REAL algorithm — qwen "fixed" it to "Bubble sort"!
    "Paket i REPL"                            -> "Packages in REPL",          // qwen typo "Pacakets"
    "Övning: en läskig mutant"                -> "Exercise: a scary mutant",  // läskig = scary, NOT "fizzy" (läsk=soda)
    "Exempel: ofärdig kod"                    -> "Example: unfinished code",  // ofärdig = unfinished, not "unreliable"
    "Matchning med eller-mönster"             -> "Matching with or-patterns", // qwen "the-or-pattern"
    "Hitta felorsaken: debugging (avlusning)" -> "Find the cause of the error: debugging", // felorsak = cause
    "Mergekonflikt"                           -> "Merge conflict",            // qwen "MergeConflict"
    "Binärsökning"                            -> "Binary search",             // qwen "BinarySearch"
    "Variansproblem -- tack kompilatorn!"     -> "Variance problem -- thanks to the compiler!", // varians = variance, not "variation"
    "Exempel: PolygonArray, ändring på plats" -> "Example: PolygonArray, in-place modification", // på plats = in place
    "Redovisning på obligatorisk schemalagd labbtid"   -> "Presentation at mandatory scheduled lab time", // redovisning = presentation, not "redemption"
    "Vad är fördelen med egentyper i stället för arv?" -> "What is the advantage of self-types instead of inheritance?", // egentyp = self-type, not "case class"
    // NB: a title's TRAILING inline \code{}/\texttt{} is peeled as structure, so the key is the PROSE
    // ONLY (the trailing command is restored automatically — and one key covers all \code{X} variants).
    "Exempel: Överskuggning och" -> "Example: Overriding and",      // överskuggning = overriding, not "overlapping"
    "Om veckans övning:"         -> "About this week's exercise:",  // Om = About here, not "If" (matrices/expressions/...)
    "Om veckans labb:"           -> "About this week's lab:",       // (kojo/life/...)
    "Om veckans övning"          -> "About this week's exercise",   // \Subsection form (no colon/\code)
    "Om veckans labb"            -> "About this week's lab",
    "Om kursen"                  -> "About the course",             // \part heading — model: "If the Course"
    "Om ditt lärande"            -> "About your learning",           // \section heading — model: "If your learning"

    // ── code-example prose the strict CODE guards can't keep build-safe ──────────────────────────
    // Swedish comments / string literals in compendium/examples/*.{scala,java}. The model's output
    // trips the code guards ($var / \n-escape / placeholder), so these become sv==sv code-cache
    // fallbacks that the code self-heal DROPS on every load (sv==sv with åäö) → a model call every
    // `--all` run. A verbatim override is the deterministic fix (and gives real English). KEY = the
    // exact comment/string body Code.translate hands to the translator (trimmed of outer whitespace).
    // NB: string-literal keys keep the source's LITERAL `\n` (use """…""" — no escape processing);
    // the block-comment key has a REAL newline+tab (use "…" so \n\t are interpreted).
    """Vilken dörr väljer du? V=vänster H=Höger\n""" -> """Which door do you choose? V=left H=Right\n""", // irritext.scala
    """\nUtökad for-sats fungerar även med primitiva vektorer:""" -> """\nEnhanced for-statement also works with primitive arrays:""", // TestGenerics.java
    """$msg\nRätt svar: $secret""" -> """$msg\nCorrect answer: $secret""", // hangman2.scala (keep $msg/$secret/\n)
    "*\n\t * Tar bort punkten på plats pos. Efterföljande element flyttas" ->
      "*\n\t * Removes the point at position pos. Subsequent elements are shifted", // vector/Polygon.java block comment
    """\n\n*** ALLA SITT UTOM VINNARE:""" -> """\n\n*** EVERYONE SIT EXCEPT THE WINNER:""", // RegisterToggleWinner.scala

    // ── slides/simple/intro + lect-w01-intro: emphasis-dense prose the model kept Swedish. Keys copied
    //    verbatim from --sweep-fallbacks; English hand-authored (beats gemma2 — e.g. restores "is" before
    //    \Emph{executed}). Kill-Swedish-first; wording is faithful but not fussed-over. ───────────────
    """Minnet innehåller endast \Alert{heltal} som \newline representerar \Emph{data} \Alert{och} \Emph{instruktioner}.""" ->
      """Memory contains only \Alert{integers} that \newline represent \Emph{data} \Alert{and} \Emph{instructions}.""",
    """Ett \Emph{programmeringsspråk} används av människor för att skriva \Emph{källkod} som kan översättas av en \Emph{kompilator} till \Emph{maskinspråk} som i sin tur \Emph{exekveras} av en dator.""" ->
      """A \Emph{programming language} is used by people to write \Emph{source code} that can be translated by a \Emph{compiler} into \Emph{machine language}, which in turn is \Emph{executed} by a computer.""",
    """När programmet tolkas sker \Emph{evaluering} av uttrycket, vilket ger ett resultat i form av ett \Emph{värde} som har en \Emph{typ}.""" ->
      """When the program is interpreted, \Emph{evaluation} of the expression takes place, producing a result in the form of a \Emph{value} that has a \Emph{type}.""",
    """Man kan \Emph{konkatenera} strängar med operatorn +""" -> """You can \Emph{concatenate} strings with the + operator""",
    """Man kan i Scala (men inte Java) få hjälp av kompilatorn att övervaka bygget av strängar med \Emph{stränginterpolatorn} \Alert{s}:""" ->
      """In Scala (but not Java) the compiler can help you oversee the building of strings with the \Emph{string interpolator} \Alert{s}:""",
    """Det finns \Alert{inte} oändligt antal decimaler vilket ger problem med \Alert{avvrundingsfel}:""" ->
      """There is \Alert{not} an infinite number of decimals, which causes problems with \Alert{rounding errors}:""",
    """En \Emph{funktion} räknar ut \Alert{resultat} baserat på indata som kallas \Emph{argument}.""" ->
      """A \Emph{function} computes a \Alert{result} based on input data called \Emph{arguments}.""",
    """Parametrarnas typ \Alert{måste} beskrivas efter \Emph{kolon}.""" ->
      """The parameters' type \Alert{must} be described after a \Emph{colon}.""",
    """En \Emph{variabeldeklaration} medför att \Alert{plats i datorns minne} reserveras så att värden av den typ som variabeln kan referera till får plats där.""" ->
      """A \Emph{variable declaration} causes \Alert{space in the computer's memory} to be reserved so that values of the type the variable can refer to fit there.""",
    """Tilldelningssatser är \emph{inte} matematisk likhet""" -> """Assignment statements are \emph{not} mathematical equality""",
    """Först beräknas \Emph{uttrycket till höger} om tilldelningstecknet.""" ->
      """First, \Emph{the expression to the right} of the assignment sign is computed.""",
    """Sedan \Emph{ersätts värdet} som variabelnamnet refererar till av det beräknade uttrycket.""" ->
      """Then \Emph{the value is replaced} that the variable name refers to, by the computed expression.""",

    // ── w01 learning goals (modules/w01-intro-exercise-goals.tex) — render in the slide overview AND
    //    compendium-en; clean prose bullets the model kept Swedish. Keys = the \item prose, verbatim. ──
    """Förstå vad som händer när satser exekveras och uttryck evalueras.""" ->
      """Understand what happens when statements are executed and expressions are evaluated.""",
    """Känna till betydelsen av begreppen sekvens, alternativ, repetition och abstraktion.""" ->
      """Know the meaning of the concepts sequence, selection, repetition and abstraction.""",
    """Känna till litteralerna för enkla värden, deras typer och omfång.""" ->
      """Know the literals for simple values, their types and range.""",
    """Kunna deklarera och använda variabler och tilldelning, samt kunna rita bilder av minnessituationen då variablers värden förändras.""" ->
      """Be able to declare and use variables and assignment, and to draw pictures of the memory situation as variables' values change.""",
    """Förstå skillnaden mellan olika numeriska typer, kunna omvandla mellan dessa och vara medveten om noggrannhetsproblem som kan uppstå.""" ->
      """Understand the difference between different numeric types, be able to convert between them, and be aware of precision problems that can arise.""",
    """Förstå booleska uttryck och värdena \code{true} och \code{false}, samt kunna förenkla booleska uttryck.""" ->
      """Understand boolean expressions and the values \code{true} and \code{false}, and be able to simplify boolean expressions.""",
    """Förstå skillnaden mellan heltalsdivision och flyttalsdivision, samt användning av rest vid heltalsdivision.""" ->
      """Understand the difference between integer division and floating-point division, and the use of the remainder in integer division.""",
    """Förstå precedensregler och användning av parenteser i uttryck.""" ->
      """Understand precedence rules and the use of parentheses in expressions.""",
    """Kunna använda \code{if}-satser och \code{if}-uttryck.""" -> """Be able to use \code{if} statements and \code{if} expressions.""",
    """Kunna använda \code{for}-satser och \code{while}-satser.""" -> """Be able to use \code{for} statements and \code{while} statements.""",
    """Kunna använda \code{math.random()} för att generera slumptal i olika intervaller.""" ->
      """Be able to use \code{math.random()} to generate random numbers in various intervals.""",
    """Kunna beskriva skillnader och likheter mellan en procedur och en funktion.""" ->
      """Be able to describe differences and similarities between a procedure and a function.""",
  )
