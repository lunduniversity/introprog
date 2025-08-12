//> using scala 3.7.2
//> using java-opt -Dfile.encoding=UTF-8
// The UTF-8 java option is needed when the "Current system locale" is set to "English"

import java.nio.charset.{StandardCharsets as C}

@main def run =
    val s = scala.io.StdIn.readLine("<123ABCÅÄÖåäö> ")
    println(s"You wrote: '$s'")
    val cs =
        Seq(C.UTF_8, C.ISO_8859_1, C.UTF_16, C.UTF_16BE, C.UTF_16LE)
    for c <- cs do
        val otherworldly = String(s.getBytes, c)
        println(s"in other world of $c: '$otherworldly'")
    
    println(s"The bytes: ${s.getBytes().mkString(",")}")
    println(System.getProperty("file.encoding"))
    println(System.getProperty("native.encoding"))