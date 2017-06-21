package test
import chord.Notes

object TestNotes {

  def main(args: Array[String]) {
    
    /* 
     * Tests "fromNbrToNote" in Notes.scala
     */
    val cpyNotes = for(i <- 0 until Notes.notes.length) yield {Notes.fromNbrToNote(i)}
    if(cpyNotes.mkString == Notes.notes.mkString) {
      println("-fromNbrToName: ok!")
    } else {
      println("-fromNbrToName: error!  -Returns wrong string")
    }
    
    /* 
     * Tests "toNumber" in Notes.scala
     */
    val toNumberList = Notes.toNumber.toArray
    if(toNumberList(6).toString() == "(G,7)" && toNumberList(11).toString() == "(D,2)" && !Notes.toNumber.isEmpty) {
      println("-toNumber: ok!")
    } else {
      println("-toNumber: error!  -Mapping not correct")
    }
    
    /* 
     * Tests "unapply" in Notes.scala
     */
    
    // String longer than 3
    if(Notes.unapply("Wrong String").getOrElse(0) == 0) {
      println("-String longer than 3 in unapply: ok!")
    } else {
      println("-String longer than 3 in unapply: error!")
    }
    
    // String shorter than 2
    if(Notes.unapply("").getOrElse(0) == 0 && Notes.unapply("1").getOrElse(0) == 0) {
      println("-String shorter than 2 in unapply: ok!")
    } else {
      println("-String shorter than 2 in unapply: error!")
    }
    
    // Wrong input, e.g "EE", "2E", "22", also checks if exceptions are handled
    try {
      if(Notes.unapply("EE").getOrElse(0) == 0){
        println("-Input *EE* in unapply: ok!")
      } else {
        println("-Input *EE* in unapply: error!")
      }
    } catch {
      case e: NumberFormatException => println("-Input *EE* in unapply: error!   -NumberFormatException not handled")
      case e: NoSuchElementException => println("-Input *EE* in unapply: error!   -NoSuchElementException not handled")
    }
    
    try {
      if(Notes.unapply("2E").getOrElse(0) == 0) {
        println("-Input *2E* in unapply: ok!")
      } else {
        println("-Input *2E* in unapply: error!")
      }
    } catch {
      case e: NumberFormatException => println("-Input *2E* in unapply: error!   -NumberFormatException not handled")
      case e: NoSuchElementException => println("-Input *2E* in unapply: error!   -NoSuchElementException not handled")
    }
    
    try {
      if(Notes.unapply("22").getOrElse(0) == 0){
        println("-Input *22* in unapply: ok!")
      } else {
        println("-Input *22* in unapply: error!")
      }
    } catch {
      case e: NumberFormatException => println("-Input *22* in unapply: error!   -NumberFormatException not handled")
      case e: NoSuchElementException => println("-Input *22* in unapply: error!   -NoSuchElementException not handled")
    }
    
    // Wrong input, e.g "E#E", "2#E", "2#2", "222"
    try {
      if(Notes.unapply("E#E").getOrElse(0) == 0){
        println("-Input *E#E* in unapply: ok!")
      } else {
        println("-Input *E#E* in unapply: error!")
      }
    } catch {
      case e: NumberFormatException => println("-Input *E#E* in unapply: error!   -NumberFormatException not handled")
      case e: NoSuchElementException => println("-Input *E#E* in unapply: error!   -NoSuchElementException not handled")
    }
    
    try {
      if(Notes.unapply("2#E").getOrElse(0) == 0) {
        println("-Input *2#E* in unapply: ok!")
      } else {
        println("-Input *2#E* in unapply: error!")
      }
    } catch {
      case e: NumberFormatException => println("-Input *2#E* in unapply: error!   -NumberFormatException not handled")
      case e: NoSuchElementException => println("-Input *2#E* in unapply: error!   -NoSuchElementException not handled")
    }
    
    try {
      if(Notes.unapply("2#2").getOrElse(0) == 0){
        println("-Input *2#2* in unapply: ok!")
      } else {
        println("-Input *2#2* in unapply: error!")
      }
    } catch {
      case e: NumberFormatException => println("-Input *2#2* in unapply: error!   -NumberFormatException not handled")
      case e: NoSuchElementException => println("-Input *2#2* in unapply: error!   -NoSuchElementException not handled")
    }
    
    try {
      if(Notes.unapply("222").getOrElse(0) == 0){
        println("-Input *222* in unapply: ok!")
      } else {
        println("-Input *222* in unapply: error!")
      }
    } catch {
      case e: NumberFormatException => println("-Input *222* in unapply: error!   -NumberFormatException not handled")
      case e: NoSuchElementException => println("-Input *222* in unapply: error!   -NoSuchElementException not handled")
    }
    
    // Checks if unapply returns the right int for strings with length 2
    if(Notes.unapply("G7").getOrElse(0) == 79 && Notes.unapply("G1").getOrElse(0) == 7) {
      println("-Checks return value with string input length 2 in unapply: ok!")
    } else {
      println("-Checks return value with string input length 2 in unapply: error!")
    }
    
    // Checks if unapply returns the right int for strings with length 3
    if(Notes.unapply("G#7").getOrElse(0) == 80 && Notes.unapply("C#1").getOrElse(0) == 1 && Notes.unapply("E#7").getOrElse(0) == 0) {
      println("-Checks return value with string input length 3 in unapply: ok!")
    } else {
      println("-Checks return value with string input length 3 in unapply: error!")
    }
  }
  
}