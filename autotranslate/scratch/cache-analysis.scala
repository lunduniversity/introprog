//> using scala 3.8.4
// Analyse the autotranslate cache: how many fallbacks (sv==en), and — for the placeholder-DROP
// hypothesis — how many inline placeholders do fallback units carry? (Many => splitting dense units
// into smaller translation pieces should reduce drops.)

import scala.io.Source

@main def run(path: String): Unit =
  val place = raw"__C\d+__".r
  val rows = Source.fromFile(path, "UTF-8").getLines()
    .filter(_.contains("\t"))
    .map { l => val a = l.split("\t", 2); (a(0), a(1)) }
    .toVector

  val fallbacks = rows.filter((sv, en) => sv == en)
  def nPlace(s: String) = place.findAllIn(s).size

  println(s"total cache units      : ${rows.size}")
  println(s"fallbacks (sv==en)     : ${fallbacks.size}  (${100 * fallbacks.size / rows.size}%)")
  val fbWithPh = fallbacks.filter((sv, _) => nPlace(sv) > 0)
  println(s"  ...with placeholders : ${fbWithPh.size}")
  println(s"  ...plain prose (0 ph): ${fallbacks.size - fbWithPh.size}")

  println("\nplaceholder-count histogram among fallbacks (count : #units):")
  fallbacks.groupBy((sv, _) => nPlace(sv)).toVector.sortBy(_._1).foreach { (k, v) =>
    val bar = "#" * math.min(60, v.size)
    println(f"  $k%2d : ${v.size}%4d $bar")
  }

  println("\nhow much could splitting help? fallbacks by placeholder bucket:")
  val dense = fbWithPh.count((sv, _) => nPlace(sv) >= 3)
  println(s"  dense (>=3 placeholders): $dense  <- prime split candidates")
  println(s"  light (1-2 placeholders): ${fbWithPh.size - dense}")

  println("\ntop-5 densest fallback units (truncated):")
  fbWithPh.sortBy((sv, _) => -nPlace(sv)).take(5).foreach { (sv, _) =>
    println(s"  [${nPlace(sv)} ph] ${sv.take(100).replace("\\n", " ")}")
  }
