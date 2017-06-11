package fileBuddy;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class GUI extends JFrame {
	static JFrame frame = new JFrame();
	static DefaultListModel fileListModel = new DefaultListModel();
	static JList fileList = new JList(fileListModel);;
	JScrollPane fileListScrollPane = new JScrollPane(fileList);
	static JTextField detailsTextField = new JTextField();
	static JTextField pathTextField = new JTextField();
	static DateFormat dateFormatter = DateFormat.getDateTimeInstance(
			DateFormat.SHORT, DateFormat.SHORT);
	static Actions listener = new Actions();
	static JTextField searchField = new JTextField();
	static boolean isCut = false;
	static String[] fileArray;
	static File currentDir;
	static FilenameFilter filter;
	static File copyFile = null;
	static JLabel searchIcon = new JLabel(new ImageIcon("Icons/SearchIcon.png"));
	static JTextField goTextField = new JTextField("");

	static JToolBar toolBar = new JToolBar("");

	static JButton cutButton = new JButton(new ImageIcon("Icons/CutIcon.png"));
	static JButton copyButton = new JButton(new ImageIcon("Icons/CopyIcon.png"));
	static JButton pasteButton = new JButton(new ImageIcon(
			"Icons/PasteIcon.png"));
	static JButton deleteButton = new JButton(new ImageIcon(
			"Icons/DeleteIcon.png"));
	static JButton duplicateButton = new JButton(new ImageIcon(
			"Icons/DuplicateIcon.png"));
	static JButton compressButton = new JButton(new ImageIcon(
			"Icons/CompressIcon.png"));
	static JButton upButton = new JButton(new ImageIcon("Icons/UpIcon.png"));
	static JButton closeButton = new JButton(new ImageIcon(
			"Icons/CloseIcon.png"));

	static JButton[] toolBarButtonArray = { cutButton, copyButton, pasteButton,
			deleteButton, duplicateButton, compressButton, upButton,
			closeButton };

	static JMenuBar menuBar = new JMenuBar();

	static JMenu fileBuddyMenu = new JMenu("FileBuddy");
	static JMenu fileMenu = new JMenu("File");
	static JMenu editMenu = new JMenu("Edit");
	static JMenu viewMenu = new JMenu("View");
	static JMenu goMenu = new JMenu("Go");
	static JMenu windowMenu = new JMenu("Window");

	static JMenuItem cutMenuItem = new JMenuItem("Cut", new ImageIcon(
			"Icons/CutIcon.png"));
	static JMenuItem copyMenuItem = new JMenuItem("Copy", new ImageIcon(
			"Icons/CopyIcon.png"));
	static JMenuItem pasteMenuItem = new JMenuItem("Paste", new ImageIcon(
			"Icons/PasteIcon.png"));
	static JMenuItem deleteMenuItem = new JMenuItem("Delete", new ImageIcon(
			"Icons/DeleteIcon.png"));
	static JMenuItem duplicateMenuItem = new JMenuItem("Duplicate",
			new ImageIcon("Icons/DuplicateIcon.png"));
	static JMenuItem aboutMenuItem = new JMenuItem("About FileBuddy",
			new ImageIcon("Icons/AboutIcon.png"));
	static JMenuItem upMenuItem = new JMenuItem("Up a Directory",
			new ImageIcon("Icons/UpIcon.png"));
	static JMenuItem closeMenuItem = new JMenuItem("Close", new ImageIcon(
			"Icons/CloseIcon.png"));
	static JMenuItem compressMenuItem = new JMenuItem("Compress",
			new ImageIcon("Icons/CompressIcon.png"));
	static JMenuItem renameMenuItem = new JMenuItem("Rename", new ImageIcon(
			"Icons/RenameIcon.png"));

	public void createGUI() {

		UIManager.put("List.focusCellHighlightBorder",
				BorderFactory.createEmptyBorder());

		Container container = frame.getContentPane();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		setMenu();
		setToolBar();
		setFileList();

		container.add(menuBar);
		container.add(toolBar);
		container.add(detailsTextField);
		container.add(fileListScrollPane);
		container.add(pathTextField);

		listener.createGhostText(goTextField, "to File Path...");

		goTextField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				listener.deleteGhostText(goTextField);
			}
		});

		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(350, 600);
		frame.pack();
		frame.setVisible(true);
		listener.printWarning(
				"This application is a work-in-progress.\nUse with caution; files and directories can be\naccidentially or unintentionally deleted, corrupted, etc.",
				"FileBuddy", frame);
	}

	private void setMenu() {
		menuBar.add(fileBuddyMenu);
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(viewMenu);
		menuBar.add(goMenu);
		goMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				listener.createGhostText(goTextField, "to File Path...");
			}
		});
		menuBar.add(windowMenu);
		menuBar.add(Box.createHorizontalGlue());
		fileBuddyMenu.add(aboutMenuItem);
		aboutMenuItem.setAccelerator(KeyStroke
				.getKeyStroke(("control shift A")));
		aboutMenuItem.addActionListener(new AboutAction());
		fileMenu.add(duplicateMenuItem);
		duplicateMenuItem.setAccelerator(KeyStroke.getKeyStroke(("control D")));
		duplicateMenuItem.addActionListener(new DuplicateAction());
		fileMenu.add(compressMenuItem);
		compressMenuItem.setAccelerator(KeyStroke.getKeyStroke(("control O")));
		compressMenuItem.addActionListener(new CompressAction());
		fileMenu.add(renameMenuItem);
		renameMenuItem.setAccelerator(KeyStroke.getKeyStroke(("control R")));
		renameMenuItem.addActionListener(new RenameAction());
		editMenu.add(cutMenuItem);
		cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(("control X")));
		cutMenuItem.addActionListener(new CutAction());
		editMenu.add(copyMenuItem);
		copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(("control C")));
		copyMenuItem.addActionListener(new CopyAction());
		editMenu.add(pasteMenuItem);
		pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(("control V")));
		pasteMenuItem.addActionListener(new PasteAction());
		editMenu.add(deleteMenuItem);
		deleteMenuItem.setAccelerator(KeyStroke
				.getKeyStroke(("control BACK_SPACE")));
		deleteMenuItem.addActionListener(new DeleteAction());
		goMenu.add(upMenuItem);
		upMenuItem.setAccelerator(KeyStroke.getKeyStroke(("control U")));
		upMenuItem.addActionListener(new UpAction());
		goMenu.add(goTextField);
		goTextField.addActionListener(new GoAction());
		windowMenu.add(closeMenuItem);
		closeMenuItem.setAccelerator(KeyStroke
				.getKeyStroke(("control shift BACK_SPACE")));
		closeMenuItem.addActionListener(new CloseAction());
	}

	private void setToolBar() {
		for (JButton button : toolBarButtonArray) {
			toolBar.add(button);
		}

		cutButton.addActionListener(new CutAction());
		copyButton.addActionListener(new CopyAction());
		pasteButton.addActionListener(new PasteAction());
		deleteButton.addActionListener(new DeleteAction());
		duplicateButton.addActionListener(new DuplicateAction());
		compressButton.addActionListener(new CompressAction());
		upButton.addActionListener(new UpAction());
		closeButton.addActionListener(new CloseAction());
		toolBar.add(searchIcon);
		toolBar.add(searchField);
		searchField.addActionListener(new SearchAction());
		toolBar.add(Box.createHorizontalGlue());
		toolBar.setFloatable(false);
	}

	private void setFileList() {

		fileList.setCellRenderer(new FileListRenderer());
		fileList.setFont(new Font("SansSerif", Font.PLAIN, 14));
		fileList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if (((JLabel) fileListModel.getElementAt(fileList
						.getSelectedIndex())).getText().equals(" ")) {
					fileList.clearSelection();

					if (currentDir.toString().equals(".Trash")) {
						detailsTextField.setText("Trash");
						pathTextField.setText("Trash");

					} else {
						detailsTextField.setText(currentDir.getAbsolutePath());
						pathTextField.setText(currentDir.getAbsolutePath());
					}
					return;
				}
				if (e.getClickCount() == 1) {
					listener.detailsAction(fileList, fileListModel, fileArray,
							currentDir, dateFormatter, detailsTextField);
					listener.pathDisplayAction(pathTextField, fileList,
							fileArray);
				} else if (e.getClickCount() == 2) {

					listener.listAction(fileList, fileListModel, fileArray,
							filter, frame, detailsTextField, currentDir,
							pathTextField);
				}

			}
		});

		fileListScrollPane.setPreferredSize(new Dimension(550, 300));

		detailsTextField.setFont(new Font("SanSerif", Font.PLAIN, 14));
		detailsTextField.setEditable(false);

		pathTextField.setFont(new Font("SanSerif", Font.PLAIN, 14));
		pathTextField.setEditable(false);

		String directory = null;

		filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
