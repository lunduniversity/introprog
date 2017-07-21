package genquiz  // see types in package object in TypesAndUtils.scala

object QuizData {  // to generate tables for a concept connection quizes in latex

  def concepts(q: QuizName): Concepts =
    quizes(q).zipWithIndex.map { case ((k,v), i) => ((k.trim,i), (v.trim,i)) }

  def quizNames: Vector[QuizName] = quizes.keys.toVector

  val quizes = Map[QuizName, Vector[Concept]](

    "quiz-w01-concepts" -> Vector(
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

    "quiz-w02-concepts" -> Vector(
      "kompilerad  " -> "maskinkod sparad och kan köras igen utan kompilering",
      "skript      " -> "maskinkod sparas ej utan skapas före varje körning",
      "paket       " -> "skapar namnrymd; maskinkod placeras i egen katalog",
      "modul       " -> "koddelar som hör ihop, exempel: singelobjekt, paket",
      "singelobjekt" -> "samlar tillstånd och funktioner i en enda instans",
      "block       " -> "kan ha lokala namn; sista uttrycket blir blockets värde",
      "import      " -> "gör namn tillgängligt utan att hela sökvägen behövs",
      "Array       " -> "en förändringsbar, indexerbar samling",
      "Vector      " -> "en oföränderlig, indexerbar samling",
      "Range       " -> "en samling som representerar ett intervall av heltal",
      "            " -> "",
      "" -> ""
    ).filter(_._1.trim.nonEmpty)

  )
}
