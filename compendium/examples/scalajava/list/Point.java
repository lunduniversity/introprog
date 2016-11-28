package week10.list;

public class Point {
	private int x;
	private int y;
	
	public Point(int x, int y){
		this.x = x;
		this.y = y;
	}; 

	public int getX(){
		return x;
	};

	public int getY(){
		return y;
	};

	public void move(int dx, int dy){
		x += dx;
		y += dy;
	};
	
	public double getDistanceTo(Point p) {
		int xDist = getX() - p.getX();
		int yDist = getY() - p.getY();
		return Math.sqrt(xDist * xDist + yDist * yDist);
	}
	
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}
}
