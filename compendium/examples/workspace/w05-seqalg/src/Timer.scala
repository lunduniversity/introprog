object Timer:
  private var startTime: Long = System.currentTimeMillis

  def elapsedMillis: Long = System.currentTimeMillis - startTime

  def reset: Unit = 
    startTime = System.currentTimeMillis

  def measure[T](block: => T): (Long, T) =
    reset
    val result = block
    (elapsedMillis, result)
