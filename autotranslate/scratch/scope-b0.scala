//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8
//> using file ../Latex.scala
//> using file ../Code.scala

// B0 SCOPING (read-only). Size the REAL case-(a) target for the bilingual code-scaffold pass:
// inline \code/\jcode/\lstinline/\verb spans AND verbatim code-ENV bodies (Code/REPL/...) embedded in
// .tex prose that actually CONTAIN SWEDISH. (Case (b) listings are already handled by mirror redirect;
// (c) Kojo is deferred; code already inside an \ifswedish clamp is absorbed by mask() and not counted.)
//
// Reuses the translator's OWN span locator (Latex.mask) + Swedish notion (Code.swedishish) so the count
// matches exactly what a real B0 pass would clamp — no divergent heuristic.
//   scala-cli run autotranslate/scratch/scope-b0.scala -- /home/bjornr/git/hub/lunduniversity/introprog

import scala.util.matching.Regex

@main def run(rootStr: String): Unit =
  val root = os.Path(rootStr)
  val skipSeg = Set("target", ".git", "old", "bin", ".bloop", ".metals", ".scala-build", ".bsp")
  // only the SHARED Swedish source trees (never the -en mirrors)
  val srcDirs = Seq(root / "compendium", root / "slides").filter(os.exists)

  val inlineRe: Regex = raw"(?s)^\\(code|jcode|lstinline|verb)\*?(.)".r
  val envRe: Regex = raw"(?s)^\\begin\{(Code|CodeSmall|REPL|REPLnonum|REPLsmall|lstlisting|verbatim|Verbatim)\}(.*)\\end\{".r

  // inner content of an inline verbatim span: drop the command + the two delimiters
  def inlineInner(span: String): String =
    val k = span.indexWhere(c => !c.isLetter && c != '\\', 1) // first delimiter char after \cmd
    if k < 0 || k + 1 >= span.length then "" else span.substring(k + 1, span.length - 1)

  final case class Unit(kind: String, content: String, span: String)

  def unitsIn(tex: String): Seq[Unit] =
    val (_, spans, _) = Latex.mask(tex, stripEng = true)
    spans.flatMap { s =>
      inlineRe.findPrefixMatchOf(s) match
        case Some(_) => Some(Unit("inline", inlineInner(s), s))
        case None =>
          envRe.findPrefixMatchOf(s) match
            case Some(m) => Some(Unit("env", m.group(2), s))
            case None    => None
    }.toSeq

  var totUnits = 0; var totSwUnits = 0
  val perFile = scala.collection.mutable.ArrayBuffer[(String, Int, Int, Int, Int)]() // file, inlineSw, envSw, inlineAll, envAll
  val sampleInline = scala.collection.mutable.ArrayBuffer[(String, String)]() // file, content
  val sampleEnv = scala.collection.mutable.ArrayBuffer[(String, String)]()
  val idFreq = scala.collection.mutable.Map[String, Int]().withDefaultValue(0)
  val idRe = "[A-Za-z_][A-Za-z0-9_]*".r

  for d <- srcDirs; f <- os.walk(d)
    if os.isFile(f) && f.ext == "tex" && !f.relativeTo(d).segments.exists(skipSeg)
  do
    val tex = os.read(f)
    val us = unitsIn(tex)
    if us.nonEmpty then
      val sw = us.filter(u => Code.swedishish(u.content))
      val inlineAll = us.count(_.kind == "inline"); val envAll = us.count(_.kind == "env")
      val inlineSw = sw.count(_.kind == "inline"); val envSw = sw.count(_.kind == "env")
      totUnits += us.size; totSwUnits += sw.size
      if sw.nonEmpty then
        perFile += ((f.relativeTo(root).toString, inlineSw, envSw, inlineAll, envAll))
        for u <- sw do
          if u.kind == "inline" && sampleInline.size < 60 then sampleInline += ((f.baseName, u.content))
          if u.kind == "env" && sampleEnv.size < 20 then sampleEnv += ((f.baseName, u.content.take(80).replace("\n", "⏎")))
          // collect å/ä/ö-bearing identifier tokens (definite Swedish) for the rename glossary preview
          for t <- idRe.findAllIn(u.content) if t.length >= 2 do () // ascii ids; å/ä/ö handled below
          for w <- u.content.split("[^A-Za-zÅÄÖåäö_]+") if w.exists("åäöÅÄÖ".contains) && w.length >= 2 do idFreq(w) += 1

  println(s"=== B0 case-(a) scope: code UNITS embedded in .tex prose (shared sv source only) ===")
  println(s"total code units (inline + env, not already \\ifswedish-clamped): $totUnits")
  println(s"  of which SWEDISH-containing (would be clamped by B0): $totSwUnits")
  println(s"  files with >=1 Swedish code unit: ${perFile.size}")
  println()
  println(s"=== per-file (Swedish inline / Swedish env / all inline / all env) — top 30 by Swedish count ===")
  for (file, iSw, eSw, iAll, eAll) <- perFile.sortBy(t => -(t._2 + t._3)).take(30) do
    println(f"  ${iSw + eSw}%4d  (inline $iSw%3d/$iAll%-4d  env $eSw%2d/$eAll%-3d)  $file")
  println()
  println(s"=== sample Swedish inline \\code contents (first ${sampleInline.size}) ===")
  for (file, c) <- sampleInline do println(f"  [$file%-28s] ${c.take(70)}")
  println()
  println(s"=== sample Swedish code-env bodies (first ${sampleEnv.size}, truncated) ===")
  for (file, c) <- sampleEnv do println(f"  [$file%-28s] $c")
  println()
  val swIds = idFreq.toSeq.sortBy(t => -t._2).take(40)
  println(s"=== top å/ä/ö identifier-ish tokens in code units (rename-glossary preview) ===")
  for (id, n) <- swIds do println(f"  $n%4d  $id")
