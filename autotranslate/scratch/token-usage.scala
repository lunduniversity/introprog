//> using scala 3.8.4
//> using dep com.lihaoyi::os-lib:0.11.8
//> using dep com.lihaoyi::ujson:4.4.3

// token-usage: read-only introspection on the CURRENT Claude Code session.
// Parses the newest *.jsonl transcript in the project's ~/.claude/projects/<enc>/ dir, sums the per-message
// `usage` fields, and reports spend + token velocity (per-turn) + token acceleration (recent vs earlier) +
// a context-fill estimate (last turn's input+cache ≈ current window occupancy). ~zero cost, mutates nothing.
//
// Usage: scala-cli run token-usage.scala [-- [<transcript-dir>] [--ceiling <L>]]
//   --ceiling <L> : smart-zone ceiling as a fraction (e.g. 0.35) → gauge "fill% vs L"; warns near/over it.
// Default dir = this project's transcript dir. Generalize later into a `tt usage` genscalator tool.

import scala.util.Try

val DefaultDir =
  "/home/bjornr/.claude/projects/-home-bjornr-git-berg-bjornregnell-muntabot-synch-introprog"
val WindowLimit = 1_000_000.0 // claude-opus-4-8[1m]

final case class Turn(out: Int, in: Int, cr: Int, cc: Int, ts: String)

def comma(n: Long): String =
  n.toString.reverse.grouped(3).mkString(",").reverse

@main def tokenUsage(args: String*): Unit =
  val argv = args.toList
  // --ceiling <L>: smart-zone ceiling fraction. Omitted → a 0.35 prior (a guess, clearly labelled).
  val (ceilingOpt, rest) =
    val i = argv.indexOf("--ceiling")
    if i >= 0 && i + 1 < argv.size then
      (scala.util.Try(argv(i + 1).toDouble).toOption, argv.patch(i, Nil, 2))
    else (None, argv)
  val ceiling = ceilingOpt.getOrElse(0.35)
  val ceilingIsPrior = ceilingOpt.isEmpty
  val dir = os.Path(rest.headOption.getOrElse(DefaultDir))
  val jsonls = Try(os.list(dir).filter(_.ext == "jsonl")).getOrElse(Nil)
  if jsonls.isEmpty then
    println(s"token-usage: no .jsonl transcripts in $dir"); sys.exit(1)
  val transcript = jsonls.maxBy(os.mtime)

  val turns: Vector[Turn] = os.read.lines(transcript).flatMap { line =>
    Try {
      val j = ujson.read(line)
      if j("type").str == "assistant" then
        val u = j("message")("usage")
        def gi(k: String) = Try(u(k).num.toInt).getOrElse(0)
        Some(Turn(gi("output_tokens"), gi("input_tokens"),
                  gi("cache_read_input_tokens"), gi("cache_creation_input_tokens"),
                  Try(j("timestamp").str).getOrElse("")))
      else None
    }.toOption.flatten
  }.toVector

  if turns.isEmpty then
    println(s"token-usage: ${transcript.last} has no assistant usage records yet"); sys.exit(0)

  val n = turns.size
  val totOut = turns.map(_.out.toLong).sum
  val totIn = turns.map(_.in.toLong).sum
  val totCR = turns.map(_.cr.toLong).sum
  val totCC = turns.map(_.cc.toLong).sum
  val last = turns.last
  val contextFill = last.in.toLong + last.cr + last.cc
  val fillPct = contextFill.toDouble / WindowLimit * 100

  val perTurn = totOut.toDouble / n
  val k = math.min(10, n)
  val recent = turns.takeRight(k)
  val earlier = turns.dropRight(k)
  val recentVel = recent.map(_.out).sum.toDouble / recent.size
  val earlierVel = if earlier.nonEmpty then earlier.map(_.out).sum.toDouble / earlier.size else recentVel
  val accel = recentVel - earlierVel
  val accelMark =
    if math.abs(accel) < 0.10 * math.max(1.0, earlierVel) then "≈ flat"
    else if accel > 0 then f"▲ RISING (+${accel}%.0f/turn)" else f"▼ falling (${accel}%.0f/turn)"

  // wall-clock (includes idle time when human is away → caveat)
  val mins: Option[Long] =
    if turns.head.ts.nonEmpty && last.ts.nonEmpty then
      Try(java.time.Duration.between(
        java.time.Instant.parse(turns.head.ts), java.time.Instant.parse(last.ts)).toMinutes).toOption
    else None

  println(s"=== token-usage — session ${transcript.baseName.take(8)} — $n assistant turns ===")
  println(s"spend (output):   ${comma(totOut)} tok   [the billable lever]")
  println(s"input/cache:      in=${comma(totIn)}  cache_read=${comma(totCR)}  cache_creation=${comma(totCC)}")
  println(f"context fill:     ~${comma(contextFill)} tok of 1M  (${fillPct}%.1f%% of window)  [last turn in+cache ≈ current occupancy]")
  val ceilRatio = (fillPct / 100.0) / ceiling
  val ceilMark =
    if ceilRatio >= 1.0 then "🛑 OVER — likely dumb zone; compact now"
    else if ceilRatio >= 0.8 then "⚠ compact trigger crossed (≥0.8·L) — propose the compact dance"
    else "✅ smart zone"
  val priorNote = if ceilingIsPrior then " prior/guess" else ""
  println(f"smart-zone ceiling: fill ${fillPct}%.1f%% vs L=${ceiling * 100}%.0f%%$priorNote → ${ceilRatio}%.2f of ceiling  $ceilMark")
  println(f"velocity:         ${perTurn}%.0f output tok/turn overall   |   last $k: ${recentVel}%.0f/turn")
  println(s"acceleration:     $accelMark   [watch for ▲ = runaway / brittle thrash / context-rot loop]")
  mins.foreach(m => println(s"wall-clock:       ${m} min elapsed (incl. idle)"))
