//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8

// ONE-TIME bootstrap: copy workspace/ -> workspace-en/ (excluding build artifacts), as the initial
// (still Swedish) committed English mirror. REFUSES to overwrite an existing workspace-en (so it can't
// clobber later translations). Run bare with the repo root as arg (no cd needed):
//   scala-cli run autotranslate/scratch/mk-workspace-en.scala -- /home/bjornr/git/hub/lunduniversity/introprog
@main def run(args: String*): Unit =
  val force = args.contains("--force")
  val root = os.Path(args.filterNot(_.startsWith("--")).head)
  val ws = root / "workspace"; val wsen = root / "workspace-en"
  require(os.exists(ws), s"no workspace at $ws")
  if os.exists(wsen) then
    if force then { println(s"--force: removing existing $wsen"); os.remove.all(wsen) }
    else { System.err.println(s"refusing: $wsen exists (would clobber translations); pass --force"); sys.exit(1) }
  val skipSeg = Set("target", ".git", "old", "bin", ".bloop", ".metals", ".scala-build", ".bsp")
  val skipExt = Set("class", "jar", "log", "aux", "out", "tasty")
  var n = 0
  for f <- os.walk(ws) if os.isFile(f) do
    val rel = f.relativeTo(ws)
    if !rel.segments.exists(skipSeg) && !skipExt(f.ext) then
      val t = wsen / rel; os.makeDir.all(t / os.up); os.copy.over(f, t); n += 1
  println(s"copied $n files -> $wsen")
