//> using scala 3.8.4
//> using dep com.lihaoyi::os-lib:0.11.8

// Confirmation-safe sbt-task runner: invokes `sbt --client <task>` in the introprog root via os.proc
// (no shell `cd`/`&&`/pipe/loop, so it won't trip the permission prompt), then prints the tail of the
// output (the `[swedish] N%` line on success, or the LaTeX error). Usage:
//   scala-cli run autotranslate/scratch/sbt-task.scala -- <introprog-root> <task> [<task> ...]
@main def run(root: String, tasks: String*): Unit =
  val r = os.proc("sbt", "--client", tasks)
    .call(cwd = os.Path(root), check = false, mergeErrIntoOut = true)
  val lines = r.out.lines()
  val relevant = lines.filter(l =>
    val s = l.toLowerCase
    s.contains("swedish]") || s.contains("output written") || s.contains("fatal") ||
    s.contains("! ") || s.contains("undefined control") || s.contains("runaway") ||
    s.contains("emergency") || s.contains("error]") || s.contains("[success]"))
  (if relevant.nonEmpty then relevant else lines.takeRight(15)).foreach(println)
  println(s"=== exit ${r.exitCode} ===")
