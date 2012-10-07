package hci;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GUI extends JFrame implements ActionListener, ListSelectionListener, MouseListener {

	JPanel mainPanel, buttonsPanel;
	ImagePanel imagePanel;
	JFileChooser fc;
	JMenuBar menuBar;
	JMenu menu;
	JMenuItem openItem, saveItem;
	JButton startButton, stopButton, editButton, deleteButton, chooseColour, saveButton;
	JList shapesList;
	JScrollPane shapesListScrollPane;
		
	int[] selectedIndex = new int[1];

	//	JColorChooser cc = new JColorChooser();

	Dimension frameSize = new Dimension(970,620);
	Dimension imagePanelSize = new Dimension(800,600);
	Dimension buttonsPanelSize = new Dimension(180,150);
	Dimension buttonSize = new Dimension(buttonsPanelSize.width - 20, 35);
	Dimension scrollPaneSize = new Dimension(buttonsPanelSize.width - 20, ((buttonSize.height + 10) * 5) - 10);

	int buttonSpacing = buttonSize.height + 10;

	public GUI() {
		this.setName("HCI Coursework");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(frameSize);
		this.setResizable(false);
		this.setLayout(new BorderLayout());

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		menuBar = new JMenuBar();
		menu = new JMenu("File");

		openItem = new JMenuItem("Open Image...");
		saveItem = new JMenuItem("Save Labels");
		openItem.addActionListener(this);
		saveItem.addActionListener(this);
		menu.add(openItem);
		menu.add(saveItem);		

		menuBar.add(menu);

		selectedIndex[0] = -1;
		System.out.println("index " + selectedIndex[0]);
		imagePanel = new ImagePanel(selectedIndex);
		System.out.println("index " + selectedIndex[0]);
		//imagePanel.setLocation(20,20);
		imagePanel.setPreferredSize(imagePanelSize);
		imagePanel.addMouseListener(this);
		//imagePanel.setBackground(new Color(255,0,0));

		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(null);
		buttonsPanel.setPreferredSize(buttonsPanelSize);

		// Button creation and sizing
		startButton = new JButton("Start Drawing");
		stopButton = new JButton("Stop Drawing");
		deleteButton = new JButton("Delete Selection");
		chooseColour = new JButton("Choose Colour");
		editButton = new JButton("Edit Name");
		saveButton = new JButton("Save Object");
		startButton.setSize(buttonSize);
		stopButton.setSize(buttonSize);
		deleteButton.setSize(buttonSize);
		chooseColour.setSize(buttonSize);
		editButton.setSize(buttonSize);
		saveButton.setSize(buttonSize);
		chooseColour.setLocation(12,10);
		startButton.setLocation(12,10 + buttonSpacing);
		stopButton.setLocation(12,10 + 3 * buttonSpacing);
		editButton.setLocation(12,10 + 9 * buttonSpacing);
		deleteButton.setLocation(12,10 + 10 * buttonSpacing);
		saveButton.setLocation(12, 10 + 2 * buttonSpacing);

		stopButton.setEnabled(false);
		deleteButton.setEnabled(false);
		editButton.setEnabled(false);
		saveButton.setEnabled(false);
		
		startButton.addActionListener(this);
		stopButton.addActionListener(this);
		deleteButton.addActionListener(this);
		chooseColour.addActionListener(this);
		editButton.addActionListener(this);
		saveButton.addActionListener(this);

		shapesList = new JList();
		shapesList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		shapesList.setLayoutOrientation(JList.VERTICAL);
		shapesList.setVisibleRowCount(-1);
		shapesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		shapesList.addListSelectionListener(this);

		shapesListScrollPane = new JScrollPane(shapesList);
		shapesListScrollPane.setSize(scrollPaneSize);
		shapesListScrollPane.setLocation(12, 10 + 4 * buttonSpacing);

		buttonsPanel.add(startButton);
		buttonsPanel.add(stopButton);
		buttonsPanel.add(deleteButton);
		buttonsPanel.add(chooseColour);
		buttonsPanel.add(editButton);
		buttonsPanel.add(saveButton);
		buttonsPanel.add(shapesListScrollPane);

		mainPanel.add(imagePanel, BorderLayout.CENTER);
		mainPanel.add(buttonsPanel, BorderLayout.EAST);
		this.add(mainPanel, BorderLayout.CENTER);

		this.setJMenuBar(menuBar);

		this.setVisible(true);

		try {
			imagePanel.loadImage(new File("images/image.jpg"));
			shapesList.setListData(imagePanel.getShapeNames());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		//display image
		imagePanel.paint(g);
	}

	private void loadImage() {
		File file;
		fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			try {
				imagePanel.loadImage(file);
				shapesList.setListData(imagePanel.getShapeNames());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openItem) {
			try {
				imagePanel.saveLabels();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			loadImage();
		} else if (e.getSource() == saveItem) {
			try {
				imagePanel.saveLabels();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == startButton) {
			startButton.setEnabled(false);
			stopButton.setEnabled(true);
			saveButton.setEnabled(true);
//			chooseColour.setEnabled(false);

			imagePanel.start();
			
		} else if (e.getSource() == saveButton) {
			imagePanel.stop(true);
			shapesList.setListData(imagePanel.getShapeNames());
			try {
				imagePanel.saveLabels();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else if (e.getSource() == stopButton) {
			stopButton.setEnabled(false);
			startButton.setEnabled(true);
			saveButton.setEnabled(false);
//			chooseColour.setEnabled(true);

			imagePanel.stop(false);
			shapesList.setListData(imagePanel.getShapeNames());

		} else if (e.getSource() == chooseColour) {
			imagePanel.setColour(JColorChooser.showDialog(this, "Shape Colour", Color.white));
		} else if (e.getSource() == editButton) {
			imagePanel.editSelection(selectedIndex[0]);
			shapesList.setListData(imagePanel.getShapeNames());
		} else if (e.getSource() == deleteButton) {
			imagePanel.deleteSelection(selectedIndex[0]);
			String[] s = imagePanel.getShapeNames();
			shapesList.setListData(imagePanel.getShapeNames());
			if (s.length == 0) {
				deleteButton.setEnabled(false);
				editButton.setEnabled(false);
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int firstIndex = e.getFirstIndex();
		int lastIndex = e.getLastIndex();
		JList lsm = (JList)e.getSource();
		
		deleteButton.setEnabled(true);
		editButton.setEnabled(true);

		if (!lsm.isSelectionEmpty()) {
			for (int i = firstIndex; i <= lastIndex; i++) {
				if (lsm.isSelectedIndex(i)) {
					selectedIndex[0] = i;
					imagePanel.paint(this.getGraphics());
					break;
				}
			}
		}
		
		//System.out.println(selectedIndex);
	}

	@Override
	public void mouseClicked(MouseEvent e) {	
		shapesList.setSelectedIndex(selectedIndex[0]);		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
