  def spawn(codeBlock: => Unit) = {
    val t = new Thread(new Runnable { def run { codeBlock } })
    t.start
    t
  }