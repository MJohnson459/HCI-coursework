package hci;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ImagePanel extends JPanel implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ArrayList<Point> currentShape = new ArrayList<Point>();
	ArrayList<Selection> selectionSet = new ArrayList<Selection>();
	int[] selectedIndex;

	Color c = Color.GREEN;

	BufferedImage image;
	String imageName;
	int i = 0;

	boolean drawing = false;

	public ImagePanel(int[] selectedIndex) {
		this.selectedIndex = selectedIndex;
		addMouseListener(this);
	}

	public String[] getShapeNames() {
		String[] shapeNames = new String[selectionSet.size()];
		for (int i = 0; i < selectionSet.size(); i++) {
			shapeNames[i] = selectionSet.get(i).name;
		}
		return shapeNames;
	}

	public void setColour(Color c) {
		this.c = c;
	}

	public void loadImage(File file) throws ClassNotFoundException {
		try {
			image = ImageIO.read(file);

			int newWidth = this.getWidth();
			int newHeight = this.getHeight();

			Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
			image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

			image.getGraphics().drawImage(scaledImage, 0, 0, this);

			selectionSet = new ArrayList<Selection>();

			imageName = file.getName();

			try {
				FileInputStream loadFile = new FileInputStream(imageName+".labels");
				ObjectInputStream load = new ObjectInputStream(loadFile);
				selectionSet = (ArrayList<Selection>) load.readObject();
				load.close();
			} catch (Exception e) {

			}
			drawImage();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveLabels() throws IOException {
		FileOutputStream saveFile = new FileOutputStream(imageName+".labels");
		ObjectOutputStream save = new ObjectOutputStream(saveFile);
		save.writeObject(selectionSet);
		save.close();
	}

	public void drawImage() {
		Graphics g = this.getGraphics();

		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}

		for (Selection selection : selectionSet) {
			selection.paint(this.getGraphics());
		}

		Graphics graphics = this.getGraphics();
		graphics.setColor(c);

		if (!currentShape.isEmpty()) {
			for (int i = 1; i < currentShape.size(); i++) {
				Point lastVertex = currentShape.get(i - 1);
				graphics.fillOval((int) lastVertex.getX() - 5, (int) lastVertex.getY() - 5, 10, 10);
				graphics.drawLine((int) lastVertex.getX(), (int) lastVertex.getY(), currentShape.get(i).x, currentShape.get(i).y);
			}
			graphics.fillOval((int) currentShape.get(currentShape.size()-1).getX() - 5, (int) currentShape.get(currentShape.size()-1).getY() - 5, 10, 10);
			//		graphics.drawLine((int) currentShape.get(currentShape.size()-1).getX(), (int) currentShape.get(currentShape.size()-1).getY(),
			//				currentShape.get(0).x, currentShape.get(0).y);
		} else {
		}

		fillShape();
	}

	@Override
	public void paint(Graphics g) {
		drawImage();
	}

	public void start() {
		drawing = true;
	}

	public void stop(boolean dialog) {

		if (dialog) {
			if (!currentShape.isEmpty()) {

				Selection selection = new Selection(currentShape, c);

				String s = (String)JOptionPane.showInputDialog(
						"Enter a name for the shape");

				if (s == null || s.length() == 0) {
					s = "Untitled Shape";
				}

				selection.setName(s);
				selectionSet.add(selection);

				currentShape = new ArrayList<Point>();
			}
		} else {
			drawing = false;
		}

		this.paint(this.getGraphics());
	}

	public void mouseClicked(MouseEvent e) {
		System.out.println("Mouse x: " + e.getX() + " y: " + e.getY());
		int x = e.getX();
		int y = e.getY();
		if (drawing) {

			Graphics2D g = (Graphics2D)this.getGraphics();

			//if the left button than we will add a vertex to poly
			if (e.getButton() == MouseEvent.BUTTON1) {
				g.setColor(c);
				if (currentShape.size() != 0) {
					Point lastVertex = currentShape.get(currentShape.size() - 1);
					g.drawLine((int) lastVertex.getX(), (int)lastVertex.getY(), x, y);
				}
				g.fillOval(x-5,y-5,10,10);

				currentShape.add(new Point(x,y));
				//				System.out.println(x + " " + y);
			} 
		} else {
			if (e.getButton() == MouseEvent.BUTTON1) {
				for (int i = 0; i < selectionSet.size(); i++) {
					if (selectionSet.get(i).isPointInShape(new Point(x,y))) {
						selectedIndex[0] = i;
						System.out.println("Selected point " + i);

						paint(this.getGraphics());
						break;
					}
				}
			}
		}
	}

	public void fillShape() {
		if ( selectionSet.size() != 0 && selectedIndex[0] != -1) {
			Graphics g = this.getGraphics();
			Selection s = selectionSet.get(selectedIndex[0]);
			Color colour = new Color(s.c.getRed(), s.c.getGreen(), s.c.getBlue(), 50);
			g.setColor(colour);
			g.fillPolygon(s.poly);
		}
	}

	public void editSelection(int index) {
		String s = (String)JOptionPane.showInputDialog(
		"Enter a name for the shape");

		if (s != null) {
			selectionSet.get(index).setName(s);
		}

	}

	public void deleteSelection(int index) {
		int b = JOptionPane.showConfirmDialog(null, "This will permanently delete this shape. Are you sure?", "Confirm Deletion", JOptionPane.OK_CANCEL_OPTION);
		if ( b == JOptionPane.OK_OPTION) {
			selectionSet.remove(index);
		}
	}




	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}



}
