package photo

import introprog.Image
/**
 * (2021-07-25) translate from java. Replaced nbrOfArgs with args.
 * (Theodor Lundqvist)
 * 
 * (2016-07-17) nbrOfArgs attribut has been added.
 * (Casper Schreiter, Björn Regnell)
 * 
 */

/**
 * The super class to all filters.
 * Create a filter object with a given name and argument descriptions.
 * @param name
 *            the name of the filter.
 * @param args
 *            optional array of strings with argument descriptions
 */
abstract class ImageFilter(val name: String, val argDescriptions: String*):

    /**The number of args this filter needs*/
    def nbrOfArgs = argDescriptions.length

    /**
     * Apply the filter on `img` and return the result as a new Image using the arguments in `args`.
        * 
        * @param img
        *            the original image.
        * @param args
        *            arguments
        * @return the resulting image.
        */
    def apply(img: Image, args: Double*): Image;

    /**
     * Calculate the intensity in each pixel of `img`.
        * 
        * @param img
        *           the image
        * @return intensitymatrix, values ranging from 0 to 255
        */
    protected def computeIntensity(img: Image): Array[Array[Short]] = 
        val intensity : Array[Array[Short]] = Array.ofDim(img.width, img.height)
        for 
            w <- 0 until img.width
            h <- 0 until img.height 
        do
            val c = img(w, h)
            intensity(w)(h) = ((c.getRed()+c.getGreen+c.getBlue())/3).toShort
        intensity

    /**
     * Convolute `p[i][j]` with the convolutionkernel `kernel`.
        * 
        * @param p
        *            matrix with numbervalues
        * @param i
        *            current row index
        * @param j
        *            current coloumn index
        * @param kernel
        *            convolutionkernel, a 3x3-matrix
        * @param weight
        *            the sum of the element in `kernel`
        * @return result of the convolution
        */
    protected def convolve(p: Array[Array[Short]], i: Int, j: Int, kernel: Array[Array[Short]],
            weight: Int): Short =
        var sum : Double = 0;

        for ii <- -1 to 1 do
            for jj <- -1 to 1 do
                sum += p(i + ii)(j + jj) * kernel(ii + 1)(jj + 1);

        Math.round(sum / weight).toShort;

