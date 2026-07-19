//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8
//> using file ../CodeGlossary.scala

// Glossary blast-radius check (born as the PR #943 review probe, promoted here post-merge).
// Runs the production CodeGlossary over every .scala/.java file that Main.scala would mirror,
// and reports which files it CHANGES. No sbt, no model, no mirror writes — read-and-report only.
//
// Modes (2nd arg, default "ids"):
//   ids   probe CodeGlossary.renderCodeIds — the LIVE mirror path (identifiers only, string
//         literals copied verbatim). Expected:
//         changes ONLY files whose identifiers are in the ratified id map; any file listed
//         under COLLATERAL is a regression to investigate.
//   full  probe CodeGlossary.render (id + str) — the clamp path, NOT applied corpus-wide.
//         This mode is what found the PR #943 half-translation collateral ("Clean toaletter").
//
// ⚠️ SCOPE LIMIT: this shows what the glossary pass does ON ITS OWN. The real pipeline runs
// Code.translate after it (model-backed). Only an end-to-end run proves what ships.
// ⚠️ isRatifiedCluster is a CRUDE PATH HEURISTIC, not a ratification list; the per-line
// SPECIMENS printed are the ground truth, the split is only indicative.
//
// RUN (from the repo root):
//   scala-cli run autotranslate/scratch/glossary-blast-radius.scala -- .
//   scala-cli run autotranslate/scratch/glossary-blast-radius.scala -- . full

@main def glossaryBlastRadius(argsIn: String*): Unit =
  val rootStr = argsIn.headOption.getOrElse { println("usage: glossary-blast-radius <introprog-root> [ids|full]"); sys.exit(2) }
  val mode = argsIn.lift(1).getOrElse("ids")
  val root = os.Path(rootStr, os.pwd)
  val apply: String => String = mode match
    case "ids"  => CodeGlossary.renderCodeIds
    case "full" => CodeGlossary.render
    case other  => println(s"unknown mode '$other' (use ids | full)"); sys.exit(2)
  val mirrors = Seq("compendium", "slides")

  // Crude path heuristic for "was this file part of a ratified cluster" — see the caveat above.
  def isRatifiedCluster(p: os.Path): Boolean =
    val s = p.toString.toLowerCase
    s.contains("vego") || s.contains("w06") || s.contains("w07-inherit") ||
      s.contains("w10") || s.contains("patterns") || s.contains("match")

  val files =
    for
      m <- mirrors
      dir = root / m
      if os.exists(dir)
      f <- os.walk(dir)
      if os.isFile(f) && (f.ext == "scala" || f.ext == "java")
    yield f

  var nSeen, nChanged, nRatified, nCollateral = 0
  val collateral = collection.mutable.ArrayBuffer.empty[(os.Path, Seq[(String, String)])]

  for f <- files do
    nSeen += 1
    val src = os.read(f)
    val out = apply(src)
    if out != src then
      nChanged += 1
      val diffs =
        src.linesIterator.zip(out.linesIterator).filter((a, b) => a != b).take(6).toSeq
      if isRatifiedCluster(f) then nRatified += 1
      else
        nCollateral += 1
        collateral += ((f, diffs))

  println(s"=== CodeGlossary blast radius (mode: $mode) ===")
  println(s"mirrored .scala/.java files scanned : $nSeen")
  println(s"files CHANGED                      : $nChanged")
  println(s"  of which in a RATIFIED cluster   : $nRatified   (crude path heuristic)")
  println(s"  of which COLLATERAL (unratified) : $nCollateral   (crude path heuristic)")
  println()
  if collateral.isEmpty then
    println("NO COLLATERAL — the glossary pass touches only ratified-cluster files.")
  else
    println(s"=== COLLATERAL: files outside the ratified clusters that get REWRITTEN ===")
    for (f, diffs) <- collateral.sortBy(_._1.toString) do
      println()
      println(s"--- ${f.relativeTo(root)}")
      for (a, b) <- diffs do
        println(s"    SV: ${a.trim}")
        println(s"    EN: ${b.trim}")
