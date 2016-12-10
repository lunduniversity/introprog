// En förenklad Scala-variant av extentaproblemet 2016-08-24 utan WorkPeriodList

case class WorkPeriod(task: String, hour: Int, length: Int){
  val (start, finish) = (hour, hour + length)
  
  def collidesWith(wp: WorkPeriod): Boolean = wp.start < finish && wp.finish > start 
  
  override def toString = s"$task $start-$finish"
}

class TimePlanner(val times: Seq[WorkPeriod]){ 

  private val worker = scala.collection.mutable.Map.empty[String, Worker]

  private class Worker(val name: String) { 
    private val scheduled = scala.collection.mutable.Set.empty[Int]
    
    def schedule(nbr: Int): Unit = scheduled += nbr   
    
    def isScheduled(nbr: Int): Boolean = scheduled.contains(nbr)
    
    def canWork(nbr: Int): Boolean = {
      def clash(i: Int) = isScheduled(i) && times(i).collidesWith(times(nbr)) && i != nbr
      !times.indices.exists(clash) 
    }
  }

  def addWorker(name: String): Unit = worker += (name -> new Worker(name))
  
  def scheduleWorker(name: String, nbr: Int): Unit = 
    worker.get(name).foreach{ w => 
      val nbrIsFree = worker.values.forall(w => !w.isScheduled(nbr))
      if (nbrIsFree && w.canWork(nbr)) w.schedule(nbr)
    }    
  
  def availableTimes: Seq[WorkPeriod] = 
    for (i <- times.indices if !worker.values.exists(_ isScheduled i)) 
    yield times(i)
}

object Test { 
  def main(args: Array[String]) = {
    val times = Vector[WorkPeriod](
      WorkPeriod("Städa toaletter", 17, 2), //0
      WorkPeriod("Vakta entren", 17, 3),    //1
      WorkPeriod("Plocka skräp", 18, 1),    //2
      WorkPeriod("Plocka skräp", 19, 1),    //3
      WorkPeriod("Städa toaletter", 19, 2), //4
      WorkPeriod("Vakta entren", 20, 2),    //5
      WorkPeriod("Plocka skräp", 22, 1)     //6
    ).sortBy(wp => (wp.start, wp.task)) 
    
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
