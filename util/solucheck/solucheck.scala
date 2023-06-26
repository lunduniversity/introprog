/* solucheck.scala

   This utility checks that latex commands \Task and \Subtask matches in two files.

   Run with sbt in terminal (note the quotes):
   $ sbt "run w01"  
   $ sbt "run --all"
   $ sbt "run --init w07"

*/

object solucheck {
  def main(args: Array[String]): Unit = 
    if (args.size == 1) doOneArg(args(0).toLowerCase)
    else if (args.size == 2 && args(0) == "--init") doInit(args(1).toLowerCase)
    else errExit(args)   

  def errExit(args: Array[String], msg: String = ""): Unit = { 
    val argString = args.mkString(" ")
    val errorMsg =  
      if (msg != "") s"*** ERROR *** args: $argString\n\n$msg"
      else s"""
        |*** ERROR *** Unkown args: $argString
        | 
        |Usage:
        |scala solucheck w01          checks solutions of week 01
        |scala solucheck --all        summary check of all weeks
        |scala solucheck --init w01   initializes solution of week 01
        """.stripMargin
    Console.err.println(errorMsg)  
    System.exit(1) 
  }

  def doOneArg(arg: String): Unit = 
    if (arg.startsWith("w")) checkWeek(arg)
    else if (arg == "--all") checkAll
    else errExit(Array(arg))

  def doInit(week: String): Unit = {
    val outFile = soluFile(week)
    if (isExistingFile(outFile)) 
      errExit(Array(week), 
        "File already exists: " + outFile + 
        "\nBackup and/or delete solution file and rerun if this is what you want...")
    initSolution(week)
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
 
  def workDir="../../compendium/modules"
  def execFile(week: String) = s"$workDir/$week-exercise.tex" 
  def soluFile(week: String) = s"$workDir/$week-solutions.tex"

  def skipToFirstTask(data: String): String = {
    val start = data.indexOf("\\Task")
    data.drop(start)
  } 
  
  def split(data: String): Vector[String] = {
    val dataNoNL = data.filterNot(_ == '\n')
    val stripped = skipToFirstTask(dataNoNL)
    val splitted = stripped.split("\\\\Task").toVector
    splitted.drop(1).map(_.trim).filterNot(_.startsWith("Section{"))
  }
  
  def countSubtasks(s: String): Int = "\\\\Subtask".r.findAllMatchIn(s).length
  
  type TaskData = Vector[(String, Int)] //first word in task and num of subtasks
  
  def count(tasks: Vector[String]): TaskData =
    tasks.map(s => (s.take(31).padTo(31, ' '), countSubtasks(s)))
  
  def prepare(file: String) = count(split(load(file)))
    
  def reportDiffSubtasks(week: String, exec: TaskData, solu: TaskData): (String, Int) = {
    val errDiff  = "*** DIFF #SUBTASKS ***"
    val errUnsol = "*** UNSOLVED TASK  ***"
    val errExtra = "*** EXTRA SOLUTION ***"
    var report = ""
    var totalSubtaskDiffs = 0
    exec.zipWithIndex.collect{ case ((text,n),i) =>
      val taskId = s"Task ${(i+1).toString.padTo(2,' ')}: $text"
      solu.lift(i) match {
        case Some((_, nsolu)) => 
          if (n - nsolu != 0) {
            report += s"$errDiff $taskId: DIFF ${n - nsolu}\n"
            totalSubtaskDiffs += 1
          }
        case None => 
          report += s"$errUnsol $taskId\n"   
          totalSubtaskDiffs += 1
      }
    }
    for (i <- exec.size until solu.size){
      report += s"$errExtra Solution $i: ${solu(i)._1}\n"
      totalSubtaskDiffs += 1
    }
    (report, totalSubtaskDiffs)
  }

  def checkWeek(week: String, isSummaryOnly: Boolean = false): Unit = {
    val (exec, solu) = (prepare(execFile(week)), prepare(soluFile(week)))
    val diffTasks = exec.size - solu.size
    val (report, diffSubtasks) = reportDiffSubtasks(week, exec, solu)
    if (isSummaryOnly) {
      if (diffTasks == 0 && diffSubtasks == 0) println(s"\n$week NO DIFFS FOUND!")
      else println(s"\n*** $week TOTAL DIFF #TASKS:    $diffTasks")   
      if (diffSubtasks > 0) { 
        println(s"*** $week TOTAL DIFF #SUBTASKS: $diffSubtasks")
        println(s"*** rerun solucheck with argument '$week' for details")
      }
    } else {
      println(s"Exercise: ${execFile(week)}: #Tasks:  ${exec.size}")
      println(s"Solution: ${soluFile(week)}: #Tasks: ${solu.size}")
      if (diffTasks != 0) println(s"*** ERROR *** Diff #Tasks: $diffTasks\n")
      println(report)
    }
  }
  
  def checkAll: Unit = {
    val weeks = (1 to 14).map(i => if (i < 10) "w0"+i else "w"+i)
    weeks.foreach(checkWeek(_, isSummaryOnly=true))
  }
  
  def initSolution(week: String): Unit = {
    val weekStrings = Vector("ONE","TWO","THREE","FOUR","FIVE","SIX","SEVEN", 
                        "EIGHT","NINE","TEN","ELEVEN","TWELVE","THIRTEEN","FOURTEEN")
    val weekStr = weekStrings(week.drop(1).toInt - 1)
    val solInit = s"""%!TEX encoding = UTF-8 Unicode
                    |
                    |%!TEX root = ../compendium.tex
                    |
                    |\\ExerciseSolution{\\ExeWeek$weekStr}
                   """.stripMargin
    val exec = prepare(execFile(week))
    val nExec = exec.size
    var nSolu = 0
    val output = solInit + exec.zipWithIndex.collect{ case ((text,n), i) =>
      val taskText = s"\n\n\\Task     %%%TODO number  ${i+1} %%%starts with: $text%%%"
      val subTaskText =  s"\\Subtask  %%%TODO in task ${i+1} %%%"
      nSolu += n
      val allSubtasks = if (n == 0) "\n" else 
        (1 to n).map(_ => subTaskText).mkString("\n\n", "\n\n", "\n")
      taskText + allSubtasks
    }.mkString
    println(s"solucheck --init $week")
    println(s"\n*** Generating $nExec dummy Tasks with in total $nSolu dummy Subtasks")
    save(soluFile(week), output)
  }
 
}
