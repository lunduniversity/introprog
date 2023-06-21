package music

object Synth:
  import javax.sound.midi._
  import GMInstruments._

  val underlying: Synthesizer =
    println("Initializing javax.sound.MidiSystem ...")
    println(MidiSystem.getMidiDeviceInfo().mkString(" "))
    val synth = MidiSystem.getSynthesizer
    synth.open
    assert(synth.loadAllInstruments(synth.getDefaultSoundbank), "Loading MIDI instruments failed")
    synth
  resetInstruments() // assign some different instruments to channels

  def midiChannel(channel: Int): MidiChannel = underlying.getChannels.apply(channel)
  def channels: Range = underlying.getChannels.indices

  def instruments: Seq[Instrument] = underlying.getLoadedInstruments.toSeq

  def changeInstrument(program: Int, channel: Int = 0): Unit =
    val patch = instruments.find(_.getPatch.getProgram == program).map(_.getPatch)
    patch match
      case Some(p) => midiChannel(channel).programChange(p.getBank, p.getProgram)
      case None => println(s"Instrument with program number $program not found")

  lazy val defaultInstruments = 
    Vector(AcousticGrandPiano, AcousticGuitarNylon, AcousticBass, Trumpet, Flute)

  def resetInstruments(): Unit = defaultInstruments.zipWithIndex.foreach {
    case (program, channel) => changeInstrument(program, channel)
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
    velocity: Int         = 60,
    duration: Long        = 300,
    spread:   Long        = 50,
    after:    Long        = 0,
    channel:  Int         = 0
  ): Unit =
    delay(after)
    noteNumbers.foreach{ nbr =>
      noteOn(nbr, velocity, channel)
      delay(spread)
    }
    delay(duration)
    noteNumbers.foreach(noteOff(_, channel))

  def playConcurrently(
    noteNumbers: Seq[Int] = Vector(60),
    velocity: Int         = 60,
    duration: Long        = 300,
    spread:   Long        = 50,
    after:    Long        = 0,
    channel:  Int         = 0
  ): Unit =
    import scala.concurrent.ExecutionContext.Implicits.global

    val _ = scala.concurrent.Future{
      playBlocking(noteNumbers, velocity, duration, spread, after, channel)
    }

  object GMInstruments:
    // These program numbers are defined as particular instruments by General MIDI.
    // They're often indexed from 1, but javax.sound.midi indexes them from 0.
    val AcousticGrandPiano = 0
    val BrightAcousticPiano = 1
    val ElectricGrandPiano = 2
    val HonkyTonkPiano = 3
    val ElectricPiano1 = 4
    val ElectricPiano2 = 5
    val Harpsichord = 6
    val Clavi = 7
    val Celesta = 8
    val Glockenspiel = 9
    val MusicBox = 10
    val Vibraphone = 11
    val Marimba = 12
    val Xylophone = 13
    val TubularBells = 14
    val Dulcimer = 15
    val DrawbarOrgan = 16
    val PercussiveOrgan = 17
    val RockOrgan = 18
    val ChurchOrgan = 19
    val ReedOrgan = 20
    val Accordion = 21
    val Harmonica = 22
    val TangoAccordion = 23
    val AcousticGuitarNylon = 24
    val AcousticGuitarSteel = 25
    val ElectricGuitarJazz = 26
    val ElectricGuitarClean = 27
    val ElectricGuitarMuted = 28
    val OverdrivenGuitar = 29
    val DistortionGuitar = 30
    val GuitarHarmonics = 31
    val AcousticBass = 32
    val ElectricBassFinger = 33
    val ElectricBassPick = 34
    val FretlessBass = 35
    val SlapBass1 = 36
    val SlapBass2 = 37
    val SynthBass1 = 38
    val SynthBass2 = 39
    val Violin = 40
    val Viola = 41
    val Cello = 42
    val Contrabass = 43
    val TremoloStrings = 44
    val PizzicatoStrings = 45
    val OrchestralHarp = 46
    val Timpani = 47
    val StringEnsemble1 = 48
    val StringEnsemble2 = 49
    val SynthStrings1 = 50
    val SynthStrings2 = 51
    val ChoirAahs = 52
    val VoiceOohs = 53
    val SynthVoice = 54
    val OrchestraHit = 55
    val Trumpet = 56
    val Trombone = 57
    val Tuba = 58
    val MutedTrumpet = 59
    val FrenchHorn = 60
    val BrassSection = 61
    val SynthBrass1 = 62
    val SynthBrass2 = 63
    val SopranoSax = 64
    val AltoSax = 65
    val TenorSax = 66
    val BaritoneSax = 67
    val Oboe = 68
    val EnglishHorn = 69
    val Bassoon = 70
    val Clarinet = 71
    val Piccolo = 72
    val Flute = 73
    val Recorder = 74
    val PanFlute = 75
    val BlownBottle = 76
    val Shakuhachi = 77
    val Whistle = 78
    val Ocarina = 79
    val SynthLeadSquare = 80
    val SynthLeadSawtooth = 81
    val SynthLeadCalliope = 82
    val SynthLeadChiff = 83
    val SynthLeadCharang = 84
    val SynthLeadVoice = 85
    val SynthLeadFifths = 86
    val SynthLeadBassLead = 87
    val SynthPadNewAge = 88
    val SynthPadWarm = 89
    val SynthPadPolysynth = 90
    val SynthPadChoir = 91
    val SynthPadBowed = 92
    val SynthPadMetallic = 93
    val SynthPadHalo = 94
    val SynthPadSweep = 95
    val SynthEffectRain = 96
    val SynthEffectSoundtrack = 97
    val SynthEffectCrystal = 98
    val SynthEffectAtmosphere = 99
    val SynthEffectBrightness = 100
    val SynthEffectGoblins = 101
    val SynthEffectEchoes = 102
    val SynthEffectSciFi = 103
    val Sitar = 104
    val Banjo = 105
    val Shamisen = 106
    val Koto = 107
    val Kalimba = 108
    val BagPipe = 109
    val Fiddle = 110
    val Shanai = 111
    val TinkleBell = 112
    val Agogo = 113
    val SteelDrums = 114
    val Woodblock = 115
    val TaikoDrum = 116
    val MelodicTom = 117
    val SynthDrum = 118
    val ReverseCymbal = 119
    val GuitarFretNoise = 120
    val BreathNoise = 121
    val Seashore = 122
    val BirdTweet = 123
    val TelephoneRing = 124
    val Helicopter = 125
    val Applause = 126
    val Gunshot = 127
