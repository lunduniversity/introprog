package music

object Synth {
  import javax.sound.midi._ //
  import scala.collection.JavaConverters._

  val underlying: Synthesizer = {
    println("Initializing javax.sound.MidiSystem ...")
    println(MidiSystem.getMidiDeviceInfo().mkString(" "))
    val synth = MidiSystem.getSynthesizer
    synth.open
    assert(synth.loadAllInstruments(synth.getDefaultSoundbank),
           "Midi loading instruments failed")
    synth
  }
  resetInstruments() // assign some different instruments to channels

  def midiChannel(channel: Int): MidiChannel = underlying.getChannels.apply(channel)
  def channels: Range = underlying.getChannels.indices

  def instruments: Seq[Instrument] = underlying.getLoadedInstruments.toSeq
  def instrIndex: Map[String, Int] = instruments.map(_.getName).zipWithIndex.toMap

  def findInstrumentIndicesByName(containing: String*): Vector[Int] = {
    val sortedInstruments = instrIndex.keys.toSeq.sorted
    val found = sortedInstruments.filter(key =>
      containing.forall(s => key.toLowerCase.contains(s.toLowerCase))
    )
    found.map(instrIndex).toVector
  }

  def changeInstrument(index: Int, channel: Int = 0): Unit = {
    val patch = instruments(index).getPatch
    midiChannel(channel).programChange(patch.getBank, patch.getProgram)
  }

  def changeInstrumentByName(containing: String, channel: Int = 0): Unit = {
    findInstrumentIndicesByName(containing).headOption match {
      case Some(instrument) => changeInstrument(instrument, channel)
      case None => println("Instrument containing \""+ containing + "\" not found")
    }
  }

  lazy val defaultInstruments = Vector("grand","guit","bass","trump","flute")

  def resetInstruments(): Unit = defaultInstruments.zipWithIndex.foreach {
    case (nameContains, channel) => changeInstrumentByName(nameContains, channel)
  }

  def noteOn(noteNumber: Int = 60, velocity: Int = 60, channel: Int = 0): Unit =
    midiChannel(channel).noteOn(noteNumber, velocity)

  def noteOff(noteNumber: Int = 60, channel: Int = 0): Unit =
    midiChannel(channel).noteOff(noteNumber)

  def allNotesOff(channel: Int): Unit = midiChannel(channel).allNotesOff()
  def allSoundOff(channel: Int): Unit = midiChannel(channel).allSoundOff()
  def silence(): Unit = channels.foreach(allSoundOff)

  def delay(millis: Long): Unit = Thread.sleep(millis)

  def playBlocking(noteNumbers: Seq[Int] = Vector(60),
                   velocity: Int         = 60,
                   duration: Long        = 300,
                   spread:   Long        = 50,
                   after:    Long        = 0,
                   channel:  Int         = 0
  ): Unit = {
    delay(after)
    noteNumbers.foreach{ nbr =>
      noteOn(nbr, velocity, channel)
      delay(spread)
    }
    delay(duration)
    noteNumbers.foreach(noteOff(_, channel))
  }

  def playConcurrently(noteNumbers: Seq[Int] = Vector(60),
                       velocity: Int         = 60,
                       duration: Long        = 300,
                       spread:   Long        = 50,
                       after:    Long        = 0,
                       channel:  Int         = 0
  ): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    scala.concurrent.Future {
      playBlocking(noteNumbers, velocity, duration, spread, after, channel)
    }
  }
}
