/** Option B: translate PROSE inside Scala/Java example code — comments (all) and string literals
  * (only Swedish-ish ones) — while leaving ALL code (identifiers, keywords, operators) verbatim, so
  * the mirror still compiles. A small hand-rolled lexer (NOT a parser): it walks the source and only
  * ever hands the TEXT of a comment or string to `tr`; everything else is copied byte-for-byte.
  *
  * Identifiers (e.g. `Grönsak`, `vikt`) stay Swedish — that is Option D's job. This is the safe,
  * compile-preserving first step. See notes/at-code-examples-swedish-investigation.md. */
object Code:
  // Detect Swedish prose in a STRING literal. åäö is a sure sign, but many Swedish strings have none
  // ("Du klarade det!"), so also look for unambiguous Swedish function words (chosen to avoid English
  // collisions — e.g. NOT "men"/"in"/"om"/"man"). Comments are always translated, so this is strings-only.
  // public (pure, immutable) so tooling — e.g. scratch/pdf-swedish.scala — can reuse the SAME notion of
  // "looks Swedish" the translator uses, instead of a divergent ad-hoc copy.
  val swedishWords = Set(
    // function words / pronouns / connectors (unambiguous — avoid English collisions like men/in/om/man)
    "och", "att", "inte", "jag", "han", "hon", "det", "den", "som", "eller", "hej", "tack", "nej",
    "vad", "vem", "vilken", "vilket", "vilka", "hur", "varför", "ingen", "inget", "inga", "alla", "allt",
    "denna", "detta", "dessa", "mycket", "ditt", "din", "dina", "mitt", "sitt", "deras", "ett", "flera",
    // verbs (no åäö)
    "skriv", "skriver", "skrev", "ange", "anger", "angav", "mata", "matar", "valde", "valt", "spara",
    "sparar", "sparade", "sluta", "slutar", "slut", "slutet", "avsluta", "starta", "startar", "skapa",
    "skapar", "skapade", "skapad", "satte", "drog", "lade", "radera", "raderar", "raderade", "raderad",
    "visa", "visar", "visade", "anropa", "anropar", "klarade", "blev", "finns", "duger", "gissa", "gissar",
    // domain nouns
    "talet", "antal", "antalet", "tal", "heltal", "decimaltal", "siffra", "siffror", "bokstav", "ordet",
    "raden", "kolumn", "namnet", "summan", "medel", "konto", "konton", "kontot", "kontonummer", "saldo",
    "belopp", "summa", "kronor", "krona", "uttag", "pengar", "betala", "medlem", "nummer", "gissning",
    "spelet", "spelare", "vinst", "vinner", "monster",
    // lab-domain verbs/nouns (no åäö) for comments
    "rita", "ritar", "returnera", "returnerar", "loopa", "loopar", "kvadrat", "rektangel", "cirkel",
    "linje", "punkt", "bredd", "ruta", "rutor", "plats", "byter", "flytta", "flyttar", "kontrollera",
    "kontrollerar", "funktion", "funktionen", "metoden", "klassen", "variabeln", "listan", "slingan",
    "villkor", "annars", "medan", "sedan", "skapar", "loopen", "anropas", "objektet", "metod")
  def swedishish(s: String): Boolean =
    s.exists("åäöÅÄÖ".contains) ||
      s.toLowerCase.split("[^a-zåäö]+").exists(swedishWords.contains)

  // A line comment that is actually a scala-cli directive (`//> using …`) or a commented-out one
  // (`//using scala …`) must be kept byte-for-byte — translating it would corrupt the build config.
  private val directiveRe = raw"using\s+(scala|dep|option|options|platform|jvm|resourceDir|file|files|test|mainClass|java|javaOpt|repository|toolkit)\b.*".r
  private def isDirective(content: String): Boolean =
    val t = content.stripLeading
    t.startsWith(">") || directiveRe.matches(t)

  /** Translate comments + Swedish-ish string content in `src` via `tr`; keep all code verbatim.
    * `tr` should be effect-free w.r.t. code safety: it must NOT introduce a `"` or newline (the caller
    * — Translate.translatePlain — guarantees this), so string/line-comment delimiters stay intact. */
  def translate(src: String, tr: String => String): String =
    val sb = StringBuilder(); val n = src.length; var i = 0
    // translate inner text of a comment OR string only if it looks Swedish — this skips already-English
    // library comments AND commented-out code (e.g. `//val w = new LifeWindow(20, 20)`), which must NOT
    // be sent to the model (wasteful + a source of masking-leak corruption).
    def piece(s: String): String =
      if s.exists(_.isLetter) && swedishish(s) then tr(s) else s
    while i < n do
      val c = src(i)
      if c == '/' && i + 1 < n && src(i + 1) == '/' then            // line comment
        val e = src.indexOf('\n', i); val end = if e < 0 then n else e
        val content = src.substring(i + 2, end)
        sb ++= "//"; sb ++= (if isDirective(content) then content else piece(content)); i = end
      else if c == '/' && i + 1 < n && src(i + 1) == '*' then       // block comment
        val cl = src.indexOf("*/", i + 2); val inner = src.substring(i + 2, if cl < 0 then n else cl)
        sb ++= "/*"; sb ++= piece(inner); if cl >= 0 then sb ++= "*/"
        i = if cl < 0 then n else cl + 2
      else if c == '"' && i + 2 < n && src(i + 1) == '"' && src(i + 2) == '"' then  // raw string """..."""
        val cl = src.indexOf("\"\"\"", i + 3); val inner = src.substring(i + 3, if cl < 0 then n else cl)
        sb ++= "\"\"\""; sb ++= piece(inner); if cl >= 0 then sb ++= "\"\"\""
        i = if cl < 0 then n else cl + 3
      else if c == '"' then                                         // string "..."
        var j = i + 1
        while j < n && src(j) != '"' do { if src(j) == '\\' then j += 1; j += 1 }
        sb ++= "\""; sb ++= piece(src.substring(i + 1, math.min(j, n))); if j < n then sb ++= "\""
        i = math.min(j + 1, n)
      else if c == '\'' then                                        // char literal — skip verbatim
        var j = i + 1
        while j < n && src(j) != '\'' do { if src(j) == '\\' then j += 1; j += 1 }
        sb ++= src.substring(i, math.min(j + 1, n)); i = math.min(j + 1, n)
      else { sb += c; i += 1 }
    sb.toString
