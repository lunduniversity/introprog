package cslib.maze;

import cslib.window.SimpleWindow;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Describes a maze. The maze definition is read from a file.
 * 
 */
public class Maze {
	private int xEntry, yEntry; // entry coordinates

	private BufferedImage image; // image data (only for drawing)
	private boolean[][] bitmap;  // labyrinth data, derived from image data

	private static final int BORDER_WIDTH = 20; // for a blank border around the maze

	private static final String MAZE_PATH = "/se/lth/cs/pt/maze/"; // path for maze files (within JAR)

	/**
	 * Creates a maze with number nbr. The maze definition is read from the file
	 * maze[nbr].maze, which must be in cs_pt.jar, which must be on the class
	 * path.
	 *
	 * @param nbr
	 *            the number of the maze
	 */
	public Maze(int nbr) {
		try {
			String filename = MAZE_PATH + "maze" + nbr + ".png";

			InputStream is = getClass().getResourceAsStream(filename);
			if (is == null) {
				throw new Error("cannot find maze file " + filename + " in JAR");
			}

			// decide on entry point for maze
			switch (nbr) {
			case 1:
			case 2:
			case 3:
				initialize(is, 105, 203);
				break;
			case 4:
				initialize(is, 165, 323);
				break;
			case 5:
				initialize(is, 162, 320);
				break;
			default:
				throw new Error("invalid maze");
			}
		}
		catch (IOException e) {
			throw new Error(e);
		}
	}

	/**
	 * Creates a maze based on an image file (.png or .jpg).
	 *
	 * @param filename
	 *                 name of image file
	 * @param xEntry
	 *                 x coordinate of the maze's entry point
	 * @param yEntry
	 *                 y coordinate of the maze's entry point
	 */
	public Maze(String filename, int xEntry, int yEntry) {
		try {
			initialize(new FileInputStream(new File(filename)), xEntry, yEntry);
		} catch (IOException e) {
			throw new Error(e);
		}
	}

	/**
	 * Draws the maze in a window.
	 * 
	 * @param w
	 *            the SimpleWindow to draw in
	 */
	public void draw(SimpleWindow w) {
		w.moveTo(BORDER_WIDTH, BORDER_WIDTH);
		w.drawImage(image);
	}

	/**
	 * Returns the x coordinate of the entry point.
	 * 
	 * @return the x coordinate
	 */
	public int getXEntry() {
		return xEntry;
	}

	/**
	 * Returns the y coordinate of the entry point.
	 * 
	 * @return the y coordinate
	 */
	public int getYEntry() {
		return yEntry;
	}

	/**
	 * Checks if the point x,y is at (or near) the exit.
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @return true if the point is at the exit, false otherwise
	 */
	public boolean atExit(int x, int y) {
		// the x coordinate can vary in many images
		return y < BORDER_WIDTH;
	}

	/**
	 * Check if you, when you're in x,y and heading in a given direction, have a
	 * wall directly to the left.
	 * 
	 * @param direction
	 *            the direction (0, 90, 180, 270 degrees)
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @return true if there's a wall directly to the left, false otherwise
	 */
	public boolean wallAtLeft(int direction, int x, int y) {
		return inWall(direction + 90, x, y);
	}

	/**
	 * Check if you, when you're in x,y and heading in a given direction, have a
	 * wall directly in front.
	 * 
	 * @param direction
	 *            the direction (0, 90, 180, 270 degrees)
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @return true if there's a wall directly in front, false otherwise
	 */
	public boolean wallInFront(int direction, int x, int y) {
		return inWall(direction, x, y);
	}

	private final int ALPHA_MASK = 0xff000000; // mask to get A value in an ARGB pixel value

	// helper method for constructor
	private void initialize(InputStream input, int xEntry, int yEntry) throws IOException {
		image = ImageIO.read(input);
		bitmap = new boolean[image.getWidth() + BORDER_WIDTH * 2][image.getHeight() + BORDER_WIDTH * 2];
		boolean hasAlpha = (image.getAlphaRaster() != null);
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int c = image.getRGB(x, y);
				boolean transparent = hasAlpha && ((c & ALPHA_MASK) == 0);
				int valueWithoutAlpha = (c & ~ALPHA_MASK);
				bitmap[BORDER_WIDTH + x][BORDER_WIDTH + y] = (valueWithoutAlpha != 0xffffff) && !transparent;
			}
		}

		this.xEntry = xEntry + BORDER_WIDTH;
		this.yEntry = yEntry + BORDER_WIDTH;
	}

	// helper method: check if a step from the given point leads to a wall
	private boolean inWall(int dir, int x, int y) {
		dir = dir % 360;
		int nextX = x + (int) Math.round(Math.cos(dir * Math.PI / 180));
		int nextY = y - (int) Math.round(Math.sin(dir * Math.PI / 180));

		for (int x1 = Math.max(0,  nextX - 1); x1 <= Math.min(bitmap.length - 1, nextX + 1); x1++) {
			for (int y1 = Math.max(0,  nextY - 1); y1 <= Math.min(bitmap[0].length - 1, nextY + 1); y1++) {
				if (bitmap[x1][y1]) {
					return true;
				}
			}
		}

		return false;
	}
}
