package music

object Synth:
  import javax.sound.midi.*
  import GMInstrument.*

  val underlying: Synthesizer =
    println("Initializing javax.sound.MidiSystem ...")
    println(MidiSystem.getMidiDeviceInfo().mkString(" "))
    val synth = MidiSystem.getSynthesizer
    synth.open
    assert(
      synth.loadAllInstruments(synth.getDefaultSoundbank),
      "Loading MIDI instruments failed"
    )
    synth
  resetInstruments() // assign some different instruments to channels

  def midiChannel(channel: Int): MidiChannel =
    underlying.getChannels.apply(channel)
  def channels: Range = underlying.getChannels.indices

  def instruments: Seq[Instrument] = underlying.getLoadedInstruments.toSeq

  def changeInstrument(program: Int, channel: Int = 0): Unit =
    val patch =
      instruments.find(_.getPatch.getProgram == program).map(_.getPatch)
    patch match
      case Some(p) =>
        midiChannel(channel).programChange(p.getBank, p.getProgram)
      case None => println(s"Instrument with program number $program not found")

  lazy val defaultInstruments =
    Vector(
      AcousticGrandPiano,
      AcousticGuitarNylon,
      AcousticBass,
      Trumpet,
      Flute
    )

  def resetInstruments(): Unit = defaultInstruments.zipWithIndex.foreach {
    case (instrument, channel) =>
      changeInstrument(instrument.programIndex, channel)
  }

  def noteOn(noteNumber: Int = 60, velocity: Int = 60, channel: Int = 0): Unit =
    midiChannel(channel).noteOn(noteNumber, velocity)

  def noteOff(noteNumber: Int = 60, channel: Int = 0): Unit =
    midiChannel(channel).noteOff(noteNumber)

  def allNotesOff(channel: Int): Unit = midiChannel(channel).allNotesOff()
  def allSoundOff(channel: Int): Unit = midiChannel(channel).allSoundOff()
  def silence(): Unit = channels.foreach(allSoundOff)

  def delay(millis: Long): Unit = Thread.sleep(millis)

  def playBlocking(
      noteNumbers: Seq[Int] = Vector(60),
      velocity: Int = 60,
      duration: Long = 300,
      spread: Long = 50,
      after: Long = 0,
      channel: Int = 0
  ): Unit =
    delay(after)
    noteNumbers.foreach { nbr =>
      noteOn(nbr, velocity, channel)
      delay(spread)
    }
    delay(duration)
    noteNumbers.foreach(noteOff(_, channel))

  def playConcurrently(
      noteNumbers: Seq[Int] = Vector(60),
      velocity: Int = 60,
      duration: Long = 300,
      spread: Long = 50,
      after: Long = 0,
      channel: Int = 0
  ): Unit =
    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.Future

    val _ = Future:
      playBlocking(noteNumbers, velocity, duration, spread, after, channel)

  // General MIDI defines instruments using numbers.
  // They're often indexed from 1, but javax.sound.midi indexes them from 0.
  enum GMInstrument(val programIndex: Int):
    case AcousticGrandPiano    extends GMInstrument(0)
    case BrightAcousticPiano   extends GMInstrument(1)
    case ElectricGrandPiano    extends GMInstrument(2)
    case HonkyTonkPiano        extends GMInstrument(3)
    case ElectricPiano1        extends GMInstrument(4)
    case ElectricPiano2        extends GMInstrument(5)
    case Harpsichord           extends GMInstrument(6)
    case Clavi                 extends GMInstrument(7)
    case Celesta               extends GMInstrument(8)
    case Glockenspiel          extends GMInstrument(9)
    case MusicBox              extends GMInstrument(10)
    case Vibraphone            extends GMInstrument(11)
    case Marimba               extends GMInstrument(12)
    case Xylophone             extends GMInstrument(13)
    case TubularBells          extends GMInstrument(14)
    case Dulcimer              extends GMInstrument(15)
    case DrawbarOrgan          extends GMInstrument(16)
    case PercussiveOrgan       extends GMInstrument(17)
    case RockOrgan             extends GMInstrument(18)
    case ChurchOrgan           extends GMInstrument(19)
    case ReedOrgan             extends GMInstrument(20)
    case Accordion             extends GMInstrument(21)
    case Harmonica             extends GMInstrument(22)
    case TangoAccordion        extends GMInstrument(23)
    case AcousticGuitarNylon   extends GMInstrument(24)
    case AcousticGuitarSteel   extends GMInstrument(25)
    case ElectricGuitarJazz    extends GMInstrument(26)
    case ElectricGuitarClean   extends GMInstrument(27)
    case ElectricGuitarMuted   extends GMInstrument(28)
    case OverdrivenGuitar      extends GMInstrument(29)
    case DistortionGuitar      extends GMInstrument(30)
    case GuitarHarmonics       extends GMInstrument(31)
    case AcousticBass          extends GMInstrument(32)
    case ElectricBassFinger    extends GMInstrument(33)
    case ElectricBassPick      extends GMInstrument(34)
    case FretlessBass          extends GMInstrument(35)
    case SlapBass1             extends GMInstrument(36)
    case SlapBass2             extends GMInstrument(37)
    case SynthBass1            extends GMInstrument(38)
    case SynthBass2            extends GMInstrument(39)
    case Violin                extends GMInstrument(40)
    case Viola                 extends GMInstrument(41)
    case Cello                 extends GMInstrument(42)
    case Contrabass            extends GMInstrument(43)
    case TremoloStrings        extends GMInstrument(44)
    case PizzicatoStrings      extends GMInstrument(45)
    case OrchestralHarp        extends GMInstrument(46)
    case Timpani               extends GMInstrument(47)
    case StringEnsemble1       extends GMInstrument(48)
    case StringEnsemble2       extends GMInstrument(49)
    case SynthStrings1         extends GMInstrument(50)
    case SynthStrings2         extends GMInstrument(51)
    case ChoirAahs             extends GMInstrument(52)
    case VoiceOohs             extends GMInstrument(53)
    case SynthVoice            extends GMInstrument(54)
    case OrchestraHit          extends GMInstrument(55)
    case Trumpet               extends GMInstrument(56)
    case Trombone              extends GMInstrument(57)
    case Tuba                  extends GMInstrument(58)
    case MutedTrumpet          extends GMInstrument(59)
    case FrenchHorn            extends GMInstrument(60)
    case BrassSection          extends GMInstrument(61)
    case SynthBrass1           extends GMInstrument(62)
    case SynthBrass2           extends GMInstrument(63)
    case SopranoSax            extends GMInstrument(64)
    case AltoSax               extends GMInstrument(65)
    case TenorSax              extends GMInstrument(66)
    case BaritoneSax           extends GMInstrument(67)
    case Oboe                  extends GMInstrument(68)
    case EnglishHorn           extends GMInstrument(69)
    case Bassoon               extends GMInstrument(70)
    case Clarinet              extends GMInstrument(71)
    case Piccolo               extends GMInstrument(72)
    case Flute                 extends GMInstrument(73)
    case Recorder              extends GMInstrument(74)
    case PanFlute              extends GMInstrument(75)
    case BlownBottle           extends GMInstrument(76)
    case Shakuhachi            extends GMInstrument(77)
    case Whistle               extends GMInstrument(78)
    case Ocarina               extends GMInstrument(79)
    case SynthLeadSquare       extends GMInstrument(80)
    case SynthLeadSawtooth     extends GMInstrument(81)
    case SynthLeadCalliope     extends GMInstrument(82)
    case SynthLeadChiff        extends GMInstrument(83)
    case SynthLeadCharang      extends GMInstrument(84)
    case SynthLeadVoice        extends GMInstrument(85)
    case SynthLeadFifths       extends GMInstrument(86)
    case SynthLeadBassLead     extends GMInstrument(87)
    case SynthPadNewAge        extends GMInstrument(88)
    case SynthPadWarm          extends GMInstrument(89)
    case SynthPadPolysynth     extends GMInstrument(90)
    case SynthPadChoir         extends GMInstrument(91)
    case SynthPadBowed         extends GMInstrument(92)
    case SynthPadMetallic      extends GMInstrument(93)
    case SynthPadHalo          extends GMInstrument(94)
    case SynthPadSweep         extends GMInstrument(95)
    case SynthEffectRain       extends GMInstrument(96)
    case SynthEffectSoundtrack extends GMInstrument(97)
    case SynthEffectCrystal    extends GMInstrument(98)
    case SynthEffectAtmosphere extends GMInstrument(99)
    case SynthEffectBrightness extends GMInstrument(100)
    case SynthEffectGoblins    extends GMInstrument(101)
    case SynthEffectEchoes     extends GMInstrument(102)
    case SynthEffectSciFi      extends GMInstrument(103)
    case Sitar                 extends GMInstrument(104)
    case Banjo                 extends GMInstrument(105)
    case Shamisen              extends GMInstrument(106)
    case Koto                  extends GMInstrument(107)
    case Kalimba               extends GMInstrument(108)
    case BagPipe               extends GMInstrument(109)
    case Fiddle                extends GMInstrument(110)
    case Shanai                extends GMInstrument(111)
    case TinkleBell            extends GMInstrument(112)
    case Agogo                 extends GMInstrument(113)
    case SteelDrums            extends GMInstrument(114)
    case Woodblock             extends GMInstrument(115)
    case TaikoDrum             extends GMInstrument(116)
    case MelodicTom            extends GMInstrument(117)
    case SynthDrum             extends GMInstrument(118)
    case ReverseCymbal         extends GMInstrument(119)
    case GuitarFretNoise       extends GMInstrument(120)
    case BreathNoise           extends GMInstrument(121)
    case Seashore              extends GMInstrument(122)
    case BirdTweet             extends GMInstrument(123)
    case TelephoneRing         extends GMInstrument(124)
    case Helicopter            extends GMInstrument(125)
    case Applause              extends GMInstrument(126)
    case Gunshot               extends GMInstrument(127)
