//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8

// Compile-check every week-dir of a workspace tree (each is its own scala-cli project). Skips w01_kojo
// (deferred special case) and the inner introprog/ lib dir (compiled by its own subdirs). Reports
// PASS/FAIL per dir + the first error line on failure. Run bare with the workspace dir:
//   scala-cli run autotranslate/scratch/compile-workspace.scala -- /abs/introprog/workspace-en
@main def run(rootStr: String): Unit =
  val root = os.Path(rootStr)
  val skip = Set("w01_kojo", "introprog")
  val dirs = os.list(root).filter(d => os.isDir(d) && !skip(d.last)
    && os.walk(d).exists(f => os.isFile(f) && (f.ext == "scala" || f.ext == "java"))).sortBy(_.last)
  var pass = 0; var fail = 0
  for d <- dirs do
    val r = os.proc("scala-cli", "compile", d.toString).call(check = false, stdout = os.Pipe, stderr = os.Pipe, mergeErrIntoOut = true)
    if r.exitCode == 0 then { pass += 1; println(s"  PASS  ${d.last}") }
    else
      fail += 1
      val err = r.out.text().linesIterator.find(_.contains("error")).getOrElse(r.out.text().linesIterator.toSeq.takeRight(1).headOption.getOrElse(""))
      println(s"  FAIL  ${d.last}  -> ${err.trim.take(140)}")
  println(s"=== compile-check: $pass PASS, $fail FAIL (of ${dirs.size} dirs) ===")
