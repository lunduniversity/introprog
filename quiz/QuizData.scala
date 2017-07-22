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
      "kompilerad    " -> "maskinkod sparad och kan köras igen utan kompilering",
      "skript        " -> "maskinkod sparas ej utan skapas före varje körning",
      "objekt        " -> "samlar variabler och funktioner",
      "main          " -> "där exekveringen av kompilerad app startar",
      "samling       " -> "datastruktur som innehåller många element av samma typ",
      "sekvenssamling" -> "datastruktur med element i en viss ordning",
      "Array         " -> "en förändringsbar, indexerbar sekvenssamling",
      "Vector        " -> "en oföränderlig, indexerbar sekvenssamling",
      "Range         " -> "en samling som representerar ett intervall av heltal",
      "yield         " -> "används i for-uttryck för att skapa ny samling",
      "map           " -> "applicerar en funktion på varje element i en samling",
      "algoritm      " -> "stegvis beskrivning av en lösning på ett problem",
      "implementation" -> "en specifik realisering av en algoritm",
      "              " -> "",
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
      "attribut          " -> "en variabel som utgör (del av) ett objekts tillstånd",
      "metod             " -> "en funktion som är medlem av ett objekt",
      "privat            " -> "modifierar synligheten av en objektmedlem",
      "överlagring       " -> "?",
      "namnrymd          " -> "en mängd av namn skapad för att förhindra krockar",
      "         " -> "",
      "" -> ""
    ).filter(_._1.trim.nonEmpty),

    "quiz-w05-concepts" -> Vector(  //classes
      "klass          " -> "en mall för att skapa flera objektinstanser av samma typ",
      "instans        " -> "ett objektupplaga med eget minne för attributvärden",
      "konstruktion   " -> "skapandet av ett objekt, kräver allokering av minne",
      "klassparameter " -> "?",
      "fabriksmetod   " -> "?",
      "referenslikhet " -> "?",
      "innehållslikhet" -> "?",
      "case-klass     " -> "?",
      "" -> ""
    ).filter(_._1.trim.nonEmpty),

    "quiz-w06-concepts" -> Vector(  //sequences
      "         " -> "",
      "         " -> "",
      "         " -> "",
      "         " -> "",
      "" -> ""
    ).filter(_._1.trim.nonEmpty),

    "quiz-w07-concepts" -> Vector(  //sets-maps
      "         " -> "",
      "         " -> "",
      "         " -> "",
      "         " -> "",
      "" -> ""
    ).filter(_._1.trim.nonEmpty),


    "" -> Vector()
  ).filter(_._1.trim.nonEmpty)
}
