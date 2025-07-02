Övergripande 
============

## Övningar vs Labbar

* Medan övningarna är små kod-snuttar som visar olika begrepp så är labbarna en helhet som byggs upp steg för steg. När man är färdig har man byggt ett större program som gör ngt vettigt.

## Svenska vs.Engelska

* Kod i Labbar som ges till de labbande ska vara på Engelska.  (Även om labbande inte blir underkända eller bannade om de använder svenska namn...). Idéen är att signalera att när man kodar för att det ska bli ngt "riktigt" kodar man på engelska.

* Men på övningar och föreläsningar är svenska variabelnamn ok för att tydliggöra att detta har jag hittat på och detta är ett nyckelord etc. Idén är att signalera att när man lär sig och experimenterar kan man hitta på tokroliga namn och använda svenska hur mycket man vill.

Skiss på labbar
================

## Lab simplewindow  

Byta namn på labben till bugs el likn. beroende på vad det blir för rolig uppgift ???

  * Huvudmål: skapa och använda funktioner; nästlade funktionsanrop, många one-liners
  
  * Träna på println-debugging?  
  
  * Uppdrag: Gör ett enkelt spel, tex köra omkring och leta mat och undvika buggar; poängräkning; game-loop
    
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
  
  * Att utveckla: (Om inte för mycket jobb) fixa non-blocking interaction: while(true) { if (w.hasEvent) { val key = w.??? } } eller liknande. Gör då en ny klass tex ```class GameWindow extends SimpleWindow```. Ska GameWindow vara Scala eller Java? Gärna Scala om det inte finns ngn speciell anledning att inte. Skiss på design: Återanvänd datastrukturerna i SimpleWindow och inför pollning så att man kan köra en game-loop ```while(!gameOver){ övergripande logik som anropar olika funktioner och uppdaterar datastrukturer }```

  * Ska vi införa javafx istf swing? Nej, antagligen inte, håll oss till SimpleWindow med swing i år så får vi se nästa år. Om man ska använda javafx får man tex införa en klass SimpleFxApp som inte kan instansieras på vanligt sätt. Antagligen kan man inte dölja detta utan att det blir förmycket magi:
  
    public static void main(String[] args) {
        javafx.application.Application.launch( SimpleFxApp.class );
    }
    
  Är SimpleFxApp för mycket jobb att hinna med redan i år? Antagligen... Eftersom SimpleWindow används även i senare labbar så får man undersöka om SimpleFxApp ska få påverka tex life och maze etc. 

  * Idé till extrauppgift: plotta funktioner (ungefär som en enkel plot i matlab) eller är det för mycket matte?  plot(math.sin _, from, to, dx)  el. likn. Kanske även stapeldiagram och tårtdiagram.
       
## Lab textfiles 

Nytt namn: "pirates"  ???

 * huvudmål: 
   1. använda samlingar, map filter i kedjor
   1. skapa enkla record-datastrukter med case classer 
   1. använda Eclipse inkl enkel debugging 
   1. bearbeta text, läsa/spara på textfil
   
 * Uppdrag: skapa program som läser och skriver filer för att lösa textbehandlingsuppgifter 

 * idé: utgå från en piratberättelse, manipulera text  

 * Förberedelse: Kolla Eclipse-gajd, skapa projekt etc

 * Vore toppen med en kort youtube-film som demonstrerar Eclipse basics 

 * Visa hur man ger parametrar, args i Eclipse

 * Använda datastrukturer: Tuple, Vector, Map, Set? för textbehandling

 * Samlingsmetoder: mkString, map, filter, indexering i Map, split(',')

 * Visa debuggern, felsöka, kolla värdet på variabel i runtime. Youtube-film på debugging?

 * Kanske extrauppg: modala dialoger med textinput istf readLine/Scanner/arg; bygg vidare på hur JOptionDialog användes i övn 2

## Lab cardgame

* huvudmål: 
  1. träna på "vektoralgoritmer" alltså indexering i Vector, Array, och ev även "associativa arrayer" alltså en Map
  1. blanda element SHUFFLE
  1. registrering, räkna frekvenser 

