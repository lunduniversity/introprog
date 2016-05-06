// compile with scalac chords.scala
// run with scala chords.Main

package chords

object textui {
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
    def doWith(args: Vector[String]): String = helpText + " TODO"
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
    def doWith(args: Vector[String]): String = helpText + " TODO"
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

  trait Chord {
    def name: String
    def grip: Grip
    def tuning: Tuning
  }
  
  case class Guitar(name: String, grip: Grip) extends Chord {
    // siffrorna anger oktaven för korrekt midispel, kolla så det blev rätt
    override val tuning = Vector("E2","A2","D3","G2","B2","E3")
  }
  object Guitar {
    def unapply(s: String): Option[Guitar] = ???
  }  

  case class Ukulele(name: String, grip: Grip) extends Chord {
    override val tuning = Vector("A4","D3","F#3","B3")
  }
  object Ukulele {
    def unapply(s: String): Option[Ukulele] = ???
  }  
  
  implicit class ChordMidiPlayer(c: Chord) {
    def play: Unit = ???  // some midi magic TODO
  }
  
  implicit class ChordSimpleWindowDrawer(c: Chord) {
    /** opens a SimpleWindow and draws chord, notes are played when clicked */
    def draw: Unit = ??? 
  }
}

object database {
  import model._
  type Chords = Vector[Chord]
  private var db: Chords = Vector()  // populated when chords are loaded from file
  private var filter: String = ""  // empty string means no filter  
  def find(s: String) = ??? // free form finding (perhaps among filtered)
  def add(c: Chord) = ???  // the chord is created in the gui using the unapply methods 
  def delete(i: Int) = ??? // remove the chord with index i (implement using chords.patch)
  def updateFilter(f: String) = ???
  def filteredChords: Chords = ??? // only those chords that has the words in filter
  def allChords: Chords = ???
  def sort: Unit = ??? // sorts the chords in instrument and chord name order
}


object terminal {
  private val scan = new java.util.Scanner(System.in)
  var prompt: String = "> "
  def readLine: String = scan.nextLine
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