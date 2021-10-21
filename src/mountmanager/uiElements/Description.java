package mountmanager.uiElements;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import mountmanager.Ui;
import mountmanager.util.ErrorHandler;
import mountmanager.util.WebsiteOpener;

/**
 * Description panel on the top of the tool. Contains a short description and
 * credits
 * 
 * @author Jakob Sailer
 *
 */
public class Description extends UiElement {
	private JLabel websiteLabel;

	public Description(Ui ui) {
		super(ui);
	}

	@Override
	protected void initialize() {
		mainPanel = new JPanel();
		mainPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		mainPanel.setPreferredSize(new Dimension(0, 100));
		mainPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
		mainPanel.setMinimumSize(new Dimension(0, 100));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
	}

	@Override
	protected void setupComponents() {
		mainPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		
		// Title and description
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
		mainPanel.add(titlePanel);
		
		JLabel title = new JLabel("Manage your mount.cfg");
		title.setFont(new Font("Tahoma", Font.PLAIN, 25));
		titlePanel.add(title);

		titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
		
		JLabel howTo1 = new JLabel("    1. Select \"GarrysMod\\garrysmod\\cfg\\mount.cfg\"");
		JLabel howTo2 = new JLabel("    2. Add a project and give it a name");
		JLabel howTo3 = new JLabel("    3. Add any folders from where you want to mount content");
		
		titlePanel.add(howTo1);
		titlePanel.add(howTo2);
		titlePanel.add(howTo3);
		
		mainPanel.add(Box.createHorizontalGlue());

		// credits and website
		JPanel creditsPanel = new JPanel();
		creditsPanel.setLayout(new BoxLayout(creditsPanel, BoxLayout.Y_AXIS));
		creditsPanel.setMaximumSize(new Dimension(130, 50));
		mainPanel.add(creditsPanel);

		mainPanel.add(Box.createRigidArea(new Dimension(5, 0)));

		// created by text
		JLabel creditsLabel1 = new JLabel("<html><b>Created by</b></html>");
		creditsLabel1.setHorizontalAlignment(SwingConstants.CENTER);
		creditsPanel.add(creditsLabel1);

		// name text
		JLabel creditsLabel2 = new JLabel("<html>Jakob Sailer aka KingPommes</b></html>");
		creditsLabel2.setHorizontalAlignment(SwingConstants.CENTER);
		creditsPanel.add(creditsLabel2);

		// website text
		websiteLabel = new JLabel("<HTML><U>www.jakobsailer.com</U></HTML>");
		websiteLabel.setHorizontalAlignment(SwingConstants.CENTER);
		websiteLabel.setForeground(Color.BLUE);
		websiteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		creditsPanel.add(websiteLabel);
	}

	protected void setupComponents2() {
		// quick tutorial of sorts
		String text = "<html>" + "<b>1.</b> Select \"GarrysMod\\garrysmod\\cfg\\mount.cfg\"<BR>"
				+ "<b>2.</b> Add a project and give it a name<BR>"
				+ "<b>3.</b> Add any folders from where you want to mount content" + "</html>";

		JLabel description = new JLabel(text);
		description.setHorizontalAlignment(SwingConstants.CENTER);
		description.setBounds(10, 11, 364, 78);
		mainPanel.add(description);

	}

	@Override
	protected void setupListeners() {
		// add a "button" to open my website
		websiteLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				try {
					WebsiteOpener.openWebpage("https://www.jakobsailer.com/");
				} catch (Exception exception) {
					ErrorHandler.errorPopup(ui.getFrame(), "Unexpected error", exception.getMessage());
				}
			}
		});
	}
}
