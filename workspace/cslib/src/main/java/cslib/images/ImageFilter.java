package cslib.images;

import java.awt.Color;

/**
 * Superklassen till alla filterklasser.
 * @version 1.2 (2016-07-17) nbrOfArgs attribut har lagts till 
 * samt en getNumberOfArguments-metod (Casper Schreiter, Björn Regnell)
 */
public abstract class ImageFilter {
	private String name; 		// filtrets namn
	private int nbrOfArgs; 		// antal argument

	/**
	 * Skapar ett filterobjekt med ett givet namn och antalet argument filtret behöver.
	 * 
	 * @param name
	 *            filtrets namn
	 * @param nbrOfArgs
	 *            antal argument
	 */
	protected ImageFilter(String name, int nbrOfArgs) {
		this.name = name;
		this.nbrOfArgs = nbrOfArgs;		
	}
	

	/**
	 * Tar reda på filtrets namn.
	 * 
	 * @return filtrets namn
	 */
	public String getName() {
		return name;
	}

	/**
	 * Tar reda på antalet argument filtret behöver.
	 *  
	 * @return antalet argument som behövs till apply-metoden.
	 */
	public int getNumberOfArguments() {
		return nbrOfArgs;
	}

	/**
	 * Filtrerar bilden i matrisen inPixels och returnerar resultatet i en ny
	 * matris. Utnyttjar eventuellt värdena i args
	 * 
	 * @param inPixels
	 *            den ursprungliga bilden
	 * @param args
	 *            argument
	 * @return den filtrerade bilden
	 */
	public abstract Color[][] apply(Color[][] inPixels, double[] args);
	
	/**
	 * Beräknar intensiteten hos alla pixlarna i pixels, returnerar resultatet i
	 * en ny matris.
	 * 
	 * @param pixels
	 *            matris med pixlar
	 * @return intensiteten i varje pixel
	 */
	protected short[][] computeIntensity(Color[][] pixels) {
		int height = pixels.length;
		int width = pixels[0].length;
		short[][] intensity = new short[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Color c = pixels[i][j];
				intensity[i][j] = (short) ((c.getRed() + c.getGreen() + c
						.getBlue()) / 3);
			}
		}
		return intensity;
	}

	/**
	 * Faltar punkten p[i][j] med faltningskärnan kernel.
	 * 
	 * @param p
	 *            matris med talvärden
	 * @param i
	 *            radindex för den aktuella punkten
	 * @param j
	 *            kolonnindex för den aktuella punkten
	 * @param kernel
	 *            faltningskärnan, en 3x3-matris
	 * @param weight
	 *            summan av elementen i kernel
	 * @return resultatet av faltningen
	 */
	protected short convolve(short[][] p, int i, int j, short[][] kernel,
			int weight) {
		short sum = 0;
		for (int ii = -1; ii <= 1; ii++) {
			for (int jj = -1; jj <= 1; jj++) {
				sum += p[i + ii][j + jj] * kernel[ii + 1][jj + 1];
			}
		}
		return (short) Math.round((double) sum / weight);
	}
}