package cslib.shapes;

import cslib.window.SimpleWindow;

/** 
 * Describes a figure that has a state.
 */
public abstract class Shape {
	/** x-coordinate for the figure's position. */
	protected int x;
	/** y-coordinate for the figure's position. */
	protected int y;

	/**
	 * Creates a figure with a given position.
	 * 
	 * @param x
	 *            the x-coordinate of the position
	 * @param y
	 *            the y-coordinate of the position
	 */
	protected Shape(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Draws the figure in a window.
	 * 
	 * @param w
	 *            the window where the figure is drawn
	 */
	public abstract void draw(SimpleWindow w);

	/**
	 * Erases the figure's image, moves the figure to newX,newY, and redraws it at its new position in the window. The erasing is done by redrawing the figure with white color, which also removes any intersecting lines.
	 * 
	 * @param w
	 *            the window where the image exists and should be redrawn
	 * @param newX
	 *            the x-coordinate for the new position
	 * @param newY
	 *            the y-coordinate for the new position
	 */
	public void moveToAndDraw(SimpleWindow w, int newX, int newY) {
		java.awt.Color savedColor = w.getLineColor();
		w.setLineColor(java.awt.Color.WHITE);
		draw(w);
		x = newX;
		y = newY;
		w.setLineColor(savedColor);
		draw(w);
	}

	/**
	 * Checks if the point xc,yc is "near" the figure. With "near" meaning within
	 * 10 pixels of the point that defines the position of the point.
	 * 
	 * @param xc
	 *            x-coordinate
	 * @param yc
	 *            y-coordinate
	 */
	public boolean near(int xc, int yc) {
		return Math.abs(x - xc) < 10 && Math.abs(y - yc) < 10;
	}
}
