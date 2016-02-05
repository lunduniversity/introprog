package se.lth.cs.pt.images;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;

/**
 * The graphical user interface for the ImageProcessor application. Loads and
 * saves jpeg files, shows original image, calls user classes to filter an
 * image, shows the filtered image.
 */
public class ImageGUI {
	// --------------- IMAGE PROCESSING RELATED ---------------
	/**
	 * The image filters supplied by the user
	 */
	private ImageFilter[] filters;

	/**
	 * The original jpeg image
	 */
	private BufferedImage originalImage;

	/**
	 * The original image, as a matrix of Color objects
	 */
	private Color[][] originalPixels;

	/**
	 * The filtered image
	 */
	private BufferedImage filteredImage;

	/**
	 * Called when the user clicks the Apply button. Finds a filter object in
	 * the filters array, calls the apply method in that object and generates a
	 * new filteredImage
	 * 
	 * @param method
	 *            The name of the filter method
	 * @param parameter
	 *            The extra parameter (from the Parameter field)
	 */
	private void dispatchFilter(String method, String parameter) {
		if (originalImage == null) {
			return;
		}

		int filterIndex = 0;
		while (filterIndex < filters.length
				&& !filters[filterIndex].getName().equals(method)) {
			++filterIndex;
		}
		if (filterIndex >= filters.length) {
			System.err.println("Internal error, aborting:");
			System.err.println("Filter method " + method + " not registered");
			System.exit(1);
		}

		double paramValue = 0;
		try {
			paramValue = Double.parseDouble(parameter);
		} catch (NumberFormatException e) {
		}
		filteredImage = matrixToImage(filters[filterIndex].apply(
				originalPixels, paramValue));
	}

