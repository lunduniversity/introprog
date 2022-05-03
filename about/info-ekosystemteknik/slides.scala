//> using lib "taggy:taggy:0.0.1,url=https://github.com/bjornregnell/taggy/releases/download/v0.0.1/taggy_3-0.0.1.jar"
//> using scala "3.nightly"
//  run in terminal> scala-cli run .

import scala.language.experimental.fewerBraces
import taggy.*

@main def run = slides.toPdf()

def slides = document("Informationsmöte om programmering för Ekosystemteknik"):
  frame("Två alternativa programmeringskurser för W"):
    p("**EDAA65** Programmering: 6 hp, lp3-4")
    itemize:
      p("Allmänbildning för ''icke-IT-program'', obl. f. M")
      p("Språk: **Java**")
      p("https://cs.lth.se/edaa65/")
    p("**EDAA45** Programmering, grundkurs: 7.5 hp, lp1-2")
    itemize: 
      p("Konceptuell grund för vidare studier, obl. f. D+C")
      //p("Lämplig för de med extra intresse och hög ambition")
      p("Språk: **Scala**")
      p("""Läses med fördel tillsammans med\\\\ 
           EDAA60 Datorer och datoranvändning ''dod'', 3hp\\\\
           som ger kunskap om linux och terminalkommanndo""")
      p("https://cs.lth.se/pgk")
    p("*Valfri fortsättning:* **EDAA01** Programmering fördjupningskurs")