* Uppdrag: simulera eller spela kortspel eller patiens

* Implementera algoritmen shuffle  (finns redan på samlingar i Scala men bra träning att implementera själv)

* Återanvänd ev patienten från EDA016 lab 8 Vektorer, alltså simulering patient; eller ngt roligare???: ett kortspel mellan människa och en dum dator som bara slumpar? 
   * http://www.spelregler.org/kortspel-for-tva-personer/ 
   * Detta verkar ganska enkelt och kan bli roliga if-satser: http://www.spelregler.org/vandatta-regler/

* Använd strängar och heltal för konstanter för korten  

* case class Card("Spades", 13) Engelska el. Svenska i strängarna? Ok med svenska i strängarna men namn som kompilatorn ser i koden ska vara på engelska.

* Lägg till registrering så att de får träna på det:
    * Visa "färdig" myCardMap.groupBy(_.color).map(p => p._1 -> p._2.size)  (kanske ska det göras redan på föreläsning och övning)
    * Inplementera egen registrering i en Map så att man kan registrera inte bara på int utan string i (key, value)-par

* Använda wild-card-syntaxen vid metodanrop på collection -> kolla så att detta övas på övningar
   
## Lab shapes    

Byta namn till turtlegraphics?

### Huvudmål 
  1. Skapa egna klasser (men vi väntar med arv till nästa lab)
  1. Skapa klasser som har attribut som är referenser till andra klasser (delegering, aggregering)
  1. Klasser som tar metoder som har andra klasser av samma typ som parameter så att man måste fatta this och other
  1. Koncept kopplade till klasser: 
    * Alternativ konstruktor vs fabriksmetoder
    * Equals
    * inklapsling av var.
    * private, public
   
### Grov Plan:
1. Implementera egen immutable Point-klass utan att använda case-klasser.
  * Skriv en equals-metod till Point. Det behövs fortfarande ett naturligt use-case för en equalsmetod, så att det inte känns intvingat.
  * Frågor i häftet om vilka objekt som "this" och "other" refererar till.
  * Diskussion: Ska x,y vara private eller public? (Turtle måste komma åt x och y för trigonometrin) 
  * Diskussion: Vad bör en Point innehålla? Ints verkar naturligt då en Point representerar ett pixelkoordinat, men då kan man få uppbyggande avrundningsfel.
1. Implementera en egen turtle
  * Träna på muterbart tillstånd. Relativ förflyttning med forward() och turn() kräver att man i huvudet följer turtlens tillstånd.
  * Frågor i häftet runt termerna attribut, konstruktor, public/private, returvärden. Hade det gått lika bra att använda en omuterbar turtle? Varför/Varför inte?
  * Skapa en primary constructor som innehåller alla parametrar, men även en alternativ konstruktor som bara placerar den i mitten av fönstret? Alternativt införa något annat attribut, som t.ex. färg.
1. (Osäkert. Finns tid till detta?) Implementera Shapes som omuterbara klasser med en draw(t : Turtle)-metod. 
  * Finns det någon fördel med att skapa klasser som ritar shapes framför att använda metoder? Potentiellt om man inför "higher order shapes" som kan kombinera shapes på ett intressant sätt, men då behövs arv, och det blir för komplext. Ett alternativ är att ha metoder på klassen som returnerar en ny, modifierad version av samma form. T.ex. scale(factor : Double), eller liknande.
  * Kombinera olika Shapes för att skapa större shapes? Julgran(t : Triangle), MussePigg(face : Circle, ear : Circle)?


