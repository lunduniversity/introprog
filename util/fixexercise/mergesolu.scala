/* NOTE: This util is not needed anymore, but kept for future inspiration of similar tasks.
         The most interesting parts are general file utils and also a latex cmd parser extractor.

   The run method merges -exercises.tex and -solutions.tex in to one file using the new format:

   \WHAT{Topic of exercise.}

   \QUESTBEGIN

   \Task \what~Some more exercise text.

   \SOLUTION

   \TaskSolved \what

   The solution.

   \QUESTEND

   Run with sbt in terminal (note the quotes):
   $ sbt 'run w02'
   or
   $ sbt
   > run w02
*/

object mergesolu {
  import java.nio.file.{Path, Paths, Files}

  def errExit(msg: String) = { Console.err.println(s"Error: $msg"); sys.exit(1)}

  def listFiles(dir: String): Vector[String] =
    Files.list(Paths.get(dir)).toArray.map(_.asInstanceOf[Path].toString).toVector.sorted

  def listFilesWith(partOfName: String, inDir: String): Vector[String] =
      listFiles(inDir).filter(_.toString.contains(partOfName))

  def workDir="../../compendium/modules"
  def exerFile(week: String) = s"$workDir/$week-exercise.tex"
  def soluFile(week: String) = s"$workDir/$week-solutions.tex"
  def outFile (week: String) = s"$workDir/$week-exercise-merged.tex"

  def extractWeekName(w: String)(fileName: String): Option[String] = {
    val start = fileName.indexOf(w)
    val stop = fileName.indexOf("-",start + w.size + 1)
    if (start > -1 && stop > -1) Some(fileName.substring(start,stop)) else None
  }


  def main(args: Array[String]): Unit = {
    val (default, msg) = ("w02", "args(0) missing, using default:")
    val arg = args.lift(0).getOrElse{ println(s"$msg $default") ; default }
    println("*** WELCOME TO mergesolu, a util for merging exercise and solution file")

    println(s"*** arg=$arg\nList of exercise files in $workDir:")
    val exeFiles =listFilesWith(partOfName="-exercise.tex", inDir=workDir)
    exeFiles.foreach(println)

    println(s"*** arg=$arg\nList of already merged files in $workDir:")
    listFilesWith(partOfName="-merged.tex", inDir=workDir).foreach(println)

    val weekNames = exeFiles.flatMap(extractWeekName(arg))
    val week      = weekNames.filter(_.contains(arg)).head

    if (isExistingFile(outFile(week))) errExit(s"merge already exists: ${outFile(week)}")
    run(week)
  }


  def replaceAll(data: String, pairs: (String, String)*): String = {
    var result = data
    for ((from, to) <- pairs) result = result.replaceAllLiterally(from, to)
    result
  }

  case class CutResult(patched: String, cutString: String, pos: Int)

  def cut(data: String, texcmd: String): CutResult = {
    var pos = data.indexOfSlice(texcmd)
    var cutString = ""
    if (pos > -1) {
      val start = pos
      pos = pos + texcmd.size
      var curlyDepth     = 0
      var wasInsideCurly = false
      var ready          = false
      while (pos < data.size && !ready) {
        if      (data(pos) == '{') { curlyDepth += 1; wasInsideCurly = true; pos += 1}
        else if (data(pos) == '}') { curlyDepth -= 1; pos += 1}
        ready = curlyDepth == 0 && (wasInsideCurly || !data(pos).isLetter)
        if (!ready) pos += 1
      }
      cutString = data.substring(start, pos)
      CutResult(data.patch(start,"", cutString.size), cutString, start)
    } else CutResult(data, "", -1)
  }

  def load(fileName: String): String =
    scala.io.Source.fromFile(fileName, "UTF-8").getLines.mkString("\n")

  def save(fileName: String, data: String) = {
    import java.nio.file.{Paths, Files}
    import java.nio.charset.StandardCharsets.UTF_8
    println("Saving file: " + Paths.get(fileName))
    Files.write(Paths.get(fileName), data.getBytes(UTF_8))
  }

  def isExistingFile(fileName: String): Boolean = new java.io.File(fileName).exists

  def run(week: String): Unit = {
    println(s"*** Merging: \n  ${exerFile(week)} \n  ${soluFile(week)}  \n")
    val (tasks, solutions) = (load(exerFile(week)), load(soluFile(week)))

    val taskLines = tasks.split("\n").toVector

    val tasksEdited: Vector[String] = taskLines.map { line =>
      if (line.trim.startsWith("\\Task")) {
        val CutResult(lineNoTask,  _, _)        = cut(line, "\\Task")
        val CutResult(lineNoLabel, labelCmd, _) = cut(lineNoTask, "\\label")
        val CutResult(lineNoEmph,  emphCmd, _)  = cut(lineNoLabel, "\\emph")
        val what = if (emphCmd.nonEmpty) emphCmd.stripPrefix("\\emph{").stripSuffix("}")
                   else "NEEDS A TOPIC DESCRIPTION"
        s"<TASKBEGIN>\n\n\n\n\\WHAT{$what}\n\n\\QUESTBEGIN\n\n\\Task $labelCmd \\what~$lineNoEmph"
      } else line
    }.mkString("\n").split("<TASKBEGIN>").toVector

    val solutionsEdited: Vector[String] = replaceAll(solutions,
      ("\\Task",    "<SOLUTIONBEGIN>\n\\TaskSolved \\what\n"),
      ("\\Subtask", "\\SubtaskSolved "),
    ).split("<SOLUTIONBEGIN>").toVector

    def filterTEXBang(s: String): String = s.split("\n").filterNot(_.startsWith("%!TEX")).mkString("\n")

    def headingTemplate(taskPre: String, solPre: String): String = s"""
      |%!TEX encoding = UTF-8 Unicode
      |%!TEX root = ../exercises.tex
      |
      |\\ifPreSolution
      |
      |$taskPre
      |
      |\\else
      |
      |$solPre
      |
      |\\fi
      |""".stripMargin

    val merged: Vector[String] = {
      val (tPre, ts) = tasksEdited    .splitAt(1)
      val (sPre, ss) = solutionsEdited.splitAt(1)

      val tHead = tPre.headOption.map(filterTEXBang).getOrElse("")
      val sHead = sPre.headOption.map(filterTEXBang).getOrElse("")

      val prefix: String = headingTemplate(tHead, sHead)

      val quests = (ts zip ss.padTo(ts.size, "")) map { case (t, s) =>
        if (t.trim.startsWith("\\WHAT")) {
          var uppgiftComment = ""
          val start = "%\\s*uppg".r.findFirstMatchIn(s.toLowerCase).map(_.start).getOrElse(-1)
          val sCut = if (start > -1) {
            val stop = s.indexWhere(_ == '\n', start)
            uppgiftComment = s.substring(start, if (stop < 0) s.size else stop)
            s.patch(start,"",uppgiftComment.size)
          } else s
          if (uppgiftComment.nonEmpty)
            uppgiftComment = s"\n\n\n%%<AUTOEXTRACTED by mergesolu>%%      $uppgiftComment"
          s"$t\\SOLUTION\n\n$sCut\n\\QUESTEND\n\n$uppgiftComment"
        } else s"$t\n$s\n"
      }
      prefix +: quests
    }


    save(fileName=outFile(week), data=merged.mkString("\n"))

    //println(merged.take(3).mkString("MERGED RESULT beginning:\n", "\n", ""))

  }
}
