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
  * Implementera algoritmen shuffle  (finns redan på samlingar i Scala men bra träning att implementera själv)
  * Använd kortspel, tex patienten från EDA016 lab 8 Vektorer, simulering patient; eller ngt roligare :)
  * Använd strängar och heltal för konstanter för korten  
  * case class Card("Spades", 13) Engelska el. Svenska???
  * Lägg till registrering
    * Visa "färdig" myCardMap.groupBy(_.color).mapValues(_.size)
    * Inplementera egen registrering i en Map så att man kan registrera intebara på int utan string
  * Använda wild-card-syntaxen vid metodanrop på collection 
   
## shapes          
  * Skapa klasser som representerar geometriska objekt och rita ut. 
  * Gör ett ritprogram? inspireras av gamla inlämningsuppg
  * Rectangle(Point(3,4), Point(7,8)).draw
  * Är detta för tråkigt utan arv???
  * Alternativ konstruktur vs fabriksmetoder
  * Equals
  * inklapsling av var.
  * private, public
  
## turtlerace-team 
  * översätt befintlig labb
  * träna på arv
  * gör team-inslaget mer omfattande så att det blir mer att göra som ska passa ihop

## chords-team     
  * 
  
## maze            
  
## surveydata-team 

## scalajava-team  

## life            
 * Inl.Uppg.       
## bank
## game
## image


# Konsekvenser av ovan lab-skisser för övningar
## övn functions kolla args(0) == 
 if (!args.isEmpty) {
   val filename = args(0) 
      
 }
 if (args.size == 2) {
 }
 
 * lägg till _ + 2 etc  ??? Förklara wild-card-syntaxen i funktioner. Anonyma argument.
 
## övn data
:type i REPL på övning "data" eller övn expressions
equals mellan case-klasser i övn data; visa att man kan kolla innehållet
