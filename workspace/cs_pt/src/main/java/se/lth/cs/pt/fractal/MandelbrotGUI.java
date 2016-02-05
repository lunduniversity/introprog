package se.lth.cs.pt.fractal;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * The graphical user interface for the Mandelbrot assignment.
 * 
 * Initial version by Christian Andersson.
 * <p>
 * May 09, PH: added proportional zooming, removed double buffering, removed
 * some interface options, added "Extra" text field, added constructor for 
 * variable image width, simplified layouts, cleaned up a lot.
 */
public class MandelbrotGUI {
	private static final String WINDOW_TITLE = "FractalGenerator";

	/**
	 * Render command.
	 * <p>
	 * This constant is returned by <code>getCommand()</code> when the user
	 * presses the <b>RENDER</b> button.
	 * 
	 * @see #getCommand()
	 */
	public static final int RENDER = 1 << 0;

	/**
	 * Zoom command.
	 * <p>
	 * This constant is returned by <code>getCommand()</code> when the user
	 * zooms in the picture.
	 * 
	 * @see #getCommand()
	 */
	public static final int ZOOM = 1 << 1;

	/**
	 * Reset command.
	 * <p>
	 * This constant is returned by <code>getCommand()</code> when the user
	 * presses the <b>RESET</b> button.
	 * 
	 * @see #getCommand()
	 */
	public static final int RESET = 1 << 3;

	/**
	 * Quit command.
	 * <p>
	 * This constant is returned by <code>getCommand()</code> when the user
	 * presses the <b>QUIT</b> button or chooses <b>Quit</b> in the File menu.
	 * 
	 * @see #getCommand()
	 */
	public static final int QUIT = 1 << 4;

	/**
	 * Black and White mode.
	 * <p>
	 * This is the value returned by <code>getMode()</code> when the user has
	 * requested a black and white image.
	 */
	public static final int MODE_BW = 1 << 5;

	/**
	 * Color mode.
	 * <p>
	 * This is the value returned by <code>getMode()</code> when the user has
	 * requested a color image.
	 */
	public static final int MODE_COLOR = 1 << 6;

	/**
	 * Very low resolution.
	 * <p>
	 * This is the value returned by <code>getResolution()</code> when the user
	 * has requested <b>very low</b> resolution of the image.
	 * 
	 * @see #getResolution()
	 */
	public static final int RESOLUTION_VERY_LOW = 1 << 7;

	/**
	 * Low resolution.
	 * <p>
	 * This is the value returned by <code>getResolution()</code> when the user
	 * has requested <b>low</b> resolution of the image.
	 * 
	 * @see #getResolution()
	 */
	public static final int RESOLUTION_LOW = 1 << 8;

	/**
	 * Medium resolution.
	 * <p>
	 * This is the value returned by <code>getResolution()</code> when the user
	 * has requested <b>medium</b> resolution of the image.
	 * 
	 * @see #getResolution()
	 */
	public static final int RESOLUTION_MEDIUM = 1 << 9;

	/**
	 * High resolution.
	 * <p>
	 * This is the value returned by <code>getResolution()</code> when the user
	 * has requested <b>high</b> resolution of the image.
	 * 
	 * @see #getResolution()
	 */
	public static final int RESOLUTION_HIGH = 1 << 10;

	/**
	 * Very high resolution.
	 * <p>
	 * This is the value returned by <code>getResolution()</code> when the user
	 * has requested <b>very high</b> resolution of the image.
	 * 
	 * @see #getResolution()
	 */
	public static final int RESOLUTION_VERY_HIGH = 1 << 11;

	/* Coordinate plane initial sizes */
	private static final double DEFAULT_MIN_REAL = -2.4;
	private static final double DEFAULT_MAX_REAL = 0.8;
	private static final double DEFAULT_MIN_IMAG = -1.4;
	private static final double DEFAULT_MAX_IMAG = 1.4;
	/* Default canvas width */
	private static final int DEFAULT_WIDTH = 700;

	private FGFrame frame;
	private FileHandler fileHandler;
	private LinkedBlockingQueue<Integer> eventQueue;

