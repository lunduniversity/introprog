package week10.generics;

import week10.list.Point;

class PointCanEquals extends Point {
	public PointCanEquals(int x, int y) {
		super(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Point) {
			Point p = (Point) obj;
			return getX() == p.getX() && getY() == p.getY();
		} else {
			return false;
		}
	}
}
