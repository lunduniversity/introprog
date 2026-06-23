/** autotranslate — (re-)creates the English-side mirror of the Swedish source.
  *
  * For now the .tex CONTENT is copied AS-IS (still Swedish); only the file names
  * get an -en suffix (X.tex -> X-en.tex). The directory structure under
  * compendium/ and slides/ is mirrored into compendium-en/ and slides-en/.
  * Actual translation will be wired in later, step by step.
  *
  * Run from the introprog root with:  sbt --client autotranslate
  */
object Main:
  val mirrors = Seq("compendium" -> "compendium-en", "slides" -> "slides-en")

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
      val texFiles = os.walk(srcDir).filter(p => os.isFile(p) && p.ext == "tex")
      for f <- texFiles do
        val rel = f.relativeTo(srcDir) // e.g. modules/w01-intro-chapter.tex
        val target = dstDir / rel / os.up / s"${f.baseName}-en.tex"
        os.makeDir.all(target / os.up)
        os.copy.over(f, target)
      println(s"autotranslate: mirrored ${texFiles.size} .tex files  $src -> $dst")
