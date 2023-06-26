package chords

import scala.util.{Try, Success, Failure}

object io {
  import scala.io.Source
  import java.nio.file.{Paths, Files}
  import java.nio.charset.StandardCharsets.UTF_8  
  def load(fileName: String): Vector[String] = {
    val result = Try {
      val lines = Source.fromFile(fileName).getLines
      lines.toVector
    } 
    result match {
      case Success(_) => println(s"Data loaded from: $fileName")
      case Failure(e) => println(s"Error: $e") 
    }
    result.getOrElse(Vector())
  }
  def save(fileName: String, lines: Vector[String]): Unit = Try {
    val data = lines.mkString("\n")
    Files.write(Paths.get(fileName), data.getBytes(UTF_8))
  } match {
    case Success(_) => println(s"Saved to file: $fileName")
    case Failure(e) => println(s"Error: $e") 
  }
}

object textui {
  import model._
  trait Cmd {
    def variants: Set[String]
    def helpText: String
    def doWith(args: Vector[String]): String
    def ===(s: String): Boolean = variants.contains(s.toLowerCase)
  }

  object Add extends Cmd {
    val variants = Set("add","a","+")
    val helpText = "Adds a chord, e.g.> add Git:D:x 0 0 2 3 2"
    def doWith(args: Vector[String]): String = helpText + " TODO"
  }

  object Lst extends Cmd {
    val variants = Set("list","li","ls", "lst")
    val helpText = "Prints a numbered list of all chords (perhaps only filtered)"
    def doWith(args: Vector[String]): String = {
      database.allChords.foreach(println) // should be filteredChords or something
      "Number of chords listed: " + database.allChords.size
    }
  }

  object Del extends Cmd {
    val variants = Set("delete","del","-")
    val helpText = "Deletes a chord by number> del 42"
    def doWith(args: Vector[String]): String = helpText + " TODO"
  }

  object Filter extends Cmd {
    val variants = Set("filter","fil", "flt")
    val helpText = "Turn filter on/off. Example: filter Git Em"
    def doWith(args: Vector[String]): String = helpText + " TODO"
  }

  object Find extends Cmd {
    val variants = Set("find","fnd", "f")
    val helpText = "Finds a chords (perhaps only among filtered)"
    def doWith(args: Vector[String]): String = helpText + " TODO"
  }

  object Load extends Cmd {
    val variants = Set("load","lo", "ld")
    val helpText = "Loads chords from file"

    def doWith(args: Vector[String]): String = {
      args match {
        case Vector(fileName) => 
          val lines = io.load(fileName)
          val validChords = lines.flatMap(Chord.fromString)
          validChords.foreach(database.add)      
          val invalidChords = 
            lines.flatMap(s => if (Chord.fromString(s) == None) Some(s) else None)
          invalidChords.mkString("Bad chords not added:", ", ", "!") 
        case Vector() => "Error: Missing file name"
        case _ => "Error: Give exacly one file name"
      }
    } 

  }

  object Save extends Cmd {
    val variants = Set("save","s")
    val helpText = "Saves all chords to file"
    def doWith(args: Vector[String]): String = helpText + " TODO"
  }

  object Sort extends Cmd {
    val variants = Set("sort","srt")
    val helpText = "sorts all chords in instrument and chord name order"
    def doWith(args: Vector[String]): String = helpText + " TODO"
  }

  object Help extends Cmd {
    val variants = Set("?","help", "h")
    val helpText = "Prints help text for all or one commmand"
    def doWith(args: Vector[String]): String = helpText + " TODO"
  }

  object Quit extends Cmd {
    val variants = Set("quit","q")
    val helpText = "Quits this app after a Y/N promtp"
    def doWith(args: Vector[String]): String = helpText + " TODO"
  }

  object doCommand {
    def unknownCmdHelp(cmd: String) = s"Unkown command: $cmd\nType ? for help."
    val cmds = Vector[Cmd](Add, Lst, Del, Filter, Find, Load, Save, Sort, Help, Quit)
    def apply(cmd: String, args: Vector[String]): String = cmds.find(_ === cmd) match {
      case Some(command) => command doWith args
      case None => unknownCmdHelp(cmd)
    }
  }
}

object model {
  type Grip = Vector[Int]
  type Tuning = Vector[String]

  def stringToGripVec(s: String): Vector[Int] = Vector(1,2,3,4) // ??? DUMMY  

  trait Chord {
    def name: String
    def grip: Grip
    def tuning: Tuning
  }

  object Chord {
    def fromString(chordText: String): Option[Chord] = {
      def isGit(s: String) = s.toLowerCase.startsWith("git")
      def isUku(s: String) = s.toLowerCase.startsWith("uku")
      def instToChord(instr: String, name: String, grip: String): Option[Chord] = instr match {
        case s if isGit(s) => Some(Guitar(name, stringToGripVec(grip)))
        case s if isUku(s) => Some(Ukulele(name, stringToGripVec(grip)))
        case _ => None  // borde ta hand om felfallen
      }
      val xs: Vector[String] = chordText.split(':').toVector
      xs match {
        case Vector(instr, name, gripString) => instToChord(instr,name,gripString) 
        case _ => None
      }
    }
  }

  
  case class Guitar(name: String, grip: Grip) extends Chord {
    // siffrorna anger oktaven för korrekt midispel, kolla så det blev rätt
    override val tuning = Vector("E2","A2","D3","G2","B2","E3")
  }

  case class Ukulele(name: String, grip: Grip) extends Chord {
    override val tuning = Vector("A4","D3","F#3","B3")
  }
  
  object PlayDecorator {
    implicit class ChordMidiPlayer(c: Chord) {
      def play: Unit = ???  // some midi magic TODO
    }
  }
  
  object DrawDecorator {
    type SimpleWindow = Nothing // To be removed and connected to cslib
    implicit class ChordSimpleWindowDrawer(c: Chord) {
      /** opens a SimpleWindow and draws chord, notes are played when clicked */
      def draw(w: SimpleWindow): Unit = ??? 
    }
  }
}

object database {
  import model._
  type Chords = Vector[Chord]
  private var db: Chords = Vector()  // populated when chords are loaded from file
  private var filter: String = ""  // empty string means no filter  
  def find(s: String) = ??? // free form finding (perhaps among filtered)
  def add(c: Chord): Unit = { db = db :+ c}   
  def delete(i: Int) = ??? // remove the chord with index i (implement using chords.patch)
  def updateFilter(f: String) = ???
  def filteredChords: Chords = ??? // only those chords that has the words in filter
  def allChords: Chords = db
  def sort: Unit = ??? // sorts the chords in instrument and chord name order
}


object terminal {
  import java.util.Scanner
  private var scan = new Scanner(System.in)
  var prompt: String = "> "
  def readLine: String = Try { scan.nextLine } getOrElse {
    // Is there a better way to handle Ctrl+D or Ctrl+Z more gracefully ????
    scan = new Scanner(System.in)
    "" 
  }
  def cmdLine: Vector[String] = {
    print(prompt)
    readLine.split(' ').toVector
  } 
}

object Main {
  def main(args: Array[String]): Unit = {println(greeting); commandLoop}

  val greeting = "*** Welcome to this awesome chords.Main app!\n*** Type ? for help.\n"

  def commandLoop: Unit = {
    import textui.{doCommand, Quit}
    val (response: String, isQuit: Boolean) = terminal.cmdLine match {
      case Vector() => ("", false)
      case cmd +: cmdArgs => (doCommand(cmd, cmdArgs), Quit === cmd)
    }
    println(response)
    if (isQuit) () else commandLoop
  }
}
