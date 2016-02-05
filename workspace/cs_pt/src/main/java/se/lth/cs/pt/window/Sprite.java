package se.lth.cs.pt.window;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Hanterar en Sprite som kan ritas, flyttas och roteras i SimpleWindow.
 */
public class Sprite extends JLabel {
	private static final long serialVersionUID = 1L;
	private Image img;
	private int width, height;
	private boolean visible = true;
	private int x, y;
	private double direction = 0;  //grader, inital rikting uppåt

	/**
	 * Skapar en ikon från en bild specificerad i filepath av angiven bredd och
	 * höjd.
	 * 
	 * @param filePath
	 * @param width
	 * @param height
	 */

	public Sprite(String filePath, int width, int height) {
		File imgFile = new File(filePath);
		try {
			img = ImageIO.read(imgFile);
			img = img.getScaledInstance(width, height, 0);

			setIcon(new ImageIcon(img));
			this.height = height;
			this.width = width;
			setSize(width, height);

		} catch (IOException e) {
			throw new Error(e);
		}
	}

	/**
	 * Roterar spriten till ett absolut vinkelvärde.  
	 * @param deg    vinkeln i grader moturs från den positiva x-axeln.
	 */
	public void setDirection(double deg) {
		
		direction = deg;

		double sin = Math.sin(Math.toRadians(deg));
		double cos = Math.cos(Math.toRadians(deg));
		int w = (int) Math.round(Math.abs(height * sin) + Math.abs(cos * width));

		int h = (int) Math.round(Math.abs(height * cos) + Math.abs(width * sin));

		int type = BufferedImage.TYPE_INT_ARGB;
		BufferedImage image = new BufferedImage(h, w, type);
		Graphics2D g2 = image.createGraphics();
		double x = (h - w) / 2.0;
		double y = (w - h) / 2.0;
		AffineTransform at = AffineTransform.getTranslateInstance(x, y);
		at.rotate(-Math.toRadians(deg), width / 2.0, height / 2.0);
		g2.drawImage(img, at, this);

		g2.dispose();
		setIcon(new ImageIcon(image));
		setSize(image.getWidth(), image.getHeight());
		repaintParent();
	}
	
	/**
	 * Returnerar riktningen i grader moturs från positiva x-axeln.
	 */
	public double getDirection(){
		return direction;
	}
	
	/**
	 * Roterar spriten ett relativt vinkelvärde.  
	 * @param deg    vinkeln i grader moturs.
	 */	
	public void rotate(double deg){
		direction = direction + deg;
		setDirection(direction);
	}

	/**
	 * Returnerar x-positionen av bilden.
	 */
	public int getX() {
		return x;
	}

	/** Returnerar y-positionen av bilden. */
	public int getY() {
		return y;
	}

	/** Returnerar x-positionen av mitten på bilden. */
	public int getMidX() {
		return x + width / 2;
	}

	/** Returnerar y-positionen av mitten på bilden. */
	public int getMidY() {
		return y + height / 2;
	}

	/** Flyttar bilden till angiven punkt (x, y). */
	public void moveTo(int x, int y) {
		this.x = x;
		this.y = y;
		setLocation(x, y);
		repaintParent();
	}

	/** Flyttar bilden relativt nuvarande position i nuvarande riktning. */
	public void forward(double length) {
		double dx = length *  Math.sin(Math.toRadians(direction-180));
		double dy = length *  Math.cos(Math.toRadians(direction-180));
		moveTo((int) Math.round(x+dx), (int) Math.round(y+dy));
	}

	/** Visar eller gömmer bilden, beroende på parameterns värde. */
	public void setVisible(boolean visible) {
		this.visible = visible;
		repaintParent();
	}

	/** Ger true om bilden är synlig. */
	public boolean isVisible() {
		return visible;
	}

	/** Överskuggar hur bilden ritas. */
	public void paintComponent(Graphics g) {
		if (visible) {
			super.paintComponent(g);
		} 
	}

	/** Flyttar mittpunkten av bilden till positionen (x, y). */
	public void moveMidTo(int x, int y) {
		moveTo(x - width / 2, y - height / 2);
	}

	private void repaintParent() {
		Component parent = getParent();
		if (parent != null) {
			parent.repaint();
		}
	}
}
