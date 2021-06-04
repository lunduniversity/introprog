val p1 = Polygon(Point(10,10), Point(30, 10), Point(30, 20), Point(10, 20))
val p2 = p1.move(100, 100)
val ps: Vector[Movable] = Vector(p1, Point())
def moveAll(xs: Seq[Movable], dx: Double, dy: Double): Seq[Movable] =
  xs.map(_.move(dx, dy))

val u0 = Point(0)
val u1 = p1.pts(0)
val u2 = p2.pts.apply(100)
val u3 = moveAll(ps, 100, 100).drop(1).head
val u4 = Point(100, 100).distanceTo()
val u5 = 
  val z = Point()
  val pts = p1.pts ++ p2.pts
  if pts.length > 0 then 
    var p = pts(0)
    var i = 1
    while (i < pts.length) do
      if pts(i).distanceTo(z) > p.distanceTo(z) 
      then p = pts(i)
      i += 1
    p
  else z

