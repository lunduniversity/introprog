package cslib.shapes;

import cslib.shapes.Shape;
import cslib.window.SimpleWindow;

/**
 * Describes a list of figure objects (objects of class Shape).
 * NOTE: this is just a skeleton, you will need to write this class yourself.
 */
public class ShapeList {
	/**
	 * Creates an empty list.
	 */
	public ShapeList() {
		// ...
	}

	/**
	 * Adds a figure to the list.
	 * 
	 * @param s
	 *            the figure to be added to the list
	 */
	public void insert(Shape s) {
		// ...
	}

	/**
	 * Draws the figures in the list.
	 * 
	 * @param w
	 *            the window where the figures are drawn
	 */
	public void draw(SimpleWindow w) {
		// ...
	}

	/**
	 * Determines a figure that is close to the point xc,yc. If multiple figures
	 * are close, then the first one found is returned; if no figure is close,
	 * null is returned.
	 * 
	 * @param xc
	 *            x-coordinate
	 * @param yc
	 *            y-coordinate
	 */
	public Shape findHit(int xc, int yc) {
		// ...
		return null;
	}
}
