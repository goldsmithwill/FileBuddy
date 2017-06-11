package fileBuddy;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class Actions {

	public void detailsAction(JList fileList, DefaultListModel fileListModel,
			String[] fileArray, File currentDir, DateFormat dateFormatter,
			JTextField detailsTextField) {
		int i = fileList.getSelectedIndex() - 1;
		if (i < 0) {
			return;
		}
		if (i == fileArray.length) {
			detailsTextField.setText("Trash");
			return;
		}
		String filename = new File(fileArray[i]).getName();
		File f = new File(fileArray[i]);

		String info = filename;
		if (f.isDirectory()) {
			info += File.separator;
		}
		info += " " + f.length() + " bytes ";
		info += dateFormatter.format(new java.util.Date(f.lastModified()));
		if (f.canRead()) {
			info += " Read";
		}
		if (f.canWrite()) {
			info += " Write";
		}
		detailsTextField.setText(info);

	}

	public void pathDisplayAction(JTextField pathTextField, JList fileList,
			String[] fileArray) {
		int i = fileList.getSelectedIndex() - 1;
		if (i < 0) {
			return;
		}
		if (i == fileArray.length) {
			pathTextField.setText("Trash");
			return;
		}
		pathTextField.setText(fileArray[i]);
	}

	public void listDirectory(File directory, String[] fileArray,
			DefaultListModel fileListModel, FilenameFilter filter,
			JList fileList, JFrame frame, JTextField detailsTextField,
			File currentDir, JTextField pathTextField) {

		boolean isSearch = false;
		if (fileArray == null) {
			fileArray = directory.list(filter);

			for (int i = 0; i < fileArray.length; i++) {
				fileArray[i] = new File(directory, fileArray[i])
						.getAbsolutePath();
			}
		} else {
			isSearch = true;
		}

		java.util.Arrays.sort(fileArray);

		fileListModel.removeAllElements();
		fileListModel.addElement(new JLabel("[Up to Parent directory]"));

		for (int i = 0; i < fileArray.length; i++) {
			if (new File(fileArray[i]).isDirectory()) {
				fileListModel.addElement(new JLabel(new File(fileArray[i])
						.getName(), new ImageIcon("Icons/FolderIcon.png"),
						JLabel.LEFT));

			} else {

				fileListModel.addElement(new JLabel(new File(fileArray[i])
						.getName(), new ImageIcon("Icons/FileIcon.png"),
						JLabel.LEFT));
			}
		}

		if (!directory.toString().equals(".Trash")) {
			fileListModel.addElement(new JLabel("Trash", new ImageIcon(
					"Icons/TrashIcon.png"), JLabel.LEFT));
		}

		fileListModel.addElement(new JLabel(" "));

		if (directory.toString().equals(".Trash")) {
			frame.setTitle("Trash");
			detailsTextField.setText("Trash");
			pathTextField.setText("Trash");
		} else {
			frame.setTitle(directory.getAbsolutePath());
			detailsTextField.setText(directory.getAbsolutePath());
			pathTextField.setText(directory.getAbsolutePath());
		}
		currentDir = directory;
		GUI.fileArray = fileArray;
		GUI.currentDir = currentDir;
	}

	public void upAction(String[] fileArray, FilenameFilter filter,
			JList fileList, DefaultListModel fileListModel, JFrame frame,
			JTextField detailsTextField, File currentDir,
			JTextField pathTextField) {
		try {
			File parent = null;
			if (currentDir.toString().equals(".Trash")) {
				parent = new File("/Users");
			} else {
				parent = new File(currentDir.getParent());
			}

			listDirectory(parent, fileArray = null, fileListModel, filter,
					fileList, frame, detailsTextField, currentDir,
					pathTextField);
		} catch (Exception e) {
			return;
		}
	}

	public void listAction(JList fileList, DefaultListModel fileListModel,
			String[] fileArray, FilenameFilter filter, JFrame frame,
			JTextField detailsTextField, File currentDir,
			JTextField pathTextField) {
		int i = fileList.getSelectedIndex();
		if (i == 0) {
			upAction(fileArray, filter, fileList, fileListModel, frame,
					detailsTextField, currentDir, pathTextField);
		} else if (i - 1 == fileArray.length) {
			currentDir = new File(".Trash");
			listDirectory(currentDir, fileArray = null, fileListModel, filter,
					fileList, frame, detailsTextField, currentDir,
					pathTextField);
		} else {
			String name = fileArray[i - 1];

			File f = new File(name);

			if (f.isDirectory()) {
				listDirectory(f, fileArray = null, fileListModel, filter,
						fileList, frame, detailsTextField, currentDir,
						pathTextField);
			} else {
				try {

					System.out.println(f.getAbsolutePath());
					java.awt.Desktop.getDesktop().edit(f);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		}
	}

	public void aboutAction() {
		UIManager.put("OptionPane.background", Color.white);
		UIManager.put("Panel.background", Color.white);

		JOptionPane.showMessageDialog(GUI.frame, new JLabel(new ImageIcon(
				"Icons/FileBuddyIcon.png")), "About FileBuddy",
				JOptionPane.PLAIN_MESSAGE);

	}

	public void closeAction(JFrame frame) {
		frame.dispose();
	}

	public void deleteAction(File file, JFrame frame, JList fileList,
			DefaultListModel fileListModel, int selectedIndex,
			String[] fileArray, File currentDir) {
		boolean shouldDelete = true;
		if (!file.canWrite()) {
			printError(file.getName() + " is write protected.", "Delete", frame);
			shouldDelete = false;
		}

		String question = null;
		String functionName = null;
		if (shouldDelete) {
			if (file.isDirectory()) {

				String[] directoryContents = file.list();

				if (directoryContents.length > 1) {
					if (GUI.isCut) {
						question = file.getName()
								+ " is a non-empty folder.\nAre you sure that you want to cut it?";
						functionName = "Cut";
					} else {

						question = file.getName()
								+ " is a non-empty folder.\nAre you sure that you want to throw it in the trash?";
						functionName = "Delete";
					}
				}

			} else if (currentDir.toString().equals(".Trash")) {
				question = "Are you sure that you want to delete\n"
						+ file.getName() + "\nforever (a long time)?";
				functionName = "Delete";
			}

			else {
				if (GUI.isCut) {
					question = "Are you sure that you want to cut\n"
							+ file.getName() + "?";
					functionName = "Cut";
				} else {
					question = "Are you sure that you want to throw\n"
							+ file.getName() + " in the trash?";
					functionName = "Delete";
				}
			}

			shouldDelete = printConfirmation(question, functionName, frame);
			if (shouldDelete) {
				System.out.println(file.exists());
				if (!currentDir.toString().equals(".Trash")) {
					pasteAction(new File(".Trash"), file, fileList,
							fileListModel, fileArray, GUI.isCut, frame);

				}
				file.delete();
			}

			String[] oldFileArray = fileArray;

			if (shouldDelete) {
				fileArray = new String[fileArray.length - 1];
				int k = 0;
				while (k < fileArray.length + 1) {
					if (k < selectedIndex - 1) {
						fileArray[k] = oldFileArray[k];
					} else if (k > selectedIndex - 1 && k != 0) {
						fileArray[k - 1] = oldFileArray[k];
					}
					k++;
				}

			}

			java.util.Arrays.sort(fileArray);

			fileListModel.removeAllElements();
			fileListModel.addElement(new JLabel("[Up to Parent directory]"));

			for (int i = 0; i < fileArray.length; i++) {
				if (new File(fileArray[i]).isDirectory()) {
					fileListModel.addElement(new JLabel(new File(fileArray[i])
							.getName(), new ImageIcon("Icons/FolderIcon.png"),
							JLabel.LEFT));

				} else {

					fileListModel.addElement(new JLabel(new File(fileArray[i])
							.getName(), new ImageIcon("Icons/FileIcon.png"),
							JLabel.LEFT));
				}
			}

			if (!currentDir.toString().equals(".Trash")) {
				fileListModel.addElement(new JLabel("Trash", new ImageIcon(
						"Icons/TrashIcon.png"), JLabel.LEFT));
			}

			fileListModel.addElement(new JLabel(" "));

			GUI.fileArray = fileArray;
			if (shouldDelete) {
				if (!GUI.isCut) {
					printSuccess("Delete", frame);
				}
			}
		}

	}

	public void printError(String error, String functionName, JFrame frame) {
		JOptionPane.showMessageDialog(frame, ("Error: " + error),
				(functionName + " Error"), JOptionPane.PLAIN_MESSAGE,
				new ImageIcon("Icons/ErrorIcon.png"));
	}

	public void printWarning(String warning, String functionName, JFrame frame) {
		JOptionPane.showMessageDialog(frame, ("Warning: " + warning),
				(functionName + " Warning"), JOptionPane.PLAIN_MESSAGE,
				new ImageIcon("Icons/WarningIcon.png"));
	}

	public boolean printConfirmation(String question, String functionName,
			JFrame frame) {
		Object[] options = { "Yes", "Cancel" };
		int i = JOptionPane.showOptionDialog(frame, question, "Confirm "
				+ functionName, JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE, new ImageIcon(
						"Icons/ConfirmIcon.png"), options, options[0]);
		if (i == 0) {
			return true;
		} else {
			return false;
		}
	}

	public void copyAction(File currentDir, String fileName, JFrame frame) {
		File copyFile = new File(fileName);
		boolean shouldCopy = true;
		if (!copyFile.isFile()) {
			printError(copyFile.getName() + " is not a file.", "Copy", frame);
			shouldCopy = false;
		}
		if (!copyFile.canRead()) {
			System.err.println("Error: " + copyFile.getName()
					+ " is unreadable.");
			shouldCopy = false;
		}
		if (shouldCopy == true) {
			GUI.copyFile = copyFile;
		}
	}

	public void pasteAction(File currentDir, File copyFile, JList fileList,
			DefaultListModel fileListModel, String[] fileArray, boolean isCut,
			JFrame frame) {
		String file = copyFile.getName();
		String fileName = file;
		String extension = "";
		if (!copyFile.isDirectory()) {
			fileName = file.substring(0, file.lastIndexOf("."));
			extension = file.substring(file.lastIndexOf("."), file.length());
		}
		if (!(currentDir.toString().equals(".Temp"))
				&& !(currentDir.toString().equals(".Trash"))) {
			fileName += " copy";
		}

		int copyNum = 1;
		while (new File(currentDir, fileName + extension).exists()) {
			if (copyNum > 1) {
				fileName = fileName.substring(0, fileName.lastIndexOf(" "));
			}
			fileName += " " + copyNum;
			copyNum++;
		}

		File pasteFile = new File(currentDir, fileName + extension);

		boolean shouldPaste = true;
		if (pasteFile.isDirectory()) {
			pasteFile = new File(pasteFile, fileName + extension);
		}

		if (shouldPaste == true) {
			String parent = pasteFile.getParent();
			if (currentDir.isFile()) {
				System.err.println(parent + "is not a directory.");
				shouldPaste = false;
			}
			if (!currentDir.canWrite()) {
				System.err.println(parent + "is unwriteable.");
				shouldPaste = false;
			}
			if (shouldPaste == true) {
				BufferedInputStream source = null;
				BufferedOutputStream destination = null;
				try {
					source = new BufferedInputStream(new FileInputStream(
							copyFile));
					destination = new BufferedOutputStream(
							new FileOutputStream(pasteFile));
					byte[] buffer = new byte[4096];
					int bytes_read;
					while ((bytes_read = source.read(buffer)) != -1) {
						destination.write(buffer, 0, bytes_read);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (source != null) {
						try {
							source.close();
						} catch (Exception e) {

						}
					}
					if (destination != null) {
						try {
							destination.close();
						} catch (Exception e) {

						}
					}
				}
			}
		}
		boolean cutIsInCurrentDir = false;

		String[] oldFileArray = fileArray;

		fileArray = new String[fileArray.length + 1];

		fileArray[fileArray.length - 1] = pasteFile.getAbsolutePath();
		for (int i = 0; i < fileArray.length - 1; i++) {
			if (cutIsInCurrentDir
					&& new File(oldFileArray[i]).getAbsolutePath() == copyFile
							.getAbsolutePath()) {
				i--;
				cutIsInCurrentDir = false;
			} else {
				fileArray[i] = oldFileArray[i];
			}
		}

		java.util.Arrays.sort(fileArray);

		fileListModel.removeAllElements();
		fileListModel.addElement(new JLabel("[Up to Parent directory]"));

		for (int i = 0; i < fileArray.length; i++) {
			if (new File(fileArray[i]).isDirectory()) {
				fileListModel.addElement(new JLabel(new File(fileArray[i])
						.getName(), new ImageIcon("Icons/FolderIcon.png"),
						JLabel.LEFT));

			} else {

				fileListModel.addElement(new JLabel(new File(fileArray[i])
						.getName(), new ImageIcon("Icons/FileIcon.png"),
						JLabel.LEFT));
			}
		}

		if (!currentDir.toString().equals(".Trash")) {
			fileListModel.addElement(new JLabel("Trash", new ImageIcon(
					"Icons/TrashIcon.png"), JLabel.LEFT));
		}

		fileListModel.addElement(new JLabel(" "));

		GUI.fileArray = fileArray;

	}

	public void cutAction(File file, JFrame frame, JList fileList,
			int selectedIndex, String[] fileArray, File currentDir,
			String fileName, DefaultListModel fileListModel) {
		GUI.isCut = true;
		File tempFile = new File(".Temp", file.getName());
		pasteAction(new File(".Temp"), file, fileList, fileListModel,
				fileArray, GUI.isCut, frame);
		deleteAction(file, frame, fileList, fileListModel, selectedIndex,
				fileArray, currentDir);
		copyAction(tempFile, tempFile.getAbsolutePath(), frame);
		GUI.isCut = false;
	}

	public void gzipFile(String from, String to, String[] fileArray,
			DefaultListModel fileListModel, File currentDir) throws IOException {
		FileInputStream in = new FileInputStream(from);
		GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(to));
		byte[] buffer = new byte[4096];
		int bytes_read;
		while ((bytes_read = in.read(buffer)) != -1)
			out.write(buffer, 0, bytes_read);
		in.close();
		out.close();
		String[] oldFileArray = fileArray;

		fileArray = new String[fileArray.length + 1];

		fileArray[fileArray.length - 1] = to;
		for (int i = 0; i < fileArray.length - 1; i++) {
			fileArray[i] = oldFileArray[i];
		}

		java.util.Arrays.sort(fileArray);

		fileListModel.removeAllElements();
		fileListModel.addElement(new JLabel("[Up to Parent directory]"));

		for (int i = 0; i < fileArray.length; i++) {
			if (new File(fileArray[i]).isDirectory()) {
				fileListModel.addElement(new JLabel(new File(fileArray[i])
						.getName(), new ImageIcon("Icons/FolderIcon.png"),
						JLabel.LEFT));

			} else {

				fileListModel.addElement(new JLabel(new File(fileArray[i])
						.getName(), new ImageIcon("Icons/FileIcon.png"),
						JLabel.LEFT));
			}
		}

		if (!currentDir.toString().equals(".Trash")) {
			fileListModel.addElement(new JLabel("Trash", new ImageIcon(
					"Icons/TrashIcon.png"), JLabel.LEFT));
		}

		fileListModel.addElement(new JLabel(" "));

		GUI.fileArray = fileArray;
	}

	public void zipDirectory(String dir, String zipfile, String[] fileArray,
			DefaultListModel fileListModel, File currentDir) throws IOException {
		File d = new File(dir);
		if (!d.isDirectory()) {

		}
		String[] entries = d.list();
		byte[] buffer = new byte[4096];
		int bytes_read;
		ZipOutputStream out;

		out = new ZipOutputStream(new FileOutputStream(zipfile));

		for (int i = 0; i < entries.length; i++) {
			File f = new File(d, entries[i]);
			if (f.isDirectory()) {
				continue;
			}
			FileInputStream in = new FileInputStream(f);
			ZipEntry entry = new ZipEntry(f.getPath());
			out.putNextEntry(entry);
			while ((bytes_read = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytes_read);
			}
			in.close();
		}
		out.close();
		String[] oldFileArray = fileArray;

		fileArray = new String[fileArray.length + 1];

		fileArray[fileArray.length - 1] = zipfile;
		for (int i = 0; i < fileArray.length - 1; i++) {
			fileArray[i] = oldFileArray[i];
		}

		java.util.Arrays.sort(fileArray);

		fileListModel.removeAllElements();
		fileListModel.addElement(new JLabel("[Up to Parent directory]"));

		for (int i = 0; i < fileArray.length; i++) {
			if (new File(fileArray[i]).isDirectory()) {
				fileListModel.addElement(new JLabel(new File(fileArray[i])
						.getName(), new ImageIcon("Icons/FolderIcon.png"),
						JLabel.LEFT));

			} else {

				fileListModel.addElement(new JLabel(new File(fileArray[i])
						.getName(), new ImageIcon("Icons/FileIcon.png"),
						JLabel.LEFT));
			}
		}

		if (!currentDir.toString().equals(".Trash")) {
			fileListModel.addElement(new JLabel("Trash", new ImageIcon(
					"Icons/TrashIcon.png"), JLabel.LEFT));
		}

		fileListModel.addElement(new JLabel(" "));

		GUI.fileArray = fileArray;
	}

	public void compressAction(DefaultListModel fileListModel, JList fileList,
			String[] fileArray, File currentDir) throws IOException {
		String oldFileName = new File(currentDir,
				((JLabel) fileListModel.getElementAt(fileList
						.getSelectedIndex())).getText()).getAbsolutePath();
		String compressedFileName = oldFileName;
		File f = new File(compressedFileName);
		boolean directory = f.isDirectory();
		if (directory) {
			oldFileName = compressedFileName + ".zip";
		} else {

			oldFileName = compressedFileName + ".gz";
		}
		if (directory) {
			zipDirectory(compressedFileName, oldFileName, fileArray,
					fileListModel, currentDir);
		} else {
			gzipFile(compressedFileName, oldFileName, fileArray, fileListModel,
					currentDir);
		}
	}

	public void searchAction(JTextField searchField, String[] fileArray,
			File currentDir, FilenameFilter filter,
			DefaultListModel fileListModel, JList fileList, JFrame frame,
			JTextField detailsTextField, JTextField pathTextField) {
		int num = 0;
		if (searchField.getText().equals("")) {
			fileArray = null;
		} else {
			File searchDir = new File(currentDir.getAbsolutePath());
			File searchParent = searchDir.getParentFile();
			String[] searchArrayNames;
			File[] searchArray;
			ArrayList<String> searchResults = new ArrayList<String>();
			ArrayList<String> visitedFolders = new ArrayList<String>();
			boolean brokeNaturally;

			do {
				searchArrayNames = searchDir.list(filter);
				java.util.Arrays.sort(searchArrayNames);
				searchArray = new File[searchArrayNames.length];
				for (int i = 0; i < searchArrayNames.length; i++) {
					searchArray[i] = new File(searchDir, searchArrayNames[i]);
				}
				brokeNaturally = true;
				for (File searchFile : searchArray) {
					if (!searchResults.contains(searchFile.getAbsolutePath())) {
						if (((searchFile.getName()).toLowerCase())
								.contains((searchField.getText()).toLowerCase())) {
							searchResults.add(searchFile.getAbsolutePath());
						}
						if (searchFile.isDirectory()
								&& !visitedFolders.contains(searchFile
										.getAbsolutePath())) {
							visitedFolders.add(searchFile.getAbsolutePath());
							brokeNaturally = false;
							searchDir = new File(searchFile.getAbsolutePath());
							searchParent = searchDir.getParentFile();
							break;
						}
					}
				}
				if (brokeNaturally) {
					searchDir = searchParent;
					searchParent = searchDir.getParentFile();
				}
				num++;
				System.out.println(num);
			} while (!(searchDir.getAbsolutePath().equals(currentDir
					.getParentFile().getAbsolutePath())));

			fileArray = new String[searchResults.size()];

			for (int i = 0; i < fileArray.length; i++) {
				fileArray[i] = searchResults.get(i);
			}
		}
		listDirectory(currentDir, fileArray, fileListModel, filter, fileList,
				frame, detailsTextField, currentDir, pathTextField);
	}

	public void printSuccess(String functionName, JFrame frame) {
		JOptionPane.showMessageDialog(frame,
				("Success: " + functionName + " has completed."),
				(functionName + " Success"), JOptionPane.PLAIN_MESSAGE,
				new ImageIcon("Icons/SuccessIcon.png"));
	}

	public void createGhostText(JTextField textField, String text) {
		textField.setForeground(Color.LIGHT_GRAY);
		textField.setText(text);
	}

	public void deleteGhostText(JTextField textField) {
		textField.setForeground(Color.BLACK);
		textField.setText("");
	}

}
