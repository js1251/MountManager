package mountmanager.uiElements;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import mountmanager.Ui;

public class Config extends UiElement {
	private JButton browseButton, applyButton;
	private JTextField mountPathTextField;
	private String lastOpened = "";

	public Config(Ui ui) {
		super(ui);
	}

	public void setMountPath(String mountPath) {
		mountPathTextField.setText(mountPath);
	}

	public void enableApplyButton(boolean isEnabled) {
		applyButton.setEnabled(isEnabled);
	}

	@Override
	protected void initialize() {
		mainPanel = new JPanel();
		mainPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		mainPanel.setPreferredSize(new Dimension(0, 50));
		mainPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
	}

	@Override
	protected void setupComponents() {
		mainPanel.add(Box.createRigidArea(new Dimension(10, 0)));

		JLabel browseLabel = new JLabel("mount.cfg path:");
		browseLabel.setPreferredSize(new Dimension(100, 0));
		mainPanel.add(browseLabel);

		mountPathTextField = new JTextField();
		mountPathTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
		mountPathTextField.setEditable(false);
		mainPanel.add(mountPathTextField);

		mainPanel.add(Box.createRigidArea(new Dimension(10, 0)));

		browseButton = new JButton("Browse");
		mainPanel.add(browseButton);

		mainPanel.add(Box.createRigidArea(new Dimension(30, 0)));

		applyButton = new JButton("Apply to mount.cfg");
		applyButton.setEnabled(false);
		mainPanel.add(applyButton);

		mainPanel.add(Box.createRigidArea(new Dimension(10, 0)));
	}

	@Override
	protected void setupListeners() {
		browseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser cfgChooser = new JFileChooser();
				cfgChooser.setCurrentDirectory(new File(lastOpened));
				cfgChooser.setFileFilter(new FileNameExtensionFilter("mount.cfg", "cfg"));
				cfgChooser.setDialogType(JFileChooser.OPEN_DIALOG);
				cfgChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

				int result = cfgChooser.showOpenDialog(ui.getFrame());
				if (result == JFileChooser.APPROVE_OPTION) {
					String mountPath = cfgChooser.getSelectedFile().getAbsolutePath();
					lastOpened = mountPath;

					if (!mountPath.endsWith("\\cfg\\mount.cfg")) {
						JOptionPane.showMessageDialog(ui.getFrame(), "Please select a valid mount.cfg!", "Warning",
								JOptionPane.WARNING_MESSAGE);
						return;
					}

					ui.getPersistance().createSaveFile(mountPath);
				} else {
					// TODO: warning popup ?
				}
			}
		});

		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ui.getMountConfig().writeToFile();
				} catch (Exception e1) {
					// TODO error
				}

				// once changes are applied reset madeChanges and disable apply button
				ui.madeChanges(false);
			}
		});
	}
}
