//> using scala 3.8.4
// Show cache entries whose Swedish side contains a needle, flagging fallbacks (sv==en = kept Swedish).
//   scala-cli run cache-grep.scala -- <cache.tsv> <needle>

import scala.io.Source

@main def run(path: String, needle: String): Unit =
  var fb, ok = 0
  Source.fromFile(path, "UTF-8").getLines().filter(_.contains("\t")).foreach { l =>
    val a = l.split("\t", 2)
    if a(0).contains(needle) then
      if a(0) == a(1) then { fb += 1; println(s"[FALLBACK] ${a(0).take(72).replace("\\n", " ")}") }
      else { ok += 1; println(s"[ok->en  ] ${a(0).take(34).replace("\\n", " ")}  =>  ${a(1).take(40).replace("\\n", " ")}") }
  }
  println(s"--- $needle: $ok translated, $fb fallback ---")
