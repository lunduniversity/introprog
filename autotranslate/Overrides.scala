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
    // vego example-file strings the model gets wrong (moved from CodeGlossary.str per #943 review): whole-unit
    // match via Code.translate/translatePlain — no substring misfire. Keys are the trimmed string content.
    "Skållas."                     -> "Blanched.",             // skålla = blanch, NOT scald
    "Skalas med skalare."          -> "Peeled with a peeler.", // skalare = peeler, NOT scaler
    "Antal skördade grönsaker:"    -> "Number of harvested vegetables:",
    "Antal ätvärda grönsaker:"     -> "Number of vegetables worth eating:",
    "alternativ"          -> "selection",
    "repetition"          -> "repetition",
    "identifierare"       -> "identifier",
    // standalone concept-list items (\item topptyp / \item bottentyp in w10-chaphead): the model mangled these
    // compound words into camelCase pseudo-identifiers ("topptype"/"bottomType"). The CONTEXTUAL rows already
    // say "top type"/"bottom type"; override the standalone ones to match. (Wrong cache rows purged.)
    // (The former `inmixning supertyp` -> `mixin supertype` override was dropped once BR fixed the missing comma
    //  at plan/Plan.scala (53730d5, #952): that item is now two separate concepts, `inmixning`->mixin and
    //  `supertyp`->supertype, each translating on its own via existing cache rows.)
    "topptyp"             -> "top type",
    "bottentyp"           -> "bottom type",
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

    // ── w01-intro-exercise: genuine prose the model kept Swedish (exact keys from --sweep-fallbacks) ──
    """Du behöver en dator med Scala och Kojo, se appendix~\ref{appendix:compile} och  \ref{appendix:kojo}.""" ->
      """You need a computer with Scala and Kojo, see appendix~\ref{appendix:compile} and  \ref{appendix:kojo}.""",
    """Vad har uttrycket \code{ "hej" * 3 } för typ och värde? Testa i REPL.""" ->
      """What type and value does the expression \code{ "hej" * 3 } have? Test it in REPL.""",
    """Gör så att paddan \emph{inte} fyller i något när den ritar.""" ->
      """Make the turtle \emph{not} fill in anything when it draws.""",
    """Sänker paddans penna så att den \emph{inte} ritar när den går.""" ->
      """Lowers the turtle's pen so that it does \emph{not} draw when it moves.""",
    """En sats \emph{gör} något (t.ex. skriver ut något), men resulterat inte i något användbart värde.""" ->
      """A statement \emph{does} something (e.g. prints something), but does not result in any useful value.""",
    """Deklarera en procedur \code{updateHighscore} som tar en parameter \code{points} och tilldelar \code{highscore} ett nytt värde om \code{points} är större än \code{highscore} och skriver ut strängen \code{"REKORD!"}. Om inte \code{points} är större än \code{highscore} ska strängen \code{"GE INTE UPP!"} skrivas ut. Testa proceduren i REPL.""" ->
      """Declare a procedure \code{updateHighscore} that takes a parameter \code{points} and assigns \code{highscore} a new value if \code{points} is greater than \code{highscore}, and prints the string \code{"REKORD!"}. If \code{points} is not greater than \code{highscore}, the string \code{"GE INTE UPP!"} should be printed. Test the procedure in REPL.""",
    """Gör en ny variant av \code{updateHighscore}, som \emph{inte} är en procedur utan i stället är en funktion som ger en sträng för senare utskrift.""" ->
      """Make a new variant of \code{updateHighscore} that is \emph{not} a procedure but instead a function that returns a string for later printing.""",
    """Utskrift: \code{1:krona 2:klave 3:krona 4:krona 5:klave } eller liknande beroende på vilka slumptal \code{math.random()} ger.""" ->
      """Output: \code{1:krona 2:klave 3:krona 4:krona 5:klave } or similar, depending on which random numbers \code{math.random()} produces.""",
    """Antag att \code{poäng} och \code{highscore} är heltalsvariabler medan \code{klar} är av typen \code{Boolean}.""" ->
      """Assume that \code{poäng} and \code{highscore} are integer variables while \code{klar} is of type \code{Boolean}.""",
    """Du skapar ett \code{BigInt}-heltal med \code{BigInt(2)} och kan anropa funktionen \code{pow} på en \code{BigInt} med punktnotation.""" ->
      """You create a \code{BigInt} integer with \code{BigInt(2)} and can call the function \code{pow} on a \code{BigInt} using dot notation.""",
    """Sök upp dokumentationen för \code{java.lang.Integer}.\\Använd metoderna \code{toBinaryString} och \code{toHexString} för att fylla i tabellen nedan.""" ->
      """Look up the documentation for \code{java.lang.Integer}.\\Use the methods \code{toBinaryString} and \code{toHexString} to fill in the table below.""",
    """Sök upp dokumentationtionen för\\\code{java.lang.Math.multiplyExact} och läs om vad den metoden gör.""" ->
      """Look up the documentation for\\\code{java.lang.Math.multiplyExact} and read about what that method does.""",

    // ── w06-patterns-exercise: prose around Swedish domain-term \code{} identifiers (kept code) ──
    """En \textbf{förseglad typ} måste ha alla sina subtyper i en och samma kodfil.""" ->
      """A \textbf{sealed type} must have all its subtypes in one and the same source file.""",
    """Skapa en funktion \code{def parafärg(f: Färg): Färg} i en editor, som med hjälp av ett match-uttryck returnerar parallellfärgen till en färg.""" ->
      """Create a function \code{def parafärg(f: Färg): Färg} in an editor that, using a match expression, returns the parallel suit of a suit.""",
    """Parallellfärgen till \code{Hjärter} är \code{Ruter} och vice versa, medan parallellfärgen till \code{Klöver} är \code{Spader} och vice versa.""" ->
      """The parallel suit of \code{Hjärter} is \code{Ruter} and vice versa, while the parallel suit of \code{Klöver} is \code{Spader} and vice versa.""",
    """Anropa \code{parafärg} med den ''glömda'' färgen. Hur lyder felmeddelandet? Är det ett kompileringsfel eller ett körtidsfel?""" ->
      """Call \code{parafärg} with the ''forgotten'' suit. What does the error message say? Is it a compile-time error or a run-time error?""",
    """Tänk på att denna gången är \code{Färg} inget \code{sealed trait}, utan istället en enumeration (\code{enum}). Klistra in funktionen i REPL.""" ->
      """Note that this time \code{Färg} is not a \code{sealed trait}, but instead an enumeration (\code{enum}). Paste the function into the REPL.""",
    """Andra grenen passar med \code{Vector("hej")} och variablen \code{a} binds till \code{"hej"}.""" ->
      """The second branch matches \code{Vector("hej")} and the variable \code{a} is bound to \code{"hej"}.""",
    """Ett \code{Exception} kastas med felmeddelandet \textit{PANG!}.""" ->
      """An \code{Exception} is thrown with the error message \textit{PANG!}.""",
    """skapas som kastar ett \code{Exception} med felmeddelandet \textit{PANG!}.""" ->
      """is created that throws an \code{Exception} with the error message \textit{PANG!}.""",
    """Slutligen ändras \code{def slumpgrönsak} till följande:""" ->
      """Finally, \code{def slumpgrönsak} is changed to the following:""",
    """tilldelas en instans av \code{Gurka}-klassen med \code{vikt = 42} och \code{ärÄtbar = true}.""" ->
      """is assigned an instance of the \code{Gurka} class with \code{vikt = 42} and \code{ärÄtbar = true}.""",

    // ── lect-w02-codestruct: clean emphasis-dense slide prose (balanced \Emph/\Alert) ──
    """Man kan i REPL \Emph{deklarera} variabler och funktioner med \Emph{samma namn} \Alert{flera gånger} på samma nivå.""" ->
      """In the REPL you can \Emph{declare} variables and functions with the \Emph{same name} \Alert{multiple times} at the same level.""",
    """Om du får krona har du vunnit.""" -> """If you get heads, you have won.""",
    """Loopa genom en samling med en \texttt{while}-sats""" -> """Loop through a collection with a \texttt{while} statement""",
    """Är \Emph{oföränderlig}: du kan lita på att elementreferenserna aldrig någonsin kommer att ändras.""" ->
      """Is \Emph{immutable}: you can trust that the element references will never ever change.""",
    """Spara nedan Scala-kod i filen \code{hej.scala}:""" -> """Save the Scala code below in the file \code{hej.scala}:""",
    """En \href{https://sv.wikipedia.org/wiki/Algoritm}{algoritm} är en sekvens av instruktioner som beskriver hur man löser ett problem.""" ->
      """An \href{https://sv.wikipedia.org/wiki/Algoritm}{algorithm} is a sequence of instructions that describes how to solve a problem.""",
    """Denna bättre \code{isHighscore} är nu en \Emph{äkta funktion} som alltid ger samma svar för samma inparametrar och \Alert{saknar sidoeffekter}; dessa funktioner är ofta lättare att förstå.""" ->
      """This better \code{isHighscore} is now a \Emph{pure function} that always gives the same answer for the same inputs and \Alert{has no side effects}; such functions are often easier to understand.""",
    """I Scala (till skillnad från många andra språk) har ett block ett \Emph{värde} och är alltså ett \Emph{uttryck}.""" ->
      """In Scala (unlike many other languages) a block has a \Emph{value} and is therefore an \Emph{expression}.""",
    """Du \Emph{deklarerar egna procedurer} genom att ange \texttt{\Alert{Unit}} som returvärdestyp.""" ->
      """You \Emph{declare your own procedures} by specifying \texttt{\Alert{Unit}} as the return type.""",
    """Det som \Alert{görs} kallas (sido)\Emph{effekt}. Ovan är utskriften själva effekten.""" ->
      """What is \Alert{done} is called a (side) \Emph{effect}. Above, the printout is the effect itself.""",
    """Java m.fl. har speciell syntax för procedurer med nyckelordet \jcode{void}, men \Alert{inte} Scala.""" ->
      """Java and others have special syntax for procedures with the keyword \jcode{void}, but Scala does \Alert{not}.""",
    """Genom de namn som definieras skapas \Emph{återanvändbara abstraktioner} som kapslar in det funktionen gör.""" ->
      """Through the names that are defined, \Emph{reusable abstractions} are created that encapsulate what the function does.""",
    """Abstraktioner som beräknar eller gör \Emph{en enda, väldefinierad sak} är enklare att använda, jämfört med de som gör många, helt olika saker.""" ->
      """Abstractions that compute or do \Emph{a single, well-defined thing} are easier to use than those that do many, completely different things.""",

    // ── lect-w01-intro "Examples of programming languages": language NAMES are proper nouns (identical
    //    in both languages). gemma2 hallucinated these tiny \item units (Java->Scala, C->Case, Go->Move on,
    //    C#->Case#). Identity overrides stop the model from touching them. ──
    """Java""" -> """Java""",
    """C""" -> """C""",
    """C++""" -> """C++""",
    """C\#""" -> """C\#""",
    """Python""" -> """Python""",
    """JavaScript""" -> """JavaScript""",
    """Rust""" -> """Rust""",
    """Go""" -> """Go""",
    """Kotlin""" -> """Kotlin""",

    // ── w01 mixed-fallback bullets the model mangled (BR-flagged) ──
    // gemma2 hallucinated a "J": "Med en VM" -> "With a JVM" (confusing, JVM is the NEXT bullet).
    """Med en VM blir källkoden \Emph{plattformsoberoende} och fungerar på många olika maskiner.""" ->
      """With a VM the source code becomes \Emph{platform-independent} and works on many different machines.""",
    """En \Emph{variabel} kan tilldelas värdet av ett enkelt eller sammansatt uttryck.""" ->
      """A \Emph{variable} can be assigned the value of a simple or compound expression.""",

    // ── w07-sequences-exercise: clean prose fallbacks ──
    """Fixa svensk sorteringsordning av ÄÅÖ.""" -> """Fix the Swedish sort order of Ä, Å, Ö.""",
    """En palindrom\footnote{\url{https://sv.wikipedia.org/wiki/Palindrom}} är ett ord som förblir oförändrat om man läser det baklänges.""" ->
      """A palindrome\footnote{\url{https://sv.wikipedia.org/wiki/Palindrom}} is a word that stays unchanged when read backwards.""",
    """Den vanliga \code{.sorted} jämför på teckenkod och ger fel svensk ordning, medan en \code{Collator} för \code{Locale} \code{"sv"} ger rätt ordning där \code{å}, \code{ä}, \code{ö} kommer sist (efter \code{z}).""" ->
      """The usual \code{.sorted} compares by character code and gives the wrong Swedish order, whereas a \code{Collator} for \code{Locale} \code{"sv"} gives the correct order where \code{å}, \code{ä}, \code{ö} come last (after \code{z}).""",
    """Observera alltså att kopiering med \code{toArray}, \code{toVector}, \code{toBuffer}, etc. \emph{inte är djup}, d.v.s. det är bara instansreferenserna som kopieras och inte själva instanserna.""" ->
      """Note then that copying with \code{toArray}, \code{toVector}, \code{toBuffer}, etc. is \emph{not deep}, i.e. only the instance references are copied and not the instances themselves.""",
    """Implementera med hjälp av en \code{while}-sats funktionen \code{deepCopy} nedan som gör \emph{djup} kopiering, d.v.s skapar en ny array med nya, innehållskopierade mutanter.""" ->
      """Using a \code{while} statement, implement the function \code{deepCopy} below that does \emph{deep} copying, i.e. creates a new array with new, content-copied mutants.""",

    // ── lect-w01-intro: clean single-line prose fallbacks (some with Swedish trapped in masked \href) ──
    """Ha picknick i \href{http://kartor.lund.se/wiki/lundanamn/index.php/Ada_Lovelace-parken}{Ada Lovelace-parken} på Brunnshög!""" ->
      """Have a picnic in \href{http://kartor.lund.se/wiki/lundanamn/index.php/Ada_Lovelace-parken}{Ada Lovelace-parken} at Brunnshög!""",
    """En VM är en ''dator'' implementerad i mjukvara som kan tolka en abstrakt ''maskinkod'' som \Emph{översätts under körning} till den \Alert{verkliga} maskinens konkreta maskinkod.""" ->
      """A VM is a ''computer'' implemented in software that can interpret an abstract ''machine code'' that is \Emph{translated at run time} into the \Alert{real} machine's concrete machine code.""",
    """GitHub för kodlagring -- men \Alert{inte} av lösningar till labbar!""" ->
      """GitHub for code storage -- but \Alert{not} for solutions to labs!""",
    """Om du vill att REPL ska vänta att tolka raden du skrivit och istället ge dig \Emph{ännu en rad}, så tryck först ner ESC-tangenten och sedan ENTER.""" ->
      """If you want the REPL to wait before interpreting the line you typed and instead give you \Emph{another line}, first press the ESC key and then ENTER.""",
    """Scala-kompilatorn gör \href{https://en.wikipedia.org/wiki/Type_inference}{\Emph{typhärledning}}: man \Alert{slipper skriva typerna} om kompilatorn kan lista ut dem med hjälp av typerna hos deluttrycken.""" ->
      """The Scala compiler does \href{https://en.wikipedia.org/wiki/Type_inference}{\Emph{type inference}}: you \Alert{don't have to write the types} if the compiler can figure them out using the types of the subexpressions.""",
    """En identifierare får \Alert{inte} vara ett \Emph{reserverat ord}, se \href{https://fileadmin.cs.lth.se/pgk/quickref.pdf}{snabbreferensen} för alla reserverade ord i Scala.""" ->
      """An identifier may \Alert{not} be a \Emph{reserved word}, see \href{https://fileadmin.cs.lth.se/pgk/quickref.pdf}{the quick reference} for all reserved words in Scala.""",
    """Genom de namn som definieras skapas \Emph{återanvändbara abstraktioner} som kapslar in det funktionen gör till ett ''byggblock''.""" ->
      """Through the names that are defined, \Emph{reusable abstractions} are created that encapsulate what the function does into a ''building block''.""",
    """Abstraktioner med \Emph{välgenomtänkta namn} är enklare att använda, jämfört med kryptiska eller missvisande namn.""" ->
      """Abstractions with \Emph{well-thought-out names} are easier to use than cryptic or misleading ones.""",

    // ── w06-patterns-exercise: prose around Swedish domain-term \code{} identifiers (kept as code) ──
    """De angivna parametrarna tilldelas namn, \code{vikt} får namnet \code{v} och \code{ärRutten} namnet \code{rutten} och skrivs sedan ut. Byts namnen dessa ges skrivs de ut i den omvända ordningen.""" ->
      """The given parameters are assigned names: \code{vikt} gets the name \code{v} and \code{ärRutten} the name \code{rutten}, and are then printed. If the given names are swapped, they are printed in the reverse order.""",
    """Vi ska nu undersöka vad som händer om man glömmer en av case-grenarna i matchningen i \code{parafärg}. ''Glöm'' alltså avsiktligt en av case-grenarna och klistra in den nya \code{parafärg} med den ofullständiga matchningen.""" ->
      """We will now investigate what happens if you forget one of the case branches in the match in \code{parafärg}. So deliberately ''forget'' one of the case branches and paste in the new \code{parafärg} with the incomplete match.""",
    """Felmeddelandet fås av att REPL:en behandlar varje inmatning individuellt och tillåter därför inte att subtypen \code{Spader} ärver från  supertypen \code{Färg} eftersom denna var förseglad . Mer om detta senare i kursen...""" ->
      """The error message arises because the REPL treats each input individually and therefore does not allow the subtype \code{Spader} to inherit from the supertype \code{Färg}, since the latter was sealed. More on this later in the course...""",
    """Nu accepteras koden utan fel eftersom den förseglade typen \code{Färg} och alla dess subtyper (\code{Spader}, \code{Hjärter}, \code{Ruter}, \code{Klöver}) ligger i samma kodfil, inuti det yttre objektet \code{Kortlek}.""" ->
      """Now the code is accepted without error because the sealed type \code{Färg} and all its subtypes (\code{Spader}, \code{Hjärter}, \code{Ruter}, \code{Klöver}) are in the same source file, inside the outer object \code{Kortlek}.""",
    """Likt uppgift \ref{task:match-sealedtrait}\ref{subtask:match-sealedtrait-function} så kan även här en \code{import}-sats skrivas för att nå medlemmarna i \code{Färg} utan punktnotation.""" ->
      """Like in task \ref{task:match-sealedtrait}\ref{subtask:match-sealedtrait-function}, here too an \code{import} statement can be written to reach the members of \code{Färg} without dot notation.""",
    """Om ett kanske saknat värde packas in i en \code{Option} , finns det i en speciell slags samling som bara kan innehålla \emph{inget} eller \emph{något} värde, och alltså har antingen storleken \code{0} eller \code{1}.""" ->
      """If a possibly-missing value is wrapped in an \code{Option}, it lives in a special kind of collection that can contain only \emph{no} or \emph{some} value, and thus has either size \code{0} or \code{1}.""",
    """applicerar \code{def öka} till det enda elementen i \code{kanske}, 42. Denna funktion returnerar en \code{Some} med värdet 43 som tilldelas \code{merKanske}.""" ->
      """applies \code{def öka} to the single element in \code{kanske}, 42. This function returns a \code{Some} with the value 43, which is assigned to \code{merKanske}.""",
    """Ta bort predikatet \code{ärÄtvärd} i objektet \code{Main} och inför i stället en abstrakt metod \code{def ärÄtbar: Boolean} i traiten \code{Grönsak}.""" ->
      """Remove the predicate \code{ärÄtvärd} in the object \code{Main} and instead introduce an abstract method \code{def ärÄtbar: Boolean} in the trait \code{Grönsak}.""",
    """Ändra i huvudprogrammet i enlighet med ovan ändringar så att \code{ärÄtvärd} anropas som en metod på de skördade grönsaksobjekten när de ätvärda ska filtreras ut.""" ->
      """Modify the main program in accordance with the changes above so that \code{ärÄtvärd} is called as a method on the harvested vegetable objects when the edible ones are to be filtered out.""",
    """Mönstret \code{namn @ delmönster} binder ett namn till hela det matchade värdet samtidigt som delmönstret plockar isär det:""" ->
      """The pattern \code{namn @ delmönster} binds a name to the whole matched value while the subpattern takes it apart:""",

    // ── w07-sequences-exercise: clean prose ──
    """Tyvärr så följer ordningen av ÄÅÖ inte svenska regler, men det ignorerar vi i fortsättningen för enkelhets skull; om du är intresserad av hur man kan fixa  detta, gör uppgift \ref{task:swedish-letter-ordering}.""" ->
      """Unfortunately the ordering of ÄÅÖ does not follow Swedish rules, but we ignore that in the following for simplicity; if you are interested in how to fix this, do task \ref{task:swedish-letter-ordering}.""",
    """Indata är en sekvens av booleska värden där krona kodas som \code{true} och klave kodas som \code{false}. För registreringen ska du använda en lokal \code{Array[Int]}. I resultatet ska antalet utfall av \code{krona} ligga på första platsen i 2-tupeln och på andra platsen ska antalet utfall av \code{klave} ligga.""" ->
      """The input is a sequence of boolean values where heads is coded as \code{true} and tails as \code{false}. For the registration, use a local \code{Array[Int]}. In the result, the count of \code{krona} outcomes should be in the first position of the 2-tuple and the count of \code{klave} outcomes in the second.""",
    """Antalet krona och klave blir ungefär lika (cirka 500 vardera); det exakta utfallet varierar mellan körningar eftersom \code{flips} singlar slumpmässigt.""" ->
      """The number of heads and tails becomes roughly equal (about 500 each); the exact outcome varies between runs because \code{flips} flips randomly.""",

    // ── w10-inheritance-exercise: clean prose around Swedish domain-term \code{} (Grönsak/Gurka kept) ──
    """Om en samling innehåller objekt av flera olika typer försöker kompilatorn härleda den mest specifika typen som objekten har gemensamt. Vad blir det för typ på värdet \code{grönsaker} ovan?""" ->
      """If a collection contains objects of several different types, the compiler tries to infer the most specific type the objects have in common. What type does the value \code{grönsaker} above get?""",
    """Du ska nu göra så att du kan komma åt vikten på alla grönsaker genom att ge gurkor och tomater en gemensam bastyp som de olika konkreta grönsakstyperna utvidgar med nyckelordet \code{extends}. Det heter att subtyperna \code{Gurka} och \code{Tomat} \textbf{ärver} egenskaperna hos supertypen \code{Grönsak}.""" ->
      """You will now make it possible to access the weight of all vegetables by giving cucumbers and tomatoes a common base type that the different concrete vegetable types extend with the keyword \code{extends}. We say that the subtypes \code{Gurka} and \code{Tomat} \textbf{inherit} the properties of the supertype \code{Grönsak}.""",
    """Skapa en bastyp \code{Grönsak} med ett abstrakt attribut \code{vikt}. Låt sedan de konkreta grönsakerna ärva bastypen:""" ->
      """Create a base type \code{Grönsak} with an abstract attribute \code{vikt}. Then let the concrete vegetables inherit the base type:""",
    """Vad blir det nu för typ på variabeln \code{grönsaker} ovan?""" ->
      """What type does the variable \code{grönsaker} above now get?""",
    """Går det nu att summera vikterna i \code{grönsaker} med uttrycket nedan?""" ->
      """Is it now possible to sum the weights in \code{grönsaker} with the expression below?""",
    """Traiten \code{Grönsak} har en abstrakt medlem \code{vikt}. Den sägs vara abstrakt eftersom den saknar implementation -- medlemmen har bara ett namn och en typ men inget värde.""" ->
      """The trait \code{Grönsak} has an abstract member \code{vikt}. It is said to be abstract because it lacks an implementation -- the member has only a name and a type but no value.""",
    """Du kan instansiera den abstrakta traiten \code{Grönsak} om du fyller i det som ''fattas'', nämligen ett värde på \code{vikt}. Man kan fylla på det som fattas i genom att ''hänga på'' ett block efter typens namn vid instansiering.""" ->
      """You can instantiate the abstract trait \code{Grönsak} if you fill in what is ''missing'', namely a value for \code{vikt}. You can fill in what is missing by ''attaching'' a block after the type's name at instantiation.""",
    """Vad får \code{anonymGrönsak} nedan för typ och strängrepresenation?""" ->
      """What type and string representation does \code{anonymGrönsak} below get?""",

    // ── lect-w02-codestruct: clean slide prose ──
    """Exempel på \Emph{färdiga samlingar} i Scalas standardbibliotek där elementen är organiserade  internt på \Alert{olika} vis så att samlingen får olika egenskaper som passar \Alert{olika användningsområden}:""" ->
      """Examples of \Emph{ready-made collections} in Scala's standard library where the elements are organised internally in \Alert{different} ways so that the collection gets different properties suited to \Alert{different use cases}:""",
    """speciella klasser som samlar data i element av \Alert{samma} typ""" ->
      """special classes that collect data in elements of the \Alert{same} type""",
    """har ofta \emph{många} färdiga \Emph{bra-att-ha-metoder}, \\ se snabbreferensen""" ->
      """often have \emph{many} ready-made \Emph{handy methods}, \\ see the quick reference""",
    """Med en \code{Range(start, slut)} kan du skapa ett \Emph{intervall}: \\ från och med \code{start} till (men inte med)""" ->
      """With a \code{Range(start, slut)} you can create an \Emph{interval}: \\ from \code{start} up to (but not including)""",
    """Paket kan vara \Emph{nästlade}: ofta finns paket i paket i paket.""" ->
      """Packages can be \Emph{nested}: there are often packages within packages within packages.""",
    """Mål: skapa \Emph{eget} program med \Emph{många små funktioner} och träna på de \Emph{begrepp} vi använt hittills.""" ->
      """Goal: create your \Emph{own} program with \Emph{many small functions} and practise the \Emph{concepts} we have used so far.""",
    """Om ni inte redan gjort det: \\Visa \href{https://github.com/bjornregnell/lth-eda016-2015/tree/master/assignments}{samarbetskontrakt} för handledare på resurstid.""" ->
      """If you haven't already done so: \\Show the \href{https://github.com/bjornregnell/lth-eda016-2015/tree/master/assignments}{collaboration contract} to an assistant during a tutorial.""",

    // ── lect-w10-override: clean slide prose (Färg/Röd/Svart/namn/ålder are example identifiers) ──
    """Om en medlem i en supertyp är abstrakt \emph{behöver} man inte använda nyckelordet \code{override} i subtypen. (Men det är bra att göra det ändå så att kompilatorn hjälper dig att kolla att du verkligen överskuggar något.)""" ->
      """If a member of a supertype is abstract you \emph{do not need} to use the keyword \code{override} in the subtype. (But it is good to do so anyway, so the compiler helps you check that you really are overriding something.)""",
    """Om en medlem i en supertyp är konkret \emph{måste} man använda nyckelordet \code{override} i subtypen, annars ges kompileringsfel.""" ->
      """If a member of a supertype is concrete you \emph{must} use the keyword \code{override} in the subtype, otherwise you get a compile error.""",
    """En konkret medlem får \Alert{inte} bytas ut mot en \code{var} -- inte ens en konkret \code{var}:""" ->
      """A concrete member may \Alert{not} be replaced by a \code{var} -- not even a concrete \code{var}:""",
    """En produkt-typ är en ''och''-typ (ä.k. record, struct), exempel: \\ \code|case class Person(namn: String, ålder: Int)| \\består av attributen namn \Alert{OCH} ålder.""" ->
      """A product type is an ''and''-type (a.k.a. record, struct), example: \\ \code|case class Person(namn: String, ålder: Int)| \\consists of the attributes namn \Alert{AND} ålder.""",
    """Exempel: \code|enum Färg { case Röd, Svart}| \\Färg kan vara antingen Röd \Emph{ELLER} Svart.""" ->
      """Example: \code|enum Färg { case Röd, Svart}| \\Färg can be either Röd \Emph{OR} Svart.""",

    // ── lect-wjava-body: clean Scala-vs-Java slide prose ──
    """Samlingarna i Scalas standardbibliotek, speciellt de \Emph{oföränderliga} samlingarna \code{Vector}, \code{Map}, \code{Set}, \code{List}, etc.""" ->
      """The collections in Scala's standard library, especially the \Emph{immutable} collections \code{Vector}, \code{Map}, \code{Set}, \code{List}, etc.""",
    """Man \Alert{måste} skriva \code{return}.""" -> """You \Alert{must} write \code{return}.""",
    """Man \Alert{måste} ha semikolon efter varje sats.""" -> """You \Alert{must} have a semicolon after every statement.""",
    """Man kan \Alert{inte} deklarera explicita singelobjekt i Java och det finns inget nyckelord \code{object}.""" ->
      """You \Alert{cannot} declare explicit singleton objects in Java, and there is no keyword \code{object}.""",
    """I Java måste man skriva \Alert{tomma parentes-par} efter metodnamnet vid \Alert{anrop av parameterlösa metoder}.""" ->
      """In Java you must write \Alert{empty parenthesis pairs} after the method name when \Alert{calling parameterless methods}.""",
    """Med \code{new java.util.Scanner("hej 42")} kan man även scanna en sträng.""" ->
      """With \code{new java.util.Scanner("hej 42")} you can also scan a string.""",
    """Scanna \code{Int} och \code{Double} med metoderna \code{nextInt} och \code{nextDouble}.\\Se doc:""" ->
      """Scan \code{Int} and \code{Double} with the methods \code{nextInt} and \code{nextDouble}.\\See the doc:""",
    """Fungerar \Alert{inte} rakt av med primitiva typer \code{int}, \code{double}, \code{char}, ... \\ (men det finns sätt komma runt detta, tack vare s.k. wrapper-klasser och autoboxning; mer om detta snart)""" ->
      """Does \Alert{not} work directly with the primitive types \code{int}, \code{double}, \code{char}, ... \\ (but there are ways around this, thanks to so-called wrapper classes and autoboxing; more on this soon)""",
    """Detta går tyvärr \Alert{INTE} i Java:""" -> """Unfortunately this does \Alert{NOT} work in Java:""",
    """Om ett \code{int}-värde förekommer där det behövs ett \code{Integer}-objekt, så lägger kompilatorn \Alert{automatiskt} ut kod som skapar ett \code{Integer}-objekt som packar in värdet.""" ->
      """If an \code{int} value appears where an \code{Integer} object is needed, the compiler \Alert{automatically} emits code that creates an \code{Integer} object wrapping the value.""",
    """metoden \code{ne} testar referens\textbf{o}likhet och \code{r1.ne(r2)} ger \code{true} om \code{r1} och \code{r2} refererar till \Alert{olika} instanser.""" ->
      """the method \code{ne} tests reference \textbf{in}equality and \code{r1.ne(r2)} gives \code{true} if \code{r1} and \code{r2} refer to \Alert{different} instances.""",
    """I Java kan man \Alert{inte} skapa en primitiv array av godtycklig typ enligt generisk typparameter:""" ->
      """In Java you \Alert{cannot} create a primitive array of arbitrary type according to a generic type parameter:""",
    """Ibland ger \code{"hej" == "hej"} förvånande nog värdet \code{true} i Java.""" ->
      """Sometimes \code{"hej" == "hej"} surprisingly gives the value \code{true} in Java.""",

    // ── lect-w01-about: course intro / academic integrity ──
    """Den \Alert{största förnyelsen} av den inledande programmeringskursen sedan vi införde \Emph{Java 1997}.""" ->
      """The \Alert{biggest renewal} of the introductory programming course since we introduced \Emph{Java in 1997}.""",
    """Kursmaterialet är \Emph{öppen källkod} och \Alert{fritt} tillgängligt.""" ->
      """The course material is \Emph{open source} and \Alert{freely} available.""",
    """Scala är \Emph{öppen källkod} + massor av fria kodbibliotek""" ->
      """Scala is \Emph{open source} + lots of free code libraries""",
    """Kompendiet och snabbreferens trycks här i E-huset och säljs av institutionen till \Emph{självkostnadspris}. För efterbeställning se Cavnvas.""" ->
      """The compendium and quick reference are printed here in the E-building and sold by the department at \Emph{cost price}. For back-orders see Canvas.""",
    """Säljs separat, se info i Canvas ''Efterbeställning snabbreferens''.""" ->
      """Sold separately, see info in Canvas ''Back-order quick reference''.""",
    """Ett studiesammanhang med \Alert{höga ambitioner} och \Alert{respektfull gemenskap} gör att \Emph{alla lär sig mer}.""" ->
      """A study environment with \Alert{high ambitions} and a \Alert{respectful community} means that \Emph{everyone learns more}.""",
    """Samarbete gör att man lär sig bättre, men man lär sig \Alert{inte} av att kopiera andras lösningar.""" ->
      """Collaboration helps you learn better, but you do \Alert{not} learn from copying others' solutions.""",
    """Du får \Alert{INTE} lägga ut laborationslösningar öppet på \Emph{\code{github}} eller på annan plats där någon annan kan komma åt dem!""" ->
      """You may \Alert{NOT} post lab solutions openly on \Emph{\code{github}} or anywhere else where someone else can access them!""",
    """Det är \Alert{inte} tillåtet att använda AI för att generera lösningar.""" ->
      """It is \Alert{not} allowed to use AI to generate solutions.""",
    """Det är \Alert{mycket viktigt} att du lär dig \Emph{koda självständigt}.""" ->
      """It is \Alert{very important} that you learn to \Emph{code independently}.""",
    """Fokusera på att \Emph{förstå} vad som händer när du kör din kod.""" ->
      """Focus on \Emph{understanding} what happens when you run your code.""",
    """Skapa en miljö för \Emph{koncentration} och lärande \Emph{på djupet}. Stäng telefon, be kompisar i datorsalen att inte prata för högt, etc.""" ->
      """Create an environment for \Emph{concentration} and \Emph{deep} learning. Turn off your phone, ask friends in the computer lab not to talk too loudly, etc.""",
    """På laborationerna \Emph{sammanför} du veckans koncept till en \Emph{helhet} i ett större program och kollar att du \Alert{kan grunderna} inför kommande veckor.""" ->
      """In the labs you \Emph{bring together} the week's concepts into a \Emph{whole} in a larger program and check that you \Alert{know the basics} for the coming weeks.""",
    """Gör \Emph{övningarna} och \Emph{labbförberedelserna} noga \Alert{innan} själva labben -- detta är ofta helt nödvändigt för att du ska hinna klart.""" ->
      """Do the \Emph{exercises} and \Emph{lab preparations} carefully \Alert{before} the lab itself -- this is often absolutely necessary for you to finish in time.""",

    // ── lect-w05-classes: OOP / classes / constructors ──
    // AT5 (lect-w05-assignments, blockbattle slide): the model kept this whole \Emph/\Alert-dense bullet as a
    // Swedish FALLBACK (uncached → it hit the model on every `--all`, and left Swedish in the EN mirror).
    // Authored EN here so `--all` needs zero model calls and the leak is gone.
    """Labbtider är \Emph{schemalagda som vanligt} under läsvecka 5. Om du mot förmodan hinner redovisa redan denna vecka så ska du \Alert{ändå närvara} och t.ex. jobba med \Emph{extrauppgifter}.""" ->
      """Lab sessions are \Emph{scheduled as usual} during study week 5. If against expectation you manage to present already this week, you must \Alert{still attend} and, for example, work on \Emph{extra assignments}.""",
    """Begreppet \Emph{klass} är en viktig abstraktionsmekanism inom \Emph{objekt-orienterad programmering} (OOP) för att modellera data i en applikationsdomän, t.ex. data om \emph{användare} och deras \emph{favoritmusik} i applikationsdomänen \emph{musikspelare}. Klasser används för att samla funktioner och data.""" ->
      """The concept of a \Emph{class} is an important abstraction mechanism in \Emph{object-oriented programming} (OOP) for modelling data in an application domain, e.g. data about \emph{users} and their \emph{favourite music} in the application domain \emph{music player}. Classes are used to gather functions and data.""",
    """Det går att skapa \Alert{många} objekt ur en och samma klass.""" ->
      """You can create \Alert{many} objects from one and the same class.""",
    """Ett objekt som skapats med klassen \code{Klassnamn} som mall kallas för en \Emph{instans} av klassen \code{Klassnamn}.""" ->
      """An object created with the class \code{Klassnamn} as a template is called an \Emph{instance} of the class \code{Klassnamn}.""",
    """Varje instans har sin \Alert{egen} uppsättning värden på attributen, som tillsammans utgör instansens \Emph{tillstånd}.""" ->
      """Each instance has its \Alert{own} set of values for the attributes, which together make up the instance's \Emph{state}.""",
    """Med en \Emph{klass} kan man skapa \Alert{godtyckligt många} \Emph{instanser av klassen} med hjälp av nyckelordet \code{new} följt av klassens namn:""" ->
      """With a \Emph{class} you can create \Alert{arbitrarily many} \Emph{instances of the class} using the keyword \code{new} followed by the class's name:""",
    """Parametrar som \Emph{inte} föregås av någon modifierare alls (t.ex. \code{val}, \code{var} etc.) blir medlemmar som är bara är synliga i \Alert{denna} instans.""" ->
      """Parameters that are \Emph{not} preceded by any modifier at all (e.g. \code{val}, \code{var} etc.) become members that are visible only in \Alert{this} instance.""",
    """Metoder som har \Emph{alfanumeriska namn}, alltså namn med bokstäver och ev. siffror ger en \Alert{varning} vid operatornotation om de \Alert{inte} är deklarerade med nyckelordet \code{infix}.""" ->
      """Methods that have \Emph{alphanumeric names}, i.e. names with letters and possibly digits, give a \Alert{warning} with operator notation if they are \Alert{not} declared with the keyword \code{infix}.""",
    """Eftersom klientkoden inte ser skillnad på metoder och variabler, kallas detta \Emph{principen om enhetlig access}. (Många andra språk har \Alert{inte} denna möjlighet, tex Java där metoder \emph{måste} ha parenteser.)""" ->
      """Since the client code sees no difference between methods and variables, this is called the \Emph{uniform access principle}. (Many other languages do \Alert{not} have this possibility, e.g. Java where methods \emph{must} have parentheses.)""",
    """Case-klasser är ett smidigt sätt att skapa \Emph{oföränderliga} datastrukturer.""" ->
      """Case classes are a convenient way to create \Emph{immutable} data structures.""",
    """... och \Alert{mer därtill} men mer om det senare...""" -> """... and \Alert{more besides}, but more on that later...""",
    """I Scala \Alert{genererar kompilatorn} en \Emph{primärkonstruktor} åt dig med maskinkod som initialiserar alla attribut baserat på klassparametrarna som du deklarerat.""" ->
      """In Scala the \Alert{compiler generates} a \Emph{primary constructor} for you with machine code that initialises all attributes based on the class parameters you declared.""",
    """I Scala \emph{kan} man också skriva egna s.k. \Emph{hjälpkonstruktorer} , men det är \Alert{ovanligt}, eftersom man har möjligheten med fabriksmetoder i kompanjonsobjekt och default-argument.""" ->
      """In Scala you \emph{can} also write your own so-called \Emph{auxiliary constructors}, but it is \Alert{uncommon}, since you have the option of factory methods in companion objects and default arguments.""",
    """I Scala kan man skapa ett alternativ till primärkonstruktorn, en så kallad \Emph{hjälpkonstruktor}  genom att deklarera en metod med det speciella namnet \code{this}.""" ->
      """In Scala you can create an alternative to the primary constructor, a so-called \Emph{auxiliary constructor}, by declaring a method with the special name \code{this}.""",
    """Hjälpkonstruktorer \Alert{måste} börja med att anropa en \Alert{annan} konstruktor som står \Alert{före} i koden, till exempel primärkonstruktorn.""" ->
      """Auxiliary constructors \Alert{must} begin by calling \Alert{another} constructor that appears \Alert{before} it in the code, for example the primary constructor.""",
    """Man kan åstadkomma \Emph{oföränderliga} datastrukturer där attributreferenserna inte förändras efter allokering om klassen \Alert{inte} erbjuder en \Alert{setter} för privata attribut.""" ->
      """You can achieve \Emph{immutable} data structures where the attribute references don't change after allocation if the class does \Alert{not} offer a \Alert{setter} for private attributes.""",

    // ── lect-w10-extends: inheritance / traits / composition ──
    """Som alternativ till att klassen X ärver klassen Y kan man i stället använda \Emph{komposition} , som innebär att klassen X \Alert{har ett attribut} som \Emph{refererar} till klassen Y.""" ->
      """As an alternative to class X inheriting class Y, you can instead use \Emph{composition}, which means that class X \Alert{has an attribute} that \Emph{refers} to class Y.""",
    """det \emph{inte} finns en tydlig \Emph{X-är-en-Y}-relation""" -> """there is \emph{no} clear \Emph{X-is-a-Y} relationship""",
    """det \emph{inte} är önskvärt ärva och exponera \emph{alla} Y:s medlemmar via X""" ->
      """it is \emph{not} desirable to inherit and expose \emph{all} of Y's members via X""",
    """Typerna \code{Grönsak} och \code{Tomat} är \Alert{orelaterade}. (\code{AnyRef} saknar \code{vikt})""" ->
      """The types \code{Grönsak} and \code{Tomat} are \Alert{unrelated}. (\code{AnyRef} lacks \code{vikt})""",
    """Typen \textit{\textbf{\texttt{Grönsak}}} är en \Emph{bastyp} i nedan arvshierarki:""" ->
      """The type \textit{\textbf{\texttt{Grönsak}}} is a \Emph{base type} in the inheritance hierarchy below:""",
    """Det specifika värdet på vikten definieras \Alert{inte} i bastypen.""" ->
      """The specific value of the weight is \Alert{not} defined in the base type.""",
    """den \Emph{kan mixas} med flera andra traits så att olika koddelar kan kombineras på flexibla sätt.""" ->
      """it \Emph{can be mixed} with several other traits so that different parts of the code can be combined in flexible ways.""",
    """den \Alert{kan inte} instansieras direkt.""" -> """it \Alert{cannot} be instantiated directly.""",
    """den \Emph{kan} ha \Emph{parametrar}\footnote{I gamla Scala 2 kan traits ej ha parametrar} på samma sätt som klasser.""" ->
      """it \Emph{can} have \Emph{parameters}\footnote{In old Scala 2, traits cannot have parameters} in the same way as classes.""",
    """En \code{trait} kan användas för att skapa en bastyp som kan vara hemvist för gemensamma delar hos subtyper:""" ->
      """A \code{trait} can be used to create a base type that can be home to common parts of subtypes:""",
    """Det \emph{finns} tillfällen när \Alert{kodduplicering} \Emph{faktiskt är att föredra}: \pause t.ex. om man vill att olika delar av koden ska vara \Alert{helt oberoende} av varandra.""" ->
      """There \emph{are} occasions when \Alert{code duplication} \Emph{is actually preferable}: \pause e.g. if you want different parts of the code to be \Alert{completely independent} of each other.""",
    """I flera språk, t.ex. Java, gäller dessa regler (men \Alert{inte} i Scala):""" ->
      """In several languages, e.g. Java, these rules apply (but \Alert{not} in Scala):""",
    """Scala är \Emph{friare}:""" -> """Scala is \Emph{more permissive}:""",
    """I Scala får man ha \Emph{så många} icke-privata klasser/traits/singelobjekt i samma kodfil \Emph{som man vill}.""" ->
      """In Scala you may have \Emph{as many} non-private classes/traits/singleton objects in the same source file \Emph{as you want}.""",
    """Om en kodfil bara innehåller \Emph{en enda} klass/trait/singelobjekt ge filen samma namn som innehållet, t.ex.""" ->
      """If a source file contains only \Emph{a single} class/trait/singleton object, give the file the same name as the content, e.g.""",
    """Med ''statisk typ'' menas den typinformation som finns vid \Alert{kompileringstid}.""" ->
      """By ''static type'' we mean the type information available at \Alert{compile time}.""",
    """Man kan ärva \Alert{flera} traits.""" -> """You can inherit \Alert{several} traits.""",
    """Genom att skapa \Emph{öppna klasser} med nyckelordet \code{open} signalerar du att klassen är tänkt att vara en supertyp vid arv.""" ->
      """By creating \Emph{open classes} with the keyword \code{open}, you signal that the class is intended to be a supertype for inheritance.""",
    """krävs om du vill tysta varning vid \Alert{arv från en annan kodfil}.""" ->
      """is required if you want to silence the warning when \Alert{inheriting from another source file}.""",
    """...du vill korta ned tiden för omkompilering vid ändringar om \emph{mycket stor} kodbas.""" ->
      """...you want to shorten the recompilation time on changes for a \emph{very large} code base.""",

    // ── lect-w04-objects: objects / state / packages / lazy / jar ──
    """En \Alert{metod} är en \Emph{funktion} som finns i ett objekt.""" ->
      """A \Alert{method} is a \Emph{function} that lives in an object.""",
    """Privata medlemmar kan bara refereras \emph{inifrån} objektet.""" ->
      """Private members can only be referenced \emph{from inside} the object.""",
    """Ett objekts \Emph{tillstånd} är den samlade uppsättningen av värden av alla de attribut som finns i objektet.""" ->
      """An object's \Emph{state} is the combined set of values of all the attributes in the object.""",
    """När en variabel tilldelas ett nytt värde sker en \Emph{tillståndsändring}. Ett \Emph{förändringsbart objekt}  har ett \Emph{förändringsbart tillstånd} .""" ->
      """When a variable is assigned a new value, a \Emph{state change} occurs. A \Emph{mutable object} has a \Emph{mutable state}.""",
    """paket kan delas upp i \Emph{flera} kodfiler -- ett objekt måste vara i \Alert{en} kodfil""" ->
      """a package can be split into \Emph{several} source files -- an object must be in \Alert{one} source file""",
    """Med nyckelordet \code{lazy} före \code{val} sker \Alert{fördröjd} (ä.k. ''\Emph{lat}'') evaluering av initialiseringsuttrycket.""" ->
      """With the keyword \code{lazy} before \code{val}, \Alert{delayed} (a.k.a. ''\Emph{lazy}'') evaluation of the initialisation expression occurs.""",
    """Singelobjekt allokeras \Alert{inte} direkt vid deklaration; allokeringen sker först då objektet refereras första gången.""" ->
      """Singleton objects are \Alert{not} allocated immediately at declaration; the allocation happens only when the object is first referenced.""",
    """Notera att det \emph{inte} ska vara något kolon efter \code{extension}-deklarationens första rad.""" ->
      """Note that there should be \emph{no} colon after the first line of the \code{extension} declaration.""",
    """En \Emph{jar}-fil följer ett standardiserat filformat och används för att \Alert{paketera flera filer} i en och samma fil, exempelvis:""" ->
      """A \Emph{jar} file follows a standardised file format and is used to \Alert{package several files} into one and the same file, for example:""",

    // ── w04-objects-exercise: Underjorden/Mullvaden/Masken etc. are example identifiers (kept as code) ──
    """Skapa ovan kod i filen \code{Underjorden.scala} med en editor och implementera predikatet  \code{ärMullvadsmat} så att det blir sant om mullvadens koordinater är samma som maskens.""" ->
      """Create the code above in the file \code{Underjorden.scala} with an editor and implement the predicate \code{ärMullvadsmat} so that it becomes true if the mole's coordinates are the same as the worm's.""",
    """Testa livet i Underjorden genom att klistra in din modul i REPL. Importera Underjordens medlemmar med asterisk så att du ser Mullvaden och Masken. Flytta med hjälp av tilldelning Maskens y-koordinat så att Masken hamnar på samma plats som Mullvaden. Kontrollera att predikatet \code{ärMullvadsmat} fungerar som tänkt.""" ->
      """Test life in Underjorden by pasting your module into the REPL. Import Underjorden's members with an asterisk so that you see Mullvaden and Masken. Using assignment, move Masken's y-coordinate so that Masken ends up in the same place as Mullvaden. Check that the predicate \code{ärMullvadsmat} works as intended.""",
    """Predikatet \code{ärMullvadsmat} ska vara sant om Masken finns på samma plats som Mullvaden.""" ->
      """The predicate \code{ärMullvadsmat} should be true if Masken is in the same place as Mullvaden.""",
    """Predikatet  \code{ärRaktUnderUppgången} ska vara sant om $x$- och $y$-koordinaterna sammanfaller med den hemliga uppgången till överjorden.""" ->
      """The predicate \code{ärRaktUnderUppgången} should be true if the $x$ and $y$ coordinates coincide with the secret entrance to the overworld.""",
    """Undersök i REPL vad uttrycket \code{"päronisglass".split('i')} har för värde.""" ->
      """Investigate in the REPL what value the expression \code{"päronisglass".split('i')} has.""",
    """I många språk finns en konstruktion med följande syntax: \code{do <satser> while <villkor>} där \code{<satser>} görs minst en gång innan sanningsvärdet för <villkor> testas.""" ->
      """In many languages there is a construct with the following syntax: \code{do <satser> while <villkor>} where \code{<satser>} are executed at least once before the truth value of <villkor> is tested.""",
    """Vad händer om du trycker \Button{Ok} efter att du valt en grön färg?""" ->
      """What happens if you press \Button{Ok} after choosing a green colour?""",
    """Vad händer om du trycker \Button{Cancel}~?""" -> """What happens if you press \Button{Cancel}~?""",
    """Använda färdigt paket: användardialoger.""" -> """Using a ready-made package: user dialogs.""",
    """Använd funktionen \code{introprog.Dialog.input} för att visa frågan \code{"Vad heter du?"} och ta reda på användarens namn.""" ->
      """Use the function \code{introprog.Dialog.input} to show the question \code{"Vad heter du?"} and find out the user's name.""",

    // ── w03-functions-exercise: clean prose (Swedish \code identifiers öka/görDettaTvåGånger kept) ──
    """Definiera funktionen \code{öka} som har en heltalsparameter \code{x} och vars returvärde är argumentet plus 1. Defaultargument ska vara 1. Ange returtypen explicit.""" ->
      """Define the function \code{öka} which has an integer parameter \code{x} and whose return value is the argument plus 1. The default argument should be 1. Specify the return type explicitly.""",
    """Vad har uttrycket \code{öka(öka(öka(öka())))} för värde?""" ->
      """What value does the expression \code{öka(öka(öka(öka())))} have?""",
    """Det finns ett \emph{ännu} kortare sätt att skriva en anonym funktion \emph{om} typen kan härledas \emph{och} den bara använder sin parameter \emph{en enda gång}; då går funktionslitteraler att skriva med s.k. \emph{platshållarsyntax} som använder understreck, till exempel \code{ _ + 1} och som automatiskt expanderas av kompilatorn till \code{ngtnamn => ngtnamn + 1} (namnet på parametern spelar ingen roll; kompilatorn väljer något eget, internt namn).""" ->
      """There is an \emph{even} shorter way to write an anonymous function \emph{if} the type can be inferred \emph{and} it uses its parameter only \emph{once}; then function literals can be written with so-called \emph{placeholder syntax} that uses an underscore, for example \code{ _ + 1}, which the compiler automatically expands to \code{ngtnamn => ngtnamn + 1} (the parameter's name doesn't matter; the compiler chooses its own internal name).""",
    """Funktionslitteraler kallas \textit{anonyma funktioner}, eftersom de inte har något namn, till skillnad från t.ex. \code{def öka(i: Int): Int = i + 1}, som ju heter \code{öka}. Ett annat vanligt namn är \textit{lambda-uttryck} efter det datalogiska matematikverktyget \href{https://sv.wikipedia.org/wiki/Lambdakalkyl}{lambdakalkyl}.""" ->
      """Function literals are called \textit{anonymous functions}, because they have no name, unlike e.g. \code{def öka(i: Int): Int = i + 1}, which is named \code{öka}. Another common name is \textit{lambda expression}, after the computer-science mathematical tool \href{https://sv.wikipedia.org/wiki/Lambdakalkyl}{lambda calculus}.""",
    """Skriv ett program i filen \texttt{fel.scala} som orsakar ett \emph{körtidsfel} och kör igång det i terminalen med \code{scala run fel.scala}. Studera den stack trace som skrivs ut. Vad innehåller en \code{stack trace}?""" ->
      """Write a program in the file \texttt{fel.scala} that causes a \emph{runtime error} and run it in the terminal with \code{scala run fel.scala}. Study the stack trace that is printed. What does a \code{stack trace} contain?""",
    """Funktioner som tar funktioner som argument kallas \emph{högre ordningens funktioner}.""" ->
      """Functions that take functions as arguments are called \emph{higher-order functions}.""",
    """Anropa \code{görDettaTvåGånger} med ett block som parameter. Blocket ska innehålla en utskriftssats. Förklara vad som händer.""" ->
      """Call \code{görDettaTvåGånger} with a block as a parameter. The block should contain a print statement. Explain what happens.""",
    """Normalt sker i Scala (och i Java) s.k. \emph{värdeanrop} vid anrop av funktioner, vilket innebär att argumentuttrycket evalueras \emph{före} bindningen till parameternamnet sker.""" ->
      """Normally in Scala (and in Java) so-called \emph{call-by-value} occurs when calling functions, which means that the argument expression is evaluated \emph{before} the binding to the parameter name happens.""",
    """Om vi placerar \code{1/x} \emph{före} det rekursiva anropet, så når vi detta uttryck direkt och det kastas ett undantag p.g.a. division med noll.""" ->
      """If we place \code{1/x} \emph{before} the recursive call, we reach this expression immediately and an exception is thrown due to division by zero.""",

    // ── lect-w09-collections: clean slide prose (sets, maps, StringOps) ──
    """Detta gör att \Alert{alla samlingsmetoder på \texttt{Seq} även funkar på strängar} och även flera andra smidiga strängmetoder erbjuds \Alert{utöver} de som finns i \href{https://www.scala-lang.org/api/current/scala/collection/StringOps.html}{\code{java.lang.String}} genom klassen \href{http://www.scala-lang.org/api/current/scala/collection/immutable/StringOps.html}{\code{StringOps}}.""" ->
      """This means that \Alert{all collection methods on \texttt{Seq} also work on strings}, and several other convenient string methods are offered \Alert{beyond} those in \href{https://www.scala-lang.org/api/current/scala/collection/StringOps.html}{\code{java.lang.String}}, through the class \href{http://www.scala-lang.org/api/current/scala/collection/immutable/StringOps.html}{\code{StringOps}}.""",
    """En mängd är \Alert{inte}  en sekvens: du kan \Alert{inte} utgå från att elementen ligger i någon viss ordning, t.ex. den ordning som de ges vid konstruktion; en mängd har ej längd, men en \Emph{storlek}; metoden \code{size} ger antalet element men metoden \code{length} saknas.""" ->
      """A set is \Alert{not} a sequence: you \Alert{cannot} assume that the elements are in any particular order, e.g. the order they were given at construction; a set has no length, but a \Emph{size}; the method \code{size} gives the number of elements but the method \code{length} is missing.""",
    """Med en \Alert{förändringsbar} mängd kan man stegvis utöka på plats.""" ->
      """With a \Alert{mutable} set you can extend incrementally in place.""",
    """En \Emph{nyckel-värde-tabell}  är en slags generaliserad vektor där man kan ''indexera'' med godtycklig typ.""" ->
      """A \Emph{key-value table} is a kind of generalised vector where you can ''index'' with an arbitrary type.""",

    // ── w02-programs-exercise: clean prose ──
    """Hämta given kod via \href{https://github.com/lunduniversity/introprog/tree/master/workspace/}{kursen github-plats}.""" ->
      """Get the given code from \href{https://github.com/lunduniversity/introprog/tree/master/workspace/}{the course's GitHub site}.""",
    """Om du tar bort indenteringen på den sista raden med utskrift-satsen så tolkar kompilatorn detta som att denna ligger \emph{utanför} main-funktionen och du får ett felmeddelande eftersom det inte är tillåtet att ha ensamma satser på toppnivå. (Det går dock bra att ha ensamma satser i ett skript med \code{.sc} i slutet av namnet på kodfilen. )""" ->
      """If you remove the indentation on the last line with the print statement, the compiler interprets this as it being \emph{outside} the main function, and you get an error message because lone statements at the top level are not allowed. (It is, however, fine to have lone statements in a script with \code{.sc} at the end of the source file's name. )""",
    """Bygg vidare på koden nedan och gör ett Sten-Sax-Påse-spel\footnote{\url{https://sv.wikipedia.org/wiki/Sten,_sax,_påse}}. Koden fungerar som den ska, förutom funktionen \code{winner} som fuskar till datorns fördel.""" ->
      """Build on the code below and make a Rock-Paper-Scissors game\footnote{\url{https://sv.wikipedia.org/wiki/Sten,_sax,_påse}}. The code works as it should, except the function \code{winner} which cheats in the computer's favour.""",

    // ── w01-intro-lab: clean prose (Kojo lab abstractions kvadrat/stapel/rutnät kept as code) ──
    """Skriv \code{\_f} och tryck på \commandchar{tab} igen; datorn fyller i till det unika filnamnet \file{report\_final.txt}. Tryck på \commandchar{retur} för att titta på filen.""" ->
      """Type \code{\_f} and press \commandchar{tab} again; the computer fills in the unique file name \file{report\_final.txt}. Press \commandchar{retur} to look at the file.""",
    """Man kan tillfälligt avbryta exekveringen av ett program med \commandchar{control-z}. Skriv \code{xeyes} och sedan \commandchar{control-z}. Notera att programmet nu inte är aktivt (ögonen följer inte musmarkören). Med kommandot \code{fg} (foreground) återupptar man exekveringen igen.""" ->
      """You can temporarily pause the execution of a program with \commandchar{control-z}. Type \code{xeyes} and then \commandchar{control-z}. Note that the program is now not active (the eyes don't follow the mouse cursor). With the command \code{fg} (foreground) you resume execution again.""",
    """Definiera en egen procedur som heter \code{kvadrat} med hjälp av nyckelordet \code{def} som vid anrop ritar en kvadrat med hjälp av en \code{for}-loop.""" ->
      """Define your own procedure named \code{kvadrat} using the keyword \code{def} which, when called, draws a square using a \code{for} loop.""",
    """Studera hur anrop av proceduren \code{kvadrat} påverkar exekveringssekvensen av dina satser genom att göra lämpliga utskrifter så att du kan se när olika delar av koden exekveras.""" ->
      """Study how calling the procedure \code{kvadrat} affects the execution sequence of your statements by making suitable printouts so that you can see when different parts of the code are executed.""",
    """Rita samma bild med 10 staplade kvadrater (se bild \ref{fig:kojo-lab:column} på sidan \pageref{fig:kojo-lab:column}), men nu \emph{utan} att använda abstraktionen \code{kvadrat} -- använd i stället en nästlad repetition (alltså en upprepning inuti en upprepning). Vilket av de två sätten (med och utan abstraktionen \code{kvadrat}) är lättast att läsa?""" ->
      """Draw the same picture with 10 stacked squares (see figure \ref{fig:kojo-lab:column} on page \pageref{fig:kojo-lab:column}), but now \emph{without} using the abstraction \code{kvadrat} -- use instead a nested repetition (i.e. a repetition inside a repetition). Which of the two ways (with and without the abstraction \code{kvadrat}) is easiest to read?""",
    """Generalisera din abstraktion \code{kvadrat} genom att ge den en parameter \code{sida: Double} som anger kvadratens storlek.""" ->
      """Generalise your abstraction \code{kvadrat} by giving it a parameter \code{sida: Double} that specifies the square's size.""",
    """Skapa en abstraktion \code{def stapel = ???} som använder din abstraktion \code{kvadrat}.""" ->
      """Create an abstraction \code{def stapel = ???} that uses your abstraction \code{kvadrat}.""",
    """Generalisera dina abstraktioner \code{kvadrat} och \code{stapel} så att man kan påverka storleken på kvadraterna som ritas ut.""" ->
      """Generalise your abstractions \code{kvadrat} and \code{stapel} so that you can affect the size of the squares that are drawn.""",
    """Skapa en abstraktion \code{rutnät} med lämpliga parametrar som gör att man kan rita rutnät med olika stora kvadrater och olika många kvadrater i både x- och y-led.""" ->
      """Create an abstraction \code{rutnät} with suitable parameters that lets you draw grids with squares of different sizes and different numbers of squares in both the x and y directions.""",
    """Generalisera dina abstraktioner \code{kvadrat} och \code{stapel} så att man kan påverka fyllfärgen och pennfärgen för kvadraterna som ritas ut.""" ->
      """Generalise your abstractions \code{kvadrat} and \code{stapel} so that you can affect the fill colour and pen colour of the squares that are drawn.""",
    """Skriv \code{räknaSnabbt} terminalen för att köra huvudprogrammet.""" ->
      """Type \code{räknaSnabbt} in the terminal to run the main program.""",

    // ── w10-inheritance-lab: snake-game group lab ──
    """Gör övning {\tt \ExeWeekNINE} i kapitel \ref{exe:W10}, speciellt uppgift \ref{exe:inheritance:labprep-pair}.""" ->
      """Do exercise {\tt \ExeWeekNINE} in chapter \ref{exe:W10}, especially task \ref{exe:inheritance:labprep-pair}.""",
    """Ett monster består av fem rosa block i kryssform.""" ->
      """A monster consists of five pink blocks in a cross shape.""",
    """Ett monster föds på en slumpvis position och rör sig i en riktning som bestäms vid monstrets födelse.""" ->
      """A monster is born at a random position and moves in a direction determined at the monster's birth.""",
    """Ett monster blir uppätet och dör om ormens huvud nuddar ett monsterblock.""" ->
      """A monster gets eaten and dies if the snake's head touches a monster block.""",
    """När ormen ätit ett visst antal äpplen, eller om ormen blivit uppäten av ett monster, är spelet slut och poängen visas.""" ->
      """When the snake has eaten a certain number of apples, or if the snake has been eaten by a monster, the game is over and the score is shown.""",
    """Varje person i gruppen ska implementera \emph{minst ett} (gärna flera) av kraven nedan.""" ->
      """Each person in the group should implement \emph{at least one} (preferably several) of the requirements below.""",

    // ── w12-extra-exercise: concurrency / threads / atomicity (Swedish \code identifiers kept) ──
    """Observera att om samlingen inte är sorterad är resultatet ''odefinierat'', d.v.s. något returneras men det är \emph{inte} att lita på; man måste alltså först sortera samlingen eller vara helt säker på att den är sorterad.""" ->
      """Note that if the collection is not sorted, the result is ''undefined'', i.e. something is returned but it is \emph{not} to be relied on; so you must first sort the collection or be completely sure that it is sorted.""",
    """Om man anropar \code{run} direkt blir det \emph{inte} jämlöpande exekvering.""" ->
      """If you call \code{run} directly, you do \emph{not} get concurrent execution.""",
    """Alltså kommer det skrivas ut \code{zzz snark hej!} i de flesta fall, men det är inte garanterat.""" ->
      """So \code{zzz snark hej!} will be printed in most cases, but it is not guaranteed.""",
    """I \code{slösaSpara} hämtas saldot, ändras och placeras tillbaka i minnet -  fördröjs -  upprepas.""" ->
      """In \code{slösaSpara} the balance is fetched, changed and placed back in memory -  delayed -  repeated.""",
    """En operation som inte kan avbrytas kallas \textbf{atomär} . Studera dokumentationen för \code{AtomicInteger}\footnote{\href{https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/AtomicInteger.html}{docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/AtomicInteger.html}} och prova nedan kod.""" ->
      """An operation that cannot be interrupted is called \textbf{atomic} . Study the documentation for \code{AtomicInteger}\footnote{\href{https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/AtomicInteger.html}{docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/AtomicInteger.html}} and try the code below.""",
    """I \code{slösaSpara} pausas tråden i en millisekund så \code{vargen}-tråden kan hinna ta ut pengar innan \code{farmor}-sätter hinner sätta in pengar igen och saldot blir negativt.""" ->
      """In \code{slösaSpara} the thread is paused for a millisecond so the \code{vargen} thread can withdraw money before the \code{farmor} depositor can deposit money again and the balance becomes negative.""",
    """Akka erbjuder automatisk  multitrådning med s.k. trådpooler och möjliggör avancerad parallellprogrammering på en hög  abstraktionsnivå, där man själv slipper skapa instanser av klassen \code{Thread}. I stället kan man helt enkelt placera sin kod inramad med \code|Future{ "körs parallellt" }| efter att man importerat det som behövs.""" ->
      """Akka offers automatic multithreading with so-called thread pools and enables advanced parallel programming at a high abstraction level, where you don't have to create instances of the class \code{Thread} yourself. Instead you can simply place your code wrapped in \code|Future{ "körs parallellt" }| after importing what is needed.""",

    // ── w06-patterns-exercise: remaining clean prose (Färg/Gurka kept as code) ──
    """Skapa med hjälp av en editor igen en funktion \code{def parafärg(f: Färg): Färg}, nästintill likadan som den som vi skapade i deluppgift \ref{task:match-sealedtrait}\ref{subtask:match-sealedtrait-function}. Funktionen ska återigen utnyttja match-uttryck för att returnera paralellfärgen till argumentet som ges.""" ->
      """Using an editor again, create a function \code{def parafärg(f: Färg): Färg}, almost identical to the one we created in subtask \ref{task:match-sealedtrait}\ref{subtask:match-sealedtrait-function}. The function should again use a match expression to return the parallel suit of the given argument.""",
    """Tredje grenen matchar \code{Vector("på", "dej")} där första värdet binds inte till någon variabel eftersom understreck finns på motsvarande plats, medan andra värdet binds till \code{b}.""" ->
      """The third branch matches \code{Vector("på", "dej")} where the first value is not bound to any variable because an underscore is in the corresponding place, while the second value is bound to \code{b}.""",
    """I de första 3 raderna sker samma som i deluppgift \textit{a}. När nu dessa jämförelser görs mellan \code{Gurka}-objekten så överskuggas \code{Any.equals} av den \code{equals} som är specificerad för just \code{Gurka}. Eftersom båda objekten \code{g1} jämförs med också är av typen \code{Gurka} så matchar den med \code{case that: Gurka}. Denna i sin tur jämför vikterna hos de båda gurkorna och returnerar en \code{Boolean} huruvida de är lika eller inte, vilket de i båda fallen är.""" ->
      """In the first 3 lines the same happens as in subtask \textit{a}. Now, when these comparisons are made between the \code{Gurka} objects, \code{Any.equals} is overridden by the \code{equals} specified specifically for \code{Gurka}. Since both objects that \code{g1} is compared with are also of type \code{Gurka}, it matches \code{case that: Gurka}. This in turn compares the weights of the two cucumbers and returns a \code{Boolean} of whether they are equal or not, which they are in both cases.""",

    // ── w01-intro-exercise: remaining clean prose ──
    """Skriver först ut hej med det innersta anropet och sen \code{()} med det yttre anropet""" ->
      """First prints hej with the innermost call and then \code{()} with the outer call""",
    """Om du \emph{verkligen} vill ha sådana operatorer är det \emph{mycket} lämpligt att också erbjuda varianter i klartext:""" ->
      """If you \emph{really} want such operators, it is \emph{very} appropriate to also offer plain-text variants:""",
  )
