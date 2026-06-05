package glossary

object explain:
  lazy val allConcepts: Seq[Concept] = 
    import Concept.*
    scala.collection.immutable.ArraySeq(
      Abstract, AbstractClass, AbstractMember, Algorithm, AnonymousClass, AnonymousFunction,
      Array, Assignment, Attribute, 
      BaseType, Block, Boolean, 
      CallByName, CallByValue,
      CaseClass, Class, ClassParameter, Collection, CollectionLibrary, Column, ColumnVector,
      CompanionObject, Compile, Compiler, CompileError, Computer, ControlStructure, Constructor, 
      DataStructure,
      DefaultArgument, Deserialize, DotNotation, DynamicBinding, 
      Enumeration, Element, Export, Expression,
      FactoryMethod, FloatingPoint, ForStatement, Function, FunctionBody, FunctionHeader,
      Generic, Getter, 
      Implementation, Import, Inheritance, Instance, 
      Key, KeyValueTable, 
      LazyInitialization, LinearSearch, LinearSearchAlgorithm, Literal, 
      MapOperation, Mapping, Matrix, Member, MemoryComplexity, Method, Mixin, Module, 
      NamedArguments, NameShadowing, Namespace, New, Null, 
      Object, Ordering, Overloading, OverriddenMember, 
      Package, ParameterList, Persistence, Polymorphism, Predicate, Private, Procedure, ProgramArgument, ProtectedMember,
      PureFunction, 
      RandomSeed, Range, RecursiveFunction, ReferenceEquality, ReferenceType,
      Registration, RowVector, RuntimeError, RuntimeType, Script, SealedType, Search, Sequence,
      SequenceAlgorithm, SequenceCollection, Serialize, Set, Setter, SingletonObject, Sorting,
      StackTrace, Statement, StringType, StringInterpolator, StructuralEquality, Subtype, Supertype, 
      TimeComplexity, Trait, Type, TypeAlias, TypeArgument, TypeInference, 
      UniformAccess, 
      ValueType, Variable, Vector,
      WhileStatement, Yield,
    ).sortBy(_.en)

  case class Concept(sv: String, en: String, svShortExplanation: String, enShortExplanation: String, svLongExplanation: String, enLongExplanation: String)

  object Concept:

    val Abstract = Concept(
      sv = "abstrahera",
      en = "abstract",
      svShortExplanation = "att införa nya begrepp som förenklar kodningen",
      enShortExplanation = "to introduce new concepts that simplify coding",
      svLongExplanation = "Att abstrahera innebär att man identifierar gemensamma egenskaper och skapar nya begrepp som döljer detaljer och förenklar kodningen; abstraktioner gör kod mer återanvändbar och lättare att resonera om.",
      enLongExplanation = "To abstract means to identify common properties and introduce new concepts that hide details and simplify coding; abstractions make code more reusable and easier to reason about.",
    )
    val AbstractClass = Concept(
      sv = "abstrakt klass",
      en = "abstract class",
      svShortExplanation = "kan ha parametrar, kan ej instansieras, kan ej mixas in",
      enShortExplanation = "can have parameters, cannot be instantiated, cannot be mixed in",
      svLongExplanation = "En abstrakt klass kan ha konstruktorparametrar och konkreta medlemmar, men kan inte instansieras direkt och kan inte mixas in; den används som basklass i en arvshierarki där subklasser implementerar de abstrakta medlemmarna.",
      enLongExplanation = "An abstract class can have constructor parameters and concrete members, but cannot be instantiated directly and cannot be mixed in; it is used as a base class in an inheritance hierarchy where subclasses implement the abstract members.",
    )
    val AbstractMember = Concept(
      sv = "abstrakt medlem",
      en = "abstract member",
      svShortExplanation = "saknar implementation",
      enShortExplanation = "lacks implementation",
      svLongExplanation = "En abstrakt medlem deklareras utan implementation i en klass eller trait och måste implementeras av konkreta subtyper innan de kan instansieras; abstrakta medlemmar specificerar ett kontrakt som subtyper är skyldiga att uppfylla.",
      enLongExplanation = "An abstract member is declared without implementation in a class or trait and must be implemented by concrete subtypes before they can be instantiated; abstract members specify a contract that subtypes are obliged to fulfill.",
    )
    val Algorithm = Concept(
      sv = "algoritm",
      en = "algorithm",
      svShortExplanation = "stegvis beskrivning av en lösning på ett problem",
      enShortExplanation = "step-by-step description of a solution to a problem",
      svLongExplanation = "En algoritm är en ändlig, väldefinierad sekvens av steg som löser ett givet problem och terminerar med ett korrekt resultat; en algoritm kan implementeras i kod oberoende av programmeringsspråk och analyseras med avseende på korrekthet, tids- och minneskomplexitet.",
      enLongExplanation = "An algorithm is a finite, well-defined sequence of steps that solves a given problem and terminates with a correct result; an algorithm can be implemented in code regardless of programming language and analyzed with respect to correctness, time, and memory complexity.",
    )
    val AnonymousClass = Concept(
      sv = "anonym klass",
      en = "anonymous class",
      svShortExplanation = "klass utan namn, utvidgad med extra implementation",
      enShortExplanation = "class without a name, extended with extra implementation",
      svLongExplanation = "En anonym klass skapas direkt vid instansiering med 'new' följt av ett trait- eller klassnamn och ett block med extra implementation; klassen ges inget eget namn och används när en engångsimplementering av ett gränssnitt behövs.",
      enLongExplanation = "An anonymous class is created directly at instantiation with 'new' followed by a trait or class name and a block with extra implementation; the class is given no name of its own and is used when a one-off implementation of an interface is needed.",
    )
    val AnonymousFunction = Concept(
      sv = "anonym funktion",
      en = "anonymous function",
      svShortExplanation = "funktion utan namn; kallas även lambda",
      enShortExplanation = "function without a name; also called lambda",
      svLongExplanation = "En anonym funktion, även kallad lambda, är en funktion utan namn som definieras inline med syntaxen '(parametrar) => uttryck'; den kan tilldelas en variabel, skickas som argument till en högre ordningens funktion, eller returneras som värde.",
      enLongExplanation = "An anonymous function, also called a lambda, is a function without a name defined inline using the syntax '(parameters) => expression'; it can be assigned to a variable, passed as an argument to a higher-order function, or returned as a value.",
    )
    val Array = Concept(
      sv = "Array",
      en = "Array",
      svShortExplanation = "en förändringsbar, indexerbar sekvenssamling",
      enShortExplanation = "a mutable, indexable sequence collection",
      svLongExplanation = "Array är en förändringsbar, indexerbar samling med fast storlek där alla element är av samma typ och nås med nollbaserat index inom hakparenteser; Array är en grundläggande datastruktur med O(1)-åtkomst men fast storlek som inte kan ändras efter skapande.",
      enLongExplanation = "Array is a mutable, indexable collection with a fixed size where all elements are of the same type and accessed using zero-based indexing with square brackets; Array is a fundamental data structure with O(1) access but a fixed size that cannot be changed after creation.",
    )
    val Assignment = Concept(
      sv = "tilldelning",
      en = "assignment",
      svShortExplanation = "för att ändra en variabels värde",
      enShortExplanation = "to change the value of a variable",
      svLongExplanation = "En tilldelning, skriven med '=', ändrar värdet på en förändringsbar variabel (var) till ett nytt värde; tilldelning är inte möjlig på en oföränderlig variabel (val) efter att den initialiserats, vilket uppmuntrar till funktionell programmeringsstil.",
      enLongExplanation = "An assignment, written with '=', changes the value of a mutable variable (var) to a new value; assignment is not possible on an immutable variable (val) after it has been initialized, which encourages a functional programming style.",
    )
    val Attribute = Concept(
      sv = "attribut",
      en = "attribute",
      svShortExplanation = "variabel som utgör (del av) ett objekts tillstånd",
      enShortExplanation = "variable that constitutes (part of) an object's state",
      svLongExplanation = "Ett attribut är en variabel definierad som del av en klass eller ett objekt och representerar en del av dess tillstånd; attribut kan vara oföränderliga (val) eller föränderliga (var), och görs ofta privata för att skydda mot godtyckliga förändringar",
      enLongExplanation = "An attribute is a variable defined as part of a class or object and represents a part of its state; attributes can be immutable (val) or mutable (var), and are often made private to protect the object from arbitrary modifications.",
    )
    val BaseType = Concept(
      sv = "bastyp",
      en = "base type",
      svShortExplanation = "den mest generella typen i en arvshierarki",
      enShortExplanation = "the most general type in an inheritance hierarchy",
      svLongExplanation = "En bastyp, även kallad superklass eller supertrait, är den mest generella typen i en arvshierarki och definierar de egenskaper och metoder som delas av alla subtyper; variabler av bastyp kan referera till instanser av vilken subtyp som helst.",
      enLongExplanation = "A base type, also called a superclass or supertrait, is the most general type in an inheritance hierarchy and defines the properties and methods shared by all subtypes; variables of the base type can refer to instances of any subtype.",
    )
    val Block = Concept(
      sv = "block",
      en = "block",
      svShortExplanation = "kan ha lokala namn; sista raden ger värdet",
      enShortExplanation = "can have local names; last line gives the value",
      svLongExplanation = "Ett block är en sekvens av satser och uttryck (i Scala 3 avgränsade av indentation) som kan innehålla lokala namnbindningar synliga endast inuti blocket; värdet av ett block är värdet av dess sista uttryck.",
      enLongExplanation = "A block is a sequence of statements and expressions (in Scala 3 delimited by indentation) that can contain local name bindings visible only inside the block; the value of a block is the value of its last expression.",
    )
    val Boolean = Concept(
      sv = "boolesk",
      en = "boolean",
      svShortExplanation = "antingen sann eller falsk",
      enShortExplanation = "either true or false",
      svLongExplanation = "En boolesk typ kan bara anta värdena 'true' (sann) eller 'false' (falsk) och används i villkorliga uttryck och logiska operationer med operatorerna && (och), || (eller) och ! (negation); booleska uttryck styr programmets kontrollflöde.",
      enLongExplanation = "A boolean type can only take the values 'true' or 'false' and is used in conditional expressions and logical operations with the operators && (and), || (or), and ! (negation); boolean expressions control the program's flow of control.",
    )
    val CallByName = Concept(
      sv = "namnanrop",
      en = "call by name",
      svShortExplanation = "fördröjd evaluering av argument",
      enShortExplanation = "delayed evaluation of the argument",
      svLongExplanation = "Vid namnanrop skickas ett argument som ett fördröjt uttryck som evalueras varje gång det används inuti funktionen; detta möjliggör skapande av egna kontrollstrukturer, och deklareras med '=>' före parametertypen.",
      enLongExplanation = "With call by name, an argument is passed as a delayed expression that is evaluated each time it is used inside the function; this enables the creation of custom control structures, and is declared with '=>' before the parameter type.",
    )
    val CallByValue = Concept(
      sv = "värdeanrop",
      en = "call by value",
      svShortExplanation = "argumentet evalueras innan anrop",
      enShortExplanation = "the argument is evaluated before the call",
      svLongExplanation = "Vid värdeanrop evalueras argumentet exakt en gång innan funktionen anropas och det beräknade värdet binds till parametern; detta är standardbeteendet i Scala och de flesta andra programmeringsspråk.",
      enLongExplanation = "With call by value, the argument is evaluated exactly once before the function is called and the computed value is bound to the parameter; this is the default behavior in Scala and most other programming languages.",
    )
    val CaseClass = Concept(
      sv = "case-klass",
      en = "case class",
      svShortExplanation = "slipper skriva new; automatisk innehållslikhet",
      enShortExplanation = "no need to write new; automatic structural equality",
      svLongExplanation = "En case-klass genererar automatiskt ett kompanjonsobjekt med apply-metod (slipper 'new'), equals och hashCode baserade på fältinnehåll, en läsbar toString, en copy-metod för att skapa modifierade kopior, samt stöd för mönstermatchning med unapply.",
      enLongExplanation = "A case class automatically generates a companion object with an apply method (no need for 'new'), equals and hashCode based on field contents, a readable toString, a copy method for creating modified copies, and support for pattern matching with unapply.",
    )
    val Class = Concept(
      sv = "klass",
      en = "class",
      svShortExplanation = "en mall för att skapa flera instanser av samma typ",
      enShortExplanation = "a template for creating multiple instances of the same type",
      svLongExplanation = "En klass är en mall som beskriver struktur och beteende för objekt av en viss typ; den kan ha konstruktorparametrar, attribut och metoder, och varje instans skapad med 'new' får sitt eget tillståndsminne men delar metodimplementationerna.",
      enLongExplanation = "A class is a template that describes the structure and behavior of objects of a certain type; it can have constructor parameters, attributes, and methods, and each instance created with 'new' gets its own state memory but shares the method implementations.",
    )
    val ClassParameter = Concept(
      sv = "klassparameter",
      en = "class parameter",
      svShortExplanation = "binds till argument som ges vid konstruktion",
      enShortExplanation = "bound to the argument given at construction time",
      svLongExplanation = "En klassparameter deklareras i klassens rubrik och binds till argumentet som ges vid konstruktion; om den föregås av 'val' eller 'var' blir den automatiskt ett synligt attribut, annars är den ett privat konstruktorargument.",
      enLongExplanation = "A class parameter is declared in the class header and bound to the argument given at construction; if preceded by 'val' or 'var' it automatically becomes a visible attribute, otherwise it is a private constructor argument.",
    )
    val Collection = Concept(
      sv = "samling",
      en = "collection",
      svShortExplanation = "datastruktur med element av samma typ",
      enShortExplanation = "data structure with elements of the same type",
      svLongExplanation = "En samling är en datastruktur som innehåller ett antal element av samma typ och erbjuder operationer för att lägga till, ta bort, söka och transformera element; Scala erbjuder ett rikt samlingsbibliotek med både föränderliga och oföränderliga varianter.",
      enLongExplanation = "A collection is a data structure containing a number of elements of the same type and offers operations for adding, removing, searching, and transforming elements; Scala offers a rich collection library with both mutable and immutable variants.",
    )
    val CollectionLibrary = Concept(
      sv = "samlingsbibliotek",
      en = "collection library",
      svShortExplanation = "många färdiga samlingar med olika egenskaper",
      enShortExplanation = "many ready-made collections with different properties",
      svLongExplanation = "Scalas samlingsbibliotek innehåller ett rikt urval av samlingstyper med ett enhetligt gränssnitt uppdelat i föränderliga (scala.collection.mutable) och oföränderliga (scala.collection.immutable) varianter; de vanligaste är Vector, List, Set, Map och ArrayBuffer.",
      enLongExplanation = "Scala's collection library contains a rich selection of collection types with a uniform interface divided into mutable (scala.collection.mutable) and immutable (scala.collection.immutable) variants; the most common are Vector, List, Set, Map, and ArrayBuffer.",
    )
    val Column = Concept(
      sv = "kolonn",
      en = "column",
      svShortExplanation = "annat ord för kolumn",
      enShortExplanation = "another word for column",
      svLongExplanation = "Kolonn är ett alternativt ord för kolumn och används i samband med matriser och tabeller för att beteckna en vertikal sekvens av värden som delar samma kolumnindex.",
      enLongExplanation = "Column is an alternative word for column and is used in the context of matrices and tables to denote a vertical sequence of values sharing the same column index.",
    )
    val ColumnVector = Concept(
      sv = "kolumnvektor",
      en = "column vector",
      svShortExplanation = "matris av dimension $m\\times{}1$ med $m$ vertikala värden",
      enShortExplanation = "matrix of dimension $m\\times{}1$ with $m$ vertical values",
      svLongExplanation = "En kolumnvektor är en matris med dimensionen m×1 som innehåller m element i en enda vertikal kolumn; den används inom linjär algebra och numerisk beräkning och är ett specialfall av en matris.",
      enLongExplanation = "A column vector is a matrix of dimension m×1 containing m elements in a single vertical column; it is used in linear algebra and numerical computation and is a special case of a matrix.",
    )
    val CompanionObject = Concept(
      sv = "kompanjonsobjekt",
      en = "companion object",
      svShortExplanation = "ser privata medlemmar i klass med samma namn",
      enShortExplanation = "can access private members of the class with the same name",
      svLongExplanation = "Ett kompanjonsobjekt har samma namn som en klass, definieras i samma fil, och har ömsesidig tillgång till klassens privata medlemmar; det används ofta för fabriksmetoder, konstanter och hjälpfunktioner kopplade till klassen.",
      enLongExplanation = "A companion object has the same name as a class, is defined in the same file, and has mutual access to the class's private members; it is often used for factory methods, constants, and helper functions associated with the class.",
    )
    val Computer = Concept(
      sv = "dator",
      en = "computer",
      svShortExplanation = "en elektronisk enhet som behandlar data enligt instruktioner",
      enShortExplanation = "an electronic device that processes data using instructions",
      svLongExplanation = "En dator är en elektronisk enhet som behandlar data enligt lagrade instruktioner, och utför beräkningar, minnesmanipulationer och logiska operationer för att lösa problem eller utföra uppgifter. En dator består av ev en centralprocessorenhet (CPU), in- och utdataenheter, samt ett minne som lagrar instruktioner och data representerade som heltal.",
      enLongExplanation = "A computer is an electronic device that processes data following stored instructions, performing calculations, memory manipulations, and logical operations to solve problems or execute tasks. A computer consists of a central processing unit (CPU), input and output devices, and a memory that stores instructions and data represented as integers.",
    )
    val Compiler = Concept(
      sv = "kompilator",
      en = "compiler",
      svShortExplanation = "ett program som tar källkod som indata och ger maskinkod som utdata",
      enShortExplanation = "a program that takes source code as input and produces machine code as output",
      svLongExplanation = "En kompilator är ett program som läser källkod, analyserar den för fel och översätter den till maskinkod som datorn kan köra; kompilatorn rapporterar kompileringsfel om koden inte är korrekt.",
      enLongExplanation = "A compiler is a program that reads source code, analyzes it for errors, and translates it into machine code that the computer can execute; the compiler reports compile errors if the code is not correct.",
    )
    val Compile = Concept(
      sv = "kompilera",
      en = "compile",
      svShortExplanation = "att översätta kod till exekverbar form",
      enShortExplanation = "to translate code into executable form",
      svLongExplanation = "Att kompilera innebär att en kompilator analyserar källkoden, utför typkontroll och andra statiska analyser, och översätter den till en körbar form såsom bytekod för JVM; eventuella fel rapporteras som kompileringsfel.",
      enLongExplanation = "To compile means that a compiler analyzes the source code, performs type checking and other static analyses, and translates it into an executable form such as bytecode for the JVM; any errors are reported as compile errors.",
    )
    val CompileError = Concept(
      sv = "kompileringsfel",
      en = "compile error",
      svShortExplanation = "kan inträffa innan exekveringen startat",
      enShortExplanation = "can occur before execution has started",
      svLongExplanation = "Ett kompileringsfel uppstår när kompilatorn inte kan tolka eller typkontrollera koden och avbryter kompileringen med ett felmeddelande som anger plats och orsak; kompileringsfel måste åtgärdas innan programmet kan köras.",
      enLongExplanation = "A compile error occurs when the compiler cannot parse or type-check the code and aborts compilation with an error message indicating the location and cause; compile errors must be fixed before the program can be run.",
    )
    val ControlStructure = Concept(
      sv = "kontrollstruktur",
      en = "control structure",
      svShortExplanation = "kan ge alternativa vägar genom koden",
      enShortExplanation = "can give alternative paths through the code",
      svLongExplanation = "En kontrollstruktur kan ge alternativa vägar genom koden. Exempel på kontrollstrukturer är if-uttryck för logiska val, for-uttryck för att gå igenom sekvenser, och while-satser för att repetera kod så länge ett villkor är uppfyllt.",
      enLongExplanation = "A control structure can provide alternative paths through the code. Examples include if-expressions for logical decisions, for-expressions for iterating sequences, and while-loops for repeating code as long as a condition holds.",
    )
    val Constructor = Concept(
      sv = "konstruktor",
      en = "constructor",
      svShortExplanation = "skapar instans, allokerar plats för tillståndsminne",
      enShortExplanation = "creates an instance and allocates space for state memory",
      svLongExplanation = "En konstruktor är den kod som körs när en ny instans skapas med 'new'; i Scala utgörs primärkonstruktorn av klassens rubrik och kropp, och den allokerar minne för instansens tillståndsvariabler och exekverar initialiseringskoden.",
      enLongExplanation = "A constructor is the code that runs when a new instance is created with 'new'; in Scala the primary constructor is formed by the class header and body, and it allocates memory for the instance's state variables and executes the initialization code.",
    )
    val DataStructure = Concept(
      sv = "datastruktur",
      en = "data structure",
      svShortExplanation = "många olika element i en helhet; elementvis åtkomst",
      enShortExplanation = "many different elements as a whole; element-wise access",
      svLongExplanation = "En datastruktur är en organiserad samling av data där elementen kan lagras, nås och modifieras på ett väldefinierat sätt; valet av datastruktur, t.ex. array, länkad lista eller träd, påverkar programmets prestanda avsevärt.",
      enLongExplanation = "A data structure is an organized collection of data where elements can be stored, accessed, and modified in a well-defined way; the choice of data structure, e.g. array, linked list, or tree, significantly affects the program's performance.",
    )
    val DefaultArgument = Concept(
      sv = "defaultargument",
      en = "default argument",
      svShortExplanation = "gör att argument kan utelämnas",
      enShortExplanation = "allows arguments to be omitted",
      svLongExplanation = "Ett defaultargument är ett förvalt värde för en parameter som används automatiskt om inget argument anges vid anropet; detta minskar behovet av överlagring, gör API:er mer flexibla och förbättrar läsbarheten.",
      enLongExplanation = "A default argument is a preset value for a parameter used automatically if no argument is provided at the call site; this reduces the need for overloading, makes APIs more flexible, and improves readability.",
    )
    val Deserialize = Concept(
      sv = "de-serialisera",
      en = "deserialize",
      svShortExplanation = "avkoda symbolsekvens och återskapa objekt i minnet",
      enShortExplanation = "decode a symbol sequence and recreate objects in memory",
      svLongExplanation = "Att deserialisera innebär att man läser en teckensekvens eller byteström (t.ex. JSON, XML eller binärdata) och rekonstruerar de ursprungliga objekten i minnet med rätt typer och tillstånd; deserialisering är inversen till serialisering.",
      enLongExplanation = "To deserialize means to read a character sequence or byte stream (e.g. JSON, XML, or binary data) and reconstruct the original objects in memory with the correct types and state; deserialization is the inverse of serialization.",
    )
    val DotNotation = Concept(
      sv = "punktnotation",
      en = "dot notation",
      svShortExplanation = "används för att komma åt icke-privata delar",
      enShortExplanation = "used to access non-private members",
      svLongExplanation = "Punktnotation används för att komma åt ett objekts synliga medlemmar genom att skriva objektreferensen, en punkt, och sedan medlemmens namn, t.ex. 'obj.metod()' eller 'obj.fält'; metoder kan anropas utan parenteser om de saknar parametrar.",
      enLongExplanation = "Dot notation is used to access an object's visible members by writing the object reference, a dot, and then the member's name, e.g. 'obj.method()' or 'obj.field'; methods can be called without parentheses if they have no parameters.",
    )
    val DynamicBinding = Concept(
      sv = "dynamisk bindning",
      en = "dynamic binding",
      svShortExplanation = "körtidstypen avgör vilken metod som körs",
      enShortExplanation = "the runtime type determines which method is called",
      svLongExplanation = "Dynamisk bindning innebär att det vid ett metodanrop är objektets körtidstyp, inte den statiska deklarerade typen, som avgör vilken implementering av metoden som faktiskt exekveras; detta är grunden för polymorfism och möjliggörs av överskuggning.",
      enLongExplanation = "Dynamic binding means that at a method call it is the object's runtime type, not the statically declared type, that determines which implementation of the method is actually executed; this is the foundation of polymorphism and is enabled by overriding.",
    )
    val Enumeration = Concept(
      sv = "enumeration",
      en = "enumeration",
      svShortExplanation = "en uppräkning av värden i en viss ordning",
      enShortExplanation = "a listing of values in a particular order",
      svLongExplanation = "En enumeration är en datastruktur som består av ett begränsat antal namngivna värden i bestämd ordning; den används för att representera en uppräknelig mängd alternativ med tydliga namn och kan förbättra typkontroll och läsbarhet.",
      enLongExplanation = "An enumeration is a data structure consisting of a finite set of named values in a specific order; it is used to represent an enumerable set of alternatives with clear names and can improve type safety and readability.",
    )
    val Element = Concept(
      sv = "element",
      en = "element",
      svShortExplanation = "objekt i en datastruktur",
      enShortExplanation = "object in a data structure",
      svLongExplanation = "Ett element är ett enskilt objekt som ingår i en datastruktur eller samling; element kan nås via index i sekvenser, via nyckel i mappningar, eller via iteration med t.ex. en for-loop eller en högre ordningens funktion.",
      enLongExplanation = "An element is a single object contained in a data structure or collection; elements can be accessed via index in sequences, via key in mappings, or via iteration using e.g. a for-loop or a higher-order function.",
    )
    val Export = Concept(
      sv = "export",
      en = "export",
      svShortExplanation = "gör namn synligt utåt som medlem i detta objekt",
      enShortExplanation = "makes a name visible externally as a member of this object",
      svLongExplanation = "Export-satsen i Scala 3 gör att utvalda medlemmar från ett inre objekt eller en delegation blir synliga som direkta medlemmar i det omgivande objektet, vilket förenklar gränssnitt och möjliggör transparent delegering.",
      enLongExplanation = "The export clause in Scala 3 makes selected members from an inner object or a delegate visible as direct members of the enclosing object, simplifying interfaces and enabling transparent delegation.",
    )
    val Expression = Concept(
      sv = "uttryck",
      en = "expression",
      svShortExplanation = "kombinerar värden och funktioner till ett nytt värde",
      enShortExplanation = "combines values and functions to produce a new value",
      svLongExplanation = "Ett uttryck är en kombination av värden, operatorer och funktionsanrop som evalueras och producerar ett resultat med ett specifikt värde och en specifik typ; i Scala är nästan allt uttryck, inklusive if-satser, match och block.",
      enLongExplanation = "An expression is a combination of values, operators, and function calls that is evaluated and produces a result with a specific value and type; in Scala almost everything is an expression, including if-statements, match, and blocks.",
    )
    val FactoryMethod = Concept(
      sv = "fabriksmetod",
      en = "factory method",
      svShortExplanation = "hjälpfunktion för indirekt konstruktion",
      enShortExplanation = "helper function for indirect construction",
      svLongExplanation = "En fabriksmetod är en funktion, ofta placerad i ett kompanjonsobjekt, som skapar och returnerar instanser av en klass utan att anroparen behöver använda 'new' direkt; den kan validera argument, välja en lämplig subtyp eller returnera en cachad instans.",
      enLongExplanation = "A factory method is a function, often placed in a companion object, that creates and returns instances of a class without the caller needing to use 'new' directly; it can validate arguments, choose an appropriate subtype, or return a cached instance.",
    )
    val FloatingPoint = Concept(
      sv = "flyttal",
      en = "floating-point number",
      svShortExplanation = "decimaltal med begränsad noggrannhet",
      enShortExplanation = "decimal number with limited precision",
      svLongExplanation = "Ett flyttal representerar ett reellt tal med begränsad precision enligt IEEE 754-standarden; Scala har typerna Float (32 bitar) och Double (64 bitar), och avrundningsfel kan uppstå i beräkningar vilket gör att exakt jämförelse med == bör undvikas.",
      enLongExplanation = "A floating-point number represents a real number with limited precision according to the IEEE 754 standard; Scala has the types Float (32 bits) and Double (64 bits), and rounding errors can occur in calculations, so exact comparison with == should be avoided.",
    )
    val ForStatement = Concept(
      sv = "for-sats",
      en = "for statement",
      svShortExplanation = "bra då antalet repetitioner är bestämt i förväg",
      enShortExplanation = "good when the number of repetitions is known in advance",
      svLongExplanation = "En for-sats itererar över ett intervall eller en samling och exekverar kroppen ett bestämt antal gånger; i Scala kan 'for' användas som sats (utan 'yield') för sidoeffekter, eller som uttryck med 'yield' för att producera en ny samling.",
      enLongExplanation = "A for statement iterates over a range or collection and executes the body a fixed number of times; in Scala 'for' can be used as a statement (without 'yield') for side effects, or as an expression with 'yield' to produce a new collection.",
    )
    val Function = Concept(
      sv = "funktion",
      en = "function",
      svShortExplanation = "vid anrop beräknas ett returvärde",
      enShortExplanation = "when called, a return value is computed",
      svLongExplanation = "En funktion tar noll eller fler inparametrar och beräknar ett returvärde; i Scala är funktioner förstklassiga värden som kan tilldelas variabler, skickas som argument och returneras från andra funktioner, vilket stöder funktionell programmeringsstil.",
      enLongExplanation = "A function takes zero or more input parameters and computes a return value; in Scala functions are first-class values that can be assigned to variables, passed as arguments, and returned from other functions, supporting a functional programming style.",
    )
    val FunctionBody = Concept(
      sv = "funktionskropp",
      en = "function body",
      svShortExplanation = "koden som exekveras vid funktionsanrop",
      enShortExplanation = "the code executed when the function is called",
      svLongExplanation = "Funktionskroppen är den kod som exekveras när funktionen anropas; den kan vara ett enstaka uttryck efter '=' eller ett block som innehåller lokala variabler och satser vars sista rad ger returvärdet.",
      enLongExplanation = "The function body is the code that executes when the function is called; it can be a single expression after '=' or a block containing local variables and statements whose last line gives the return value.",
    )
    val FunctionHeader = Concept(
      sv = "funktionshuvud",
      en = "function header",
      svShortExplanation = "har parameterlista och eventuellt en returtyp",
      enShortExplanation = "has a parameter list and optionally a return type",
      svLongExplanation = "Funktionshuvudet börjar med nyckelordet 'def' följt av funktionsnamn, eventuella typparametrar inom hakparenteser, en eller flera parameterlistor med namn och typer, samt en valfri returtypsannotering efter kolon.",
      enLongExplanation = "The function header starts with the keyword 'def' followed by the function name, optional type parameters in square brackets, one or more parameter lists with names and types, and an optional return type annotation after a colon.",
    )
    val Generic = Concept(
      sv = "generisk",
      en = "generic",
      svShortExplanation = "har abstrakt typparameter, typen är generell",
      enShortExplanation = "has an abstract type parameter; the type is general",
      svLongExplanation = "En generisk klass, trait eller funktion har en eller fler abstrakta typparametrar skrivna inom hakparenteser som konkretiseras med faktiska typer vid användning; detta möjliggör återanvändbar kod som fungerar korrekt för många olika typer, t.ex. 'Vector[Int]' eller 'Vector[String]'.",
      enLongExplanation = "A generic class, trait, or function has one or more abstract type parameters written in square brackets that are concretized with actual types at use; this enables reusable code that works correctly for many different types, e.g. 'Vector[Int]' or 'Vector[String]'.",
    )
    val Getter = Concept(
      sv = "getter",
      en = "getter",
      svShortExplanation = "indirekt åtkomst av attributvärde",
      enShortExplanation = "indirect access to an attribute value",
      svLongExplanation = "En getter är en metod som ger indirekt läsåtkomst till ett privat attribut; i Scala definieras den typiskt som en 'def' eller 'val' med samma namn som attributet, utan 'private'-modifieraren, och följer principen om enhetlig access.",
      enLongExplanation = "A getter is a method that provides indirect read access to a private attribute; in Scala it is typically defined as a 'def' or 'val' with the same name as the attribute, without the 'private' modifier, following the uniform access principle.",
    )
    val Implementation = Concept(
      sv = "implementation",
      en = "implementation",
      svShortExplanation = "en specifik realisering av en algoritm",
      enShortExplanation = "a specific realization of an algorithm",
      svLongExplanation = "En implementation är en konkret realisering av en algoritm, ett gränssnitt eller en abstrakt specifikation i ett specifikt programmeringsspråk; samma algoritm kan ha flera implementationer med olika tids- och minneskomplexitet.",
      enLongExplanation = "An implementation is a concrete realization of an algorithm, an interface, or an abstract specification in a specific programming language; the same algorithm can have multiple implementations with different time and memory complexity.",
    )
    val Import = Concept(
      sv = "import",
      en = "import",
      svShortExplanation = "gör namn tillgängligt lokalt utan att hela sökvägen behövs",
      enShortExplanation = "makes a name locally available without needing the full path",
      svLongExplanation = "En import-sats gör ett namn tillgängligt i det lokala omfånget utan att hela den kvalificerade sökvägen behöver skrivas varje gång; i Scala 3 kan import placeras var som helst i koden och man kan importera flera namn eller använda wildcard med '_'.",
      enLongExplanation = "An import statement makes a name available in the local scope without needing to write the full qualified path every time; in Scala 3, imports can be placed anywhere in the code and multiple names can be imported or a wildcard can be used with '_'.",
    )
    val Inheritance = Concept(
      sv = "arv",
      en = "inheritance",
      svShortExplanation = "arv beskriver relationen 'är en'",
      enShortExplanation = "inheritance describes the 'is a' relationship",
      svLongExplanation = "Med arv kan en typ (subtyp) ärvas från en annan typ (supertyp), vilket innebär att subtypen får tillgång till supertypens medlemmar och kan användas där supertypen förväntas; det uttrycker relationen 'är en' och stödjer polymorfism och specialisering.",
      enLongExplanation = "With inheritance, one type (subtype) derives from another type (supertype), which means the subtype gains access to the supertype's members and can be used wherever the supertype is expected; it expresses the 'is a' relationship and supports polymorphism and specialization.",
    )
    val Instance = Concept(
      sv = "instans",
      en = "instance",
      svShortExplanation = "upplaga av ett objekt med eget tillståndsminne",
      enShortExplanation = "a copy of an object with its own state memory",
      svLongExplanation = "En instans är ett konkret objekt skapat från en klass med hjälp av 'new' (eller en fabriksmetod); varje instans har sitt eget minne för tillståndsvariabler men delar metodimplementationerna med alla andra instanser av samma klass.",
      enLongExplanation = "An instance is a concrete object created from a class using 'new' (or a factory method); each instance has its own memory for state variables but shares the method implementations with all other instances of the same class.",
    )
    val Key = Concept(
      sv = "nyckel",
      en = "key",
      svShortExplanation = "en unik identifierare",
      enShortExplanation = "a unique identifier",
      svLongExplanation = "En nyckel är en unik identifierare i en nyckel-värde-tabell (Map) som används för att slå upp ett associerat värde; varje nyckel förekommer högst en gång i en Map och kan vara av vilken typ som helst med korrekt equals och hashCode.",
      enLongExplanation = "A key is a unique identifier in a key-value table (Map) used to look up an associated value; each key appears at most once in a Map and can be of any type with correct equals and hashCode.",
    )
    val KeyValueTable = Concept(
      sv = "nyckel-värde-tabell",
      en = "key-value table",
      svShortExplanation = "oordnad samling av mappningar med unika nycklar",
      enShortExplanation = "unordered collection of mappings with unique keys",
      svLongExplanation = "En nyckel-värde-tabell (Map) är en oordnad samling av par där varje unik nyckel mappas till ett värde och som stöder effektiv sökning, insättning och borttagning baserat på nyckeln; i Scala finns den som HashMap, TreeMap m.fl.",
      enLongExplanation = "A key-value table (Map) is an unordered collection of pairs where each unique key is mapped to a value and which supports efficient lookup, insertion, and deletion by key; in Scala it exists as HashMap, TreeMap, and others.",
    )
    val LazyInitialization = Concept(
      sv = "lat initialisering",
      en = "lazy initialization",
      svShortExplanation = "allokering sker först när namnet refereras",
      enShortExplanation = "allocation occurs only when the name is first referenced",
      svLongExplanation = "Lat initialisering med nyckelordet 'lazy' innebär att en vals värde beräknas och allokeras först vid första åtkomst, inte vid definitionen; detta sparar resurser om värdet aldrig används och löser initialiseringsordningsproblem vid cirkulära beroenden.",
      enLongExplanation = "Lazy initialization with the 'lazy' keyword means that a val's value is computed and allocated only at first access, not at definition; this saves resources if the value is never used and resolves initialization order issues with circular dependencies.",
    )
    val LinearSearch = Concept(
      sv = "linjärsöka",
      en = "linear search",
      svShortExplanation = "leta i sekvens tills sökkriteriet är uppfyllt",
      enShortExplanation = "search a sequence until the search criterion is met",
      svLongExplanation = "Att linjärsöka innebär att man traverserar en sekvens element för element tills sökkriteriet är uppfyllt eller alla element undersökts; tidskomplexiteten är O(n) och algoritmen kräver ingen förutgående sortering av data.",
      enLongExplanation = "To linear search means to traverse a sequence element by element until the search criterion is met or all elements have been examined; the time complexity is O(n) and the algorithm requires no prior sorting of the data.",
    )
    val LinearSearchAlgorithm = Concept(
      sv = "linjärsökning",
      en = "linear search algorithm",
      svShortExplanation = "sökalgoritm som letar i sekvens tills element hittas",
      enShortExplanation = "search algorithm that scans a sequence until the element is found",
      svLongExplanation = "Linjärsökning är en sökalgoritm som itererar sekventiellt genom en samling och jämför varje element med sökvärdet tills en matchning hittas eller samlingen är slut; algoritmen är enkel men har O(n) tidskomplexitet i värsta fall.",
      enLongExplanation = "Linear search is a search algorithm that iterates sequentially through a collection and compares each element with the target value until a match is found or the collection is exhausted; the algorithm is simple but has O(n) time complexity in the worst case.",
    )
    val Literal = Concept(
      sv = "litteral",
      en = "literal",
      svShortExplanation = "anger ett specifikt datavärde",
      enShortExplanation = "specifies a specific data value",
      svLongExplanation = "En litteral är en direkt notation för ett specifikt värde i källkoden, t.ex. '42' för ett heltal, '3.14' för ett Double, '\"hej\"' för en sträng, 'true' för ett booleskt värde, eller ''a'' för ett tecken.",
      enLongExplanation = "A literal is a direct notation for a specific value in source code, e.g. '42' for an integer, '3.14' for a Double, '\"hello\"' for a string, 'true' for a boolean, or ''a'' for a character.",
    )
    val MapOperation = Concept(
      sv = "map",
      en = "map",
      svShortExplanation = "applicerar en funktion på varje element i en samling",
      enShortExplanation = "applies a function to each element in a collection",
      svLongExplanation = "Map-operationen applicerar en given funktion på varje element i en samling och returnerar en ny samling av samma längd med de transformerade elementen; originalsamlingen förändras inte, vilket gör map till en grundläggande operation i funktionell programmering.",
      enLongExplanation = "The map operation applies a given function to each element in a collection and returns a new collection of the same length with the transformed elements; the original collection is not modified, making map a fundamental operation in functional programming.",
    )
    val Mapping = Concept(
      sv = "mappning",
      en = "mapping",
      svShortExplanation = "nyckel -> värde",
      enShortExplanation = "key -> value",
      svLongExplanation = "En mappning är ett nyckel-värde-par i en Map där nyckeln unikt identifierar värdet; i Scala skrivs en mappning med syntaxen 'nyckel -> värde' och flera mappningar kan kombineras för att konstruera en Map.",
      enLongExplanation = "A mapping is a key-value pair in a Map where the key uniquely identifies the value; in Scala a mapping is written with the syntax 'key -> value' and multiple mappings can be combined to construct a Map.",
    )
    val Matrix = Concept(
      sv = "matris",
      en = "matrix",
      svShortExplanation = "indexerbar datastruktur i två dimensioner",
      enShortExplanation = "indexable data structure in two dimensions",
      svLongExplanation = "En matris är en tvådimensionell datastruktur med m rader och n kolumner indexerad med rad- och kolumnindex; i Scala representeras den ofta som en Vector av Vektorer, och används inom t.ex. bildbehandling och linjär algebra.",
      enLongExplanation = "A matrix is a two-dimensional data structure with m rows and n columns indexed by row and column indices; in Scala it is often represented as a Vector of Vectors, and is used in e.g. image processing and linear algebra.",
    )
    val Member = Concept(
      sv = "medlem",
      en = "member",
      svShortExplanation = "tillhör ett objekt; nås med punktnotation om synlig",
      enShortExplanation = "belongs to an object; accessed with dot notation if visible",
      svLongExplanation = "En medlem är en namngiven del av ett objekt eller en klass, t.ex. en metod, ett attribut eller ett typalias; synliga (icke-privata) medlemmar nås utifrån med punktnotation, och kan vara konkreta eller abstrakta.",
      enLongExplanation = "A member is a named part of an object or class, e.g. a method, an attribute, or a type alias; visible (non-private) members are accessed from outside using dot notation, and can be concrete or abstract.",
    )
    val MemoryComplexity = Concept(
      sv = "minneskomplexitet",
      en = "memory complexity",
      svShortExplanation = "hur minnesåtgången växer med problemstorleken",
      enShortExplanation = "how memory usage grows with the size of the problem",
      svLongExplanation = "Minneskomplexitet beskriver hur mycket minne ett program eller en algoritm behöver som funktion av indatastorleken n, uttryckt med O-notation; O(1) innebär konstant minne, O(n) linjärt och O(n²) kvadratiskt minnesbehov.",
      enLongExplanation = "Memory complexity describes how much memory a program or algorithm requires as a function of the input size n, expressed in O-notation; O(1) means constant memory, O(n) linear, and O(n²) quadratic memory requirement.",
    )
    val Method = Concept(
      sv = "metod",
      en = "method",
      svShortExplanation = "funktion som är medlem av ett objekt",
      enShortExplanation = "function that is a member of an object",
      svLongExplanation = "En metod är en funktion som är definierad som del av ett objekt eller en klass och har tillgång till objektets egna attribut och andra medlemmar; metoder anropas med punktnotation på en objektreferens.",
      enLongExplanation = "A method is a function defined as part of an object or class and has access to the object's own attributes and other members; methods are called using dot notation on an object reference.",
    )
    val Mixin = Concept(
      sv = "inmixning",
      en = "mixin",
      svShortExplanation = "tillföra flera egenskaper genom arv av trait",
      enShortExplanation = "adding features using with and a trait",
      svLongExplanation = "Inmixning innebär att man utökar en klass eller ett objekt med egenskaperna hos en eller flera traits; detta möjliggör återanvändning av kodegenskaper och stöder ett slags multipelt arv av beteenden.",
      enLongExplanation = "Mixin means extending a class or object with the properties of one or more traits; this enables reuse of code properties and supports a form of multiple inheritance of behaviors.",
    )
    val Module = Concept(
      sv = "modul",
      en = "module",
      svShortExplanation = "kodenhet med abstraktioner som kan återanvändas",
      enShortExplanation = "code unit with abstractions that can be reused",
      svLongExplanation = "En modul är en avgränsad kodenhet som samlar relaterade abstraktioner under ett gemensamt namn och exponerar ett väldefinierat gränssnitt; i Scala realiseras moduler som singelobjekt, paket eller filer och stöder separation of concerns.",
      enLongExplanation = "A module is a self-contained code unit that groups related abstractions under a common name and exposes a well-defined interface; in Scala modules are realized as singleton objects, packages, or files and support separation of concerns.",
    )
    val NamedArguments = Concept(
      sv = "namngivna argument",
      en = "named arguments",
      svShortExplanation = "gör att argument kan ges i valfri ordning",
      enShortExplanation = "allows arguments to be given in any order",
      svLongExplanation = "Namngivna argument låter anroparen ange parameternamnet explicit vid anropet, t.ex. 'f(y = 2, x = 1)'; detta gör att argument kan ges i valfri ordning, förbättrar läsbarheten och fungerar bra i kombination med defaultargument.",
      enLongExplanation = "Named arguments let the caller explicitly specify the parameter name at the call site, e.g. 'f(y = 2, x = 1)'; this allows arguments to be given in any order, improves readability, and works well in combination with default arguments.",
    )
    val NameShadowing = Concept(
      sv = "namnskuggning",
      en = "name shadowing",
      svShortExplanation = "lokalt namn döljer samma namn i omgivande block",
      enShortExplanation = "a local name hides the same name in the surrounding block",
      svLongExplanation = "Namnskuggning uppstår när ett lokalt namn i ett inre block har samma identifierare som ett namn i ett omgivande block; inom det inre blocket syftar identifieraren på den lokala definitionen och det yttre namnet är otillgängligt utan kvalificering.",
      enLongExplanation = "Name shadowing occurs when a local name in an inner block has the same identifier as a name in an enclosing block; within the inner block the identifier refers to the local definition and the outer name is inaccessible without qualification.",
    )
    val Namespace = Concept(
      sv = "namnrymd",
      en = "namespace",
      svShortExplanation = "omgivning där är alla namn är unika",
      enShortExplanation = "environment where all names are unique",
      svLongExplanation = "En namnrymd är en omgivning där alla namn är unika och kan identifieras entydigt; paket och objekt i Scala skapar egna namnrymder som förhindrar namnkollisioner och gör det möjligt att ha identiska namn i olika moduler.",
      enLongExplanation = "A namespace is an environment where all names are unique and can be unambiguously identified; packages and objects in Scala create their own namespaces that prevent name collisions and allow identical names to exist in different modules.",
    )
    val New = Concept(
      sv = "new",
      en = "new",
      svShortExplanation = "nyckelord vid direkt instansiering av klass",
      enShortExplanation = "keyword for direct instantiation of a class",
      svLongExplanation = "Nyckelordet 'new' används för att direkt instansiera en klass och anropar dess konstruktor; för case-klasser och objekt med apply-fabriksmetoder behöver 'new' vanligtvis inte skrivas, men krävs vid instansiering av vanliga klasser och anonyma klasser.",
      enLongExplanation = "The keyword 'new' is used to directly instantiate a class and call its constructor; for case classes and objects with apply factory methods 'new' usually does not need to be written, but is required when instantiating regular classes and anonymous classes.",
    )
    val Null = Concept(
      sv = "null",
      en = "null",
      svShortExplanation = "ett värde som ej refererar till någon instans",
      enShortExplanation = "a value that does not refer to any instance",
      svLongExplanation = "Null är ett specialvärde av referenstyp som indikerar att en referens inte pekar på någon instans; att referera null ger NullPointerException och null rekommenderas inte i Scala — använd istället Option[T] för att representera ett möjligen saknat värde.",
      enLongExplanation = "Null is a special reference-type value indicating that a reference does not point to any instance; dereferencing null gives a NullPointerException and null is not recommended in Scala — use Option[T] instead to represent a possibly absent value.",
    )
    val Object = Concept(
      sv = "objekt",
      en = "object",
      svShortExplanation = "samlar variabler och funktioner",
      enShortExplanation = "groups variables and functions together",
      svLongExplanation = "Ett objekt i Scala är antingen ett singelobjekt (definierat med 'object') som samlar variabler och funktioner i en modul och existerar i exakt en upplaga, eller en instans av en klass med eget tillståndsminne skapad med 'new'.",
      enLongExplanation = "An object in Scala is either a singleton object (defined with 'object') that groups variables and functions in a module and exists in exactly one instance, or an instance of a class with its own state memory created with 'new'.",
    )
    val Ordering = Concept(
      sv = "ordning",
      en = "ordering",
      svShortExplanation = "definierar hur element av en viss typ ska ordnas",
      enShortExplanation = "defines how elements of a certain type should be ordered",
      svLongExplanation = "Ordning definierar en total ordning på element av en viss typ via en jämförelsefunktion som returnerar negativt, noll eller positivt tal; Scalas Ordering-typklass används av sorteringsalgoritmer och sorterade samlingar som SortedSet.",
      enLongExplanation = "Ordering defines a total order on elements of a certain type via a comparison function returning a negative, zero, or positive number; Scala's Ordering type class is used by sorting algorithms and sorted collections such as SortedSet.",
    )
    val Overloading = Concept(
      sv = "överlagring",
      en = "overloading",
      svShortExplanation = "metoder med samma namn men olika parametertyper",
      enShortExplanation = "methods with the same name but different parameter types",
      svLongExplanation = "Överlagring innebär att en klass eller ett objekt har flera metoder med samma namn men med olika parameterlistor (antal eller typer); kompilatorn väljer rätt variant vid kompilering baserat på argumentens typer, vilket kallas statisk bindning.",
      enLongExplanation = "Overloading means that a class or object has multiple methods with the same name but with different parameter lists (number or types); the compiler selects the correct variant at compile time based on the types of the arguments, which is called static binding.",
    )
    val OverriddenMember = Concept(
      sv = "överskuggad medlem",
      en = "overridden member",
      svShortExplanation = "medlem i subtyp ersätter medlem i supertyp",
      enShortExplanation = "member in a subtype replaces a member in the supertype",
      svLongExplanation = "En överskuggad medlem är en metod eller val i en subtyp som med nyckelordet 'override' ersätter en namngiven medlem i supertypen; vid dynamisk bindning är det subtypens implementation som anropas, även när referensen är av supertyp.",
      enLongExplanation = "An overridden member is a method or val in a subtype that uses the 'override' keyword to replace a named member in the supertype; with dynamic binding it is the subtype's implementation that is called, even when the reference is of the supertype.",
    )
    val Package = Concept(
      sv = "paket",
      en = "package",
      svShortExplanation = "modul som skapar namnrymd; maskinkod får egen katalog",
      enShortExplanation = "module that creates a namespace; machine code gets its own directory",
      svLongExplanation = "Ett paket i Scala är en namngiven namnrymd som grupperar relaterade klasser och objekt; paket deklareras med 'package' och deras kompilerade bytekod lagras i en katalogstruktur som speglar paketnamnet, t.ex. 'se.lth.cs.prog'.",
      enLongExplanation = "A package in Scala is a named namespace that groups related classes and objects; packages are declared with 'package' and their compiled bytecode is stored in a directory structure mirroring the package name, e.g. 'se.lth.cs.prog'.",
    )
    val ParameterList = Concept(
      sv = "parameterlista",
      en = "parameter list",
      svShortExplanation = "beskriver namn och typ på parametrar",
      enShortExplanation = "describes the name and type of parameters",
      svLongExplanation = "En parameterlista omges av parenteser och innehåller en kommaseparerad lista av parametrar med namn och typ; en funktion kan ha flera parameterlistor (currying), t.ex. för implicit-parametrar eller för att möjliggöra partiell applikation.",
      enLongExplanation = "A parameter list is enclosed in parentheses and contains a comma-separated list of parameters with names and types; a function can have multiple parameter lists (currying), e.g. for implicit parameters or to enable partial application.",
    )
    val Persistence = Concept(
      sv = "persistens",
      en = "persistence",
      svShortExplanation = "egenskapen att finnas kvar efter programmets avslut",
      enShortExplanation = "the property of surviving after the program has ended",
      svLongExplanation = "Persistens är egenskapen hos data att överleva bortom en enskild programkörning; data lagras persistent i t.ex. filer, databaser eller andra externa lagringsmedier och kan läsas tillbaka i en senare programkörning.",
      enLongExplanation = "Persistence is the property of data to survive beyond a single program execution; data is stored persistently in e.g. files, databases, or other external storage media and can be read back in a later program run.",
    )
    val Polymorphism = Concept(
      sv = "polymorfism",
      en = "polymorphism",
      svShortExplanation = "kan ha många former, t.ex. en av flera subtyper",
      enShortExplanation = "can have many forms, e.g. one of several subtypes",
      svLongExplanation = "Polymorfism innebär att ett värde kan ha många möjliga typer; i Scala uppnås detta via subtyppolymorfism (arv och överskuggning), parameterpolymorfism (generics och typparametrar) och ad hoc-polymorfism (typklasser och implicita parametrar).",
      enLongExplanation = "Polymorphism means that a value can have many possible types; in Scala this is achieved via subtype polymorphism (inheritance and overriding), parametric polymorphism (generics and type parameters), and ad hoc polymorphism (type classes and implicit parameters).",
    )
    val Predicate = Concept(
      sv = "predikat",
      en = "predicate",
      svShortExplanation = "en funktion som ger ett booleskt värde",
      enShortExplanation = "a function that returns a boolean value",
      svLongExplanation = "Ett predikat är en funktion returnerar ett booleskt värde (true eller false); predikat används som filterfunktioner i samlingsoperationer som 'filter', 'exists', 'forall' och 'takeWhile'.",
      enLongExplanation = "A predicate is a function that returns a boolean value (true or false); predicates are used as filter functions in collection operations such as 'filter', 'exists', 'forall', and 'takeWhile'.",
    )
    val Private = Concept(
      sv = "privat",
      en = "private",
      svShortExplanation = "modifierar synligheten av en objektmedlem",
      enShortExplanation = "modifies the visibility of an object member",
      svLongExplanation = "En privat medlem, deklarerad med 'private', är synlig endast inuti den klass eller det objekt den tillhör; detta döljer implementationsdetaljer från användare av klassen, skyddar mot godtycklig förändring och minskar kopplingen mellan klasser.",
      enLongExplanation = "A private member, declared with 'private', is visible only inside the class or object it belongs to; this hides implementation details from users of the class, protects against arbitrary modifications, and reduces coupling between classes.",
    )
    val Procedure = Concept(
      sv = "procedur",
      en = "procedure",
      svShortExplanation = "vid anrop sker (sido)effekt; returvärdet är tomt",
      enShortExplanation = "when called, a (side) effect occurs; the return value is empty",
      svLongExplanation = "En procedur är en funktion vars syfte är att utföra en sidoeffekt, t.ex. skriva ut text, ändra ett attribut eller kommunicera med omvärlden; returvärdet är av typen Unit som representerar ett tomt värde utan information.",
      enLongExplanation = "A procedure is a function whose purpose is to perform a side effect, e.g. printing text, modifying an attribute, or communicating with the outside world; the return value is of type Unit which represents an empty value with no information.",
    )
    val ProgramArgument = Concept(
      sv = "programargument",
      en = "program argument",
      svShortExplanation = "kan överföras via parametern args till main",
      enShortExplanation = "can be passed via the args parameter to main",
      svLongExplanation = "Programargument är strängar som skickas till programmet från kommandoraden när det startas; de är tillgängliga via parametern 'args: Array[String]' i main-metoden och används för att anpassa programmets beteende utan att ändra källkoden.",
      enLongExplanation = "Program arguments are strings passed to the program from the command line when it is started; they are available via the 'args: Array[String]' parameter in the main method and are used to customize the program's behavior without changing the source code.",
    )
    val ProtectedMember = Concept(
      sv = "skyddad medlem",
      en = "protected member",
      svShortExplanation = "är endast synlig i subtyper",
      enShortExplanation = "visible only in subtypes",
      svLongExplanation = "En skyddad medlem, deklarerad med 'protected', är synlig inuti den klass den tillhör och i alla direkta och indirekta subklasser, men inte utifrån i övrigt; skyddade medlemmar kan överskuggas i subklasser.",
      enLongExplanation = "A protected member, declared with 'protected', is visible inside the class it belongs to and in all direct and indirect subclasses, but not from outside otherwise; protected members can be overridden in subclasses.",
    )
    val PureFunction = Concept(
      sv = "äkta funktion",
      en = "pure function",
      svShortExplanation = "ger alltid samma resultat om samma argument",
      enShortExplanation = "always gives the same result for the same arguments",
      svLongExplanation = "En äkta funktion (pure function) ger alltid exakt samma returvärde för samma argument och har inga sidoeffekter som påverkar tillståndet utanför funktionen; äkta funktioner är lätta att testa, resonera om, memoize och parallellisera.",
      enLongExplanation = "A pure function always returns exactly the same value for the same arguments and has no side effects that affect state outside the function; pure functions are easy to test, reason about, memoize, and parallelize.",
    )
    val RandomSeed = Concept(
      sv = "slumptalsfrö",
      en = "random seed",
      svShortExplanation = "ger återupprepningsbar sekvens av pseudoslumptal",
      enShortExplanation = "produces a reproducible sequence of pseudo-random numbers",
      svLongExplanation = "Ett slumptalsfrö är ett startvärde för en pseudoslumptalsgenerator; med samma frö produceras alltid exakt samma sekvens av pseudoslumptal, vilket möjliggör reproducerbara experiment och deterministiska tester av kod som använder slumpmässighet.",
      enLongExplanation = "A random seed is a starting value for a pseudo-random number generator; with the same seed exactly the same sequence of pseudo-random numbers is always produced, enabling reproducible experiments and deterministic tests of code that uses randomness.",
    )
    val Range = Concept(
      sv = "Range",
      en = "Range",
      svShortExplanation = "en samling som representerar ett intervall av heltal",
      enShortExplanation = "a collection representing an interval of integers",
      svLongExplanation = "Range är en oföränderlig, lat samling i Scala som representerar ett intervall av heltal med start, slut och steglängd; den lagrar inte alla värden utan beräknar dem vid behov, vilket gör den mycket minneseffektiv för stora intervall.",
      enLongExplanation = "Range is an immutable, lazy collection in Scala representing an interval of integers with a start, end, and step; it does not store all values but computes them on demand, making it very memory-efficient for large intervals.",
    )
    val RecursiveFunction = Concept(
      sv = "rekursiv funktion",
      en = "recursive function",
      svShortExplanation = "en funktion som anropar sig själv",
      enShortExplanation = "a function that calls itself",
      svLongExplanation = "En rekursiv funktion löser ett problem genom att anropa sig själv med ett enklare eller mindre delfall tills ett basfall nås som kan lösas direkt; svansrekursion (tail recursion) optimeras av Scala-kompilatorn till en loop och undviker stackspill.",
      enLongExplanation = "A recursive function solves a problem by calling itself with a simpler or smaller subcase until a base case is reached that can be solved directly; tail recursion is optimized by the Scala compiler into a loop and avoids stack overflow.",
    )
    val ReferenceEquality = Concept(
      sv = "referenslikhet",
      en = "reference equality",
      svShortExplanation = "instanser anses olika även om tillstånden är lika",
      enShortExplanation = "instances are considered different even if their states are equal",
      svLongExplanation = "Referenslikhet jämför om två referenser pekar på exakt samma objekt i minnet och implementeras i Scala med 'eq'; två separata objekt med identiskt innehåll är inte referenslika, till skillnad från innehållslikhet som jämförs med '=='.",
      enLongExplanation = "Reference equality compares whether two references point to the exact same object in memory and is implemented in Scala with 'eq'; two separate objects with identical content are not reference-equal, unlike structural equality which is compared with '=='.",
    )
    val ReferenceType = Concept(
      sv = "referenstyp",
      en = "reference type",
      svShortExplanation = "har supertypen AnyRef, allokeras i heapen via referens",
      enShortExplanation = "has supertype AnyRef, allocated on the heap via reference",
      svLongExplanation = "En referenstyp är en typ vars instanser allokeras på heapen och nås via en referens (pekare); alla klasser i Scala som ärver från AnyRef är referenstyper, t.ex. String, List och egna klasser, till skillnad från värdetyper som Int och Double.",
      enLongExplanation = "A reference type is a type whose instances are allocated on the heap and accessed via a reference (pointer); all classes in Scala that inherit from AnyRef are reference types, e.g. String, List, and custom classes, unlike value types such as Int and Double.",
    )
    val Registration = Concept(
      sv = "registrering",
      en = "counting",
      svShortExplanation = "algoritm som räknar element med vissa egenskaper",
      enShortExplanation = "algorithm that counts elements with certain properties",
      svLongExplanation = "Registrering är ett algoritmiskt mönster som räknar hur många element i en samling som uppfyller ett visst kriterium; det implementeras t.ex. med samlingsmetoden 'count' eller med en räknarvariabel som ökas i en loop.",
      enLongExplanation = "Registration (counting) is an algorithmic pattern that counts how many elements in a collection satisfy a certain criterion; it is implemented e.g. with the collection method 'count' or with a counter variable incremented in a loop.",
    )
    val RowVector = Concept(
      sv = "radvektor",
      en = "row vector",
      svShortExplanation = "matris av dimension $1\\times{}m$ med $m$ horisontella värden",
      enShortExplanation = "matrix of dimension $1\\times{}m$ with $m$ horizontal values",
      svLongExplanation = "En radvektor är en matris med dimensionen 1×m som innehåller m element i en enda horisontell rad; den används inom linjär algebra och numerisk beräkning och är ett specialfall av en matris.",
      enLongExplanation = "A row vector is a matrix of dimension 1×m containing m elements in a single horizontal row; it is used in linear algebra and numerical computation and is a special case of a matrix.",
    )
    val RuntimeError = Concept(
      sv = "exekveringsfel",
      en = "runtime error",
      svShortExplanation = "kan inträffa medan programmet kör",
      enShortExplanation = "can occur while the program is running",
      svLongExplanation = "Ett exekveringsfel, även kallat körtidsfel, inträffar medan programmet körs, t.ex. ArithmeticException vid division med noll eller IndexOutOfBoundsException vid felaktigt index; det leder vanligtvis till att programmet avslutas med ett felmeddelande och en stack trace.",
      enLongExplanation = "A runtime error occurs while the program is running, e.g. ArithmeticException when dividing by zero IndexOutOfBoundsException from an invalid index; it typically causes the program to terminate with an error message and a stack trace.",
    )
    val RuntimeType = Concept(
      sv = "körtidstyp",
      en = "runtime type",
      svShortExplanation = "kan vara mer specifik än den statiska typen",
      enShortExplanation = "can be more specific than the static type",
      svLongExplanation = "Körtidstypen är den faktiska typen hos ett objekt vid exekvering och kan vara mer specifik än den statiska typen som kompilatorn känner till; körtidstypen avgör vilken överskuggad metod som anropas vid dynamisk bindning.",
      enLongExplanation = "The runtime type is the actual type of an object at execution time and can be more specific than the static type known to the compiler; the runtime type determines which overridden method is called under dynamic binding.",
    )
    val Script = Concept(
      sv = "skript",
      en = "script",
      svShortExplanation = "ensam kodfil, huvudprogram behövs ej",
      enShortExplanation = "a single code file; no main program needed",
      svLongExplanation = "Ett skript i Scala är en ensam kodfil vars toppnivåkod exekveras direkt utan att en explicit main-metod eller ett omgivande objekt behöver definieras; detta förenklar snabba program, automatisering och interaktiva sessioner.",
      enLongExplanation = "A script in Scala is a single code file whose top-level code executes directly without needing to define an explicit main method or enclosing object; this simplifies quick programs, automation, and interactive sessions.",
    )
    val SealedType = Concept(
      sv = "förseglad typ",
      en = "sealed type",
      svShortExplanation = "subtypning utanför denna kodfil är förhindrad",
      enShortExplanation = "subtyping outside this code file is prevented",
      svLongExplanation = "En förseglad typ, deklarerad med 'sealed', tillåter bara subtyper definierade i samma kodfil; detta gör uppsättningen subtyper stängd och möjliggör uttömmande kontroll vid mönstermatchning, och kompilatorn varnar om ett fall saknas.",
      enLongExplanation = "A sealed type, declared with 'sealed', only allows subtypes defined in the same code file; this makes the set of subtypes closed and enables exhaustive checking in pattern matching, with the compiler warning if a case is missing.",
    )
    val Search = Concept(
      sv = "sökning",
      en = "search",
      svShortExplanation = "algoritm som letar upp element enligt sökkriterium",
      enShortExplanation = "algorithm that finds elements according to a search criterion",
      svLongExplanation = "Sökning är ett algoritmiskt mönster som letar upp ett eller flera element i en samling som uppfyller ett sökkriterium; effektiviteten beror på om data är sorterat och vilken datastruktur som används, t.ex. O(n) för linjärsökning och O(log n) för binärsökning.",
      enLongExplanation = "Search is an algorithmic pattern that finds one or more elements in a collection satisfying a search criterion; efficiency depends on whether data is sorted and which data structure is used, e.g. O(n) for linear search and O(log n) for binary search.",
    )
    val Sequence = Concept(
      sv = "sekvens(samling)",
      en = "sequence (collection)",
      svShortExplanation = "noll el. flera element av samma typ i viss ordning",
      enShortExplanation = "zero or more elements of the same type in a certain order",
      svLongExplanation = "En sekvenssamling är en ordnad samling med noll eller fler element av samma typ där varje elements position (index) är väldefinierad; element kan nås via index, och sekvenser stödjer operationer som map, filter, foldLeft och zip.",
      enLongExplanation = "A sequence collection is an ordered collection with zero or more elements of the same type where each element's position (index) is well-defined; elements can be accessed by index, and sequences support operations such as map, filter, foldLeft, and zip.",
    )
    val SequenceAlgorithm = Concept(
      sv = "sekvensalgoritm",
      en = "sequence algorithm",
      svShortExplanation = "lösning på problem som drar nytta av sekvenssamling",
      enShortExplanation = "solution to a problem that benefits from a sequence collection",
      svLongExplanation = "En sekvensalgoritm löser ett problem genom att utnyttja att data är organiserad som en ordnad sekvens; typiska mönster är sökning, sortering, filtrering, mappning, summering och reducering, ofta implementerade med samlingsoperationer eller loopar.",
      enLongExplanation = "A sequence algorithm solves a problem by exploiting the fact that data is organized as an ordered sequence; typical patterns are searching, sorting, filtering, mapping, summing, and reducing, often implemented with collection operations or loops.",
    )
    val SequenceCollection = Concept(
      sv = "sekvenssamling",
      en = "sequence collection",
      svShortExplanation = "datastruktur med element i en viss ordning",
      enShortExplanation = "data structure with elements in a certain order",
      svLongExplanation = "En sekvenssamling är en datastruktur där elementen lagras och nås i en bestämd ordning via heltalsindex; exempel i Scala är Vector (oföränderlig, effektiv), List (oföränderlig, länkad), Array (föränderlig, fast storlek) och ArrayBuffer (föränderlig, dynamisk storlek).",
      enLongExplanation = "A sequence collection is a data structure where elements are stored and accessed in a definite order via integer indices; examples in Scala are Vector (immutable, efficient), List (immutable, linked), Array (mutable, fixed size), and ArrayBuffer (mutable, dynamic size).",
    )
    val Serialize = Concept(
      sv = "serialisera",
      en = "serialize",
      svShortExplanation = "koda objekt till avkodningsbar sekvens av symboler",
      enShortExplanation = "encode an object into a decodable sequence of symbols",
      svLongExplanation = "Att serialisera innebär att man kodar ett objekt till en sekvens av tecken eller bytes i ett väldefinierat format (t.ex. JSON, XML eller ett binärt format) som kan lagras på disk, skickas över ett nätverk och sedan deserialiseras tillbaka till ett objekt.",
      enLongExplanation = "To serialize means to encode an object into a sequence of characters or bytes in a well-defined format (e.g. JSON, XML, or a binary format) that can be stored on disk, sent over a network, and then deserialized back into an object.",
    )
    val Set = Concept(
      sv = "mängd",
      en = "set",
      svShortExplanation = "oordnad samling med unika element",
      enShortExplanation = "unordered collection with unique elements",
      svLongExplanation = "En mängd (Set) är en oordnad samling av unika element utan duplicat; Scala erbjuder både oföränderliga och föränderliga implementationer med operationer som union, snitt, differens och effektiv membership-test.",
      enLongExplanation = "A set (Set) is an unordered collection of unique elements with no duplicates; Scala offers both immutable and mutable implementations with operations such as union, intersection, difference, and efficient membership testing.",
    )
    val Setter = Concept(
      sv = "setter",
      en = "setter",
      svShortExplanation = "indirekt tilldelning av attributvärde",
      enShortExplanation = "indirect assignment of an attribute value",
      svLongExplanation = "En setter är en metod som ger indirekt skrivåtkomst till ett privat attribut; i Scala kan en setter definieras med syntaxen 'def attributnamn_=(värde: Typ)' vilket möjliggör tilldelning med vanlig '='-syntax från utsidan av klassen.",
      enLongExplanation = "A setter is a method that provides indirect write access to a private attribute; in Scala a setter can be defined with the syntax 'def attributename_=(value: Type)' which enables assignment using ordinary '=' syntax from outside the class.",
    )
    val SingletonObject = Concept(
      sv = "singelobjekt",
      en = "singleton object",
      svShortExplanation = "modul som kan ha tillstånd; finns i en enda upplaga",
      enShortExplanation = "module that can have state; exists as a single instance",
      svLongExplanation = "Ett singelobjekt definieras med nyckelordet 'object' och existerar i exakt en upplaga per program; det kan ha tillståndsvariabler och metoder, initialiseras vid första åtkomst, och används som modul, kompanjonsobjekt eller programingångspunkt.",
      enLongExplanation = "A singleton object is defined with the 'object' keyword and exists in exactly one instance per program; it can have state variables and methods, is initialized on first access, and is used as a module, companion object, or program entry point.",
    )
    val Sorting = Concept(
      sv = "sortering",
      en = "sorting",
      svShortExplanation = "algoritm som ordnar element i en viss ordning",
      enShortExplanation = "algorithm that arranges elements in a certain order",
      svLongExplanation = "Sortering är ett algoritmiskt mönster som ordnar elementen i en samling i stigande eller fallande ordning enligt en given ordning; effektiva algoritmer som mergesort och quicksort har O(n log n) tidskomplexitet, och Scala erbjuder inbyggd sortering via 'sorted' och 'sortBy'.",
      enLongExplanation = "Sorting is an algorithmic pattern that arranges the elements of a collection in ascending or descending order according to a given ordering; efficient algorithms such as mergesort and quicksort have O(n log n) time complexity, and Scala provides built-in sorting via 'sorted' and 'sortBy'.",
    )
    val StackTrace = Concept(
      sv = "stack trace",
      en = "stack trace",
      svShortExplanation = "lista anropskedja vid körtidsfel",
      enShortExplanation = "lists the call chain when a runtime error occurs",
      svLongExplanation = "En stack trace är en lista som skrivs ut när ett körtidsfel inträffar och som beskriver den fullständiga anropskedjan från felpunkten upp till programmets startpunkt, med information om klassnamn, metodnamn, filnamn och radnummer.",
      enLongExplanation = "A stack trace is a list printed when a runtime error occurs and describes the complete call chain from the point of error up to the program's starting point, with information about class names, method names, file names, and line numbers.",
    )
    val Statement = Concept(
      sv = "sats",
      en = "statement",
      svShortExplanation = "en kodrad som gör något; kan särskiljas med semikolon",
      enShortExplanation = "a line of code that does something; can be separated by semicolons",
      svLongExplanation = "En sats är en kodkonstruktion som utför en åtgärd, t.ex. en tilldelning, ett metodanrop eller en kontrollstruktur; i Scala är satser även uttryck och har ett värde (ofta Unit), och flera satser på samma rad separeras med semikolon.",
      enLongExplanation = "A statement is a code construct that performs an action, e.g. an assignment, a method call, or a control structure; in Scala statements are also expressions and have a value (often Unit), and multiple statements on the same line are separated by semicolons.",
    )
    val StringInterpolator = Concept(
      sv = "stränginterpolator",
      en = "string interpolator",
      svShortExplanation = "en funktion för att bädda in uttryck i strängar",
      enShortExplanation = "a feature to embed expressions in strings",
      svLongExplanation = "Att 'interpolera' innebär att skjuta in mellanliggande ord i en text. Stränginterpolation är en Scala-funktion som tillåter inbäddning av variabelreferenser och uttryck direkt i strängliteraler med hjälp av s-interpolatorn, som skrives med ett inledande s-tecken.",
      enLongExplanation = "To 'interpolate' means to insert intermediate words in a text. String interpolation is a Scala feature that allows embedding variable references and expressions directly in string literals using the s interpolator, written with a leading s character.",
    )
    val StringType = Concept(
      sv = "sträng",
      en = "string",
      svShortExplanation = "en sekvens av tecken",
      enShortExplanation = "a sequence of characters",
      svLongExplanation = "En sträng  är en oföränderlig sekvens av Unicode-tecken; i Scala är String ett alias för java.lang.String och erbjuder ett rikt urval av metoder för sökning, ersättning, delsträngar och transformation.",
      enLongExplanation = "A string is an immutable sequence of Unicode characters; in Scala String is an alias for java.lang.String and offers a rich set of methods for searching, replacing, substrings, and transformation.",
    )
    val StructuralEquality = Concept(
      sv = "innehållslikhet",
      en = "structural equality",
      svShortExplanation = "instanser anses lika om de har samma tillstånd",
      enShortExplanation = "instances are considered equal if they have the same state",
      svLongExplanation = "Innehållslikhet (strukturell likhet) innebär att två instanser anses lika om deras fältvärden är lika; i Scala implementeras detta med 'equals' och '==', och genereras automatiskt av case-klasser; vanliga klasser ärver referenslikhet från AnyRef som standard.",
      enLongExplanation = "Structural equality means that two instances are considered equal if their field values are equal; in Scala this is implemented with 'equals' and '==', and is automatically generated by case classes; regular classes inherit reference equality from AnyRef by default.",
    )
    val Subtype = Concept(
      sv = "subtyp",
      en = "subtype",
      svShortExplanation = "en typ som är mer specifik",
      enShortExplanation = "a type that is more specific",
      svLongExplanation = "En subtyp är en typ som är mer specifik än sin supertyp och kan användas överallt där supertypen förväntas (Liskovs substitutionsprincip); en subtyp ärver supertypens gränssnitt och kan lägga till nya medlemmar eller överskugga befintliga.",
      enLongExplanation = "A subtype is a type that is more specific than its supertype and can be used anywhere the supertype is expected (Liskov substitution principle); a subtype inherits the supertype's interface and can add new members or override existing ones.",
    )
    val Supertype = Concept(
      sv = "supertyp",
      en = "supertype",
      svShortExplanation = "en typ som är mer generell",
      enShortExplanation = "a type that is more general",
      svLongExplanation = "En supertyp är en typ som är mer generell och vars gränssnitt ärvs av subtyper; en variabel av supertyp kan referera till instanser av vilken subtyp som helst, vilket möjliggör polymorfism och utbytbarhet av implementationer.",
      enLongExplanation = "A supertype is a type that is more general and whose interface is inherited by subtypes; a variable of supertype can refer to instances of any subtype, which enables polymorphism and interchangeability of implementations.",
    )
    val TimeComplexity = Concept(
      sv = "tidskomplexitet",
      en = "time complexity",
      svShortExplanation = "hur exekveringstiden växer med problemstorleken",
      enShortExplanation = "how execution time grows with the size of the problem",
      svLongExplanation = "Tidskomplexitet beskriver hur exekveringstiden för en algoritm växer som funktion av indatastorleken n, uttryckt med O-notation; t.ex. är O(1) konstant, O(n) linjär, O(n log n) typisk för effektiv sortering och O(n²) kvadratisk tidskomplexitet.",
      enLongExplanation = "Time complexity describes how the execution time of an algorithm grows as a function of the input size n, expressed in O-notation; e.g. O(1) is constant, O(n) linear, O(n log n) typical for efficient sorting, and O(n²) quadratic time complexity.",
    )
    val Trait = Concept(
      sv = "trait",
      en = "trait",
      svShortExplanation = "är abstrakt, kan mixas in, kan ha parametrar",
      enShortExplanation = "is abstract, can be mixed in, can have parameters",
      svLongExplanation = "En trait är en abstrakt typ som kan ha både abstrakta och konkreta medlemmar; den kan mixas in i klasser med 'with', kan i Scala 3 ha konstruktorparametrar, och möjliggör ett slags multipelt arv av beteenden utan diamantproblemet.",
      enLongExplanation = "A trait is an abstract type that can have both abstract and concrete members; it can be mixed into classes with 'with', can in Scala 3 have constructor parameters, and enables a form of multiple inheritance of behaviors without the diamond problem.",
    )
    val Type = Concept(
      sv = "typ",
      en = "type",
      svShortExplanation = "beskriver vad data kan användas till",
      enShortExplanation = "describes what data can be used for",
      svLongExplanation = "En typ är en etikett på data som beskriver vilka operationer som är tillåtna och vilka värden som är möjliga; Scalas statiska typsystem kontrollerar typkorrekthet vid kompilering och förhindrar många klasser av fel innan programmet körs.",
      enLongExplanation = "A type is a label on data that describes which operations are permitted and which values are possible; Scala's static type system checks type correctness at compile time and prevents many classes of errors before the program is run.",
    )
    val TypeAlias = Concept(
      sv = "typalias",
      en = "type alias",
      svShortExplanation = "alternativt namn på typ som ofta ökar läsbarheten",
      enShortExplanation = "alternative name for a type that often improves readability",
      svLongExplanation = "Ett typalias, definierat med 'type Namn = BefintligTyp', skapar ett alternativt namn för en typ och ökar kodens läsbarhet och domänuttryckskraft utan att påverka körbeteendet; typalias kan parameteriseras och kan vara abstrakta i traits.",
      enLongExplanation = "A type alias, defined with 'type Name = ExistingType', creates an alternative name for a type and increases code readability and domain expressiveness without affecting runtime behavior; type aliases can be parameterized and can be abstract in traits.",
    )
    val TypeArgument = Concept(
      sv = "typargument",
      en = "type argument",
      svShortExplanation = "konkret typ, binds till typparameter vid kompilering",
      enShortExplanation = "concrete type, bound to a type parameter at compile time",
      svLongExplanation = "Ett typargument är en konkret typ som anges inom hakparenteser när en generisk klass eller funktion används, t.ex. 'Vector[Int]' där 'Int' är typargumentet; kompilatorn binder typargumentet till typparametern och kontrollerar att det är korrekt använt.",
      enLongExplanation = "A type argument is a concrete type specified in square brackets when a generic class or function is used, e.g. 'Vector[Int]' where 'Int' is the type argument; the compiler binds the type argument to the type parameter and checks that it is used correctly.",
    )
    val TypeInference = Concept(
      sv = "typhärledning",
      en = "type inference",
      svShortExplanation = "kompilatorn beräknar typ ur sammanhanget",
      enShortExplanation = "the compiler infers the type from context",
      svLongExplanation = "Typhärledning innebär att Scala-kompilatorn automatiskt bestämmer typen för ett uttryck eller en variabel utifrån sammanhanget, så att explicita typannoteringar ofta kan utelämnas; detta minskar kodmängden utan att försaka typsäkerheten.",
      enLongExplanation = "Type inference means that the Scala compiler automatically determines the type of an expression or variable from context, so that explicit type annotations can often be omitted; this reduces the amount of code without sacrificing type safety.",
    )
    val UniformAccess = Concept(
      sv = "enhetlig access",
      en = "uniform access",
      svShortExplanation = "ändring mellan def och val påverkar ej användning",
      enShortExplanation = "switching between def and val does not affect usage",
      svLongExplanation = "Principen om enhetlig access innebär att en egenskap kan implementeras antingen som ett attribut (val/var) eller en metod (def) utan att anropande kod behöver ändras; detta ger frihet att byta implementation utan att påverka klassens gränssnitt.",
      enLongExplanation = "The uniform access principle means that a property can be implemented either as an attribute (val/var) or a method (def) without requiring changes to the calling code; this gives freedom to change the implementation without affecting the class's interface.",
    )
    val ValueType = Concept(
      sv = "värdetyp",
      en = "value type",
      svShortExplanation = "har supertypen AnyVal, lagras direkt på stacken",
      enShortExplanation = "has supertype AnyVal, stored directly on the stack",
      svLongExplanation = "En värdetyp är en typ vars instanser lagras direkt på stacken eller inline i ett objekt och kopieras vid tilldelning; i Scala är Int, Double, Boolean, Char m.fl. värdetyper med supertypen AnyVal och boxas automatiskt till objekt vid behov.",
      enLongExplanation = "A value type is a type whose instances are stored directly on the stack or inline in an object and copied on assignment; in Scala Int, Double, Boolean, Char, etc. are value types with supertype AnyVal and are automatically boxed to objects when needed.",
    )
    val Variable = Concept(
      sv = "variabel",
      en = "variable",
      svShortExplanation = "kan initialiseras, refereras och eventuellt ändras med tilldelning",
      enShortExplanation = "can be initialized, referenced, and optionally changed by assignment",
      svLongExplanation = "En variabel används för att ge datavärden ett namn. I de delar av koden där variabeln är synlig kan värdet refereras via variabelns namn. En variable deklareras med val eller var och ges då ett initialvärde. En val-variabel är oföränderlig och kan ej ändras efter initialisering medan en var-variabel kan tilldelas nya värden efter initialisering genom tilldelning.",
      enLongExplanation = "A variable is used to give data values a name. In the parts of the code where the variable is visible, the value can be referred to by the variable name. A variable is declared with val or var and given an initial value. A val variable is immutable and cannot be changed after initialization, while a var variable can be assigned new values after initialization.",
    )
    val Vector = Concept(
      sv = "Vector",
      en = "Vector",
      svShortExplanation = "en oföränderlig, indexerbar sekvenssamling",
      enShortExplanation = "an immutable, indexable sequence collection",
      svLongExplanation = "Vector är Scalas standardval för oföränderliga, indexerbara sekvenssamlingar; den har effektiv (nästan O(1)) åtkomst, uppdatering och tillägg tack vare en intern trädbaserad (trie) struktur och rekommenderas som default-sekvenssamling i Scala.",
      enLongExplanation = "Vector is Scala's default choice for immutable, indexable sequence collections; it has efficient (near O(1)) access, update, and append operations thanks to an internal tree-based (trie) structure and is recommended as the default sequence collection in Scala.",
    )
    val WhileStatement = Concept(
      sv = "while-sats",
      en = "while statement",
      svShortExplanation = "bra då antalet repetitioner ej är bestämt i förväg",
      enShortExplanation = "good when the number of repetitions is not known in advance",
      svLongExplanation = "En while-sats upprepar sin kropp så länge ett booleskt villkor är sant; antalet iterationer behöver inte vara känt i förväg, vilket gör den lämpad för algoritmer som körs tills ett villkor är uppfyllt, t.ex. inläsning tills filen är slut.",
      enLongExplanation = "A while statement repeats its body as long as a boolean condition is true; the number of iterations need not be known in advance, making it suitable for algorithms that run until a condition is met, e.g. reading until end of file.",
    )
    val Yield = Concept(
      sv = "yield",
      en = "yield",
      svShortExplanation = "används i for-uttryck för att skapa ny samling",
      enShortExplanation = "used in for-expressions to create a new collection",
      svLongExplanation = "Nyckelordet 'yield' används i ett for-uttryck för att samla upp de producerade värdena i en ny samling; typen på den nya samlingen är densamma som den iterade samlingens typ, och for-yield är syntaktisk socker för flatMap/map-kombinationer.",
      enLongExplanation = "The 'yield' keyword is used in a for-expression to collect the produced values into a new collection; the type of the new collection matches the type of the iterated collection, and for-yield is syntactic sugar for flatMap/map combinations.",
    )
