//> using scala 3.8.4
//> using dep com.lihaoyi::os-lib:0.11.8

// Run an autotranslate task via `sbt --client autotranslateProject/run <args>` with cwd PINNED to the
// introprog root — works from any shell cwd (the harness resets cwd per call, and sbt --client needs the
// project dir). Mirrors build-release.scala's os.proc(cwd=root) pattern.
//   scala-cli run autotranslate/scratch/at-run.scala -- <introprog-root> <at-args...>
//   e.g. ... -- /home/bjornr/git/hub/lunduniversity/introprog --prose-leaks
@main def runAt(root: String, atArgs: String*): Unit =
  val cmd = ("autotranslateProject/run " + atArgs.mkString(" ")).trim
  println(s"=== sbt --client $cmd  (cwd=$root) ===")
  val r = os.proc("sbt", "--client", cmd)
    .call(cwd = os.Path(root), check = false, stdout = os.Inherit, stderr = os.Inherit)
  println(s"=== exit=${r.exitCode} ===")
