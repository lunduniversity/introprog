package cslib.window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.concurrent.*;

/**
 * A simple window to draw in, used in "Objektorienterad programmering och
 * Java".
 * 
 * @author Per Holm (and several others in earlier versions)
 * @version 2.2 (2015-08-09) Add Sprite support (Maj Stenmark, Björn Regnell)
 * @version 2.3 (2016-05-09) Add setExitOnLastClose (Björn Regnell)
 * @version 2.4 (2016-07-09) Add waitForEvent(int timeoutMillis) (Björn Regnell, Gustav Cedersjö, Patrik Persson)
 */
public class SimpleWindow {
	public final static int MOUSE_EVENT = 1; // mouse click event type
	public final static int KEY_EVENT = 2; // key pressed event type
	public final static int CLOSE_EVENT = 3; // window closed event type
	public final static int TIMEOUT_EVENT = 4; // waitForEvent timeout event type
	public final static int TIMEOUT_FOREVER = -1; // wait forever for new event
	
	private static int nbrOpenFrames = 0; // number of open frames
	private static boolean isExitOnLastClose = true; // controls exit window behavior
	
	private JFrame frame; // the frame
	private SWCanvas canvas; // the drawing area
	private AWTEvent lastEvent; // the last event (mouse or key)
	private BlockingQueue<AWTEvent> eventQueue; // a queue for events
	private int eventType; // type of last event
	private int mouseX; // x-coordinate, mouse click
	private int mouseY; // y-coordinate, mouse click
	private char key; // key event parameter
	private MouseEventHandler mouseHandler; // mouse event handler
	private KeyEventHandler keyHandler; // key event handler
	

	/*-------- Public window operations --------*/

