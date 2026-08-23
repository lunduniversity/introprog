//> using scala 3.8.4
//> using jvm 21
//> using dep com.lihaoyi::os-lib:0.11.8

// Scope workspace-en + surface identifiers for the shared rename glossary.
// Strips comments/strings (crude scanner), collects identifier tokens from CODE only, excludes Scala
// keywords + common English words, prints frequency (recurring ones matter most for cross-file consistency).
//   scala-cli run autotranslate/scratch/scan-identifiers.scala -- /home/bjornr/git/hub/lunduniversity/introprog/workspace-en
@main def run(dirStr: String): Unit =
  val dir = os.Path(dirStr)
  val files = os.walk(dir).filter(f => os.isFile(f) && (f.ext == "scala" || f.ext == "java"))
  // strip //line, /*block*/, "...", """...""", '...'
  def stripCode(s: String): String =
    val b = new StringBuilder; var i = 0; val n = s.length
    while i < n do
      val c = s(i)
      if c == '/' && i+1 < n && s(i+1) == '/' then { while i < n && s(i) != '\n' do i += 1 }
      else if c == '/' && i+1 < n && s(i+1) == '*' then { i += 2; while i+1 < n && !(s(i)=='*'&&s(i+1)=='/') do i += 1; i += 2 }
      else if c == '"' && i+2 < n && s(i+1)=='"' && s(i+2)=='"' then { i += 3; while i+2 < n && !(s(i)=='"'&&s(i+1)=='"'&&s(i+2)=='"') do i += 1; i += 3 }
      else if c == '"' then { i += 1; while i < n && s(i) != '"' do { if s(i)=='\\' then i += 1; i += 1 }; i += 1 }
      else if c == '\'' then { i += 1; while i < n && s(i) != '\'' do { if s(i)=='\\' then i += 1; i += 1 }; i += 1; b.append(' ') }
      else { b.append(c); i += 1 }
    b.toString
  val keywords = Set("def","val","var","class","object","trait","extends","with","new","if","then","else",
    "while","for","do","yield","match","case","import","package","return","this","super","type","given",
    "using","enum","end","true","false","null","override","private","protected","final","lazy","implicit",
    "abstract","sealed","def","main","Unit","Int","String","Double","Boolean","Long","Char","Float","Short",
    "Array","List","Vector","Seq","Map","Set","Option","Some","None","println","print","public","static","void",
    "int","double","boolean","char","class","import","extends","args","to","until","by","map","filter","foreach",
    "length","size","head","tail","apply","toString","sum","max","min","abs","sqrt","mkString","split","trim")
  val idRe = "[A-Za-zÅÄÖåäö_][A-Za-z0-9ÅÄÖåäö_]*".r
  val freq = scala.collection.mutable.Map[String, Int]().withDefaultValue(0)
  val where = scala.collection.mutable.Map[String, scala.collection.mutable.Set[String]]()
  var totLines = 0
  for f <- files do
    val src = os.read(f)
    totLines += src.linesIterator.count(_.trim.nonEmpty)
    for m <- idRe.findAllIn(stripCode(src)) if !keywords(m) && m.length >= 2 do
      freq(m) += 1
      where.getOrElseUpdate(m, scala.collection.mutable.Set()) += f.relativeTo(dir).toString
  println(s"=== ${files.size} code files, $totLines non-blank lines, ${freq.size} unique identifiers ===")
  // DEFINITE Swedish identifiers: contain åäöÅÄÖ
  val swed = freq.keys.filter(_.exists("åäöÅÄÖ".contains)).toSeq.sortBy(k => -freq(k))
  println(s"=== ${swed.size} identifiers containing åäöÅÄÖ (definitely Swedish) ===")
  for k <- swed do println(f"  ${freq(k)}%4d  ${where(k).mkString(", ")}  ->  $k")
  // dump ALL unique identifiers (sorted) for eyeballing non-åäö Swedish ones
  os.write.over(dir / os.up / "autotranslate" / "scratch" / "all-identifiers.txt",
    freq.toSeq.sortBy((k, _) => k.toLowerCase).map((k, c) => f"$c%4d  ${where(k).size}%2d  $k").mkString("\n"))
  println("(all identifiers dumped to autotranslate/scratch/all-identifiers.txt)")