//				if ((name.charAt(0) != '.')) {
//					return true;
//				} else {
//					return false;
//				}
				return true;
			}
		};

		directory = "/Users";

		listener.listDirectory(new File(directory), null, fileListModel,
				filter, fileList, frame, detailsTextField, currentDir,
				pathTextField);
	}

	class AboutAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			listener.aboutAction();
		}

	}

	class UpAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			listener.upAction(fileArray, filter, fileList, fileListModel,
					frame, detailsTextField, currentDir, pathTextField);
		}

	}

	class CloseAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			listener.closeAction(frame);
		}

	}

	class DeleteAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (fileList.getSelectedIndex() > 0) {
				try {
					listener.deleteAction(
							new File(fileArray[fileList.getSelectedIndex() - 1]),
							frame, fileList, fileListModel,
							fileList.getSelectedIndex(), fileArray, currentDir);
				} catch (ArrayIndexOutOfBoundsException ex) {

					String[] trashContents = new File(".Trash").list();
					String question = "Are you sure that you want to empty the trash?"
							+ "\nEverything in it will be deleted forever (a long time).";
					String functionName = "Delete";
					boolean shouldDelete = listener.printConfirmation(question,
							functionName, frame);
					if (shouldDelete) {
						for (int i = 0; i < trashContents.length; i++) {
							new File(".Trash", trashContents[i]).delete();
						}
					}
				}

			} else if ((fileList.getSelectedIndex() == 0)) {
				listener.printError("Invalid selection, cannot be deleted.",
						"Delete", frame);
			} else {
				listener.printError("No selection, cannot delete.", "Delete",
						frame);
			}

		}

	}

	class CopyAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (fileList.getSelectedIndex() > 0
					&& fileList.getSelectedIndex() - 1 != fileArray.length) {
				listener.copyAction(currentDir,
						fileArray[fileList.getSelectedIndex() - 1], frame);
				listener.printSuccess("Copy", frame);
			} else if ((fileList.getSelectedIndex() == 0)
					|| (fileList.getSelectedIndex() - 1 == fileArray.length)) {
				listener.printError("Invalid selection, cannot be copied.",
						"Copy", frame);
			} else {
				listener.printError("No selection, cannot copy.", "Copy", frame);
			}
		}
	}

	class PasteAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (copyFile != null && !copyFile.isDirectory()) {
				listener.pasteAction(currentDir, copyFile, fileList,
						fileListModel, fileArray, isCut, frame);
				listener.printSuccess("Paste", frame);
			} else if (copyFile.isDirectory()) {
				listener.printError("",
						"Paste", frame);
			} else {
				listener.printError("Nothing is copied, cannot paste.",
						"Paste", frame);
			}

		}
	}

	class CutAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (fileList.getSelectedIndex() > 0
					&& fileList.getSelectedIndex() - 1 != fileArray.length) {
				listener.cutAction(
						new File(fileArray[fileList.getSelectedIndex() - 1]),
						frame, fileList, fileList.getSelectedIndex(),
						fileArray, currentDir,
						fileArray[fileList.getSelectedIndex() - 1],
						fileListModel);
				listener.printSuccess("Cut", frame);
			} else if ((fileList.getSelectedIndex() == 0)
					|| (fileList.getSelectedIndex() - 1 == fileArray.length)) {
				listener.printError("Invalid selection, cannot be cut.", "Cut",
						frame);
			} else {
				listener.printError("No selection, cannot cut.", "Cut", frame);
			}

		}
	}

	class SearchAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			listener.searchAction(searchField, fileArray, currentDir, filter,
					fileListModel, fileList, frame, detailsTextField,
					pathTextField);
		}
	}

	class CompressAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ev) {
			if (fileList.getSelectedIndex() > 0
					&& fileList.getSelectedIndex() - 1 != fileArray.length) {
				try {
					listener.compressAction(fileListModel, fileList, fileArray,
							currentDir);
					listener.printSuccess("Compress", frame);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if ((fileList.getSelectedIndex() == 0)
					|| (fileList.getSelectedIndex() - 1 == fileArray.length)) {
				listener.printError("Invalid selection, cannot be compressed.",
						"Compress", frame);
			} else {
				listener.printError("No selection, cannot compress.",
						"Compress", frame);
			}
		}

	}

	class DuplicateAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (fileList.getSelectedIndex() > 0
					&& fileList.getSelectedIndex() - 1 != fileArray.length) {
				listener.pasteAction(currentDir,
						new File(fileArray[fileList.getSelectedIndex() - 1]),
						fileList, fileListModel, fileArray, false, frame);
				listener.printSuccess("Duplicate", frame);
			} else if ((fileList.getSelectedIndex() == 0)
					|| (fileList.getSelectedIndex() - 1 == fileArray.length)) {
				listener.printError("Invalid selection, cannot be duplicated.",
						"Duplicate", frame);
			} else {
				listener.printError("No selection, cannot duplicate.",
						"Duplicate", frame);
			}

		}

	}

	class GoAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (new File(goTextField.getText()).exists()) {
				if (new File(goTextField.getText()).isDirectory()) {
					currentDir = new File(goTextField.getText());
				} else {
					currentDir = new File(goTextField.getText())
							.getParentFile();
				}
				listener.listDirectory(currentDir, fileArray = null,
						fileListModel, filter, fileList, frame,
						detailsTextField, currentDir, pathTextField);
			} else if (goTextField.getText().equals("")) {
				listener.printError(
						"Search field is empty, cannot go to file/directory.",
						"Go", frame);
			} else {
				listener.printError("Path does not exist.", "Go", frame);
			}

			listener.createGhostText(goTextField, "to File Path...");

		}
	}

	class RenameAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {

			if (fileList.getSelectedIndex() > 0
					&& fileList.getSelectedIndex() - 1 != fileArray.length) {
				String s = (String) JOptionPane.showInputDialog(
						frame,
						"Please type in what you want\n"
								+ new File(fileArray[fileList
										.getSelectedIndex() - 1]).getName()
								+ " to be called:", "Rename",
						JOptionPane.PLAIN_MESSAGE, new ImageIcon(
								"Icons/ConfirmIcon.png"), null, null);
				if (s != null && !s.equals("")) {
					new File(fileArray[fileList.getSelectedIndex() - 1])
							.renameTo(new File(currentDir, s));
					listener.listDirectory(currentDir, fileArray = null,
							fileListModel, filter, fileList, frame,
							detailsTextField, currentDir, pathTextField);
					listener.printSuccess("Rename", frame);
				}
			} else if ((fileList.getSelectedIndex() == 0)
					|| (fileList.getSelectedIndex() - 1 == fileArray.length)) {
				listener.printError("Invalid selection, cannot rename.",
						"Rename", frame);
			} else {
				listener.printError("No selection, cannot rename.", "Rename",
						frame);
			}
		}
	}
}
