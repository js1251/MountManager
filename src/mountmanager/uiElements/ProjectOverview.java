package mountmanager.uiElements;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	private JButton addButton, removeButton, copyButton;

	public ProjectOverview(Ui ui) {
		super(ui);
	}

	public JButton getAddButton() {
		return this.addButton;
	}

	public void fillProjectList() {
		tableModel.setRowCount(0);
		for (MountEntry entry : ui.getMountConfig().getEntries()) {
			tableModel.addRow(new Object[] { entry.isEnabled(), entry.getName() });
		}
	}

	@Override
	protected void initialize() {
		mainPanel = new JPanel();
		mainPanel.setBorder(new TitledBorder(null, "Projects", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setPreferredSize(new Dimension(200, 0));
	}

	@SuppressWarnings("serial")
	@Override
	protected void setupComponents() {
		// padding on top of table
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

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
		mainPanel.add(scrollPane);

		// padding above buttons
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

		// a panel that holds the add and remove buttons
		JPanel buttonPanel = new JPanel();
		mainPanel.add(buttonPanel);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		// create a button to add new projects
		addButton = new JButton("Add");
		addButton.setEnabled(false);
		buttonPanel.add(addButton);

		// padding between buttons
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));

		// create a new button to remove projects
		removeButton = new JButton("Remove");
		removeButton.setEnabled(false);
		buttonPanel.add(removeButton);

		// padding between buttons
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));

		// create a new button to remove projects
		copyButton = new JButton("Copy");
		copyButton.setEnabled(false);
		buttonPanel.add(copyButton);

		buttonPanel.add(Box.createHorizontalGlue());
	}

	@Override
	protected void setupListeners() {
		// listen to the table selection
		ListSelectionModel projectTableSelectionModel = table.getSelectionModel();
		projectTableSelectionModel.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {

				int selectedIndex = table.getSelectedRow();

				// get the current selected row
				boolean selectionExists = selectedIndex != -1;

				removeButton.setEnabled(selectionExists);
				copyButton.setEnabled(selectionExists);
				ui.getMountOverview().enableAddButton(selectionExists);

				if (selectionExists) {
					ui.getMountConfig().setActiveIndex(selectedIndex);
					ui.getMountOverview().fillMountList();

					// set the name of the mount panel title
					String activeProjectName = ui.getMountConfig().getActiveEntry().getName();
					ui.getMountOverview().setTitle("Project \"" + activeProjectName + "\"");
				} else {
					ui.getMountOverview().setTitle("No Project selected");
					ui.getMountOverview().clear();
				}
			}
		});

		tableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
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

		projectName = projectName.replaceAll("[^a-zA-Z0-9 -_.,]", "").trim();

		// warn user if project name is empty
		if (projectName.isEmpty()) {
			throw new Exception("Project name cant be blank!");
		}

		// add new MountEntry to mountconfig
		MountEntry newEntry = new MountEntry(projectName);
		boolean addedEntry = ui.getMountConfig().addEntry(newEntry);

		// warning if project name already exists
		if (!addedEntry) {
			throw new Exception(projectName + " already exists!");
		}

		// add new project to table and select it after
		tableModel.addRow(new Object[] { true, projectName });
		table.setRowSelectionInterval(0, table.getRowCount() - 1);

		// changes have been made!
		ui.madeChanges(true);

		return newEntry;
	}
}
