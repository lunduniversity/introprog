//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8

// Thin driver: run the autotranslate sbt project with arbitrary args, from the introprog root (no cd).
//   scala-cli run autotranslate/scratch/at.scala -- /abs/introprog --workspace-en --only irritext
@main def run(rootStr: String, atArgs: String*): Unit =
  val root = os.Path(rootStr)
  // NOTE: `sbt --client` output is NOT captured via inherited stdio through this tool, so we pipe +
  // print at completion (reliable capture). Trade-off: no live progress mid-run. TODO for live progress:
  // stream to a log file the caller can tail, or call pdflatex/sbt-server output differently.
  val r = os.proc("sbt", "--client", s"autotranslateProject/run ${atArgs.mkString(" ")}")
    .call(cwd = root, check = false, stdout = os.Pipe, stderr = os.Pipe, mergeErrIntoOut = true)
  println(r.out.text())
