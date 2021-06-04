object ActivationRecord:
  var stackDepth = 0

  def activate(record: String): Unit =
    stackDepth += 1
    println(("." * stackDepth) + s"CALL $record")

  def ready(value: Int): Int =
    println((" " * stackDepth) + s"RETURN, stackDepth = $stackDepth")
    stackDepth -= 1
    value

  def inc(x: Int) =
    activate(s"inc(x = $x)")
    ready(x + 1)

  def dec(x: Int) =
    activate(s"dec(x = $x)")
    ready(-inc(-x))

  def add(x: Int, y: Int) =
    activate(s"add(x = $x, y = $y)")
    var result = x
    var i = 0
    while i < math.abs(y) do
      result = if y >= 0 then inc(result) else dec(result)
      i = i + 1
    ready(result)

  def main(args: Array[String]): Unit =
    val result = add(5, add(1, add(3, -3)))
    println(result)
