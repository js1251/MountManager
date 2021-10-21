package mountmanager.uiElements;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import mountmanager.Ui;
import mountmanager.mountcfg.MountEntry;
import mountmanager.util.ErrorHandler;

public class ProjectOverview extends UiElement {
	private JTable table;
	private DefaultTableModel tableModel;
	private JButton addButton, removeButton, copyButton, moveUpButton, moveDownButton;
	private boolean internalTableManipulation = true;

	public ProjectOverview(Ui ui) {
		super(ui);
	}

	public JButton getAddButton() {
		return this.addButton;
	}

	public void fillProjectList() {
		internalTableManipulation = true;
		tableModel.setRowCount(0);
		for (MountEntry entry : ui.getMountConfig().getEntries()) {
			tableModel.addRow(new Object[] { entry.isEnabled(), entry.getName() });
		}
		internalTableManipulation = false;
	}

	@Override
	protected void initialize() {
		mainPanel = new JPanel();
		mainPanel.setBorder(new TitledBorder(null, "Projects", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		mainPanel.setPreferredSize(new Dimension(200, 0));
	}

	@SuppressWarnings("serial")
	@Override
	protected void setupComponents() {
		// panel that holds the table
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
		tablePanel.setPreferredSize(new Dimension(200, 0));
		mainPanel.add(tablePanel);

		// padding on top of table
		tablePanel.add(Box.createRigidArea(new Dimension(0, 5)));

		// create a table with tickbox and string
		table = new JTable() {
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
		table.getTableHeader().setResizingAllowed(false);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setEnabled(false);
		table.setRowHeight(30);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// override to make only tickbox cells editable
		tableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return columnIndex == 0;
			}
		};

		// create a tablemodel with two columns
		tableModel.setRowCount(0);
		tableModel.setColumnCount(0);
		tableModel.addColumn("Enabled");
		tableModel.addColumn("Name");
		table.setModel(tableModel);

		// make checkbox column slimmer
		table.getColumnModel().getColumn(0).setPreferredWidth(50);
		table.getColumnModel().getColumn(0).setMaxWidth(50);

		// make the table scrollable
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tablePanel.add(scrollPane);

		// padding above buttons
		tablePanel.add(Box.createRigidArea(new Dimension(0, 5)));

		// a panel that holds the add and remove buttons
		JPanel buttonPanel = new JPanel();
		tablePanel.add(buttonPanel);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		// create a button to add new projects
		addButton = new JButton("Add");
		addButton.setEnabled(false);
		buttonPanel.add(addButton);

		// padding between buttons
		buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));

		// create a new button to remove projects
		removeButton = new JButton("Remove");
		removeButton.setEnabled(false);
		buttonPanel.add(removeButton);

