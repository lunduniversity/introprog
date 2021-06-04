// kod till facit

object fib:
  import java.util.concurrent.ConcurrentHashMap
  val memcache = new ConcurrentHashMap[BigInt, BigInt]
  
  def apply(n: BigInt): BigInt =  
    if memcache.containsKey(n) then 
      println("CACHE HIT!!! FOUND: " + n)
      memcache.get(n)
    else
      println("cache miss :( could not find: " + n)
      val f = fastFib(n)
      memcache.put(n, f)
      f
  
  private def fastFib(n: BigInt): BigInt = 
    if n <= 0 then 0 
    else if n == 1 || n == 2 then 1 
    else
      var secondLast: BigInt = 1
      var last: BigInt = 1
      var sum: BigInt = secondLast + last
      var i = 3
      while i < n do
        if memcache.containsKey(i) then
          sum = memcache.get(i)
        else 
          secondLast = last
          last = sum
          sum = secondLast + last
          memcache.put(i, sum)
        i += 1
      sum

