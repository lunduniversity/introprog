Övergripande 
============

## Övningar vs Labbar

* Medan övningarna är små kod-snuttar som visar olika begrepp så är labbarna en helhet som byggs upp steg för steg. När man är färdig har man byggt ett större program som gör ngt vettigt.

## Svenska vs.Engelska

* Kod i Labbar som ges och ska utvecklas av den labbande ska vara på Engelska.  (Även man inte blir uk om man använder svenska). Idéen är att signalera att när man kodar för att det ska bli ngt "riktigt" kodar man på engelska.

* Men på övningar och förel är svenska variabelnamn ok för att tydliggöra att detta har jag hittat på och detta är nyckelord etc. Idéen är att signalera att när man lär sig och lekel kan man hitta på tokroliga namn och använda svenska hur mycket man vill.

Skiss på labbar
================

## Lab simplewindow  (ska vi byta namn på labben till draw) ???

  * Ska vi införa javafx istf swing? Nej, håll oss till SimpleWindow med swing i år så får vi se nästa år.
  * Huvudmål: skapa och använda funktioner; nästlade funktionsanrop, många one-liners
  * Skapa modul som samlar ritfunktioner. Eventuellt dela upp i flera moduler.
  
    object Draw {
        w = new SimpleWindow(100,100,"Draw")
        def rect(x1: Int, y1: Int, height: Int, width: Int) = {
           w.moveTo(x1,y1) 
           ... etc
        }
    }  
    object Main {
       import Draw._
       rect(x,y,h,w) ... etc
    }
    
  * Uppdrag: Gör ett enkelt spel, tex köra omkring och leta mat och undvika buggar; poängräkning; game-loop
  
  * Att utveckla: (Om inte för mycket jobb) fixa non-blocking interaction: while(true) { if (w.hasEvent) { val key = w.??? } } eller liknande. Gör en ny klass som utvidgar SimpleWindow, tex class GameWindow extends SimpleWindow. Välj själv om GameWindow är Scala eller Java; men iofs gärna Scala om det inte finns ngn speciell anledning att inte göra det i Scala. Återanvänd datastrukturerna i SimpleWindow och inför pollning så att man kan köra en game-loop ```while(!gameOver){ övergripande logik som anropar olika funktioner och uppdaterar datastrukturer }```

  * Kanske extrauppgift: rita funktioner (ungefär som en enkel plot i matlab) eller är det för mycket matte?  plot(math.sin _, from, to, dx)  el. likn.
       
## textfiles "save the word??"
 * huvudmål: 
   1. använda samlingar, map filter i kedjor
   1. skapa enkla record-datastrukter med case classer 
   1. använda Eclipse inkl enkel debugging 
   1. bearbeta text, läsa/spara på textfil
   
 * Uppdrag: skapa program som läser och skriver filer för att lösa en textbehandlingsuppgift 
 * idé: utgå från en piratberättelse, manipulera text  
 * Förberedelse: Kolla Eclipse-gajd, skapa projekt etc
 * Vore toppen med en kort youtube-film som demonstrerar Eclipse basics 
 * Visa hur man ger parametrar, args i Eclipse
 * Använda datastrukturer: Tuple, Vector, Map, Set? för textbehandling
 * Samlingsmetoder: mkString, .ap, filter, indexering i Map, split(',')
 * Visa debuggern, felsöka, kolla värdet på variabel i runtime. Youtube-film på debugging?
 * Kanske extrauppg: modala dialoger med textinput istf readLine/Scanner/arg; bygg vidare på hur JOptionDialog användes i övn 2

## cardgame
  * huvudmål: 
    1. träna på "vektoralgoritmer" alltså indexering i Vector, Array, och ev även "associativa arrayer" alltså en Map
    1. blanda element SHUFFLE
    1. registrering, räkna frekvenser 
  * Uppdrag: simulera eller spela kortspel eller patiens
  * Implementera algoritmen shuffle  (finns redan på samlingar i Scala men bra träning att implementera själv)
  * Återanvänd ev patienten från EDA016 lab 8 Vektorer, simulering patient; eller ngt roligare :)
  * Använd strängar och heltal för konstanter för korten  
  * case class Card("Spades", 13) Engelska el. Svenska???
  * Lägg till registrering
    * Visa "färdig" myCardMap.groupBy(_.color).mapValues(_.size)
    * Inplementera egen registrering i en Map så att man kan registrera intebara på int utan string
  * Använda wild-card-syntaxen vid metodanrop på collection 
   
