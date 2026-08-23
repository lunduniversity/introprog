//> using scala 3.8.3

@main def run(x0: Double, eps: Double) = println(NewtonRaphson.solve(x0, eps))

object NewtonRaphson:
  def f(x: Double) = math.exp(-x) - math.sin(x)

  def fprim(x: Double) = -math.exp(-x) - math.cos(x)

  def solve(start: Double, eps: Double) = 
    var x0 = start
    var x1 = start
    while {
      x0 = x1
      x1 = x0 - f(x0) / fprim(x0)
      math.abs(x1 - x0) > math.abs(x1) * eps
    } do ()  
    x1