package genquiz  // see types in package object in TypesAndUtils.scala

object QuizData {  // to generate tables for a concept connection quizes in latex

  def concepts(q: QuizName): Concepts =
    quizes(q).zipWithIndex.map { case ((k,v), i) => ((k.trim,i), (v.trim,i)) }

  def quizNames: Vector[QuizName] = quizes.keys.toVector

  val quizes = Map[QuizName, Vector[Concept]](

    "quiz-w01-concepts" -> Vector( //expressions
      "litteral       " -> "anger ett specifikt datavärde",
      "sträng         " -> "en sekvens av tecken",
      "sats           " -> "en kodrad som gör något; kan särskiljas med semikolon",
      "uttryck        " -> "kombinerar värden och funktioner till ett nytt värde",
      "funktion       " -> "vid anrop beräknas ett returvärde",
      "procedur       " -> "vid anrop sker (sido)effekt; returvärdet är tomt",
      "exekveringsfel " -> "sker medan programmet kör",
      "kompileringsfel" -> "sker innan exekveringen startat",
      "abstrahera     " -> "att införa nya begrepp som förenklar kodningen",
      "kompilera      " -> "att översätta kod till exekverbar form",
      "typ            " -> "beskriver vad data kan användas till",
      "for-sats       " -> "bra då antalet repetitioner är bestämt i förväg",
      "while-sats     " -> "bra då antalet repetitioner ej är bestämt i förväg",
      "tilldelning    " -> "för att ändra en variabels värde",
      "flyttal        " -> "decimaltal med begränsad noggrannhet",
      "boolesk        " -> "antingen sann eller falsk"
    ),

    "quiz-w01-types" -> Vector(
      "\\code|1    |" -> "\\code|Int    |",
      "\\code|1L   |" -> "\\code|Long   |",
      "\\code|1.0  |" -> "\\code|Double |",
      "\\code|1D   |" -> "\\code|Double |",
      "\\code|1F   |" -> "\\code|Float  |",
      "\\code|'1'  |" -> "\\code|Char   |",
      "\\code|\"1\"|" -> "\\code|String |",
      "\\code|true |" -> "\\code|Boolean|",
      "\\code|false|" -> "\\code|Boolean|",
      "\\code|()   |" -> "\\code|Unit   |"
    ),

    "quiz-w01-values" -> Vector(
      "\\code|1.0 + 18          |"    ->   "\\code|19.0: Double    | ",
      "\\code|(41 + 1).toDouble |"    ->   "\\code|42.0: Double    | ",
      "\\code|1.042e42 + 1      |"    ->   "\\code|1.042E42: Double| ",
      "\\code|12E6.toLong       |"    ->   "\\code|12000000: Long  | ",
      "\\code|32.toChar.toString|"    ->   "\\code|\" \": String   | ",
      "\\code|'A'.toInt         |"    ->   "\\code|65: Int         | ",
      "\\code|0.toInt           |"    ->   "\\code|0: Int          | ",
      "\\code|'0'.toInt         |"    ->   "\\code|48: Int         | ",
      "\\code|'9'.toInt         |"    ->   "\\code|57: Int         | ",
      "\\code|'A' + '0'         |"    ->   "\\code|113: Int        | ",
      "\\code|('A' + '0').toChar|"    ->   "\\code|'q': Char       | ",
      "\\code|\"*!%#\".charAt(0)|"    ->   "\\code|'*': Char       | "
    ),

    "quiz-w01-intdiv" -> Vector(
      "\\code| 4 / 42      |" ->   "\\code|    0: Int      | ",
      "\\code| 42.0 / 2    |" ->   "\\code| 10.5: Double   | ",
      "\\code| 42 / 4      |" ->   "\\code|   10: Int      | ",
      "\\code| 42 % 4      |" ->   "\\code|    2: Int      | ",
      "\\code| 4 % 42      |" ->   "\\code|    4: Int      | ",
      "\\code| 40 % 4 == 0 |" ->   "\\code|true : Boolean  | ",
      "\\code| 42 % 4 == 0 |" ->   "\\code|false: Boolean  | "
    ),

    "quiz-w02-concepts" -> Vector(  //programs
      "kompilerad     " -> "maskinkod sparad och kan köras igen utan kompilering",
      "skript         " -> "maskinkod sparas ej utan skapas vid varje körning",
      "objekt         " -> "samlar variabler och funktioner",
      "main           " -> "där exekveringen av kompilerad app startar",
      "programargument" -> "överförs via parametern args i main",
      "datastruktur   " -> "många olika element i en helhet; elementvis åtkomst",
      "samling        " -> "datastruktur med element av samma typ",
      "sekvenssamling " -> "datastruktur med element i en viss ordning",
      "Array          " -> "en förändringsbar, indexerbar sekvenssamling",
      "Vector         " -> "en oföränderlig, indexerbar sekvenssamling",
      "Range          " -> "en samling som representerar ett intervall av heltal",
      "yield          " -> "används i for-uttryck för att skapa ny samling",
      "map            " -> "applicerar en funktion på varje element i en samling",
      "algoritm       " -> "stegvis beskrivning av en lösning på ett problem",
      "implementation " -> "en specifik realisering av en algoritm",
      "" -> ""
    ).filter(_._1.trim.nonEmpty),

    "quiz-w02-hello-scala-java" -> Vector(  //programs
       "\\code|object|       " -> "\\jcode|public class|      ",
       "\\code|def main|     " -> "\\jcode|public static main|",
       "\\code|Array[String]|" -> "\\jcode|String[]|          ",
       "\\code|: Unit|       " -> "\\jcode|void|              ",
       "\\code|=|            " -> "\\jcode|) {|               ",
       "\\code|println|      " -> "\\jcode|System.out.println|",
      "" -> ""
    ).filter(_._1.trim.nonEmpty),

    "quiz-w02-array-vector-mutability" -> Vector(  //programs
      "Vector" -> "oföränderlig",
      "Array " -> "förändringsbar",
      "" -> ""
    ).filter(_._1.trim.nonEmpty),

    "quiz-w02-array-vector-append" -> Vector(  //programs
      "Vector" -> "varianter med fler/andra element skapas snabbt ur befintlig",
      "Array " -> "långsam vid ändring av storlek (kopiering av rubbet krävs)",
      "" -> ""
    ).filter(_._1.trim.nonEmpty),

    "quiz-w02-array-vector-equality" -> Vector(  //programs
      "Vector" -> "\\code|xs == ys| är \\code|true| om alla element lika",
      "Array " -> "olikt andra samlingar kollar \\code|==| ej innehållslikhet",
      "" -> ""
    ).filter(_._1.trim.nonEmpty),


    "quiz-w02-collection-methods" -> Vector(  //programs
      "\\code|val xs = Vector(2) |" -> "ny referens till sekvens av längd 1",
      "\\code|Array.fill(9)(0)   |" -> "ny förändringsbar sekvens med nollor",
      "\\code|Vector.fill(9)(' ')|" -> "ny oföränderlig sekvens med blanktecken",
      "\\code|xs(0)              |" -> "förkortad skrivning av \\code|apply(0)|",
      "\\code|xs.apply(0)        |" -> "indexering, ger första elementet",
      "\\code|xs :+ 0            |" -> "ny samling med en nolla tillagd på slutet",
      "\\code|0 +: xs            |" -> "ny samling med en nolla tillagd i början",
      "\\code|xs.mkString        |" -> "ny sträng med alla element intill varandra",
      "\\code|xs.mkString(\",\") |" -> "ny sträng med komma mellan elementen",
      "\\code|xs.map(_.toString))|" -> "ny samling, elementen omgjorda till strängar",
      "\\code|xs.map(_.toInt))   |" -> "ny samling, elementen omgjorda till heltal",
      "" -> ""
    ).filter(_._1.trim.nonEmpty),

    "quiz-w02-for-yield-map" -> Vector(  //programs
      "\\code|for (x <- xs) yield x - 1|" -> "\\code|Vector(0, 1, 2)|",
      "\\code|xs.map(x => x + 1)    |" -> "\\code|Vector(2, 3, 4)|",
      "\\code|for (i <- 0 to 1) yield xs(i)|" -> "\\code|Vector(1, 2)|",
      "\\code|(1 to 3).map(i => i)|" -> "\\code|Vector(1, 2, 3)|",
      "\\code|(1 until 3).map(i => xs(i))|" -> "\\code|Vector(2, 3)|",
      "" -> ""
    ).filter(_._1.trim.nonEmpty),

    "quiz-w03-concepts" -> Vector(  //functions
      "funktionshuvud    " -> "har parameterlista och eventuellt returtyp",
      "funktionskropp    " -> "koden som exekveras vid funktionsanrop",
      "parameterlista    " -> "beskriver namn och typ på parametrar",
      "parameter         " -> "namn i funktionshuvud; binds till argument",
      "argument          " -> "uttryck som är invärde vid funktionsanrop",
      "block             " -> "kan ha lokala namn; sista raden ger värdet",
      "namngivna argument" -> "gör att argument kan ges i valfri ordning",
      "default-argument  " -> "gör att argument kan utelämnas",
      "värdeanrop        " -> "argumentet evalueras innan anrop",
      "namnanrop         " -> "fördröjd evaluering av argument",
      "tupel             " -> "lista med bestämt antal (heterogena) värden",
      "tupelreturtyp     " -> "gör att en funktion kan flera resultatvärden",
      "äkta funktion     " -> "ger alltid samma resultat om samma argument",
      "slumptalsfrö      " -> "om lika blir sekvensen av pseudoslumptal samma",
      "anonym funktion   " -> "funktion utan namn; kallas även lambda",
      "rekursiv funktion " -> "en funktion som anropar sig själv",
      "" -> ""
    ).filter(_._1.trim.nonEmpty),

    "quiz-w04-concepts" -> Vector(  //objects
      "modul             " -> "kodenhet med abstraktioner som kan återanvändas",
      "singelobjekt      " -> "modul som kan ha tillstånd; finns i en enda upplaga",
      "paket             " -> "modul som skapar namnrymd; maskinkod får egen katalog",
      "import            " -> "gör namn tillgängligt utan att hela sökvägen behövs",
      "lat initialisering" -> "allokering sker först när namnet refereras",
      "medlem            " -> "tillhör ett objekt; nås med punktnotation om synlig",
      "attribut          " -> "variabel som utgör (del av) ett objekts tillstånd",
      "metod             " -> "funktion som är medlem av ett objekt",
      "privat            " -> "modifierar synligheten av en objektmedlem",
      "överlagring       " -> "metoder med samma namn men olika parametertyper",
      "namnskuggning     " -> "lokalt namn döljer samma namn i omgivande block",
      "namnrymd          " -> "omgivning där är alla namn är unika",
      "uniform access    " -> "ändring mellan def och val påverkar ej användning",
      "typalias          " -> "alternativt namn på typ som ofta ökar läsbarheten",
      "         " -> "",
      "" -> ""
    ).filter(_._1.trim.nonEmpty),

    "quiz-w05-concepts" -> Vector(  //classes
      "klass           " -> "en mall för att skapa flera instanser av samma typ",
      "instans         " -> "upplaga av ett objekt med eget tillståndsminne",
      "konstruktor     " -> "skapar instans, allokerar plats för tillståndsminne",
      "klassparameter  " -> "ge argument vid konstruktion, initialisera tillstånd",
      "fabriksmetod    " -> "hjälpfunktion för att anropa konstruktor",
      "referenslikhet  " -> "instanser anses olika även om de har samma tillstånd",
      "innehållslikhet " -> "olika instanser anses lika om de har samma tillstånd",
      "case-klass      " -> "slipper skriva new; automatisk innehållslikhet",
      "kompanjonsobjekt" -> "ser privata medlemmar i klassen med samma namn",
      "" -> ""
    ).filter(_._1.trim.nonEmpty),

    "quiz-w06-concepts" -> Vector(  //sequences
      "samlingsbibliotek" -> "många färdiga datastrukturer med olika egenskaper",
      "sekvenssamling   " -> "noll el. flera element av samma typ i viss ordning",
      "sekvensalgoritm  " -> "lösning på problem som drar nytta av sekvenser",
      "ordning          " -> "beskriver hur element av en viss typ ska ordnas",
      "sortering        " -> "algoritm som ordnar element i en viss ordning",
      "söking           " -> "algoritm som leta upp element enligt sökkriterium",
      "registrering     " -> "algoritm som räknar element med vissa egenskaper",
      "varargs          " -> "variabelt antal argument, asterisk efter parametertyp "
    ).filter(_._1.trim.nonEmpty),

    "quiz-w07-concepts" -> Vector(  //search, sets, maps
      "linjärsökning      " -> "leta i sekvens tills sökkriteriet är uppfyllt",
      "tidskomplexitet    " -> "hur exekveringstiden växer med problemstorleken",
      "minneskomplexitet  " -> "hur minnesåtgången växer med problemstorleken",
      "mängd              " -> "unika element, kan snabbt se om element finns",
      "nyckel-värde-tabell" -> "för att snabbt hitta tillhörande värde",
      "nyckelmängd        " -> "unika identifierare, associerade med ett enda värde",
      "persistens         " -> "egenskapen att finnas kvar efter programmets avslut",
      "serialisera        " -> "koda objekt till avkodningsbar sekvens av symboler",
      "de-serialisera     " -> "avkoda symbolsekvens och återskapa objekt i minnet",
      "" -> ""
    ).filter(_._1.trim.nonEmpty),


    "" -> Vector()
  ).filter(_._1.trim.nonEmpty)
}