	private double minReal;
	private double maxReal;
	private double minImag;
	private double maxImag;
	private int resolution;
	private int mode;

	/**
	 * Creates a graphical user interface with the default width. The height is
	 * scaled so the default complex plane can be displayed.
	 */
	public MandelbrotGUI() {
		this(DEFAULT_WIDTH);
	}

	/**
	 * Creates a graphical user interface with the specified width. The height
	 * is scaled so the default complex plane can be displayed.
	 * 
	 * @param width
	 *            The width in pixels.
	 */
	public MandelbrotGUI(int width) {
		double scale = (DEFAULT_MAX_IMAG - DEFAULT_MIN_IMAG)
				/ (DEFAULT_MAX_REAL - DEFAULT_MIN_REAL);
		int height = (int) (scale * width);

		resolution = RESOLUTION_VERY_HIGH;
		mode = MODE_BW;

		eventQueue = new LinkedBlockingQueue<Integer>();

		fileHandler = new FileHandler();
		
		frame = new FGFrame(width, height);
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
		resetPlane();
		clearPlane();
	}

	/**
	 * Disables user input.
	 * 
	 * @see #enableInput()
	 */
	public void disableInput() {
		frame.enableComponents(false);
	}

	/**
	 * Enables user input.
	 * 
	 * @see #disableInput()
	 */
	public void enableInput() {
		frame.enableComponents(true);
	}

	/**
	 * Returns the minimum real value.
	 * 
	 * @return Minimum real value of the complex coordinate system.
	 * @see #getMaximumReal()
	 * @see #getMinimumImag()
	 * @see #getMaximumImag()
	 */
	public double getMinimumReal() {
		return minReal;
	}

	/**
	 * Returns the maximum real value.
	 * 
	 * @return Maximum real value of the complex coordinate system.
	 * @see #getMinimumReal()
	 * @see #getMinimumImag()
	 * @see #getMaximumImag()
	 */
	public double getMaximumReal() {
		return maxReal;
	}

	/**
	 * Returns the minimum imaginary value.
	 * 
	 * @return Minimum imaginary value of the complex coordinate system.
	 * @see #getMinimumReal()
	 * @see #getMaximumReal()
	 * @see #getMinimumImag()
	 */
	public double getMinimumImag() {
		return minImag;
	}

	/**
	 * Returns the maximum imaginary value.
	 * 
	 * @return Maximum imaginary value of the complex coordinate system.
	 * @see #getMinimumReal()
	 * @see #getMaximumReal()
	 * @see #getMinimumImag()
	 */
	public double getMaximumImag() {
		return maxImag;
	}

	/**
	 * Returns the image mode.
	 * <p>
	 * The mode is chosen among the two alternatives <code>MODE_BW</code> and
	 * <code>MODE_COLOR</code>, given as constants by the class.
	 * 
	 * @return The image mode chosen by the user.
	 * @see #MODE_BW
	 * @see #MODE_COLOR
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Returns the image resolution.
	 * <p>
	 * The resolution is chosen among five alternatives given as constants in
	 * the class.
	 * 
	 * @return The image resolution chosen by the user.
	 * @see #RESOLUTION_VERY_LOW
	 * @see #RESOLUTION_LOW
	 * @see #RESOLUTION_MEDIUM
	 * @see #RESOLUTION_HIGH
	 * @see #RESOLUTION_VERY_HIGH
	 */
	public int getResolution() {
		return resolution;
	}

	/**
	 * Returns the string that the user has typed in the "Extra" field.
	 * 
	 * @return The text in the "Extra" field.
	 */
	public String getExtraText() {
		return frame.toolbar.getExtraText();
	}

	/**
	 * Returns the image width.
	 * 
	 * @return The width of the image in pixels.
	 * @see #getHeight
	 */
	public int getWidth() {
		return frame.plane.getWidth();
	}

	/**
	 * Returns the image height.
	 * 
	 * @return The height of the image in pixels.
	 * @see #getWidth
	 */
	public int getHeight() {
		return frame.plane.getHeight();
	}

