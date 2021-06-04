// En Java-orienterad Scala-specifikation av extentalösningen 2016-08-24

import scala.collection.mutable.ArrayBuffer // import krävs ej på tentan

class WorkPeriod(private val task: String, private val hour: Int, length: Int):
  def collidesWith(wp: WorkPeriod): Boolean = ???
    
  def compareTo(wp: WorkPeriod): Int =  ???
  
  override def toString = ???

class WorkPeriodList:
  def add(task: String, hour: Int, length: Int): Unit =  ???
  
  def toSortedArray: Array[WorkPeriod] = ???

class Worker(val name: String, private val times: Array[WorkPeriod]):
  def schedule(nbr: Int): Unit = ???  
  
  def isScheduled(nbr: Int): Boolean = ???
  
  def canWork(nbr: Int): Boolean = ???

class TimePlanner(private val times: Array[WorkPeriod]): 
  def addWorker(name: String): Boolean = ???
  
  def scheduleWorker(name: String, nbr: Int): Unit = ???
  
  def availableTimes: ArrayBuffer[WorkPeriod] = ???

object Test { //testprogram, ingår ej i tentan men kan hjälpa att fundera på
  def main(args: Array[String]) =
    val wpl = new WorkPeriodList
    wpl.add("Städa toaletter", 17, 2)  // 0
    wpl.add("Vakta entren", 17, 3)     // 1
    wpl.add("Plocka skräp", 18, 1)     // 2
    wpl.add("Plocka skräp", 19, 1)     // 3
    wpl.add("Städa toaletter", 19, 2)  // 4
    wpl.add("Vakta entren", 20, 2)     // 5
    wpl.add("Plocka skräp", 22, 1)     // 6
    val times = wpl.toSortedArray

    println(times.mkString("ARBETSPASS:\n  ","\n ","\n"))
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