package mountmanager.uiElements;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mountmanager.Ui;
import mountmanager.mountcfg.MountEntry;
import mountmanager.util.ErrorHandler;

public class MountOverview extends UiElement {
	private JList<String> list;
	private DefaultListModel<String> listModel;
	private JButton addButton, removeButton, editButton;
	private String lastOpened = "";

	public MountOverview(Ui ui) {
		super(ui);
	}

	public void fillMountList() {
		listModel.clear();

		MountEntry activeEntry = ui.getMountConfig().getActiveEntry();
		if (activeEntry == null) {
			return;
		}

		for (int i = 0; i < activeEntry.getFolders().size(); i++) {
			String folder = activeEntry.getFolder(i);

			// check if folder can be found
			if (!new File(folder).exists()) {
				String title = "Missing folder";
				String message = "<html>" + folder + "<br>cannot be found anymore! Remove from configuration?</html>";
				int result = JOptionPane.showConfirmDialog(ui.getFrame(), message, title, JOptionPane.YES_NO_OPTION);

				if (result == JOptionPane.YES_OPTION) {
					activeEntry.removeFolder(i);
					ui.madeChanges(true);

					// once the element was removed restart the process and return once finished as
					// to not cause an endless loop
					fillMountList();
					return;
				}
			}

			listModel.addElement(folder);
		}
	}

	public void setTitle(String title) {
		mainPanel.setBorder(new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP, null, null));
	}

	public void clear() {
		this.listModel.clear();
	}

	public void enableAddButton(boolean isEnabled) {
		addButton.setEnabled(isEnabled);
	}

	@Override
	protected void initialize() {
		mainPanel = new JPanel();
		mainPanel.setBorder(
				new TitledBorder(null, "No Project Selected", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
	}

	@Override
	protected void setupComponents() {
		// padding above list
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

		// create a list that will hold all folder names
		listModel = new DefaultListModel<String>();
		list = new JList<String>(listModel);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setFixedCellHeight(30);

		// make the list scrollable
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainPanel.add(scrollPane);

		// padding above buttons
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

		// a panel that holds add and remove buttons
		JPanel buttonPanel = new JPanel();
		mainPanel.add(buttonPanel);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		buttonPanel.add(Box.createHorizontalGlue());

		// create a button to add mount folders
		addButton = new JButton("Add");
		addButton.setEnabled(false);
		buttonPanel.add(addButton);

		// padding between buttons
		buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));

		// a button to remove mount folders
		removeButton = new JButton("Remove");
		removeButton.setEnabled(false);
		buttonPanel.add(removeButton);

		// padding between buttons
		buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));

		editButton = new JButton("Edit");
		editButton.setEnabled(false);
		buttonPanel.add(editButton);
	}

	@Override
	protected void setupListeners() {
		// listen to the list selection
		ListSelectionModel mountListSelectionModel = list.getSelectionModel();
		mountListSelectionModel.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				// get the current selected entry
				boolean selectionExists = list.getSelectedIndex() != -1;
				removeButton.setEnabled(selectionExists);
				editButton.setEnabled(selectionExists);
			}
		});

		// open windows file browser at selected location
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					// Double-click detected
					int index = list.locationToIndex(evt.getPoint());
					if (index == -1) {
						return;
					}

					String folder = ui.getMountConfig().getActiveEntry().getFolder(index);
					try {
						Runtime.getRuntime().exec("explorer.exe /select," + folder);
					} catch (IOException exception) {
						ErrorHandler.errorPopup(ui.getFrame(), "Unexpected error", exception.getMessage());
					}
				}
			}
		});

		// listen to button presses on add button
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// String openedPath = showFileBrowser(lastOpened);
				// open a file browser and ask for a dictionary
				JFileChooser mountChooser = new JFileChooser();
				mountChooser.setCurrentDirectory(new File(lastOpened));
				mountChooser.setMultiSelectionEnabled(true);
				mountChooser.setDialogType(JFileChooser.OPEN_DIALOG);
				mountChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				// if the result matches the requirements go along
				int result = mountChooser.showOpenDialog(ui.getFrame());
				if (result == JFileChooser.CANCEL_OPTION) {
					return;
				}

				if (result == JFileChooser.APPROVE_OPTION) {
					// get the folderpath
					File[] openedFiles = mountChooser.getSelectedFiles();
					lastOpened = openedFiles[0].getAbsolutePath(); // TODO: substring to parent folder
					for (File file : openedFiles) {
						String path = file.getAbsolutePath();

						// folders should not be added multiple times
						if (ui.getMountConfig().getActiveEntry().getFolders().contains(path)) {
							JOptionPane.showMessageDialog(ui.getFrame(), "\"" + path + "\" is already added!",
									"Folder selection warning", JOptionPane.WARNING_MESSAGE);
							continue;
						}

						// add opened folder to list and MountConfigentry
						ui.getMountConfig().getActiveEntry().addFolder(path);
						listModel.addElement(path);

						// changes have been made!
						ui.madeChanges(true);
					}
				} else if (result != JFileChooser.CANCEL_OPTION) {
					JOptionPane.showMessageDialog(ui.getFrame(), "Please only select directories.",
							"Folder selection warning", JOptionPane.WARNING_MESSAGE);
				}
			}
		});

		// listen to remove button presses
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int confirm = JOptionPane.showConfirmDialog(ui.getFrame(),
						"Are you sure you want to remove the selected folder(s)?", "Remove mount folders",
						JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					// get selected entry in mountlist
					// Note: remove button cannot be pressed when nothing is selected
					List<String> selected = list.getSelectedValuesList();
					for (String folderName : selected) {
						int index = listModel.lastIndexOf(folderName);
						// remove entry from mountConfig and list
						ui.getMountConfig().getActiveEntry().removeFolder(index);
						listModel.remove(index);
					}
					// changes have been made!
					ui.madeChanges(true);
				}
			}
		});

		// listen to edit button presses
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = list.getSelectedIndex();
				String currentFolder = listModel.get(index);

				// open a file browser and ask for a dictionary
				JFileChooser mountChooser = new JFileChooser();
				mountChooser.setCurrentDirectory(new File(currentFolder));
				mountChooser.setDialogType(JFileChooser.OPEN_DIALOG);
				mountChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				// if the result matches the requirements go along
				int result = mountChooser.showOpenDialog(ui.getFrame());

				if (result == JFileChooser.APPROVE_OPTION) {
					// open a file browser at the location of the current folder to apply changes
					String newFolder = mountChooser.getSelectedFile().getAbsolutePath();

					// in case the file browser was canceled
					if (newFolder == null) {
						return;
					}

					// replace the element in the list
					ui.getMountConfig().getActiveEntry().addFolderAt(newFolder, index);
					listModel.setElementAt(newFolder, index);

					// finally mark that changes have been made
					ui.madeChanges(true);
				} else if (result != JFileChooser.CANCEL_OPTION) {
					JOptionPane.showMessageDialog(ui.getFrame(), "Please only select directories.",
							"Folder selection warning", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
	}
}
