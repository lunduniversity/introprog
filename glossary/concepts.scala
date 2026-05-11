object explain:
  case class Concept(sv: String, en: String, svShortExplanation: String, enShortExplanation: String)
  object Concept:
    lazy val allConcepts: Vector[Concept] = Vector(
      Abstract, AbstractClass, AbstractMember, Algorithm, AnonymousClass, AnonymousFunction,
      Array, Assignment, Attribute, BaseType, Block, Boolean, CallByName, CallByValue,
      CaseClass, Class, ClassParameter, Collection, CollectionLibrary, Column, ColumnVector,
      CompanionObject, Compile, CompileError, CompileFromFiles, Constructor, DataStructure,
      DefaultArgument, Deserialize, DotNotation, DynamicBinding, Element, Export, Expression,
      FactoryMethod, FloatingPoint, ForStatement, Function, FunctionBody, FunctionHeader,
      Generic, Getter, Implementation, Import, Instance, Key, KeyValueTable, LazyInitialization,
      LinearSearch, LinearSearchAlgorithm, Literal, MapOperation, Mapping, Matrix, Member,
      MemoryComplexity, Method, Mixin, Module, NamedArguments, NameShadowing, Namespace,
      New, Null, Object, Ordering, Overloading, OverriddenMember, Package, ParameterList,
      Persistence, Polymorphism, Predicate, Private, Procedure, ProgramArgument, ProtectedMember,
      PureFunction, RandomSeed, Range, RecursiveFunction, ReferenceEquality, ReferenceType,
      Registration, RowVector, RuntimeError, RuntimeType, Script, SealedType, Search, Sequence,
      SequenceAlgorithm, SequenceCollection, Serialize, Set, Setter, SingletonObject, Sorting,
      StackTrace, Statement, StringType, StructuralEquality, Subtype, Supertype, TimeComplexity,
      Trait, Type, TypeAlias, TypeArgument, TypeInference, UniformAccess, ValueType, Vector,
      WhileStatement, Yield,
    )

    val Abstract = Concept(
      sv = "abstrahera",
      en = "abstract",
      svShortExplanation = "att införa nya begrepp som förenklar kodningen",
      enShortExplanation = "to introduce new concepts that simplify coding",
    )
    val AbstractClass = Concept(
      sv = "abstrakt klass",
      en = "abstract class",
      svShortExplanation = "kan ha parametrar, kan ej instansieras, kan ej mixas in",
      enShortExplanation = "can have parameters, cannot be instantiated, cannot be mixed in",
    )
    val AbstractMember = Concept(
      sv = "abstrakt medlem",
      en = "abstract member",
      svShortExplanation = "saknar implementation",
      enShortExplanation = "lacks implementation",
    )
    val Algorithm = Concept(
      sv = "algoritm",
      en = "algorithm",
      svShortExplanation = "stegvis beskrivning av en lösning på ett problem",
      enShortExplanation = "step-by-step description of a solution to a problem",
    )
    val AnonymousClass = Concept(
      sv = "anonym klass",
      en = "anonymous class",
      svShortExplanation = "klass utan namn, utvidgad med extra implementation",
      enShortExplanation = "class without a name, extended with extra implementation",
    )
    val AnonymousFunction = Concept(
      sv = "anonym funktion",
      en = "anonymous function",
      svShortExplanation = "funktion utan namn; kallas även lambda",
      enShortExplanation = "function without a name; also called lambda",
    )
    val Array = Concept(
      sv = "Array",
      en = "Array",
      svShortExplanation = "en förändringsbar, indexerbar sekvenssamling",
      enShortExplanation = "a mutable, indexable sequence collection",
    )
    val Assignment = Concept(
      sv = "tilldelning",
      en = "assignment",
      svShortExplanation = "för att ändra en variabels värde",
      enShortExplanation = "to change the value of a variable",
    )
    val Attribute = Concept(
      sv = "attribut",
      en = "attribute",
      svShortExplanation = "variabel som utgör (del av) ett objekts tillstånd",
      enShortExplanation = "variable that constitutes (part of) an object's state",
    )
    val BaseType = Concept(
      sv = "bastyp",
      en = "base type",
      svShortExplanation = "den mest generella typen i en arvshierarki",
      enShortExplanation = "the most general type in an inheritance hierarchy",
    )
    val Block = Concept(
      sv = "block",
      en = "block",
      svShortExplanation = "kan ha lokala namn; sista raden ger värdet",
      enShortExplanation = "can have local names; last line gives the value",
    )
    val Boolean = Concept(
      sv = "boolesk",
      en = "boolean",
      svShortExplanation = "antingen sann eller falsk",
      enShortExplanation = "either true or false",
    )
    val CallByName = Concept(
      sv = "namnanrop",
      en = "call by name",
      svShortExplanation = "fördröjd evaluering av argument",
      enShortExplanation = "delayed evaluation of the argument",
    )
    val CallByValue = Concept(
      sv = "värdeanrop",
      en = "call by value",
      svShortExplanation = "argumentet evalueras innan anrop",
      enShortExplanation = "the argument is evaluated before the call",
    )
    val CaseClass = Concept(
      sv = "case-klass",
      en = "case class",
      svShortExplanation = "slipper skriva new; automatisk innehållslikhet",
      enShortExplanation = "no need to write new; automatic structural equality",
    )
    val Class = Concept(
      sv = "klass",
      en = "class",
      svShortExplanation = "en mall för att skapa flera instanser av samma typ",
      enShortExplanation = "a template for creating multiple instances of the same type",
    )
    val ClassParameter = Concept(
      sv = "klassparameter",
      en = "class parameter",
      svShortExplanation = "binds till argument som ges vid konstruktion",
      enShortExplanation = "bound to the argument given at construction time",
    )
    val Collection = Concept(
      sv = "samling",
      en = "collection",
      svShortExplanation = "datastruktur med element av samma typ",
      enShortExplanation = "data structure with elements of the same type",
    )
    val CollectionLibrary = Concept(
      sv = "samlingsbibliotek",
      en = "collection library",
      svShortExplanation = "många färdiga samlingar med olika egenskaper",
      enShortExplanation = "many ready-made collections with different properties",
    )
    val Column = Concept(
      sv = "kolonn",
      en = "column",
      svShortExplanation = "annat ord för kolumn",
      enShortExplanation = "another word for column",
    )
    val ColumnVector = Concept(
      sv = "kolumnvektor",
      en = "column vector",
      svShortExplanation = "matris av dimension $m\\times{}1$ med $m$ vertikala värden",
      enShortExplanation = "matrix of dimension $m\\times{}1$ with $m$ vertical values",
    )
    val CompanionObject = Concept(
      sv = "kompanjonsobjekt",
      en = "companion object",
      svShortExplanation = "ser privata medlemmar i klass med samma namn",
      enShortExplanation = "can access private members of the class with the same name",
    )
    val Compile = Concept(
      sv = "kompilera",
      en = "compile",
      svShortExplanation = "att översätta kod till exekverbar form",
      enShortExplanation = "to translate code into executable form",
    )
    val CompileError = Concept(
      sv = "kompileringsfel",
      en = "compile error",
      svShortExplanation = "kan inträffa innan exekveringen startat",
      enShortExplanation = "can occur before execution has started",
    )
    val CompileFromFiles = Concept(
      sv = "kompilera",
      en = "compile",
      svShortExplanation = "maskinkod skapas ur en eller flera källkodsfiler",
      enShortExplanation = "machine code is created from one or more source code files",
    )
    val Constructor = Concept(
      sv = "konstruktor",
      en = "constructor",
      svShortExplanation = "skapar instans, allokerar plats för tillståndsminne",
      enShortExplanation = "creates an instance and allocates space for state memory",
    )
    val DataStructure = Concept(
      sv = "datastruktur",
      en = "data structure",
      svShortExplanation = "många olika element i en helhet; elementvis åtkomst",
      enShortExplanation = "many different elements as a whole; element-wise access",
    )
    val DefaultArgument = Concept(
      sv = "defaultargument",
      en = "default argument",
      svShortExplanation = "gör att argument kan utelämnas",
      enShortExplanation = "allows arguments to be omitted",
    )
    val Deserialize = Concept(
      sv = "de-serialisera",
      en = "deserialize",
      svShortExplanation = "avkoda symbolsekvens och återskapa objekt i minnet",
      enShortExplanation = "decode a symbol sequence and recreate objects in memory",
    )
    val DotNotation = Concept(
      sv = "punktnotation",
      en = "dot notation",
      svShortExplanation = "används för att komma åt icke-privata delar",
      enShortExplanation = "used to access non-private members",
    )
    val DynamicBinding = Concept(
      sv = "dynamisk bindning",
      en = "dynamic binding",
      svShortExplanation = "körtidstypen avgör vilken metod som körs",
      enShortExplanation = "the runtime type determines which method is called",
    )
    val Element = Concept(
      sv = "element",
      en = "element",
      svShortExplanation = "objekt i en datastruktur",
      enShortExplanation = "object in a data structure",
    )
    val Export = Concept(
      sv = "export",
      en = "export",
      svShortExplanation = "gör namn synligt utåt som medlem i detta objekt",
      enShortExplanation = "makes a name visible externally as a member of this object",
    )
    val Expression = Concept(
      sv = "uttryck",
      en = "expression",
      svShortExplanation = "kombinerar värden och funktioner till ett nytt värde",
      enShortExplanation = "combines values and functions to produce a new value",
    )
    val FactoryMethod = Concept(
      sv = "fabriksmetod",
      en = "factory method",
      svShortExplanation = "hjälpfunktion för indirekt konstruktion",
      enShortExplanation = "helper function for indirect construction",
    )
    val FloatingPoint = Concept(
      sv = "flyttal",
      en = "floating-point number",
      svShortExplanation = "decimaltal med begränsad noggrannhet",
      enShortExplanation = "decimal number with limited precision",
    )
    val ForStatement = Concept(
      sv = "for-sats",
      en = "for statement",
      svShortExplanation = "bra då antalet repetitioner är bestämt i förväg",
      enShortExplanation = "good when the number of repetitions is known in advance",
    )
    val Function = Concept(
      sv = "funktion",
      en = "function",
      svShortExplanation = "vid anrop beräknas ett returvärde",
      enShortExplanation = "when called, a return value is computed",
    )
    val FunctionBody = Concept(
      sv = "funktionskropp",
      en = "function body",
      svShortExplanation = "koden som exekveras vid funktionsanrop",
      enShortExplanation = "the code executed when the function is called",
    )
    val FunctionHeader = Concept(
      sv = "funktionshuvud",
      en = "function header",
      svShortExplanation = "har parameterlista och eventuellt en returtyp",
      enShortExplanation = "has a parameter list and optionally a return type",
    )
    val Generic = Concept(
      sv = "generisk",
      en = "generic",
      svShortExplanation = "har abstrakt typparameter, typen är generell",
      enShortExplanation = "has an abstract type parameter; the type is general",
    )
    val Getter = Concept(
      sv = "getter",
      en = "getter",
      svShortExplanation = "indirekt åtkomst av attributvärde",
      enShortExplanation = "indirect access to an attribute value",
    )
    val Implementation = Concept(
      sv = "implementation",
      en = "implementation",
      svShortExplanation = "en specifik realisering av en algoritm",
      enShortExplanation = "a specific realization of an algorithm",
    )
    val Import = Concept(
      sv = "import",
      en = "import",
      svShortExplanation = "gör namn tillgängligt lokalt utan att hela sökvägen behövs",
      enShortExplanation = "makes a name locally available without needing the full path",
    )
    val Instance = Concept(
      sv = "instans",
      en = "instance",
      svShortExplanation = "upplaga av ett objekt med eget tillståndsminne",
      enShortExplanation = "a copy of an object with its own state memory",
    )
    val Key = Concept(
      sv = "nyckel",
      en = "key",
      svShortExplanation = "en unik identifierare",
      enShortExplanation = "a unique identifier",
    )
    val KeyValueTable = Concept(
      sv = "nyckel-värde-tabell",
      en = "key-value table",
      svShortExplanation = "oordnad samling av mappningar med unika nycklar",
      enShortExplanation = "unordered collection of mappings with unique keys",
    )
    val LazyInitialization = Concept(
      sv = "lat initialisering",
      en = "lazy initialization",
      svShortExplanation = "allokering sker först när namnet refereras",
      enShortExplanation = "allocation occurs only when the name is first referenced",
    )
    val LinearSearch = Concept(
      sv = "linjärsöka",
      en = "linear search",
      svShortExplanation = "leta i sekvens tills sökkriteriet är uppfyllt",
      enShortExplanation = "search a sequence until the search criterion is met",
    )
    val LinearSearchAlgorithm = Concept(
      sv = "linjärsökning",
      en = "linear search algorithm",
      svShortExplanation = "sökalgoritm som letar i sekvens tills element hittas",
      enShortExplanation = "search algorithm that scans a sequence until the element is found",
    )
    val Literal = Concept(
      sv = "litteral",
      en = "literal",
      svShortExplanation = "anger ett specifikt datavärde",
      enShortExplanation = "specifies a specific data value",
    )
    val MapOperation = Concept(
      sv = "map",
      en = "map",
      svShortExplanation = "applicerar en funktion på varje element i en samling",
      enShortExplanation = "applies a function to each element in a collection",
    )
    val Mapping = Concept(
      sv = "mappning",
      en = "mapping",
      svShortExplanation = "nyckel -> värde",
      enShortExplanation = "key -> value",
    )
    val Matrix = Concept(
      sv = "matris",
      en = "matrix",
      svShortExplanation = "indexerbar datastruktur i två dimensioner",
      enShortExplanation = "indexable data structure in two dimensions",
    )
    val Member = Concept(
      sv = "medlem",
      en = "member",
      svShortExplanation = "tillhör ett objekt; nås med punktnotation om synlig",
      enShortExplanation = "belongs to an object; accessed with dot notation if visible",
    )
    val MemoryComplexity = Concept(
      sv = "minneskomplexitet",
      en = "memory complexity",
      svShortExplanation = "hur minnesåtgången växer med problemstorleken",
      enShortExplanation = "how memory usage grows with the size of the problem",
    )
    val Method = Concept(
      sv = "metod",
      en = "method",
      svShortExplanation = "funktion som är medlem av ett objekt",
      enShortExplanation = "function that is a member of an object",
    )
    val Mixin = Concept(
      sv = "inmixning",
      en = "mixin",
      svShortExplanation = "tillföra egenskaper med with och en trait",
      enShortExplanation = "adding features using with and a trait",
    )
    val Module = Concept(
      sv = "modul",
      en = "module",
      svShortExplanation = "kodenhet med abstraktioner som kan återanvändas",
      enShortExplanation = "code unit with abstractions that can be reused",
    )
    val NamedArguments = Concept(
      sv = "namngivna argument",
      en = "named arguments",
      svShortExplanation = "gör att argument kan ges i valfri ordning",
      enShortExplanation = "allows arguments to be given in any order",
    )
    val NameShadowing = Concept(
      sv = "namnskuggning",
      en = "name shadowing",
      svShortExplanation = "lokalt namn döljer samma namn i omgivande block",
      enShortExplanation = "a local name hides the same name in the surrounding block",
    )
    val Namespace = Concept(
      sv = "namnrymd",
      en = "namespace",
      svShortExplanation = "omgivning där är alla namn är unika",
      enShortExplanation = "environment where all names are unique",
    )
    val New = Concept(
      sv = "new",
      en = "new",
      svShortExplanation = "nyckelord vid direkt instansiering av klass",
      enShortExplanation = "keyword for direct instantiation of a class",
    )
    val Null = Concept(
      sv = "null",
      en = "null",
      svShortExplanation = "ett värde som ej refererar till någon instans",
      enShortExplanation = "a value that does not refer to any instance",
    )
    val Object = Concept(
      sv = "objekt",
      en = "object",
      svShortExplanation = "samlar variabler och funktioner",
      enShortExplanation = "groups variables and functions together",
    )
    val Ordering = Concept(
      sv = "ordning",
      en = "ordering",
      svShortExplanation = "definierar hur element av en viss typ ska ordnas",
      enShortExplanation = "defines how elements of a certain type should be ordered",
    )
    val Overloading = Concept(
      sv = "överlagring",
      en = "overloading",
      svShortExplanation = "metoder med samma namn men olika parametertyper",
      enShortExplanation = "methods with the same name but different parameter types",
    )
    val OverriddenMember = Concept(
      sv = "överskuggad medlem",
      en = "overridden member",
      svShortExplanation = "medlem i subtyp ersätter medlem i supertyp",
      enShortExplanation = "member in a subtype replaces a member in the supertype",
    )
    val Package = Concept(
      sv = "paket",
      en = "package",
      svShortExplanation = "modul som skapar namnrymd; maskinkod får egen katalog",
      enShortExplanation = "module that creates a namespace; machine code gets its own directory",
    )
    val ParameterList = Concept(
      sv = "parameterlista",
      en = "parameter list",
      svShortExplanation = "beskriver namn och typ på parametrar",
      enShortExplanation = "describes the name and type of parameters",
    )
    val Persistence = Concept(
      sv = "persistens",
      en = "persistence",
      svShortExplanation = "egenskapen att finnas kvar efter programmets avslut",
      enShortExplanation = "the property of surviving after the program has ended",
    )
    val Polymorphism = Concept(
      sv = "polymorfism",
      en = "polymorphism",
      svShortExplanation = "kan ha många former, t.ex. en av flera subtyper",
      enShortExplanation = "can have many forms, e.g. one of several subtypes",
    )
    val Predicate = Concept(
      sv = "predikat",
      en = "predicate",
      svShortExplanation = "en funktion som ger ett booleskt värde",
      enShortExplanation = "a function that returns a boolean value",
    )
    val Private = Concept(
      sv = "privat",
      en = "private",
      svShortExplanation = "modifierar synligheten av en objektmedlem",
      enShortExplanation = "modifies the visibility of an object member",
    )
    val Procedure = Concept(
      sv = "procedur",
      en = "procedure",
      svShortExplanation = "vid anrop sker (sido)effekt; returvärdet är tomt",
      enShortExplanation = "when called, a (side) effect occurs; the return value is empty",
    )
    val ProgramArgument = Concept(
      sv = "programargument",
      en = "program argument",
      svShortExplanation = "kan överföras via parametern args till main",
      enShortExplanation = "can be passed via the args parameter to main",
    )
    val ProtectedMember = Concept(
      sv = "skyddad medlem",
      en = "protected member",
      svShortExplanation = "är endast synlig i subtyper",
      enShortExplanation = "visible only in subtypes",
    )
    val PureFunction = Concept(
      sv = "äkta funktion",
      en = "pure function",
      svShortExplanation = "ger alltid samma resultat om samma argument",
      enShortExplanation = "always gives the same result for the same arguments",
    )
    val RandomSeed = Concept(
      sv = "slumptalsfrö",
      en = "random seed",
      svShortExplanation = "ger återupprepningsbar sekvens av pseudoslumptal",
      enShortExplanation = "produces a reproducible sequence of pseudo-random numbers",
    )
    val Range = Concept(
      sv = "Range",
      en = "Range",
      svShortExplanation = "en samling som representerar ett intervall av heltal",
      enShortExplanation = "a collection representing an interval of integers",
    )
    val RecursiveFunction = Concept(
      sv = "rekursiv funktion",
      en = "recursive function",
      svShortExplanation = "en funktion som anropar sig själv",
      enShortExplanation = "a function that calls itself",
    )
    val ReferenceEquality = Concept(
      sv = "referenslikhet",
      en = "reference equality",
      svShortExplanation = "instanser anses olika även om tillstånden är lika",
      enShortExplanation = "instances are considered different even if their states are equal",
    )
    val ReferenceType = Concept(
      sv = "referenstyp",
      en = "reference type",
      svShortExplanation = "har supertypen AnyRef, allokeras i heapen via referens",
      enShortExplanation = "has supertype AnyRef, allocated on the heap via reference",
    )
    val Registration = Concept(
      sv = "registrering",
      en = "counting",
      svShortExplanation = "algoritm som räknar element med vissa egenskaper",
      enShortExplanation = "algorithm that counts elements with certain properties",
    )
    val RowVector = Concept(
      sv = "radvektor",
      en = "row vector",
      svShortExplanation = "matris av dimension $1\\times{}m$ med $m$ horisontella värden",
      enShortExplanation = "matrix of dimension $1\\times{}m$ with $m$ horizontal values",
    )
    val RuntimeError = Concept(
      sv = "exekveringsfel",
      en = "runtime error",
      svShortExplanation = "kan inträffa medan programmet kör",
      enShortExplanation = "can occur while the program is running",
    )
    val RuntimeType = Concept(
      sv = "körtidstyp",
      en = "runtime type",
      svShortExplanation = "kan vara mer specifik än den statiska typen",
      enShortExplanation = "can be more specific than the static type",
    )
    val Script = Concept(
      sv = "skript",
      en = "script",
      svShortExplanation = "ensam kodfil, huvudprogram behövs ej",
      enShortExplanation = "a single code file; no main program needed",
    )
    val SealedType = Concept(
      sv = "förseglad typ",
      en = "sealed type",
      svShortExplanation = "subtypning utanför denna kodfil är förhindrad",
      enShortExplanation = "subtyping outside this code file is prevented",
    )
    val Search = Concept(
      sv = "sökning",
      en = "search",
      svShortExplanation = "algoritm som letar upp element enligt sökkriterium",
      enShortExplanation = "algorithm that finds elements according to a search criterion",
    )
    val Sequence = Concept(
      sv = "sekvens(samling)",
      en = "sequence (collection)",
      svShortExplanation = "noll el. flera element av samma typ i viss ordning",
      enShortExplanation = "zero or more elements of the same type in a certain order",
    )
    val SequenceAlgorithm = Concept(
      sv = "sekvensalgoritm",
      en = "sequence algorithm",
      svShortExplanation = "lösning på problem som drar nytta av sekvenssamling",
      enShortExplanation = "solution to a problem that benefits from a sequence collection",
    )
    val SequenceCollection = Concept(
      sv = "sekvenssamling",
      en = "sequence collection",
      svShortExplanation = "datastruktur med element i en viss ordning",
      enShortExplanation = "data structure with elements in a certain order",
    )
    val Serialize = Concept(
      sv = "serialisera",
      en = "serialize",
      svShortExplanation = "koda objekt till avkodningsbar sekvens av symboler",
      enShortExplanation = "encode an object into a decodable sequence of symbols",
    )
    val Set = Concept(
      sv = "mängd",
      en = "set",
      svShortExplanation = "oordnad samling med unika element",
      enShortExplanation = "unordered collection with unique elements",
    )
    val Setter = Concept(
      sv = "setter",
      en = "setter",
      svShortExplanation = "indirekt tilldelning av attributvärde",
      enShortExplanation = "indirect assignment of an attribute value",
    )
    val SingletonObject = Concept(
      sv = "singelobjekt",
      en = "singleton object",
      svShortExplanation = "modul som kan ha tillstånd; finns i en enda upplaga",
      enShortExplanation = "module that can have state; exists as a single instance",
    )
    val Sorting = Concept(
      sv = "sortering",
      en = "sorting",
      svShortExplanation = "algoritm som ordnar element i en viss ordning",
      enShortExplanation = "algorithm that arranges elements in a certain order",
    )
    val StackTrace = Concept(
      sv = "stack trace",
      en = "stack trace",
      svShortExplanation = "lista anropskedja vid körtidsfel",
      enShortExplanation = "lists the call chain when a runtime error occurs",
    )
    val Statement = Concept(
      sv = "sats",
      en = "statement",
      svShortExplanation = "en kodrad som gör något; kan särskiljas med semikolon",
      enShortExplanation = "a line of code that does something; can be separated by semicolons",
    )
    val StringType = Concept(
      sv = "sträng",
      en = "string",
      svShortExplanation = "en sekvens av tecken",
      enShortExplanation = "a sequence of characters",
    )
    val StructuralEquality = Concept(
      sv = "innehållslikhet",
      en = "structural equality",
      svShortExplanation = "instanser anses lika om de har samma tillstånd",
      enShortExplanation = "instances are considered equal if they have the same state",
    )
    val Subtype = Concept(
      sv = "subtyp",
      en = "subtype",
      svShortExplanation = "en typ som är mer specifik",
      enShortExplanation = "a type that is more specific",
    )
    val Supertype = Concept(
      sv = "supertyp",
      en = "supertype",
      svShortExplanation = "en typ som är mer generell",
      enShortExplanation = "a type that is more general",
    )
    val TimeComplexity = Concept(
      sv = "tidskomplexitet",
      en = "time complexity",
      svShortExplanation = "hur exekveringstiden växer med problemstorleken",
      enShortExplanation = "how execution time grows with the size of the problem",
    )
    val Trait = Concept(
      sv = "trait",
      en = "trait",
      svShortExplanation = "är abstrakt, kan mixas in, kan ha parametrar",
      enShortExplanation = "is abstract, can be mixed in, can have parameters",
    )
    val Type = Concept(
      sv = "typ",
      en = "type",
      svShortExplanation = "beskriver vad data kan användas till",
      enShortExplanation = "describes what data can be used for",
    )
    val TypeAlias = Concept(
      sv = "typalias",
      en = "type alias",
      svShortExplanation = "alternativt namn på typ som ofta ökar läsbarheten",
      enShortExplanation = "alternative name for a type that often improves readability",
    )
    val TypeArgument = Concept(
      sv = "typargument",
      en = "type argument",
      svShortExplanation = "konkret typ, binds till typparameter vid kompilering",
      enShortExplanation = "concrete type, bound to a type parameter at compile time",
    )
    val TypeInference = Concept(
      sv = "typhärledning",
      en = "type inference",
      svShortExplanation = "kompilatorn beräknar typ ur sammanhanget",
      enShortExplanation = "the compiler infers the type from context",
    )
    val UniformAccess = Concept(
      sv = "enhetlig access",
      en = "uniform access",
      svShortExplanation = "ändring mellan def och val påverkar ej användning",
      enShortExplanation = "switching between def and val does not affect usage",
    )
    val ValueType = Concept(
      sv = "värdetyp",
      en = "value type",
      svShortExplanation = "har supertypen AnyVal, lagras direkt på stacken",
      enShortExplanation = "has supertype AnyVal, stored directly on the stack",
    )
    val Vector = Concept(
      sv = "Vector",
      en = "Vector",
      svShortExplanation = "en oföränderlig, indexerbar sekvenssamling",
      enShortExplanation = "an immutable, indexable sequence collection",
    )
    val WhileStatement = Concept(
      sv = "while-sats",
      en = "while statement",
      svShortExplanation = "bra då antalet repetitioner ej är bestämt i förväg",
      enShortExplanation = "good when the number of repetitions is not known in advance",
    )
    val Yield = Concept(
      sv = "yield",
      en = "yield",
      svShortExplanation = "används i for-uttryck för att skapa ny samling",
      enShortExplanation = "used in for-expressions to create a new collection",
    )
