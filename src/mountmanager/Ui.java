package mountmanager;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import mountmanager.mountcfg.MountConfig;
import mountmanager.mountcfg.MountEntry;
import mountmanager.uiElements.MountManagerDescription;

import javax.swing.JSplitPane;
import javax.swing.JTable;
import java.awt.Component;

import javax.swing.JFileChooser;

public class Ui {
	private JFrame frame;
	private JPanel middlePanel, applyPanel;
	private String mountLastOpened = "";
	private String cfgLastOpened = "";
	private MountConfig mountConfig = new MountConfig();

	private JPanel mountsPanel;

	private JButton addProjectButton;
	private JButton removeProjectButton;
	private JButton copyProjectButton;
	private JButton addMountButton;
	private JButton removeMountButton;
	private JButton editMountButton;
	private JButton applyButton;

	private JTable projectTable;
	private DefaultTableModel projectTableModel;

	private JTextField mountTextField;
	private DefaultListModel<String> mountListModel;
	private JList<String> mountList;

	public Ui() {
		initialize();
		setupPanels();

		//setupTop();
		setupMiddle();
		setupBottom();

		readSaveFile();

		frame.pack();
		frame.setVisible(true);
	}

	private void madeChanges(boolean madeChanges) {
		applyButton.setEnabled(madeChanges);
	};

	private void readSaveFile() {
		try {
			String saveFilePath = "./mountmanager.cfg";
			File saveFile = new File(saveFilePath);

			if (saveFile.exists()) {
				// get mount.cfg path
				String mountPath;
				mountPath = new String(Files.readAllBytes(Paths.get(saveFilePath)));

				// set mount.cfg path textfield
				mountTextField.setText(mountPath);

				// parse mount.cfg
				mountConfig.fromFile(mountPath);

				fillProjectList();

				// enable all UI elements
				addProjectButton.setEnabled(true);
			}
		} catch (Exception e) {
			// TODO show error message
			e.printStackTrace();
		}
	}

	private void fillProjectList() {
		projectTableModel.setRowCount(0);
		for (MountEntry entry : mountConfig.getEntries()) {
			projectTableModel.addRow(new Object[] { entry.isEnabled(), entry.getName() });
		}
	}

	private void fillMountList() {
		mountListModel.clear();

		MountEntry activeEntry = mountConfig.getActiveEntry();
		if (activeEntry == null) {
			return;
		}

		for (String folder : activeEntry.getFolders()) {
			mountListModel.addElement(folder);
		}
	}

	private void createSaveFile(String saveFilePath) {
		try {
			FileWriter writer = new FileWriter("./mountmanager.cfg");
			writer.write(saveFilePath);
			writer.close();

			readSaveFile();
		} catch (IOException e) {
			// TODO: show error message
		}
	}

	private void initialize() {
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension minSize = new Dimension(700, 600);

		// basics
		frame = new JFrame();
		int xStart = (screenDimension.width - minSize.width) / 2;
		int yStart = (screenDimension.height - minSize.height) / 2;
		frame.setBounds(xStart, yStart, minSize.width, minSize.height);
		frame.setMinimumSize(new Dimension(minSize.width, minSize.height));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.setTitle("Garry's Mod MountManager v2021.10.18 DEV PREVIEW");
		
		// top description
		new MountManagerDescription(frame);
	}

	private void setupPanels() {
		middlePanel = new JPanel();
		middlePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		middlePanel.setPreferredSize(new Dimension(10, 10));
		frame.getContentPane().add(middlePanel);

		applyPanel = new JPanel();
		applyPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		applyPanel.setPreferredSize(new Dimension(0, 50));
		applyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		applyPanel.setLayout(new BoxLayout(applyPanel, BoxLayout.X_AXIS));
		frame.getContentPane().add(applyPanel);
	}

	private void setupMiddle() {
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.1);
		splitPane.setAlignmentY(Component.CENTER_ALIGNMENT);
		splitPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		middlePanel.add(splitPane);

