package cslib.square;

import cslib.window.SimpleWindow;

public class Square {
	private int x;		// coordinates for x- and y- 
	private int y;		// the upper left corner
	private int side;	// screenHeight

	/** Creates a square with the upper-left corner at x,y and with side length side. */
	public Square(int x, int y, int side) {
		this.x = x;
		this.y = y;
		this.side = side;
	}
	
	/** Draws the square in window w. */
	public void draw(SimpleWindow w) {
		w.moveTo(x, y) ;
		w.lineTo(x, y + side);
		w.lineTo(x + side, y + side);
		w.lineTo(x + side, y);
		w.lineTo(x, y);
	}
	
	/** Removes the image of the square from window w. */
    public void erase(SimpleWindow w) {
		w.setLineColor(java.awt.Color.white);
		draw(w);
		w.setLineColor(java.awt.Color.black);
  	}		
    
    /** Moves the square's position by dx in the x-direction, dy in the y-direction. */
	public void move(int dx, int dy) {
		x = x + dx;
		y = y + dy;
	}

	/** Find the x-coordinate for the position of the square. */
	public int getX() {
		return x;
	}
	
	/** Find the y-coordinate for the position of the square. */
	public int getY() {
		return y;
	}
	
	/** Change the square's side to side. */
	public void setSide(int side) {
		this.side = side;
	}
	
	/** Find out the side of the square. */
	public int getSide() {
		return side;
	}
	
	/** Find out the square's area. */
	public int getArea() {
		return side * side;
	}
}