		// padding between buttons
		buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));

		// create a new button to remove projects
		copyButton = new JButton("Copy");
		copyButton.setEnabled(false);
		buttonPanel.add(copyButton);

		// padding between buttons
		buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));

		moveUpButton = new JButton("▲");
		moveUpButton.setMargin(new Insets(2, 5, 3, 5));
		moveUpButton.setEnabled(false);
		buttonPanel.add(moveUpButton);

		// padding between buttons
		buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));

		moveDownButton = new JButton("▼");
		moveDownButton.setMargin(new Insets(2, 5, 3, 5));
		moveDownButton.setEnabled(false);
		buttonPanel.add(moveDownButton);

		buttonPanel.add(Box.createHorizontalGlue());
	}

	@Override
	protected void setupListeners() {
		// listen to the table selection
		ListSelectionModel projectTableSelectionModel = table.getSelectionModel();
		projectTableSelectionModel.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				// the currently selected row
				int selectedIndex = table.getSelectedRow();

				// get the current selected row
				boolean selectionExists = selectedIndex != -1;

				// enable remove and copy buttons only if selection exists
				removeButton.setEnabled(selectionExists);
				copyButton.setEnabled(selectionExists);

				// enable move up/ down buttons depending on position in the table
				moveUpButton.setEnabled(selectionExists && selectedIndex > 0);
				moveDownButton.setEnabled(selectionExists && selectedIndex < table.getRowCount() - 1);

				// enable the add button of the mount list
				ui.getMountOverview().enableAddButton(selectionExists);

				if (selectionExists) {
					// set the active project and rebuild mount list
					ui.getMountConfig().setActiveIndex(selectedIndex);
					ui.getMountOverview().fillMountList();

					// set the name of the mount panel title
					String activeProjectName = ui.getMountConfig().getActiveEntry().getName();
					ui.getMountOverview().setTitle("Mounted folders for project \"" + activeProjectName + "\"");
				} else {
					// set the mount list to empty
					ui.getMountOverview().setTitle("No Project selected");
					ui.getMountOverview().clear();
				}
			}
		});

		tableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {

				// if the rows are switched for a split second there are two of the same rows.
				// this triggers this listener which then changes the enabled state of the
				// project.
				// Effectively copying the state of one of the two. To prevent this check for
				// this edgecase.
				if (internalTableManipulation) {
					return;
				}

				TableModel model = (TableModel) e.getSource();
				int row = e.getFirstRow();
				if (row >= tableModel.getRowCount() || row < 0) {
					return;
				}

				boolean checked = (boolean) model.getValueAt(row, 0);

				if (checked != ui.getMountConfig().getActiveEntry().isEnabled()) {
					ui.getMountConfig().getActiveEntry().setEnabled(checked);
					ui.madeChanges(true);
				}
			}
		});

		// double click project to change name
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					// Double-click detected
					int index = table.getSelectedRow();
					if (index == -1) {
						return;
					}

					String currentName = ui.getMountConfig().getActiveEntry().getName();
					String newName = JOptionPane.showInputDialog(ui.getFrame(),
							"What should the project be renamed to ?", currentName);

					if (newName == null) {
						return;
					}

					try {
						newName = parseName(newName);
						ui.getMountConfig().getActiveEntry().setName(newName);
						tableModel.setValueAt(newName, index, 1);
						ui.madeChanges(true);
					} catch (Exception exception) {
						ErrorHandler.warningPopup(ui.getFrame(), "Invalid project name", exception.getMessage());
					}
				}
			}
		});

		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					makeNewEntry();
				} catch (Exception exception) {
					ErrorHandler.warningPopup(ui.getFrame(), "Invalid project name", exception.getMessage());
				}
			}
		});

		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// get selected index and name of project
				int index = table.getSelectedRow();
				String projectName = ui.getMountConfig().getActiveEntry().getName();

				// double check if user wants to actually remove project
				int result = JOptionPane.showConfirmDialog(ui.getFrame(),
						"Are you sure you want to delete " + projectName + "?", "Confirmation",
						JOptionPane.YES_NO_OPTION);

				// if the the user doesnt want to remove project return
				if (result != JOptionPane.YES_OPTION) {
					return;
				}

				// remove project from mountconfig and table
				tableModel.removeRow(index);
				ui.getMountConfig().removeIndex(index);

				// disable remove button
				removeButton.setEnabled(false);
				copyButton.setEnabled(false);

				// changes have been made!
				ui.madeChanges(true);
			}
		});

		copyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					MountEntry newEntry = makeNewEntry();
					if (newEntry == null) {
						return;
					}

					for (String folder : ui.getMountConfig().getActiveEntry().getFolders()) {
						newEntry.addFolder(folder);
					}
				} catch (Exception exception) {
					ErrorHandler.warningPopup(ui.getFrame(), "Invalid project name", exception.getMessage());
				}
			}
		});

		moveUpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = table.getSelectedRow();
				switchElements(selectedRow, true);
			}
		});

		moveDownButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = table.getSelectedRow();
				switchElements(selectedRow, false);
			}
		});
	}

	private void switchElements(int row, boolean moveUp) {
		Object[] currentRow = { tableModel.getValueAt(row, 0), tableModel.getValueAt(row, 1) };
		int offset = moveUp ? -1 : 1;
		Object[] otherRow = { tableModel.getValueAt(row + offset, 0), tableModel.getValueAt(row + offset, 1) };

		// internal check to know that rows are being switched
		internalTableManipulation = true;

		// the first column
		tableModel.setValueAt(currentRow[0], row + offset, 0);
		tableModel.setValueAt(currentRow[1], row + offset, 1);

		// the second column
		tableModel.setValueAt(otherRow[0], row, 0);
		tableModel.setValueAt(otherRow[1], row, 1);

		internalTableManipulation = false;

		// also switch the mount config
		MountEntry currentEntry = ui.getMountConfig().getEntries().get(row);
		MountEntry otherEntry = ui.getMountConfig().getEntries().get(row + offset);

		ui.getMountConfig().getEntries().set(row + offset, currentEntry);
		ui.getMountConfig().getEntries().set(row, otherEntry);

		table.setRowSelectionInterval(row + offset, row + offset);
		ui.madeChanges(true);
	}

	/**
	 * Creates a new project entry by asking the user for a name.
	 * 
	 * @return The new project entry.
	 * @throws Exception - when the name is invalid or already in use
	 */
	private MountEntry makeNewEntry() throws Exception {
		// ask user for a new project name
		String projectName = JOptionPane.showInputDialog(ui.getFrame(), "Project name:");
		if (projectName == null) {
			return null;
		}

		projectName = parseName(projectName);

		// add new MountEntry to mountconfig
		MountEntry newEntry = new MountEntry(projectName);
		ui.getMountConfig().addEntry(newEntry);

		// add new project to table and select it after
		tableModel.addRow(new Object[] { false, projectName });
		table.setRowSelectionInterval(0, table.getRowCount() - 1);

		// changes have been made!
		ui.madeChanges(true);

		return newEntry;
	}

	private String parseName(String name) throws Exception {
		name = name.replaceAll("[^a-zA-Z0-9 -_.,]", "").trim();

		// warn user if project name is empty
		if (name.isEmpty()) {
			throw new Exception("Project name cant be blank!");
		}

		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String rowName = (String) tableModel.getValueAt(i, 1);
			if (rowName.equals(name)) {
				throw new Exception(name + " already exists!");
			}
		}

		return name;
	}
}