		JPanel projectsPanel = new JPanel();
		projectsPanel.setBorder(new TitledBorder(null, "Projects", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		projectsPanel.setLayout(new BoxLayout(projectsPanel, BoxLayout.Y_AXIS));
		projectsPanel.setPreferredSize(new Dimension(200, 0));
		splitPane.setLeftComponent(projectsPanel);

		setupProjects(projectsPanel);

		mountsPanel = new JPanel();
		mountsPanel.setBorder(
				new TitledBorder(null, "No Project Selected", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mountsPanel.setLayout(new BoxLayout(mountsPanel, BoxLayout.Y_AXIS));
		splitPane.setRightComponent(mountsPanel);

		setupMounts(mountsPanel);
	}

	@SuppressWarnings("serial")
	private void setupProjects(JPanel projectsPanel) {
		// padding on top of table
		addPadding(projectsPanel, 0, 5);

		// create a table with tickbox and string
		projectTable = new JTable() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Class getColumnClass(int column) {
				switch (column) {
				case 0:
					return Boolean.class;
				case 1:
					return String.class;
				default:
					return null;
				}
			}
		};

		// setup some table parameters
		projectTable.getTableHeader().setResizingAllowed(false);
		projectTable.getTableHeader().setReorderingAllowed(false);
		projectTable.getTableHeader().setEnabled(false);
		projectTable.setRowHeight(30);
		projectTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		projectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// override to make only tickbox cells editable
		projectTableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return columnIndex == 0;
			}
		};

		// create a tablemodel with two columns
		projectTableModel.setRowCount(0);
		projectTableModel.setColumnCount(0);
		projectTableModel.addColumn("Enabled");
		projectTableModel.addColumn("Name");
		projectTable.setModel(projectTableModel);

		// make checkbox column slimmer
		projectTable.getColumnModel().getColumn(0).setPreferredWidth(50);
		projectTable.getColumnModel().getColumn(0).setMaxWidth(50);

		// make the table scrollable
		JScrollPane scrollPane = new JScrollPane(projectTable);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		projectsPanel.add(scrollPane);

		// padding above buttons
		addPadding(projectsPanel, 0, 5);

		// a panel that holds the add and remove buttons
		JPanel buttonPanel = new JPanel();
		projectsPanel.add(buttonPanel);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		// create a button to add new projects
		addProjectButton = new JButton("Add");
		addProjectButton.setEnabled(false);
		buttonPanel.add(addProjectButton);

		addProjectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// ask user for a new project name
				String projectName = JOptionPane.showInputDialog("Project name:");
				projectName = projectName.replaceAll("[^a-zA-Z0-9]", "").trim();

				// warn user if project name is empty
				if (projectName.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Project name cant be blank!", "Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				// add new MountEntry to mountconfig
				MountEntry newEntry = new MountEntry(projectName);
				boolean addedEntry = mountConfig.addEntry(newEntry);

				// warning if project name already exists
				if (!addedEntry) {
					JOptionPane.showMessageDialog(frame, projectName + " already exists!", "Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				// add new project to table and select it after
				projectTableModel.addRow(new Object[] { true, projectName });
				projectTable.setRowSelectionInterval(0, projectTable.getRowCount() - 1);

				// changes have been made!
				madeChanges(true);
			}
		});

		// padding between buttons
		addPadding(buttonPanel, 10, 0);

		// create a new button to remove projects
		removeProjectButton = new JButton("Remove");
		removeProjectButton.setEnabled(false);
		buttonPanel.add(removeProjectButton);

		removeProjectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// get selected index and name of project
				int index = projectTable.getSelectedRow();
				String projectName = mountConfig.getActiveEntry().getName();

				// double check if user wants to actually remove project
				int result = JOptionPane.showConfirmDialog(frame,
						"Are you sure you want to delete " + projectName + "?", "Confirmation",
						JOptionPane.YES_NO_OPTION);

				// if the the user doesnt want to remove project return
				if (result != JOptionPane.YES_OPTION) {
					return;
				}

				// remove project from mountconfig and table
				projectTableModel.removeRow(index);
				mountConfig.removeIndex(index);

				// disable remove button
				removeProjectButton.setEnabled(false);
				copyProjectButton.setEnabled(false);

				// changes have been made!
				madeChanges(true);
			}
		});

		// padding between buttons
		addPadding(buttonPanel, 10, 0);

		// create a new button to remove projects
		copyProjectButton = new JButton("Copy");
		copyProjectButton.setEnabled(false);
		buttonPanel.add(copyProjectButton);

		buttonPanel.add(Box.createHorizontalGlue());

		// TODO: refactor together with add button into its own method
		copyProjectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// ask user for a new project name
				String projectName = JOptionPane.showInputDialog("Project name:");
				projectName = projectName.replaceAll("[^a-zA-Z0-9]", "").trim();

				// warn user if project name is empty
				if (projectName.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Project name cant be blank!", "Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				// add new MountEntry to mountconfig
				MountEntry newEntry = new MountEntry(projectName);

				// copy setup of selected Project
				for (String folder : mountConfig.getActiveEntry().getFolders()) {
					newEntry.addFolder(folder);
				}

				boolean addedEntry = mountConfig.addEntry(newEntry);

				// warning if project name already exists
				if (!addedEntry) {
					JOptionPane.showMessageDialog(frame, projectName + " already exists!", "Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				// add new project to table and select it after
				projectTableModel.addRow(new Object[] { true, projectName });
				projectTable.setRowSelectionInterval(0, projectTable.getRowCount() - 1);

				// changes have been made!
				madeChanges(true);
			}
		});

		// listen to the table selection
		ListSelectionModel projectTableSelectionModel = projectTable.getSelectionModel();
		projectTableSelectionModel.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {

				int selectedIndex = projectTable.getSelectedRow();

				// get the current selected row
				boolean selectionExists = selectedIndex != -1;

				removeProjectButton.setEnabled(selectionExists);
				copyProjectButton.setEnabled(selectionExists);
				addMountButton.setEnabled(selectionExists);

				if (selectionExists) {
					mountConfig.setActiveIndex(selectedIndex);
					fillMountList();

					// set the name of the mount panel title
					String activeProjectName = mountConfig.getActiveEntry().getName();
					mountsPanel.setBorder(new TitledBorder(null, "Project \"" + activeProjectName + "\"",
							TitledBorder.LEADING, TitledBorder.TOP, null, null));
				} else {
					mountsPanel.setBorder(new TitledBorder(null, "No Project selected", TitledBorder.LEADING,
							TitledBorder.TOP, null, null));
					mountListModel.clear();
				}
			}
		});

		projectTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				TableModel model = (TableModel) e.getSource();
				int row = e.getFirstRow();
				if (row >= projectTableModel.getRowCount() || row < 0) {
					return;
				}

				boolean checked = (boolean) model.getValueAt(row, 0);

				if (checked != mountConfig.getActiveEntry().isEnabled()) {
					mountConfig.getActiveEntry().setEnabled(checked);
					madeChanges(true);
				}
			}
		});
	}

	private void setupMounts(JPanel mountsPanel) {
		// padding above list
		addPadding(mountsPanel, 0, 5);

		// create a list that will hold all folder names
		mountListModel = new DefaultListModel<String>();
		mountList = new JList<String>(mountListModel);
		mountList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mountList.setFixedCellHeight(30);

		// listen to the list selection
		ListSelectionModel mountListSelectionModel = mountList.getSelectionModel();
		mountListSelectionModel.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				// get the current selected entry
				boolean selectionExists = mountList.getSelectedIndex() != -1;
				removeMountButton.setEnabled(selectionExists);
			}
		});

		mountList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					// Double-click detected
					int index = mountList.locationToIndex(evt.getPoint());
					if (index == -1) {
						return;
					}
					
					String folder = mountConfig.getActiveEntry().getFolder(index);
					try {
						Runtime.getRuntime().exec("explorer.exe /select," + folder);
					} catch (IOException e) {
						// TODO error
					}
				}
			}
		});

		// make the list scrollable
		JScrollPane scrollPane = new JScrollPane(mountList);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mountsPanel.add(scrollPane);

		// padding above buttons
		addPadding(mountsPanel, 0, 5);

		// a panel that holds add and remove buttons
		JPanel buttonPanel = new JPanel();
		mountsPanel.add(buttonPanel);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		buttonPanel.add(Box.createHorizontalGlue());

		// create a button to add mount folders
		addMountButton = new JButton("Add");
		addMountButton.setEnabled(false);
		buttonPanel.add(addMountButton);

		// listen to button presses on add button
		addMountButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// open a file browser and ask for a dictionary
				JFileChooser mountChooser = new JFileChooser();
				mountChooser.setCurrentDirectory(new File(mountLastOpened));
				mountChooser.setDialogType(JFileChooser.OPEN_DIALOG);
				mountChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				// if the result matches the requirements go along
				int result = mountChooser.showOpenDialog(frame);
				if (result == JFileChooser.APPROVE_OPTION) {
					// get the folderpath
					mountLastOpened = mountChooser.getSelectedFile().getAbsolutePath();

					// check if already inserted
					for (String folderName : mountConfig.getActiveEntry().getFolders()) {
						if (folderName.equals(mountLastOpened)) {
							JOptionPane.showMessageDialog(frame, "\"" + mountLastOpened + "\" is already added!",
									"Warning", JOptionPane.WARNING_MESSAGE);
							return;
						}
					}

					// add opened folder to list and MountConfigentry
					mountConfig.getActiveEntry().addFolder(mountLastOpened);
					mountListModel.addElement(mountLastOpened);

					// changes have been made!
					madeChanges(true);
				} else {
					// TODO: warning popup
				}
			}
		});

		// padding between buttons
		addPadding(buttonPanel, 10, 0);

		// a button to remove mount folders
		removeMountButton = new JButton("Remove");
		removeMountButton.setEnabled(false);
		buttonPanel.add(removeMountButton);

		// listen to remove button presses
		removeMountButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// get selected entry in mountlist
				// Note: remove button cannot be pressed when nothing is selected
				int mountListIndex = mountList.getSelectedIndex();

				// Note: negate confirm dialog since a miss-click isn't that bad and it would
				// get annoying

				// remove entry from mountConfig and list
				mountConfig.getActiveEntry().removeFolder(mountListIndex);
				mountListModel.remove(mountListIndex);

				// changes have been made!
				madeChanges(true);
			}
		});

		// padding between buttons
		addPadding(buttonPanel, 10, 0);

		editMountButton = new JButton("Edit");
		editMountButton.setEnabled(false);
		buttonPanel.add(editMountButton);
	}

	private void setupBottom() {
		addPadding(applyPanel, 10, 0);

		JLabel browseLabel = new JLabel("mount.cfg path:");
		browseLabel.setPreferredSize(new Dimension(100, 0));
		applyPanel.add(browseLabel);

		mountTextField = new JTextField();
		mountTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
		mountTextField.setEditable(false);
		applyPanel.add(mountTextField);

		addPadding(applyPanel, 10, 0);

		JButton mountButton = new JButton("Browse");
		applyPanel.add(mountButton);
		mountButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser cfgChooser = new JFileChooser();
				cfgChooser.setCurrentDirectory(new File(cfgLastOpened));
				cfgChooser.setFileFilter(new FileNameExtensionFilter("mount.cfg", "cfg"));
				cfgChooser.setDialogType(JFileChooser.OPEN_DIALOG);
				cfgChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

				int result = cfgChooser.showOpenDialog(frame);
				if (result == JFileChooser.APPROVE_OPTION) {
					cfgLastOpened = cfgChooser.getSelectedFile().getAbsolutePath();

					if (!cfgLastOpened.endsWith("\\cfg\\mount.cfg")) {
						JOptionPane.showMessageDialog(frame, "Please select a valid mount.cfg!", "Warning",
								JOptionPane.WARNING_MESSAGE);
						return;
					}

					createSaveFile(cfgLastOpened);
				} else {
					// TODO: warning popup ?
				}
			}
		});

		addPadding(applyPanel, 30, 0);

		applyButton = new JButton("Apply to mount.cfg");
		applyButton.setEnabled(false);
		applyPanel.add(applyButton);

		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					mountConfig.writeToFile();
				} catch (Exception e1) {
					// TODO error
				}

				// once changes are applied reset madeChanges and disable apply button
				madeChanges(false);
			}
		});

		addPadding(applyPanel, 10, 0);
	}

	private void addPadding(JPanel parent, int paddingX, int paddingY) {
		parent.add(Box.createRigidArea(new Dimension(paddingX, paddingY)));
	}
}
