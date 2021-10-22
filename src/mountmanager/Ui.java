package mountmanager;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.BoxLayout;

import mountmanager.mountcfg.MountConfig;
import mountmanager.uiElements.Config;
import mountmanager.uiElements.Description;
import mountmanager.uiElements.MountOverview;
import mountmanager.uiElements.ProjectOverview;
import mountmanager.util.ErrorHandler;
import mountmanager.util.Persistance;

import javax.swing.JSplitPane;
import java.awt.Component;

public class Ui {
	private JFrame frame;
	private Persistance persistance;
	private MountConfig mountConfig;

	private ProjectOverview projectOverview;
	private MountOverview mountOverview;
	private Config config;

	public Ui() {
		mountConfig = new MountConfig(this);
		persistance = new Persistance(this);

		initialize();
		frame.pack();
		frame.setVisible(true);

		load();
	}

	public JFrame getFrame() {
		return this.frame;
	}

	public MountConfig getMountConfig() {
		return this.mountConfig;
	}

	public Persistance getPersistance() {
		return this.persistance;
	}

	public MountOverview getMountOverview() {
		return this.mountOverview;
	}

	public void madeChanges(boolean madeChanges) {
		config.enableApplyButton(madeChanges);
	}

	public void load() {
		String mountPath = persistance.getMountPath();
		if (mountPath != null) {
			mountConfig.fromFile(mountPath);

			config.setMountPath(mountPath);
			projectOverview.fillProjectList();
			projectOverview.getAddButton().setEnabled(true);
		} else {
			ErrorHandler.infoPopup(frame, "Missing mount.cfg",
					"Please select your mount.cfg at the bottom of the tool.");
		}
	}

	private void initialize() {
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension minSize = new Dimension(800, 700);

		// basics
		frame = new JFrame();
		int xStart = (screenDimension.width - minSize.width) / 2;
		int yStart = (screenDimension.height - minSize.height) / 2;
		frame.setBounds(xStart, yStart, minSize.width, minSize.height);
		frame.setMinimumSize(new Dimension(minSize.width, minSize.height));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.setTitle("Garry's Mod MountManager v2021.10.22");

		// top description
		frame.getContentPane().add(new Description(this).create());

		// middle
		JPanel middlePanel = new JPanel();
		middlePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		middlePanel.setPreferredSize(new Dimension(10, 10));
		frame.getContentPane().add(middlePanel);

		// splitting the middle of the tool in two sections
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.1);
		splitPane.setAlignmentY(Component.CENTER_ALIGNMENT);
		splitPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		middlePanel.add(splitPane);

		// left has all user projects
		projectOverview = new ProjectOverview(this);
		splitPane.setLeftComponent(projectOverview.create());

		// right has mounted folders for that project
		mountOverview = new MountOverview(this);
		splitPane.setRightComponent(mountOverview.create());

		// configuration panel at the bottom
		config = new Config(this);
		frame.getContentPane().add(config.create());
	}
}