### Öppna frågor:
  * Är svårighetsgraden/innehållsmängden rätt? Klasser är ett svårt koncept att greppa för totala nybörjare, men lätt när man har fattat det. Ska vi utöka med extrauppgifter för att ge en utmaning till de som känner sig mer vana vid konceptet?
  * Hur kan man bäst få in kravet om equals-metoden i Point, utan att det känns tvingat? Behöver en Turtle någonsin jämföra två points?
  * Ska Point ha några metoder? Man kan potentiellt flytta all matte till Point, vilket ger möjlighet att visa hur metoderna på en omuterbar klass kan returnera nya objekt av samma typ.
  * Med tanke på att Scala erbjuder sätt att transparent byta ut publika attribut mot properties finns det inte samma behov av att direkt från början inkapsla attribut med get/set-metoder. Hur påverkar detta valet av synlighetsnivå till Turtles attribut? I denna labb kanske det räcker att göra allt private och utan getters, då omvärlden inte behöver veta något om turtlen.
  * Kan man fylla i sina shapes med färg eller är det för svårt?
  * Räcker det med att Turtle innehåller en SimpleWindow för att illustrera delegering?
  
## Lab turtlerace-team 
* Huvudmål: träna på arv, super, polymorfism, stoppa subklasser i samma samling

* översätt befintlig labb och gör den roligare och lite svårare

* gör team-inslaget mer omfattande så att det blir mer att göra som ska passa ihop

* Hur kan man bygga ut denna labb?

* Sprites för varje sköldpadda så det ser lite roligare ut

* Idé till extrauppgift: Kan man bygga en enkel sprite-editor där man klickar på färgpalett och klickar på pixelrutnät? Eller i en annan labb?

## Lab chords-team     

* Huvudmål:
  1. Träna på mönstermatchning
  1. case-klasser
  1. Option
  1. Prova ngt enkelt med undantag try catch finally
  1. Try ???
  1. unapply ??? Nej, antagligen ev. bara på fördjupningsuppgift på övningar

* Ide till extrauppgift om det inte är för svårt: spela toner med java midi, kanske man kan göra en SimpleMidi-klass som spelar en ton i taget och wrappa detta:
  * http://stackoverflow.com/questions/16462854/midi-beginner-need-to-play-one-note 
  * https://docs.oracle.com/javase/8/docs/api/javax/sound/midi/MidiSystem.html
  
## maze            
  
* Huvudmål
  1. träna på matriser
  1. indexering i flera nivåer
  1. Använda egna klassen ColorTurtle och se hur man i eclipse kopplar kod i olika projekt
  1. Kanske ha flera olika Maze-walkers tex RandomMazeWalker och använda arv även här?

* återanvänd gamla maze, men kanske lägg till animering av sprite som går genom labyrinten som extrauppgift eller ngt

* hur blir denna roligare utan att det blir för svårt
  
## surveydata-team 

* Huvudmål
  1. Förstå ordning och hur ordning kopplar till likhet... compareTo, sortBy
  1. Implementera en egen sortering av strängar med enkel sortering till ny vektor med insticksortering
  1. Sortera raderna i en matris av strängar med avseende på en viss kolumn
  1. Träna igen på registrering
  1. extrauppgift: gör din sorteringsfunktion generisk ?? 
  
  ```def sort[T](in: Vector[T], isBefore: (T, T) => Boolean):Vector[T]```
  
* Idé till uppdrag: utgå från tentan om enkätanalys och gör ngt kul:
  * http://fileadmin.cs.lth.se/cs//Education/grundkurs/extentor/160113.pdf
  * http://fileadmin.cs.lth.se/cs//Education/grundkurs/extentor/sol-160113.pdf
  * Kanske simpel spreadsheet med strängar i denna klass eller liknande:
       
        case class StringMatrix(matrix: Vector[Vector[String]], headings: Vector[String]){
          // en massa bra-att-ha-metoder
        } 
        object StringMatrix {
          def fromFile(fileName: String, separator: String = ";"): StringMatrix = ???
        }
        
        
## scalajava-team  

* Denna labb är extra lurig att utveckla. Ideer och förslag emottages tacksamt!

* Tänk ut en design som gives där ett antal klasser är i Java som används från Scala. Största kodmängden i Java men en del i Scala och gärna använda  scala.jdk.CollectionConverters i scala-delen av koden. Uppgiften ska konfrontera labbande med javas autoboxing och javas primitiva arrayer och kanske även arraylist. Gärna arv och super och hela baletten. 