	/**
	 * Returns a command triggered by the user.
	 * <p>
	 * The possible return values are defined as constants in the class.
	 * 
	 * @return A command triggered by the user.
	 * @see #QUIT
	 * @see #RENDER
	 * @see #ZOOM
	 * @see #RESET
	 */
	public int getCommand() {
		int command = 0;
		try {
			command = eventQueue.take();
		} catch (InterruptedException ie) {
		}
		return command;
	}

	private void signal(int command) {
		eventQueue.offer(command);
	}

	/**
	 * Clears the image plane.
	 */
	public void clearPlane() {
		fileHandler.filename = "[noname]";
		frame.clear();
	}

	/**
	 * Resets the axis limits to their default values.
	 */
	public void resetPlane() {
		minReal = DEFAULT_MIN_REAL;
		maxReal = DEFAULT_MAX_REAL;
		minImag = DEFAULT_MIN_IMAG;
		maxImag = DEFAULT_MAX_IMAG;
		frame.updateDimensions();
	}

	/**
	 * Draws an image on the complex plane.
	 * <p>
	 * An image represented as a matrix of colors is drawn on the complex plane
	 * of the interface. Matrix element <code>[0][0]</code> is drawn at image
	 * position <code>(0,0)</code>.
	 * 
	 * @param image
	 *            A matrix of color objects, each of whose elements is
	 *            interpreted as a picture element of the image to draw.
	 * @param pixelWidth
	 *            The <b>width</b> of a picture element.
	 * @param pixelHeight
	 *            The <b>height</b> of a picture element.
	 */
	public void putData(Color[][] image, int pixelWidth, int pixelHeight) {
		frame.plane.putData(image, pixelWidth, pixelHeight);
	}

	/*
	 * === I N N E R C L A S S E S ===
	 */

	/**
	 * FGFrame is the main frame of the Mandelbrot GUI, with the toolbar, the
	 * drawing canvas and the status bar.
	 * 
	 */
	class FGFrame extends JFrame implements ActionListener {
		private static final long serialVersionUID = 1;
		private JMenuItem mSaveAs;
		private JMenuItem mQuit;
		private ComplexPlane plane;
		private FGDimensions dimensions;
		private FGToolbar toolbar;

