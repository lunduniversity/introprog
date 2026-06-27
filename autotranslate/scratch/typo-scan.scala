//> using scala 3.8.4
//> using dep com.lihaoyi::os-lib:0.11.8

// Swedish typo CANDIDATES for BR review (Task E): find rare words that are edit-distance 1 from a
// much more common word in the same corpus (a classic typo signature). Heuristic only — BR approves
// each before any Swedish-source edit (creative/coined language is kept). Run from anywhere in the tree.

def findRoot: os.Path =
  var p = os.pwd
  while !os.exists(p / "compendium") && p != p / os.up do p = p / os.up
  p

def edit1(a: String, b: String): Boolean =
  if math.abs(a.length - b.length) > 1 then false
  else
    // single substitution (equal length) or single insertion/deletion
    if a.length == b.length then a.zip(b).count(_ != _) == 1
    else
      val (s, l) = if a.length < b.length then (a, b) else (b, a)
      var i = 0; var j = 0; var diff = 0
      while i < s.length && diff <= 1 do
        if s(i) == l(j) then { i += 1; j += 1 } else { diff += 1; j += 1 }
      diff + (l.length - j) <= 1

@main def run(): Unit =
  val root = findRoot
  val files = List("compendium", "slides").flatMap(d => os.walk(root / d))
    .filter(p => p.ext == "tex" && os.isFile(p) && !p.segments.contains("old"))
  val wordRe = raw"[A-Za-zÅÄÖåäö]{4,}".r
  // strip LaTeX commands so we count PROSE words, not macro names
  val freq = scala.collection.mutable.Map[String, Int]().withDefaultValue(0)
  for f <- files do
    val prose = raw"\\[A-Za-z@]+".r.replaceAllIn(os.read(f), " ")
    for m <- wordRe.findAllIn(prose) do freq(m.toLowerCase) += 1

  val common = freq.filter(_._2 >= 6).keys.toVector
  val rare   = freq.filter((w, c) => c <= 2 && w.length >= 6).keys.toVector.sorted
  println(s"corpus: ${freq.size} distinct prose words; ${rare.size} rare (<=2)")
  println("=== typo candidates: rare word (freq) ~ common word (freq) ===")
  var n = 0
  for r <- rare do
    common.find(c => c != r && edit1(r, c) && freq(c) >= freq(r) * 4) match
      case Some(c) => n += 1; println(f"  $r%-22s (${freq(r)})  ~  $c (${freq(c)})")
      case None    =>
  println(s"--- $n candidates (BR reviews; keep creative/coined words) ---")
