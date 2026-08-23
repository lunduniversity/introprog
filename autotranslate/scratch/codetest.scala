//> using scala 3.8.4
//> using jvm 21
//> using file ../Code.scala

// Test the Code lexer with a MOCK translator that wraps each selected piece in <<...>>, so we can SEE
// exactly which spans (comments/Swedish-strings) get handed to `tr` and that code stays verbatim.
//   scala-cli run autotranslate/scratch/codetest.scala -- <file.scala>
@main def codetest(file: String): Unit =
  val src = String(java.nio.file.Files.readAllBytes(java.nio.file.Path.of(file)), "UTF-8")
  print(Code.translate(src, s => s"<<$s>>"))