		/**
		 * Creates a frame where the canvas has the specified size
		 * 
		 * @param canvasWidth
		 *            The width of the canvas (in pixels).
		 * @param canvasHeight
		 *            The height of the canvas (in pixels)
		 */
		public FGFrame(int canvasWidth, int canvasHeight) {
			super(WINDOW_TITLE);

			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			JMenuBar bar = new JMenuBar();
			JMenu file = new JMenu("File");
			file.setMnemonic(KeyEvent.VK_F);

			mSaveAs = new JMenuItem("Save as...");
			mSaveAs.setMnemonic(KeyEvent.VK_A);
			mSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
					ActionEvent.CTRL_MASK));
			mSaveAs.addActionListener(this);
			file.add(mSaveAs);
			file.addSeparator();
			mQuit = new JMenuItem("Quit");
			mQuit.setMnemonic(KeyEvent.VK_Q);
			mQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
					ActionEvent.CTRL_MASK));
			mQuit.addActionListener(this);
			file.add(mQuit);
			bar.add(file);

			setJMenuBar(bar);

			setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

			toolbar = new FGToolbar();
			plane = new ComplexPlane(canvasWidth, canvasHeight);
			dimensions = new FGDimensions();

			add(toolbar);
			add(plane);
			add(dimensions);
		}

		/**
		 * Enables or disables all components of the frame
		 * 
		 * @param enabled
		 *            True/false for enable/disable.
		 */
		public void enableComponents(boolean enabled) {
			toolbar.enableComponents(enabled);
			plane.enableZoom(enabled);
			setStatus(enabled ? fileHandler.filename : "Rendering...");
		}

		/**
		 * Clears the canvas
		 */
		public void clear() {
			plane.clear();
			plane.repaint();
		}

		/**
		 * Sets the status message
		 * 
		 * @param s
		 *            The status message.
		 */
		public void setStatus(String s) {
			dimensions.setStatus(s);
		}

		/**
		 * Updates the dimensions fields.
		 */
		public void updateDimensions() {
			dimensions.updateDimensions();
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof JMenuItem) {
				JMenuItem item = (JMenuItem) e.getSource();
				if (item == mSaveAs) {
					fileHandler.saveAs();
				} else if (item == mQuit) {
					signal(QUIT);
				}
			}
		}

		/**
		 * ComplexPlane is where the Mondelbrot set is drawn. *
		 */
		class ComplexPlane extends JPanel implements MouseListener,
				MouseMotionListener {
			private static final long serialVersionUID = 1;

			/**
			 * The image where everything is drawn
			 */
			private BufferedImage img;

			/**
			 * Dragging is true while zooming
			 */
			private boolean dragging;

			/**
			 * Upper left and lower right corners of dragged rectangle
			 */
			private int xMin, yMin, xMax, yMax;

			/**
			 * Current mouse position while zooming
			 */
			private int currentX, currentY;

			/**
			 * Creates a drawing plane with the specified size
			 * 
			 * @param width
			 *            The width (in pixels).
			 * @param height
			 *            The height (in pixels).
			 */
			public ComplexPlane(int width, int height) {
				img = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);

				setDoubleBuffered(false);
				setPreferredSize(new Dimension(width, height));
				setMinimumSize(getPreferredSize());
				setMaximumSize(getPreferredSize());
				setSize(getPreferredSize());
				setBackground(Color.WHITE);
				setBorder(new SoftBevelBorder(BevelBorder.RAISED));
				enableZoom(true);
			}

			/**
			 * Enables or disables zooming in the canvas.
			 * 
			 * @param enabled
			 *            True/false for enable/disable
			 */
			public void enableZoom(boolean enabled) {
				if (enabled) {
					addMouseListener(this);
					addMouseMotionListener(this);
				} else {
					removeMouseListener(this);
					removeMouseMotionListener(this);
				}
			}

			/**
			 * Clears the canvas and draws the real and imaginary axes
			 */
			public void clear() {
				int width = getWidth();
				int height = getHeight();
				Graphics g = img.getGraphics();
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, width, height);
				g.setColor(Color.lightGray);
				int remin = (int) Math.ceil(minReal);
				int remax = (int) Math.floor(maxReal);
				int immin = (int) Math.ceil(minImag);
				int immax = (int) Math.floor(maxImag);
				double deltaRe = maxReal - minReal;
				double deltaIm = maxImag - minImag;
				int origx = (int) (width * (-minReal) / deltaRe);
				int origy = (int) (height * maxImag / deltaIm);
				for (int x = remin; x <= remax; ++x) {
					int offX = (int) (width * (x - minReal) / deltaRe);
					g.drawLine(offX, origy - 3, offX, origy + 3);
					if (x != 0) {
						g.drawString(Integer.toString(x), offX - 5, origy + 20);
					}
				}
				for (int y = immin; y <= immax; ++y) {
					int offY = (int) (height * (maxImag - y) / deltaIm);
					g.drawLine(origx - 3, offY, origx + 3, offY);
					if (y != 0) {
						g.drawString(Integer.toString(y), origx - 20, offY + 5);
					}
				}
				if (maxReal > 0 && minReal < 0) {
					g.drawLine(origx, 0, origx, height);
				}
				if (maxImag > 0 && minImag < 0) {
					g.drawLine(0, origy, width, origy);
				}

				g.setColor(Color.BLACK);
			}

			/**
			 * Draws the contents of a Color matrix on the canvas.
			 * 
			 * @param color
			 *            The color matrix
			 * @param pw
			 *            The "pixel width"
			 * @param ph
			 *            The "pixel height"
			 */
			public void putData(Color[][] color, int pw, int ph) {
				Graphics g = img.getGraphics();
				int height = color.length;
				int width = color[0].length;
				for (int h = 0; h < height; ++h) {
					for (int w = 0; w < width; ++w) {
						g.setColor(color[h][w]);
						g.fillRect(pw * w, ph * h, pw, ph);
					}
				}
				repaint();
			}

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(img, 0, 0, this);
			}

			public boolean imageUpdate(Image img, int infoFlags, int x, int y,
					int width, int height) {
				repaint();
				return true;
			}

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
				dimensions.mousePosLabel.setForeground(Color.RED);
			}

			public void mouseExited(MouseEvent e) {
				dimensions.mousePosLabel.setForeground(Color.BLACK);
				dimensions.mousePosLabel.setText(dimensions.mousePosLabel
						.getText());
			}

			/**
			 * Used to start zooming in the canvas. Sets the upper left corner
			 * of the zooming rectangle.
			 */
			public void mousePressed(MouseEvent e) {
				if (!dragging) {
					dragging = true;
					setStatus("Zooming...");
					currentX = xMin = e.getX();
					currentY = yMin = e.getY();
					zoomRect();
				}
			}

			/**
			 * Used to end zooming. Computes new minimum and maximum limits for
			 * the coordinate system.
			 */
			public void mouseReleased(MouseEvent e) {
				if (dragging) {
					dragging = false;
					zoomRect();
					Graphics g = img.getGraphics();
					g.setPaintMode();
					setStatus(fileHandler.filename);

					if (xMax > xMin && yMax > yMin) {
						double deltaRe = maxReal - minReal;
						double deltaIm = maxImag - minImag;
						int w = getWidth();
						int h = getHeight();
						double oldMinReal = minReal;
						minReal = oldMinReal + deltaRe * xMin / w;
						maxReal = oldMinReal + deltaRe * xMax / w;
						double oldMaxImag = maxImag;
						minImag = oldMaxImag - deltaIm * yMax / h;
						maxImag = oldMaxImag - deltaIm * yMin / h;
						updateDimensions();
						signal(ZOOM);
					}
				}
			}

			/**
			 * Updates the zoomed rectangle while dragging the mouse.
			 */
			public void mouseDragged(MouseEvent e) {
				updateMousePosLabel(e.getX(), e.getY());
				if (dragging) {
					zoomRect();
					currentX = e.getX();
					currentY = e.getY();
					zoomRect();
				}
			}

			/**
			 * Redraws the zoomed rectangle, computes the lower right corner of
			 * the rectangle
			 */
			private void zoomRect() {
				if (currentX <= xMin || currentY <= yMin) {
					xMax = yMax = 0;
					return;
				}
				int w = currentX - xMin;
				double scale = (double) getHeight() / getWidth();
				int h = (int) Math.round(w * scale);

				Graphics g = img.getGraphics();
				g.setXORMode(Color.WHITE);
				g.setColor(Color.RED);
				g.fillRect(xMin, yMin, Math.abs(w), Math.abs(h));
				xMax = xMin + w;
				yMax = yMin + h;
				repaint();
			}

			public void mouseMoved(MouseEvent e) {
				updateMousePosLabel(e.getX(), e.getY());
			}

			/**
			 * Updates the mouse position in the GUI. Converts from pixels to
			 * coordinate values in the complex plane.
			 * 
			 * @param x
			 *            The x coordinate (in pixels)
			 * @param y
			 *            The y coordinate (in pixels)
			 */
			private void updateMousePosLabel(int x, int y) {
				int w = getWidth();
				int h = getHeight();
				double deltaRe = maxReal - minReal;
				double deltaIm = maxImag - minImag;
				double re = minReal + deltaRe * x / w;
				double im = maxImag - deltaIm * y / h;
				dimensions.mousePosLabel.setText("(" + re + " , " + im + ")");
			}
		}

		/**
		 * Handles the dimensions field, where the current minimum and maximum
		 * values of the real and imaginary axes are displayed
		 */
		class FGDimensions extends JPanel {
			private static final long serialVersionUID = 1;

			private JLabel minRealLabel;
			private JLabel maxRealLabel;
			private JLabel minImagLabel;
			private JLabel maxImagLabel;

			private JLabel mousePosLabel;

			private JLabel statusLabel;

			public FGDimensions() {
				super(new BorderLayout());
				String numberField = "0.00000000000000000";
				minRealLabel = new JLabel(numberField);
				maxRealLabel = new JLabel(numberField);
				minImagLabel = new JLabel(numberField);
				maxImagLabel = new JLabel(numberField);
				updateDimensions();

				mousePosLabel = new JLabel(" ");

				statusLabel = new JLabel(fileHandler.filename);
				statusLabel.setForeground(Color.BLUE);

				JPanel topPanel = new JPanel(new GridLayout(1, 2));

				JPanel realAxis = new JPanel(new GridLayout(2, 1));
				realAxis.setBorder(new TitledBorder(new EtchedBorder(),
						"Real axis"));

				JPanel remin = new JPanel(new FlowLayout(FlowLayout.LEFT));
				remin.add(new JLabel("Min:"));
				remin.add(minRealLabel);
				realAxis.add(remin);

				JPanel remax = new JPanel(new FlowLayout(FlowLayout.LEFT));
				remax.add(new JLabel("Max:"));
				remax.add(maxRealLabel);
				realAxis.add(remax);

				topPanel.add(realAxis);

				JPanel imagAxis = new JPanel(new GridLayout(2, 1));
				imagAxis.setBorder(new TitledBorder(new EtchedBorder(),
						"Imaginary axis"));

				JPanel immin = new JPanel(new FlowLayout(FlowLayout.LEFT));
				immin.add(new JLabel("Min:"));
				immin.add(minImagLabel);
				imagAxis.add(immin);

				JPanel immax = new JPanel(new FlowLayout(FlowLayout.LEFT));
				immax.add(new JLabel("Max:"));
				immax.add(maxImagLabel);
				imagAxis.add(immax);

				topPanel.add(imagAxis);

				add(topPanel, BorderLayout.CENTER);

				JPanel bottomPanel = new JPanel(new GridLayout(2, 1));

				JPanel mouse = new JPanel();
				mouse.add(mousePosLabel);
				bottomPanel.add(mouse);

				JPanel statusp = new JPanel(new FlowLayout(FlowLayout.LEFT));
				statusp.setBorder(new EtchedBorder());
				statusp.setBackground(Color.WHITE);
				statusp.add(statusLabel);
				bottomPanel.add(statusp);

				add(bottomPanel, BorderLayout.SOUTH);
			}

			public void updateDimensions() {
				minRealLabel.setText(Double.toString(minReal));
				maxRealLabel.setText(Double.toString(maxReal));
				minImagLabel.setText(Double.toString(minImag));
				maxImagLabel.setText(Double.toString(maxImag));
			}

			public void setStatus(String status) {
				statusLabel.setText(status);
				statusLabel.repaint();
			}
		}

		/**
		 * The toolbar with buttons for different actions, the text field for
		 * extra input, the combo boxes for resolution and mode
		 */
		class FGToolbar extends JPanel implements ActionListener {
			private static final long serialVersionUID = 1;

			private JButton bRender;
			private JButton bReset;
			private JButton bQuit;

			private JTextField extraText;

			private JComboBox<String> cbResolution;
			private JComboBox<String> cbMode;

			public FGToolbar() {
				super(new FlowLayout(FlowLayout.LEFT));

				bRender = new JButton("Render");
				bRender.setToolTipText("Start the fractal rendering");
				bRender.addActionListener(this);

				bReset = new JButton("Reset");
				bReset.setToolTipText("Reset plane axes");
				bReset.addActionListener(this);

				bQuit = new JButton("Quit");
				bQuit.setToolTipText("Quit application");
				bQuit.addActionListener(this);

				extraText = new JTextField(5);

				String[] resolutions = { "Very low", "Low", "Medium", "High",
						"Very high" };
				cbResolution = new JComboBox<String>(resolutions);
				cbResolution.setToolTipText("Set image resolution");
				int index = (int) (Math.round((Math.log(resolution) - Math
						.log(RESOLUTION_VERY_LOW))
						/ Math.log(2)));
				cbResolution.setSelectedIndex(index);
				cbResolution.addActionListener(this);

				String[] modes = { "Black/White", "Color" };
				cbMode = new JComboBox<String>(modes);
				cbMode.setToolTipText("Set image mode");
				index = (int) (Math.round((Math.log(mode) - Math.log(MODE_BW))
						/ Math.log(2)));
				cbMode.setSelectedIndex(index);
				cbMode.addActionListener(this);

				JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
				actions.setBorder(new TitledBorder(new EtchedBorder(),
						"Actions"));
				actions.add(bRender);
				actions.add(bReset);
				actions.add(bQuit);
				add(actions);

				JPanel extra = new JPanel();
				extra.setBorder(new TitledBorder(new EtchedBorder(), "Extra"));
				extra.add(extraText);
				add(extra);

				JPanel resmode = new JPanel(new GridLayout(1, 2));
				resmode.setBorder(new TitledBorder(new EtchedBorder(),
						"Resolution / Mode"));
				resmode.add(cbResolution);
				resmode.add(cbMode);
				add(resmode);
			}

			public void enableComponents(boolean enabled) {
				bRender.setEnabled(enabled);
				bReset.setEnabled(enabled);
				bQuit.setEnabled(enabled);
				extraText.setEnabled(enabled);
				cbResolution.setEnabled(enabled);
				cbMode.setEnabled(enabled);
			}

			public String getExtraText() {
				return extraText.getText();
			}

			public void actionPerformed(ActionEvent e) {
				if (e.getSource() instanceof JButton) {
					JButton b = (JButton) e.getSource();
					if (b == bRender) {
						signal(RENDER);
					} else if (b == bReset) {
						signal(RESET);
					} else if (b == bQuit) {
						signal(QUIT);
					}
				} else if (e.getSource() instanceof JComboBox<?>) {
					@SuppressWarnings("unchecked")
					JComboBox<String> cb = (JComboBox<String>) e.getSource();
					if (cb == cbResolution) {
						resolution = RESOLUTION_VERY_LOW << cb
								.getSelectedIndex();
					} else if (cb == cbMode) {
						mode = MODE_BW << cb.getSelectedIndex();
					}
				}
			}
		}
	}

	/*
	 * Handles image saving.
	 */
	class FileHandler {
		private String filename;
		private JFileChooser fc;

		public FileHandler() {
			filename = "[noname]";
			fc = new JFileChooser();
			fc.addChoosableFileFilter(new JPEGFileFilter());
		}

		public void saveAs() {
			File file = null;
			int ret = fc.showSaveDialog(frame);
			if (ret == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
			}

			if (file != null) {
				String ext = getExtension(file);
				if (ext == null || !ext.equals("jpg") && !ext.equals("jpeg")) {
					file = new File(file.getPath() + ".jpg");
				}

				if (file.exists()) {
					int n = JOptionPane.showConfirmDialog(frame, "File "
							+ file.getName() + " already exists.\nOverwrite?",
							"Overwrite existing file?",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (n == JOptionPane.NO_OPTION
							|| n != JOptionPane.YES_OPTION) {
						return;
					}
				}
				frame.setStatus("Saving image as " + file.getName());

				try {
					ImageIO.write(frame.plane.img, "jpeg", file);
					filename = file.getPath();
				} catch (java.io.IOException e) {
					JOptionPane.showMessageDialog(frame, "Error writing file.",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
				frame.setStatus(filename);
			}
		}

		/*
		 * Returns the extension of the specified file
		 * 
		 * @param f The file.
		 * 
		 * @return The extension, or null if no extension.
		 */
		private String getExtension(File f) {
			String s = f.getName();
			int i = s.lastIndexOf('.');
			if (i <= 0 || i >= s.length() - 1) {
				return null;
			}
			return s.substring(i + 1).toLowerCase();
		}

		/*
		 * Filters out JPEG files in the file chooser dialog.
		 */
		class JPEGFileFilter extends FileFilter {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				String ext = getExtension(f);
				return ext != null && (ext.equals("jpg") || ext.equals("jpeg"));
			}

			public String getDescription() {
				return "JPEG Images";
			}
		}
	}
}
