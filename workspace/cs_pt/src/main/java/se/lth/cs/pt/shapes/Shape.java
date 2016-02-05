package se.lth.cs.pt.shapes;

import se.lth.cs.pt.window.SimpleWindow;

/**
 * Beskriver en figur som har ett läge.
 */
public abstract class Shape {
	/** x-koordinat för figurens läge. */
	protected int x;
	/** y-koordinat för figurens läge. */
	protected int y;

	/**
	 * Skapar en figur med ett givet läge.
	 * 
	 * @param x
	 *            x-koordinaten för läget
	 * @param y
	 *            y-koordinaten för läget
	 */
	protected Shape(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Ritar upp figuren i ett fönster.
	 * 
	 * @param w
	 *            fönstret där figuren ritas
	 */
	public abstract void draw(SimpleWindow w);

	/**
	 * Raderar bilden av figuren, flyttar figuren till newX,newY och ritar upp
	 * den på sin nya plats i fönstret. Raderingen görs genom att figuren ritas
	 * över med vit färg, vilket medför att korsande linjer också raderas.
	 * 
	 * @param w
	 *            fönstret där bilden finns och ska ritas igen
	 * @param newX
	 *            x-koordinaten för det nya läget
	 * @param newY
	 *            y-koordinaten för det nya läget
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
	 * Undersöker om punkten xc,yc ligger "nära" figuren. Med "nära" menas inom
	 * 10 pixlar från den punkt som definierar punktens läge.
	 * 
	 * @param xc
	 *            x-koordinat
	 * @param yc
	 *            y-koordinat
	 */
	public boolean near(int xc, int yc) {
		return Math.abs(x - xc) < 10 && Math.abs(y - yc) < 10;
	}
}
