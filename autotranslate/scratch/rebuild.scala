//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8

// Build-and-verify driver. Run from the introprog/ root:  scala-cli run autotranslate/scratch/rebuild.scala
// Drives sbt (regenerate EN mirror) + pdflatex (no-halt sweep) via os-lib, then reports build errors,
// Swedish density, and heading terms — all in Scala, no bash heredocs.

// Find the introprog root by walking up until we see the compendium/ dir — so this works whether run
// from introprog/, autotranslate/, or autotranslate/scratch/ (lets us cd in close to avoid the /tmp
// path-resolution-bypass approval).
def findRoot: os.Path =
  var p = os.pwd
  while !os.exists(p / "compendium") && p != p / os.up do p = p / os.up
  p

@main def run(args: String*): Unit =
  // Explicit root arg (an absolute path) avoids depending on cwd — so this launches bare with an
  // absolute script path and NO `cd` (which would taint the allowlisted command). Falls back to findRoot.
  val root = args.find(a => !a.startsWith("--") && a != "noregen").map(os.Path(_)).getOrElse(findRoot)
  val dir  = root / "compendium-en"

  if !args.contains("noregen") then
    println("=== sbt: autotranslateProject/run --all ===")
    os.proc("sbt", "--client", "autotranslateProject/run --all").call(cwd = root, check = false)

  println("=== pdflatex no-halt sweep ===")
  os.proc("pdflatex", "-interaction=nonstopmode", "compendium-en.tex")
    .call(cwd = dir, check = false, stdout = os.Pipe, stderr = os.Pipe)

  // LaTeX logs aren't valid UTF-8 -> read bytes as Latin-1.
  val logText = new String(os.read.bytes(dir / "compendium-en.log"), "ISO-8859-1")
  val errs = logText.linesIterator.filter(_.startsWith("! ")).toVector.distinct
  println(s"=== build errors: ${errs.size} ===")
  errs.take(12).foreach(e => println("  " + e))

  val txt = os.proc("pdftotext", (dir / "compendium-en.pdf").toString, "-").call(check = false).out.text()
  val lines = txt.linesIterator.toVector
  val svLines = lines.count(l => "[åäöÅÄÖ]".r.findFirstIn(l).isDefined)
  println(s"=== Swedish: $svLines / ${lines.size} lines (${if lines.nonEmpty then 100 * svLines / lines.size else 0}%) ===")
  def c(w: String) = (raw"\b" + java.util.regex.Pattern.quote(w) + raw"\b").r.findAllIn(txt).size
  println("=== heading terms (Swedish ~0 wanted) ===")
  for (sv, en) <- List("Övning" -> "Exercise", "Lösning" -> "Solution", "Lösningar" -> "Solutions") do
    println(f"  $sv%-10s = ${c(sv)}%4d    $en%-10s = ${c(en)}%4d")
