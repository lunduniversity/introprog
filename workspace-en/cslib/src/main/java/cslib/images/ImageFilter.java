package cslib.images;

import java.awt.Color;

/**
 * Superclass for all filter classes.
 * @version 1.2 (2016-07-17) nbrOfArgs attribute has been added 
 * along with a getNumberOfArguments-method (Casper Schreiter, Björn Regnell)
 */
public abstract class ImageFilter {
	private String name; 		// filtrets namn
	private int nbrOfArgs; 		// number of arguments

	/**
	 * Creates a filter object with a given name and the number of arguments the filter requires.
	 * 
	 * @param name
	 *            the filter's name
	 * @param nbrOfArgs
	 *            the number of arguments
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
 * Determines the number of arguments the filter requires.
 * 
 * @return the number of arguments needed by the apply method.
	 */
	public int getNumberOfArguments() {
		return nbrOfArgs;
	}

	/**
	 * Filtering the image in the matrix inPixels and returning the result in a new
	 * matrix. May utilize values from args
	 * 
	 * @param inPixels
	 *            the original image
	 * @param args
	 *            arguments
	 * @return the filtered image
	 */
	public abstract Color[][] apply(Color[][] inPixels, double[] args);
	
	/**
	 * Calculates the intensity of all pixels in pixels, returning the result in
	 * a new matrix.
	 * 
	 * @param pixels
	 *            matrix with pixels
	 * @return the intensity of each pixel

*
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
	 * Missing the point p[i][j] with folding key kernel.
	 * 
	 * @param p
	 *            matrix with numerical values
	 * @param i
	 *            row index for the current point
	 * @param j
	 *            column index for the current point
	 * @param kernel
	 *            the folding key, a 3x3-matrix
	 * @param weight
	 *            sum of elements in kernel
	 * @return result of the convolution
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