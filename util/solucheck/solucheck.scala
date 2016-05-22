/* solucheck.scala

   This utility checks that latex commands \Task and \Subtask matches in two files.

   Run with sbt in terminal (note the quotes):
   $ sbt "run w01"  

   Or compile and run manually in terminal:
   $ scalac solucheck.scala
   $ scala solucheck w01   

*/

object solucheck {

  def load(fileName: String): String =
    scala.io.Source.fromFile(fileName, "UTF-8").getLines.mkString("\n")

  def errExit(msg: String): Unit = { Console.err.println(msg); System.exit(1) }

  def welcome = println("*** solucheck utility ***\n")  
  
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
    tasks.map(s => (s.take(30).padTo(30, ' '), countSubtasks(s)))
    
  def reportDiffSubtasks(week: String, exec: TaskData, solu: TaskData): Unit = {
    val errDiff  = "*** DIFF #SUBTASKS ***"
    val errUnsol = "*** UNSOLVED TASKS ***"
    val errExtra = "*** EXTRA SOLUTION ***"
    exec.zipWithIndex.collect{ case ((text,n),i) =>
      val taskId = s"Task ${i.toString.padTo(2,' ')}: $text"
      solu.lift(i) match {
        case Some((_, nsolu)) => 
          if (n - nsolu != 0) println(s"$errDiff $taskId: DIFF ${n - nsolu}")
        case None => println(s"$errUnsol $taskId")   
      }
    }
    for (i <- exec.size until solu.size){
      println(s"$errExtra Solution $i: " + solu(i)._1)
    }
  }

  def checkWeek(week: String): Unit = {
    val dir="../../compendium/modules"
    val execFile = s"$dir/$week-exercise.tex" 
    val soluFile = s"$dir/$week-solutions.tex"
    def prepare(file: String) = count(split(load(file)))
    val (exec, solu) = (prepare(execFile), prepare(soluFile))
    println(s"Exercise: $execFile: #Tasks:  ${exec.size}")
    println(s"Solution: $soluFile: #Tasks: ${solu.size}")
    val diffTasks = exec.size - solu.size
    if (diffTasks != 0) println(s"*** ERROR *** Diff #Tasks: $diffTasks\n")
    reportDiffSubtasks(week, exec, solu)
  }

  def main(args: Array[String]): Unit = 
    if (args.size == 1) checkWeek(args(0))
    else errExit("Usage: scala solucheck w01")   
}
