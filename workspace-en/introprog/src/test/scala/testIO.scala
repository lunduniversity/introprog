package introprog

val tmpDir = "target/tmp"
def createTmp(): Boolean = IO.createDirIfNotExist(tmpDir)

class TestIO extends munit.FunSuite:

  test("TestIO: createDirIfNotExist"):
    val existed = createTmp()
    assert(IO.isExisting(tmpDir), s"dir should exists: $tmpDir")

  test("TestIO: saveString, loadString, appendString, loadLines, appendLines"):
    createTmp()
    val s1 = "hello"
    val fn = s"$tmpDir/hello.txt"
    IO.saveString(s1, fileName = fn)
    val s2 = IO.loadString(fileName = fn)
    assertEquals(s1, s2, "saved string different from loaded")
    IO.appendString("!\n", fileName = fn )
    val s3 = IO.loadString(fileName = fn)
    assertEquals(s3, s2 + "!\n", "saved string is missing appended '!+newline'")
    IO.appendLines(Seq("line2"),fileName = fn)
    val s4 = IO.loadLines(fileName = fn)
    assertEquals(s4, Vector("hello!", "line2"), s"loadLines not as expected: $s4")
    val s5 = IO.loadString(fileName = fn)
    assertEquals(s5, "hello!\nline2\n", s"loadLines not as expected: $s5")
    IO.appendLines(Seq(),fileName = fn) // nothing should be added, not even newline
    assertEquals(s5, IO.loadString(fileName = fn), s"loadLines not as expected: $s5")