	/**
	 * Creates a window and makes it visible.
	 * 
	 * @param width
	 *            the width of the window
	 * @param height
	 *            the height of the window
	 * @param title
	 *            the title of the window
	 */
	public SimpleWindow(int width, int height, String title) {
		frame = new JFrame(title);
		frame.setResizable(false);
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.addWindowListener(new WindowEventHandler());

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		JMenuItem fileQuit = new JMenuItem("Quit");
		fileQuit.setMnemonic(KeyEvent.VK_Q);
		fileQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				ActionEvent.CTRL_MASK));
		fileMenu.add(fileQuit);
		menuBar.add(fileMenu);
		frame.setJMenuBar(menuBar);
		fileQuit.addActionListener(new MenuEventHandler());

		canvas = new SWCanvas(width, height);
		frame.getContentPane().add(canvas, BorderLayout.CENTER);

		mouseHandler = new MouseEventHandler();
		keyHandler = new KeyEventHandler();
		eventQueue = new LinkedBlockingQueue<AWTEvent>();

		canvas.addMouseListener(mouseHandler);
		canvas.addKeyListener(keyHandler);
		
		frame.pack();
		open();
	}

	/**
	 * Returns the width of the window.
	 * 
	 * @return the width of the window
	 */
	public int getWidth() {
		return canvas.width;
	}

	/**
	 * Returns the height of the window.
	 * 
	 * @return the height of the window
	 */
	public int getHeight() {
		return canvas.height;
	}

	/**
	 * Clears the window.
	 */
	public void clear() {
		canvas.clear();
	}

	/**
	 * Closes the window.
	 */
	public void close() {
		if (frame.isVisible()) {
			frame.setVisible(false);
			nbrOpenFrames--;
		}
	}

	/**
	 * Opens the window.
	 */
	public void open() {
		if (!frame.isVisible()) {
			frame.setVisible(true);
			nbrOpenFrames++;
		}
	}

	/*-------- drawing operations --------*/

	/**
	 * Moves the pen to a new position.
	 * 
	 * @param x
	 *            the x coordinate of the new position
	 * @param y
	 *            the y coordinate of the new position
	 */
	public void moveTo(int x, int y) {
		canvas.moveTo(x, y);
	}

	/**
	 * Moves the pen to a new position while drawing a line.
	 * 
	 * @param x
	 *            the x coordinate of the new position
	 * @param y
	 *            the y coordinate of the new position
	 */
	public void lineTo(int x, int y) {
		canvas.lineTo(x, y);
	}

	/**
	 * Writes a string at the current position.
	 * 
	 * @param txt
	 *            the string to write
	 */
	public void writeText(String txt) {
		canvas.writeText(txt);
	}

	/**
	 * Draws a bitmap image at the current position.
	 *
	 * @param image
	 *              bitmap image to draw
	 */
	public void drawImage(Image image) {
		canvas.drawImage(image);
	}

	/**
	 * Returns the pen's x coordinate.
	 * 
	 * @return the x coordinate
	 */
	public int getX() {
		return canvas.x;
	}

	/**
	 * Returns the pen's y coordinate.
	 * 
	 * @return the y coordinate
	 */
	public int getY() {
		return canvas.y;
	}

	/**
	 * Sets the line width.
	 * 
	 * @param width
	 *            the new width (in pixels)
	 */
	public void setLineWidth(int width) {
		canvas.lineWidth = width;
	}

	/**
	 * Sets the line color.
	 * 
	 * @param col
	 *            the new color
	 */
	public void setLineColor(Color col) {
		canvas.lineColor = col;
	}

	/**
	 * Returns the current line width.
	 * 
	 * @return the line width (in pixels)
	 */
	public int getLineWidth() {
		return canvas.lineWidth;
	}

	/**
	 * Returns the current line color.
	 * 
	 * @return the line color
	 */
	public Color getLineColor() {
		return canvas.lineColor;
	}

	/*-------- mouse-related operations --------*/

	/**
	 * Waits for a mouse click.
	 */
	public void waitForMouseClick() {
		do {
			waitForEvent();
		} while (getEventType() != MOUSE_EVENT);
	}

	/**
	 * Returns the mouse x coordinate at the last mouse click.
	 * 
	 * @return the x coordinate
	 */
	public int getMouseX() {
		return mouseX;
	}

	/**
	 * Returns the mouse y coordinate at the last mouse click.
	 * 
	 * @return the y coordinate
	 */
	public int getMouseY() {
		return mouseY;
	}

	/**
	 * Adds a sprite to the window.
	 * 
	 * @param sprite
	 */
	public void addSprite(Sprite sprite) {
		canvas.add(sprite);
	}

	/*-------- Event handling --------*/

	/**
	 * Waits forever for event (mouse click or key press).
	 */
	public void waitForEvent() {
	    waitForEvent(TIMEOUT_FOREVER);
	}
	
	/**
	 * Waits for event (mouse click or key press) or timeout.
	 * @param timeoutMillis
	 *            maximum waiting duration in milliseconds, 
	 *            if timeoutMillis == TIMEOUT_FOREVER then block until event
	 *            if timeoutMillis == 0 then return immediately
	 *            if event queue is empty then a TIMEOUT_EVENT is generated
	 */
	public void waitForEvent(int timeoutMillis) {
		canvas.setFocusable(true);
		canvas.requestFocus();
		
		if (timeoutMillis == TIMEOUT_FOREVER || timeoutMillis < 0) {
		    try {
			      lastEvent = eventQueue.take();  // block until event 
		    } catch (InterruptedException e) {
			      System.err.println("SimpleWindow take interrupted: " + e);
		    }
		} else if (timeoutMillis == 0) {
        lastEvent = eventQueue.poll();  // returns immediately if no event
    } else {
		    try {
		        lastEvent = eventQueue.poll(timeoutMillis, TimeUnit.MICROSECONDS);
		    } catch (InterruptedException e) {
			      System.err.println("SimpleWindow poll interrupted: " + e);
		    }
    }		
    
		if (lastEvent == null){
			  eventType = TIMEOUT_EVENT;
		} else {
			if (lastEvent.getID() == KeyEvent.KEY_TYPED) {
				  eventType = KEY_EVENT;
				  key = ((KeyEvent) lastEvent).getKeyChar();
			} else if (lastEvent.getID() == MouseEvent.MOUSE_PRESSED) {
				  eventType = MOUSE_EVENT;
				  MouseEvent mEvent = (MouseEvent) lastEvent;
				  mouseX = mEvent.getX();
				  mouseY = mEvent.getY();
			} else if (lastEvent.getID() == WindowEvent.WINDOW_CLOSED ||
					lastEvent.getID() == WindowEvent.WINDOW_CLOSING) {
				  eventType = CLOSE_EVENT;
			} else {
				  System.err.println("Internal SimpleWindowError: "
						+ " unknown event type, " + lastEvent.getID());
			}
		}
	}

	/**
	 * Returns the type of the last event.
	 * 
	 * @return the event type (KEY_EVENT = key, MOUSE_EVENT = mouse)
	 */
	public int getEventType() {
		return eventType;
	}

	/**
	 * Returns the key that was pressed on a key event.
	 * 
	 * @return the character code for the key
	 */
	public char getKey() {
		return key;
	}
	
	/*-------- Delay --------*/

	/**
	 * Wait for a specified time.
	 * 
	 * @param ms
	 *            the number of milliseconds to wait
	 */
	public static void delay(int ms) {
		if (ms > 0) {
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
			}
		}
	}
	
	/* ------- Exit Behavior ---------*/
	
	/**
	 * 
	 * @param exitOnLastClose
	 *               Enables/disables System.Exit(0) on close of last window
	 */
	
	public static void setExitOnLastClose(boolean exitOnLastClose){
		isExitOnLastClose = exitOnLastClose;
	}

	/*-------- Event handlers --------*/

	class MouseEventHandler extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			eventQueue.offer(e);
		}
	}

	class KeyEventHandler extends KeyAdapter {
		public void keyTyped(KeyEvent e) {
			eventQueue.offer(e);
		}
	}

	class MenuEventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	class WindowEventHandler extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			nbrOpenFrames--;
			if (nbrOpenFrames > 0 || !isExitOnLastClose) {
				eventQueue.offer(e);
				frame.setVisible(false);
				frame.dispose();
			} else {
				System.exit(0);
			}
		}
	}
}

