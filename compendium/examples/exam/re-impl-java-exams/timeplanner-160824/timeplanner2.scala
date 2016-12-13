// En lite mer Scala-idiomatisk version av extentaproblemet 2016-08-24

import scala.collection.mutable.ArrayBuffer // import krävs ej på tentan
 
case class WorkPeriod(task: String, hour: Int, length: Int){
  val (start, finish) = (hour, hour + length)
  
  def collidesWith(wp: WorkPeriod): Boolean = wp.start < finish && wp.finish > start 
    
  def compareTo(wp: WorkPeriod): Int = {
    val timeResult = start - wp.start
    if (timeResult == 0) task.compareTo(wp.task) else timeResult
  }
  override def toString = s"$task $start-$finish"
}

class WorkPeriodList {
  private val times: ArrayBuffer[WorkPeriod] = ArrayBuffer.empty
  
  def add(task: String, hour: Int, length: Int): Unit = {
    val wp = WorkPeriod(task, hour, length)
    val i = times.indexWhere(_.compareTo(wp) > 0) 
    if (i >= 0) times.insert(i, wp)
    else times.append(wp)
  }
  
  def toSortedVector: Vector[WorkPeriod] = times.toVector 
}

class Worker(val name: String, val times: Vector[WorkPeriod]) {
  private val scheduled = Array.fill(times.size)(false)
  
  def schedule(nbr: Int): Unit = scheduled.update(nbr, true)  
  
  def isScheduled(nbr: Int): Boolean = scheduled(nbr)
  
  def canWork(nbr: Int): Boolean = {
    var (i, foundCollision) = (0, false)
    while (!foundCollision && i < times.size) {
      if (scheduled(i) && times(i).collidesWith(times(nbr)) && i != nbr) 
        foundCollision = true
      else i += 1
    }
    !foundCollision
  }    
}

class TimePlanner(private val times: Vector[WorkPeriod]){ 
  private val persons: ArrayBuffer[Worker] = ArrayBuffer.empty
  
  def addWorker(name: String): Boolean = {
    val workerOpt = persons.find(w => w.name == name)
    if (workerOpt.isEmpty) persons.append(new Worker(name, times))
    workerOpt.isDefined 
  }
  
  def scheduleWorker(name: String, nbr: Int): Unit = {
    val workerOpt = persons.find(w => w.name == name)
    workerOpt.foreach{ worker => 
      val nbrIsFree = persons.forall(w => !w.isScheduled(nbr))
      if (nbrIsFree && worker.canWork(nbr)) worker.schedule(nbr)
    }    
  }
  
  def availableTimes: Vector[WorkPeriod] = {
    val result: ArrayBuffer[WorkPeriod] = ArrayBuffer.empty
    for (i <- times.indices) {
      if (!persons.exists(_ isScheduled i))
        result.append(times(i))
    }
    result.toVector
  }  
}

object Test { 
  def main(args: Array[String]) = {
    val wpl = new WorkPeriodList
    wpl.add("Städa toaletter", 17, 2)  // 0
    wpl.add("Vakta entren", 17, 3)     // 1
    wpl.add("Plocka skräp", 18, 1)     // 2
    wpl.add("Plocka skräp", 19, 1)     // 3
    wpl.add("Städa toaletter", 19, 2)  // 4
    wpl.add("Vakta entren", 20, 2)     // 5
    wpl.add("Plocka skräp", 22, 1)     // 6
    val times = wpl.toSortedVector

    println(times.mkString("ARBETSPASS:\n ","\n  ","\n"))
    val planner = new TimePlanner(times)

    def showAvailable(head: String) = 
      println(planner.availableTimes.mkString(s"$head:\n ","\n ", "\n"))

    showAvailable("LEDIGA före allokering:")

    val allocation = Map(
      "Ingert" -> Vector(0,3), 
      "Jessica" -> Vector(1,6), 
      "Joey" -> Vector(2), 
      "Kim" -> Vector(5)
    )
    allocation.foreach { tuple2 =>
      val (name, nbrs) = tuple2
      planner.addWorker(name)
      nbrs.foreach(nbr => planner.scheduleWorker(name, nbr))
    }
    showAvailable("LEDIGA efter allokering:")
  }
}
