//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8

// Run sbt compile for the autotranslate subproject from the introprog root (explicit cwd, no cd).
//   scala-cli run autotranslate/scratch/compile.scala -- /home/bjornr/git/hub/lunduniversity/introprog
@main def run(rootStr: String): Unit =
  val root = os.Path(rootStr)
  val r = os.proc("sbt", "--client", "autotranslateProject/compile").call(cwd = root, check = false, stdout = os.Pipe, stderr = os.Pipe, mergeErrIntoOut = true)
  println(r.out.text())
  println(s"=== exit ${r.exitCode} ===")
