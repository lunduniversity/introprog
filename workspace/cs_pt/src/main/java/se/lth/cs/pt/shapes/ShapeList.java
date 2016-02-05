package se.lth.cs.pt.shapes;

import se.lth.cs.pt.shapes.Shape;
import se.lth.cs.pt.window.SimpleWindow;

/**
 * Beskriver en lista av figurobjekt (objekt av klassen Shape).
 * OBSERVERA: bara skelett, du ska själv skriva denna klass.
 */
public class ShapeList {
	/**
	 * Skapar en tom lista.
	 */
	public ShapeList() {
		// ...
	}

	/**
	 * Lägger in en figur i listan.
	 * 
	 * @param s
	 *            figuren som ska läggas in i listan
	 */
	public void insert(Shape s) {
		// ...
	}

	/**
	 * Ritar upp figurerna i listan.
	 * 
	 * @param w
	 *            fönstret där figurerna ritas
	 */
	public void draw(SimpleWindow w) {
		// ...
	}

	/**
	 * Tar reda på en figur som ligger nära punkten xc,yc. Om flera figurer
	 * ligger nära så returneras den första som hittas, om ingen figur ligger
	 * nära returneras null.
	 * 
	 * @param xc
	 *            x-koordinaten
	 * @param yc
	 *            y-koordinaten
	 */
	public Shape findHit(int xc, int yc) {
		// ...
		return null;
	}
}