* Uppgiften ska gå att dela upp uppgiften i ett team (om det funkar att detta är en teamlab, vilket är hypotesen). Ev. kan man kräva att en eller flera klasser ska implementeras både i Scala och Java för att göra det extra tydligt med skillnader och likheter.

* En idé är att göra en **quiz-applikation** som hjälper användaren att träna på flervalsfrågor med ett enkelt text-ui i terminalen. Alltså typ:
  * läsa in frågor i ngt format från fil
  * en design av en bra datastruktur för frågorna ska vara given, inkl fråga, svarsalternativ och förklaringar varför alternativen är rätt/fel som kan användas som hintar
  * slumpa frågor
  * räkna poäng
  * ge tips när man svarat fel
  * träna på frågor inom områden man har svårt för; (frågans topic bör alltså ingå) etc. Vi kan bjuda in alla att hitta på bra quiz-frågor som tränar programmeringskoncept i Scala och Java.

  
## life      

* Huvudmål:
  1. Se uppdelning mellan Model och View och se hur klasser kan samarbeta
  1. Beroende mellan klasser (och inte...) Vem känner till vem? Delegering.
  1. Träna mer på matriser
  1. Träna på logik 
  1. Kan man få in ngt mycket enkelt om ngt som körs i en egen tråd??? Antagligen inte; det får nog räcka med simpel grej om trådar på övningen

* Återanvänd gamla life men gör den roligare, om det går utan att den blir för svår.
* Ta ställning till vad som ska vara i Scala och vad som ska vara i Java av det som studenterna själv ska utveckla. 
* Idé till extrauppgift: att byta ut view Scala.js så allt kör i browsern. Kräver att man installerar sbt på sin egen dator.
* Idé till extrauppgift: att byta view mot Android så allt kör i telefonen - detta finns redan en sådan som Patrik Persson har gjort så vi ska prata med honom om denna - kanske kan man bara hänvisa till info på EDA017-011-hemsidan för de hugade. Kräver att man installerar Android Studio på sin egen dator och lite meck med att sätta på virtualiseringsstöd i bios om man har otur...

 
## Inlämningsuppgifter
  
* Inlämningsuppgifter ska vara ett "större" program. Hur ska vi definiera "större"?

* Programmet ska innehålla många klasser i Scala och några klasser i Java som används från Scala-klasserna (men inte tvärtom)

* Vi skulle kunna höja ribba lite och kräva att de lägger ut scaladoc + javadoc för sin uppgift på sin skol-hemsida.   

* Det kan också vara fint om man får sätta sig in lite i ngn del av JDK så att man tränar på att använda befintliga api. 

### Inl.Uppg. bank

* Definiera vilka klasser som ska vara i Java och vilka som ska vara i Scala

* Eventuellt göra banken (lite) mer realistisk; använda immutable data och spara transaktioner som utförs för varje konto? Kanske man kan köra om alla transaktioner som är sparade i en fil mot en banks alla konto från ett visst datum etc.? Räkna ut ränta?

* Inpspireras av joda.time och joda.money? Här finns att läsa om datum i Java 8: http://www.oracle.com/technetwork/articles/java/jf14-date-time-2125367.html

* Denna klassen är en fasad mot gamla trådosäkra Gregorian Calendar och Date som döljer så tokiga grejer som månader som börjar på noll etc. även om den är lite ful som läcker gamla java.util.Date:  https://github.com/lunduniversity/introprog/blob/master/plan/Date.scala men man kanske kan byta ut inälvorna i denna fasad som de labbande får och som istället wrappar de nya fina klasserna i java 8 och nya java.time? Och inte läcka java.util.Date...
  
### Inl. Uppg game

* Idéer välkomna! Ngt turtagningsspel som ritas upp. Två mänskliga spelare eller en ganska dum datorspelare? Tic tac toe? Go? Othello? Synka med lab 2 Gustav som nog kommer göra ett enkelt spel som kanske kan utvidgas här?

### Inl. Uppg image

* Gör ngt kul med bildbehandling typ image-filter från inl.uppg EDA016/2015. Vad ska vi ev. översätta till Scala och vad ska vara i Java?


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
