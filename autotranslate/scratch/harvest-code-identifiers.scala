//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8
//> using file ../Latex.scala
//> using file ../Code.scala

// Glossary HARVESTER (read-only) for the fused B0+D code-identifier pass. Collects every SWEDISH
// identifier across ALL three code surfaces that must stay aligned:
//   (1) inline \code/\jcode/\lstinline + verbatim code ENVS in .tex prose (via Latex.mask),
//   (2) .scala/.java example files under compendium/examples/,
//   (3) .scala/.java under workspace/.
// Tokenizes identifiers (incl. åäö), strips comments/strings in code files so only CODE identifiers count,
// classifies an identifier as Swedish if it (a) contains åäöÅÄÖ [DEFINITE] or (b) its camelCase/under_score
// parts hit Code.swedishWords [PROBABLE]. Emits frequency + file locations + a draft glossary TSV stub.
// Also prints its scanned-file INVENTORY (absorbs the ad-hoc find|wc survey).
//   scala-cli run autotranslate/scratch/harvest-code-identifiers.scala -- /home/bjornr/git/hub/lunduniversity/introprog

import scala.util.matching.Regex
import scala.collection.mutable

@main def run(rootStr: String): Unit =
  val root = os.Path(rootStr)
  val skipSeg = Set("target", ".git", "old", "bin", ".bloop", ".metals", ".scala-build", ".bsp")

  // ── identifier tokenization + Swedish classification ──
  val idRe: Regex = "[A-Za-zÅÄÖåäö_][A-Za-z0-9ÅÄÖåäö_]*".r
  val keywords = Set("def","val","var","class","object","trait","extends","with","new","if","then","else",
    "while","for","do","yield","match","case","import","package","return","this","super","type","given",
    "using","enum","end","true","false","null","override","private","protected","final","lazy","implicit",
    "abstract","sealed","main","Unit","Int","String","Double","Boolean","Long","Char","Float","Short",
    "Array","List","Vector","Seq","Map","Set","Option","Some","None","println","print","public","static",
    "void","int","double","boolean","char","args","to","until","by","map","filter","foreach","length",
    "size","head","tail","apply","toString","sum","max","min","abs","sqrt","mkString","split","trim",
    "scala","java","run","compile","object","value","values")
  // split a (possibly compound camelCase / under_score) identifier into lowercased word parts
  def parts(id: String): Seq[String] =
    id.split("(?<=[a-zåäö])(?=[A-ZÅÄÖ])|_").filter(_.nonEmpty).map(_.toLowerCase).toSeq
  def isSwedish(id: String): String =
    if id.exists("åäöÅÄÖ".contains) then "DEF"
    else if parts(id).exists(Code.swedishWords.contains) then "PROB"
    else ""

  // strip comments/strings from code so only CODE identifiers are harvested
  def stripCode(s: String): String =
    val b = new StringBuilder; var i = 0; val n = s.length
    while i < n do
      val c = s(i)
      if c == '/' && i+1 < n && s(i+1) == '/' then { while i < n && s(i) != '\n' do i += 1 }
      else if c == '/' && i+1 < n && s(i+1) == '*' then { i += 2; while i+1 < n && !(s(i)=='*'&&s(i+1)=='/') do i += 1; i += 2 }
      else if c == '"' && i+2 < n && s(i+1)=='"' && s(i+2)=='"' then { i += 3; while i+2 < n && !(s(i)=='"'&&s(i+1)=='"'&&s(i+2)=='"') do i += 1; i += 3 }
      else if c == '"' then { i += 1; while i < n && s(i) != '"' do { if s(i)=='\\' then i += 1; i += 1 }; i += 1 }
      else if c == '\'' then { i += 1; while i < n && s(i) != '\'' do { if s(i)=='\\' then i += 1; i += 1 }; i += 1; b.append(' ') }
      else { b.append(c); i += 1 }
    b.toString

  // pure Swedish function/prose words that, as a WHOLE one-part identifier NOT declared in code, are almost
  // always transcript/output/prose noise (leaking from REPL `scala>` sessions + program output), not a
  // renameable code identifier. (åäö-bearing so they classify DEF, but they are noise.)
  val proseNoise = Set("är","på","än","för","från","över","så","där","här","när","då","två","små","gå","få",
    "första","sådan","någon","något","några","många","därför","även","redan","alltså","väl","lägg","väldigt")

  // ── collectors ──
  val freq = mutable.Map[String, Int]().withDefaultValue(0)
  val where = mutable.Map[String, mutable.Set[String]]()
  val kindOf = mutable.Map[String, String]() // DEF / PROB
  val prov = mutable.Map[String, mutable.Set[String]]() // inline / env / code
  val declared = mutable.Set[String]() // identifiers DECLARED in a .scala/.java (def/val/var/class/...)
  def add(id: String, loc: String, p: String): Unit =
    val sw = isSwedish(id)
    if sw.nonEmpty && id.length >= 2 && !keywords(id) then
      freq(id) += 1; where.getOrElseUpdate(id, mutable.Set()) += loc; kindOf(id) = sw
      prov.getOrElseUpdate(id, mutable.Set()) += p

  // surface (1): .tex code units
  val inlineRe: Regex = raw"(?s)^\\(code|jcode|lstinline|verb)\*?(.)".r
  val envRe: Regex = raw"(?s)^\\begin\{(Code|CodeSmall|REPL|REPLnonum|REPLsmall|lstlisting|verbatim|Verbatim)\}(.*)\\end\{".r
  def inlineInner(span: String): String =
    val k = span.indexWhere(c => !c.isLetter && c != '\\', 1)
    if k < 0 || k + 1 >= span.length then "" else span.substring(k + 1, span.length - 1)
  var nTex = 0; var nTexUnits = 0
  for d <- Seq(root / "compendium", root / "slides").filter(os.exists); f <- os.walk(d)
    if os.isFile(f) && f.ext == "tex" && !f.relativeTo(d).segments.exists(skipSeg)
  do
    nTex += 1
    val (_, spans, _) = Latex.mask(os.read(f), stripEng = true)
    for s <- spans do
      val content =
        if inlineRe.findPrefixMatchOf(s).isDefined then inlineInner(s)
        else envRe.findPrefixMatchOf(s).map(_.group(2)).getOrElse("")
      if content.nonEmpty then
        nTexUnits += 1
        val isInline = inlineRe.findPrefixMatchOf(s).isDefined
        for id <- idRe.findAllIn(stripCode(content)) do add(id, "tex:" + f.baseName, if isInline then "inline" else "env")

  // surfaces (2)+(3): example + workspace code files
  val codeDirs = Seq(root / "compendium" / "examples", root / "workspace").filter(os.exists)
  val byDir = mutable.Map[String, Int]().withDefaultValue(0)
  var nCode = 0
  for d <- codeDirs; f <- os.walk(d)
    if os.isFile(f) && (f.ext == "scala" || f.ext == "java") && !f.relativeTo(d).segments.exists(skipSeg)
  do
    nCode += 1; byDir(d.last + "/" + f.relativeTo(d).segments.headOption.getOrElse("")) += 1
    val raw = os.read(f); val stripped = stripCode(raw)
    for id <- idRe.findAllIn(stripped) do add(id, "code:" + f.relativeTo(d).toString, "code")
    // declarations: def/val/var/class/object/trait/enum/given/type NAME, and case class/object NAME
    for m <- raw"\b(?:def|val|var|class|object|trait|enum|given|type)\s+([A-Za-zÅÄÖåäö_][A-Za-z0-9ÅÄÖåäö_]*)".r.findAllMatchIn(stripped) do declared += m.group(1)

  // ── inventory (absorbs the find|wc survey) ──
  println(s"=== scanned-file inventory ===")
  println(s"  .tex files: $nTex  (code units with content: $nTexUnits)")
  println(s"  code files: $nCode  across ${codeDirs.map(_.relativeTo(root)).mkString(", ")}")
  for (dir, c) <- byDir.toSeq.sortBy(-_._2).take(18) do println(f"    $c%4d  $dir")

  // ── tiering by provenance + declaration ──
  // T1 RENAME   — declared in a .scala/.java (authoritative code identifier; rename code+listing+inline \code)
  // T2 INLINE   — not declared, but used in inline \code{} in prose (rename in the \else branch + must match)
  // T3 ENVONLY  — only in code ENVS / not inline / not declared: REPL/output/example bodies (review case-by-case)
  // NOISE       — a pure Swedish prose/function word, env-only, not declared (transcript/output leakage)
  def tier(id: String): String =
    val ps = prov.getOrElse(id, mutable.Set())
    if declared(id) then "T1-RENAME"
    else if proseNoise(id.toLowerCase) && !ps.contains("inline") then "NOISE"
    else if ps.contains("inline") then "T2-INLINE"
    else "T3-ENVONLY"
  val tierOrder = Map("T1-RENAME" -> 0, "T2-INLINE" -> 1, "T3-ENVONLY" -> 2, "NOISE" -> 3)
  val sorted = freq.toSeq.sortBy { case (id, c) => (tierOrder(tier(id)), -c, id.toLowerCase) }
  val byTier = sorted.groupBy { case (id, _) => tier(id) }
  println(s"\n=== ${freq.size} distinct Swedish identifiers, tiered ===")
  for t <- Seq("T1-RENAME", "T2-INLINE", "T3-ENVONLY", "NOISE") do
    val rows = byTier.getOrElse(t, Nil)
    println(s"\n--- $t (${rows.size}) ---")
    for (id, c) <- rows.sortBy { case (id, c) => (-c, id.toLowerCase) } do
      println(f"  $c%4d  $id%-28s  ${where(id).toSeq.sorted.take(5).mkString(", ")}")

  // draft glossary stub: en column blank, tiered so review can focus on T1+T2 (the real rename set)
  val stub = root / "autotranslate" / "scratch" / "code-glossary-draft.tsv"
  val body = sorted.map { case (id, c) => s"${tier(id)}\t$id\t\t$c\t${where(id).size}\t${prov.getOrElse(id, Set()).toSeq.sorted.mkString("|")}" }.mkString("\n")
  os.write.over(stub, s"# tier\tsv-identifier\ten-identifier\tfreq\tnfiles\tprovenance  (fill col 3; whole-identifier rename; focus T1+T2)\n$body\n")
  println(s"\n(draft glossary stub -> ${stub.relativeTo(root)} — ${freq.size} rows; T1=${byTier.getOrElse("T1-RENAME",Nil).size} T2=${byTier.getOrElse("T2-INLINE",Nil).size} T3=${byTier.getOrElse("T3-ENVONLY",Nil).size} NOISE=${byTier.getOrElse("NOISE",Nil).size})")
