package photo

import introprog.{Image, PixelWindow, IO}
import scala.collection.mutable.Stack
import scala.util.Random

class ImageEditor(filters: Array[ImageFilter]):
  
	val history = ???

	/** 
	 * Show which filters are available and let the user choose a filter to apply. 
	 * Draw edited image in PixelWindow.
	 * User can then add more filters, undo, save image or exit.
	 *  
	 *  Example: 
	 *  0. för Blått-filter
	 *	2. för Kontrast-filter
	 * 	3. för Gauss-filter
	 *	4. för Sobel-filter
	 *	a. AVBRYT
	 *	s. SPARA
	 *	z. UNDO
   */
	def edit(im: Image): Unit = ???

	/**Ask for arguments if required and apply filter with index `i`*/
	private def applyFilter(index: Int) = ???

  
  
