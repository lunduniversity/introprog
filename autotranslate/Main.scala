import java.util.regex.Matcher

/** autotranslate — (re-)creates the English-side mirror of the Swedish source.
  *
  * For now the .tex CONTENT is copied AS-IS (still Swedish); only the file names
  * get an -en suffix (X.tex -> X-en.tex). The directory structure under
  * compendium/ and slides/ is mirrored into compendium-en/ and slides-en/.
  * Actual translation will be wired in later, step by step.
  *
  * To make the mirror BUILDABLE we also:
  *   - rewrite LOCAL \input{X.tex}/\include{X.tex} targets to X-en.tex, so a
  *     mirrored file pulls in its mirrored neighbours (refs starting with ../
  *     or / are left alone — they reach siblings like ../glossary, ../img);
  *   - copy non-.tex assets as-is (same name): .cls, images, the scala/java
  *     files under examples, etc., skipping build artifacts and tooling dirs.
  *
  * Run from the introprog root with:  sbt --client autotranslate
  */
object Main:
  val mirrors = Seq("compendium" -> "compendium-en", "slides" -> "slides-en")

  /** File extensions that are build artifacts and are never mirrored. */
  val skipExt = Set(
    "aux", "log", "out", "nav", "snm", "vrb", "toc", "gz", "fdb_latexmk",
    "fls", "dvi", "xdv", "pdf", "class", "bbl", "blg", "idx", "ilg", "ind"
  )

  /** Path segments whose whole subtree is never mirrored. */
  val skipSeg = Set("target", ".git", "old", "bin", ".bloop", ".metals", ".scala-build", ".bsp")

  private val inputRef = raw"\\(input|include)\{([^}]*)\}".r

  /** Append -en to LOCAL \input/\include .tex targets; leave ../ and / refs alone. */
  def rewriteInputs(tex: String): String =
    inputRef.replaceAllIn(
      tex,
      m =>
        val t = m.group(2)
        val nt =
          if t.startsWith("..") || t.startsWith("/") then t
          else if t.endsWith(".tex") then t.dropRight(4) + "-en.tex"
          else t + "-en"
        Matcher.quoteReplacement(s"\\${m.group(1)}{$nt}")
    )

  def main(args: Array[String]): Unit =
    // Same idea as glossary/Main.scala's inputPrefix hack (here with os-lib):
    // works whether run from the introprog root or from the autotranslate sub-dir.
    val root = if os.pwd.last == "autotranslate" then os.pwd / os.up else os.pwd
    require(
      os.exists(root / "compendium"),
      s"cannot find 'compendium' dir from $root (run in introprog root or autotranslate/)"
    )
    for (src, dst) <- mirrors do
      val srcDir = root / src
      val dstDir = root / dst
      if os.exists(dstDir) then os.remove.all(dstDir) // clean re-create
      var nTex = 0
      var nAsset = 0
      for f <- os.walk(srcDir) if os.isFile(f) do
        val rel = f.relativeTo(srcDir)
        if rel.segments.exists(skipSeg) || skipExt(f.ext) then () // artifact: skip
        else if f.ext == "tex" then
          val target = dstDir / rel / os.up / s"${f.baseName}-en.tex"
          os.makeDir.all(target / os.up)
          os.write.over(target, rewriteInputs(os.read(f)))
          nTex += 1
        else
          val target = dstDir / rel
          os.makeDir.all(target / os.up)
          os.copy.over(f, target)
          nAsset += 1
      println(s"autotranslate: $src -> $dst  ($nTex .tex rewritten+renamed, $nAsset assets copied)")