/* The drawing area */
class SWCanvas extends JPanel {
	private static final long serialVersionUID = 1;
	int width; // width
	int height; // height
	private BufferedImage img; // image where all drawing takes place
	int x; // pen attributes: current x
	int y; // - current y
	int lineWidth; // - line width
	Color lineColor; // - line color

	public SWCanvas(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		/*
		 * Creating the offscreen drawing image. Used to do it like this:
		 * img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);
		 * but then the colors are wrong (IndexColorModel instead of 
		 * DirectColorModel.
		 */
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gs.getDefaultConfiguration();
		img = gc.createCompatibleImage(width, height, Transparency.OPAQUE);

		setBackground(Color.WHITE);
		setDoubleBuffered(true);
		setPreferredSize(new Dimension(width, height));
		setMinimumSize(new Dimension(width, height));

		x = 0;
		y = 0;
		lineWidth = 1;
		lineColor = Color.BLACK;
		clear();
	}

	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, this);
	}

	public boolean imageUpdate(Image img, int infoFlags, int x, int y,
			int width, int height) {
		repaint();
		return true;
	}

	void clear() {
		Graphics2D g = img.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		repaint();
	}

	void moveTo(int x, int y) {
		this.x = x;
		this.y = y;
	}

	void lineTo(int x, int y) {
		Graphics2D g = img.createGraphics();
		g.setColor(lineColor);
		g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, 
				BasicStroke.JOIN_MITER));
		g.drawLine(this.x, this.y, x, y);
		moveTo(x, y);
		repaint();
	}

	void writeText(String txt) {
		Graphics2D g = img.createGraphics();
		g.setColor(lineColor);
		g.drawString(txt, x, y);
		repaint();
	}

	void drawImage(Image image) {
		Graphics2D g = img.createGraphics();
		g.drawImage(image, x, y, Color.WHITE, null);
		repaint();
	}
}