	/**
	 * Converts a BufferedImage to a Color matrix. Called once every time a new
	 * image is loaded (from FileHandler.open())
	 */
	private Color[][] imageToMatrix(BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();
		Color[][] pixels = new Color[height][width];
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				pixels[i][k] = new Color(image.getRGB(k, i));
			}
		}
		return pixels;
	}

	/**
	 * Converts a Color matrix to a BufferedImage
	 */
	private BufferedImage matrixToImage(Color[][] pixels) {
		int height = pixels.length;
		int width = pixels[0].length;
		BufferedImage filteredImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < height; ++i) {
			for (int k = 0; k < width; ++k) {
				filteredImage.setRGB(k, i, pixels[i][k].getRGB());
			}
		}
		return filteredImage;
	}

	/**
	 * Replaces the original image with the filtered image.
	 */
	private void replaceImage() {
		originalImage = filteredImage;
		originalPixels = (originalImage != null) ? imageToMatrix(originalImage)
				: null;
		filteredImage = null;
		frame.setSaveEnabled(false);
		frame.setSaveAsEnabled(false);
	}

	// --------------- GUI RELATED ---------------

	/**
	 * The main frame for the GUI
	 */
	private ImageFrame frame;

	/**
	 * The file handler, loads and saves images
	 */
	private FileHandler fileHandler;

	/**
	 * Constructs an image GUI, using the filters supplied in the parameter.
	 * 
	 * @param filters
	 *            The image filter object array
	 */
	public ImageGUI(ImageFilter[] filters) {
		this.filters = filters;
		originalImage = filteredImage = null;
		originalPixels = null;
		fileHandler = new FileHandler();
		frame = new ImageFrame();
	}

	/**
	 * ImageFrame is a normal GUI class
	 */
	class ImageFrame extends JFrame implements ActionListener {
		private static final long serialVersionUID = 1;
		private DrawingArea drawingArea; // the area to draw the images in
		private JMenuItem mOpen; // Open menu alternative
		private JMenuItem mSave; // Save menu alternative
		private JMenuItem mSaveAs; // Save as menu alternative
		private JMenuItem mQuit; // Quit menu alternative
		private ToolPanel toolbar; // toolbar for selecting filter method
		private StatusPanel statusPanel; // shows the file name

		public ImageFrame() {
			super("Image Processor");

			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			JMenuBar bar = new JMenuBar();
			JMenu file = new JMenu("File");
			file.setMnemonic(KeyEvent.VK_F);

			mOpen = new JMenuItem("Open ...");
			mOpen.setMnemonic(KeyEvent.VK_O);
			mOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
					ActionEvent.CTRL_MASK));
			mSave = new JMenuItem("Save");
			setSaveEnabled(false);
			mSave.setMnemonic(KeyEvent.VK_S);
			mSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					ActionEvent.CTRL_MASK));
			mSaveAs = new JMenuItem("Save as ...");
			setSaveAsEnabled(false);
			mSaveAs.setMnemonic(KeyEvent.VK_A);
			mSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
					ActionEvent.CTRL_MASK));
			mQuit = new JMenuItem("Quit");
			mQuit.setMnemonic(KeyEvent.VK_Q);
			mQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
					ActionEvent.CTRL_MASK));
			file.add(mOpen);
			file.add(mSave);
			file.add(mSaveAs);
			file.addSeparator();
			file.add(mQuit);
			bar.add(file);
			setJMenuBar(bar);

			setLayout(new BorderLayout());

			drawingArea = new DrawingArea();
			add(new JScrollPane(drawingArea), BorderLayout.CENTER);
			statusPanel = new StatusPanel();
			add(statusPanel, BorderLayout.SOUTH);
			toolbar = new ToolPanel();
			add(toolbar, BorderLayout.NORTH);

			mOpen.addActionListener(this);
			mSave.addActionListener(this);
			mSaveAs.addActionListener(this);
			mQuit.addActionListener(this);

			pack();
			setVisible(true);
		}

		public void setSaveEnabled(boolean status) {
			mSave.setEnabled(status);
		}

		public void setSaveAsEnabled(boolean status) {
			mSaveAs.setEnabled(status);
		}

		public void setStatus(String s) {
			statusPanel.setStatus(s);
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof JMenuItem) {
				JMenuItem item = (JMenuItem) e.getSource();
				if (item == mOpen) {
					fileHandler.open();
				} else if (item == mSave) {
					fileHandler.save();
				} else if (item == mSaveAs) {
					fileHandler.saveAs();
				} else if (item == mQuit) {
					System.exit(0);
				}
			}
		}

		/**
		 * DrawingArea shows the original image to the left and the filtered
		 * image to the right
		 */
		class DrawingArea extends JPanel {
			private static final long serialVersionUID = 1;

			public DrawingArea() {
				setPreferredSize(new Dimension(800, 400));
			}

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				int newWidth = 0;
				int newHeight = 0;
				if (originalImage != null) {
					g2.drawImage(originalImage, null, 0, 0);
					newWidth = originalImage.getWidth();
					newHeight = originalImage.getHeight();
					if (filteredImage != null) {
						g2.drawImage(filteredImage, null, originalImage
								.getWidth() + 5, 0);
						newWidth += filteredImage.getWidth();
						if (filteredImage.getHeight() > newHeight)
							newHeight = filteredImage.getHeight();
					}
				}
				if (newWidth != 0 && newHeight != 0) {
					setPreferredSize(new Dimension(newWidth, newHeight));
					revalidate();
				}
			}
		}

		/**
		 * StatusPanel shows a label
		 */
		class StatusPanel extends JPanel {
			private static final long serialVersionUID = 1;
			private JLabel statusLabel;

			public StatusPanel() {
				super();
				setLayout(new FlowLayout(FlowLayout.LEFT));
				setBorder(new TitledBorder("Status"));
				statusLabel = new JLabel("No file");
				add(statusLabel);
			}

			public void setStatus(String status) {
				statusLabel.setText(status);
				statusLabel.repaint();
			}
		}

		/**
		 * ToolPanel is a toolbar with a combo box to select filtering method, a
		 * button to start the filtering, and a text field to enter a parameter,
		 * which is transferred to the user filter method
		 */
		class ToolPanel extends JPanel implements ActionListener {
			private static final long serialVersionUID = 1;
			private JComboBox<String> cbMethod; // the combo box
			private JButton bFilter; // the filter button
			private JButton bReplace; // the replace button
			private JTextField paramField; // the parameter field

			public ToolPanel() {
				super();
				setLayout(new FlowLayout(FlowLayout.LEFT));

				String[] methods = new String[filters.length];
				for (int i = 0; i < methods.length; ++i) {
					methods[i] = filters[i].getName();
				}
				cbMethod = new JComboBox<String>(methods);
				cbMethod.setToolTipText("Choose filter method");

				bFilter = new JButton("Apply");
				bFilter.setToolTipText("Start the image filtering");

				bReplace = new JButton("Replace");
				bReplace.setToolTipText("Replace the original image with the filtered image");

				paramField = new JTextField(6);
				paramField.setToolTipText("Enter a parameter "
						+ "to the filter method");

				add(cbMethod);
				add(new JLabel("Parameter:"));
				add(paramField);
				add(bFilter);
				add(bReplace);

				bFilter.addActionListener(this);
				bReplace.addActionListener(this);
			}

			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == bFilter) {
					dispatchFilter((String) cbMethod.getSelectedItem(),
							paramField.getText());
					drawingArea.repaint();
					setSaveAsEnabled(true);
				} else if (e.getSource() == bReplace) {
					replaceImage();
					drawingArea.repaint();
				}
			}
		}
	}

	/**
	 * FileHandler handles loading and saving of image files
	 */
	class FileHandler {
		private String filename;
		private JFileChooser fc;

		public FileHandler() {
			filename = "No file";
			String dirName = System.getProperty("user.dir") + "/images";
			File dir = new File(dirName);
			if (!(dir.exists() && dir.isDirectory())) {
				dirName = System.getProperty("user.dir");
			}
			fc = new JFileChooser(dirName);
			fc.addChoosableFileFilter(new JPEGFilter());
		}

		public void open() {
			File file = null;
			int ret = fc.showOpenDialog(frame);
			if (ret == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
			}
			if (file == null) {
				return;
			}

			String ext = getExtension(file);
			if (!(ext.equals("jpeg") || ext.equals("jpg"))) {
				JOptionPane.showMessageDialog(frame,
						"Can only read JPEG Image files.", "File type error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			frame.setStatus("Loading file " + file.getName());
			originalImage = null;
			try {
				originalImage = ImageIO.read(file);
				originalPixels = imageToMatrix(originalImage);
				filteredImage = null;
				frame.repaint();
				filename = file.getPath();
				frame.setStatus(filename);
				frame.setSaveEnabled(false);
				frame.setSaveAsEnabled(false);
			} catch (java.io.IOException e) {
				JOptionPane.showMessageDialog(frame, "Error reading file.", "",
						JOptionPane.ERROR_MESSAGE);
			}
		}

		public void save() {
			File file = new File(filename);
			frame.setStatus("Saving image as " + file.getName());
			try {
				ImageIO.write(filteredImage, "jpeg", file);
				frame.setStatus(filename);
			} catch (java.io.IOException e) {
				JOptionPane.showMessageDialog(frame, "Error writing file.", "",
						JOptionPane.ERROR_MESSAGE);
			}
		}

		public void saveAs() {
			File file = null;
			int ret = fc.showSaveDialog(frame);
			if (ret == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
			}
			if (file == null) {
				return;
			}

			String ext = getExtension(file);
			if (!(ext.equals("jpeg") || ext.equals("jpg"))) {
				file = new File(file.getPath() + ".jpg");
			}

			if (file.exists()) {
				int n = JOptionPane.showConfirmDialog(frame, "File "
						+ file.getName() + " already exists.\nOverwrite?",
						"Overwrite existing file?", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (n == JOptionPane.NO_OPTION || n != JOptionPane.YES_OPTION) {
					return;
				}
			}

			frame.setStatus("Saving image as " + file.getName());
			try {
				ImageIO.write(filteredImage, "jpeg", file);
				filename = file.getPath();
				frame.setStatus(filename);
				frame.setSaveEnabled(true);
			} catch (java.io.IOException e) {
				JOptionPane.showMessageDialog(frame, "Error writing file.", "",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		
		private String getExtension(File f) {
			String ext = "";
			String s = f.getName();
			int i = s.lastIndexOf('.');
			if (i > 0 && i < s.length() - 1) {
				ext = s.substring(i + 1).toLowerCase();
			}
			return ext;
		}

		class JPEGFilter extends FileFilter {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				String ext = getExtension(f);
				return ext.equals("jpg") || ext.equals("jpeg");
			}

			public String getDescription() {
				return "JPEG Images";
			}
		}
	}
}
