package week10.generics;

import java.util.ArrayList;
import week10.list.Point;

public class TestPitfall3 {

	public static void main(String[] args) {
		ArrayList<Point> points = new ArrayList<Point>();
		Point p = new Point(0,0);
		points.add(p);
		System.out.println(points.contains(p));
		System.out.println(points.contains(new Point(0,0)));
		System.out.println(points.contains(new PointCanEquals(0,0)));
		
		PolygonCanContains poly = new PolygonCanContains();
		poly.addVertex(0, 0);
		poly.addVertex(10, 20);
		poly.addVertex(20, 10);
		
		if (poly.hasVertex(0, 0)) {  // Problem med hasVertex()!!
			System.out.println("(0,0) FINNS :)");
		} else {
			System.out.println("(0,0) FINNS INTE :(");
		}
		
		if (poly.contains(0, 0)) {
			System.out.println("(0,0) FINNS :)");
		} else {
			System.out.println("(0,0) FINNS INTE :(");
		}
	}

}