## shapes          
  * Huvudmål (vi väntar alltså med arv)
    1. Skapa egna klasser
    1. Skapa klasser som har attribut som är referenser till andra klasser
    1. Klasser som tar metoder som har andra klasser av samma typ som parameter så att man måste fatta this och other
    1. Koncept kopplade till klasser: 
      * Alternativ konstruktur vs fabriksmetoder
      * Equals
      * inklapsling av var.
      * private, public

  * Ide till uppdrag: Skapa klasser som representerar geometriska objekt och rita ut. 
  * Gör ett ritprogram? inspireras av gamla inlämningsuppg Draw; gör allt själv i simple window som man känner att man har kontroll.
  * Rita ngt fint med många trianglar, tex en julgran
  * Rectangle(Point(3,4), Point(7,8)).draw
  * Kan man fylla i sina shapes med färg eller är det för svårt?
  * Är detta för tråkigt/krystat utan arv? Rectangle extends Shape... Arv behövs egentligen bara om man ska lägga olika shapes i samma datastruktur.
  
## turtlerace-team 
  * Huvudmål: träna på arv, super, polymorfism, stoppa subklasser i samma samling
  * översätt befintlig labb och gör den roligare och lite svårare
  * gör team-inslaget mer omfattande så att det blir mer att göra som ska passa ihop

## chords-team     
  * Utgå från kontrollskrivningen om ackord och gör ngt kul, kanske rita ackord i simple window: 
    * http://fileadmin.cs.lth.se/cs/Education/eda016/2015/kontroll/kontroll-2015okt27.pdf
    * http://fileadmin.cs.lth.se/cs/Education/eda016/2015/kontroll/kontroll-2015okt27-losning.pdf
  * Ide till extrauppgift om det inte är för svårt: spela toner med java midi, kanske man kan göra en SimpleMidi-klass som spelar en ton i taget och wrappa detta:
    * http://stackoverflow.com/questions/16462854/midi-beginner-need-to-play-one-note 
    * https://docs.oracle.com/javase/8/docs/api/javax/sound/midi/MidiSystem.html
  
## maze            
  * återanvänd gamla maze, men kanske lägg till animering av sprite som går genom labyrinten som extrauppgift eller ngt
  
## surveydata-team 
  * utgå från tentan om enkätanalys och gör ngt kul:
    * http://fileadmin.cs.lth.se/cs//Education/grundkurs/extentor/160113.pdf
    * http://fileadmin.cs.lth.se/cs//Education/grundkurs/extentor/sol-160113.pdf
    
## scalajava-team  
  * ideer välkomna!
  
## life            
  * återanvänd gamla life men gör ev. ngt roligare
  * extrauppgift att byta ut view Scala.js så allt kör i browsern
  * extrauppgift att byta view mot Android så allt kör i telefonen

  
## Inlämningsuppgifter
  
* Inlämningsuppgifter ska vara lite större program. Hur ska vi definiera "större"?
* Programmet ska innehålla mpnga klasser i Scala och några klasser i Java som används från Scala-klasserna (men inte tvärtom)
* Vi skulle kunna höja ribba lite och kräva att de lägger ut scaladoc + javadoc för sin uppgift på sin skol-hemsida.   

### Inl.Uppg. bank
  * Definiera vilka klasser som ska vara i Java och vilka som ska vara i Scala?
  * Gör banken mer realistsik; använd immutable saker och spara transaktioner som utförs för varje konto?
  * inpspireras av joda.time och joda.money  ?  

### Inl. Uppg game
  * Idéer välkomna. Ngt turtagningsspel som ritas upp. Två mänskliga spelare eller en ganska dum datorspelare? Tic tac toe? Go? Othello?

### Inl. Uppg image
  * Gör ngt kul med bildbehandling typ image-filter från tidigare. Vad ska vi ev översätta till Scala?


# Konsekvenser av ovan lab-skisser för övningar

## övn functions kolla vad vi kan ha med om args(0) 
 if (args.size == 1) {
   val filename = args(0) 
   etc...    
 } else if (args.size == 2) {
   etc....
 } else  if (args.size == 0 {
   etc..
 } else ???
 
 * lägg till _ + 2 etc  ??? Förklara wild-card-syntaxen i funktioner "anonyma argument"
 
## övn data
:type i REPL på övning "data" eller övn expressions
equals mellan case-klasser i övn data; visa att man kan kolla innehållet
