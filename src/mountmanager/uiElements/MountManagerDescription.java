package mountmanager.uiElements;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import mountmanager.util.WebsiteOpener;

/**
 * Description panel on the top of the tool. Contains a short description and
 * credits
 * 
 * @author Jakob Sailer
 *
 */
public class MountManagerDescription extends UiElement {
	private JPanel mainPanel;
	private JLabel websiteLabel;

	public MountManagerDescription(JFrame frame) {
		super(frame);
	}

	protected void initialize() {
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		mainPanel.setPreferredSize(new Dimension(0, 100));
		mainPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
		mainPanel.setMinimumSize(new Dimension(0, 100));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		frame.getContentPane().add(mainPanel);
	}

	protected void setupComponents() {
		String text = "<html>" + "<b>1.</b> Select \"GarrysMod\\garrysmod\\cfg\\mount.cfg\"<BR>"
				+ "<b>2.</b> Add a project and give it a name<BR>"
				+ "<b>3.</b> Add any folders from where you want to mount content" + "</html>";

		JLabel description = new JLabel(text);
		description.setHorizontalAlignment(SwingConstants.CENTER);
		description.setBounds(10, 11, 364, 78);
		mainPanel.add(description);

		JPanel panel = new JPanel();
		mainPanel.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel creditsLabel1 = new JLabel("<html><b>Created by</b></html>");
		creditsLabel1.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(creditsLabel1);

		JLabel creditsLabel2 = new JLabel("<html>Jakob Sailer aka KingPommes</b></html>");
		creditsLabel2.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(creditsLabel2);

		websiteLabel = new JLabel("<HTML><U>www.jakobsailer.com</U></HTML>");
		websiteLabel.setHorizontalAlignment(SwingConstants.CENTER);
		websiteLabel.setForeground(Color.BLUE);
		websiteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		panel.add(websiteLabel);
	}

	protected void setupListeners() {
		websiteLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				try {
					WebsiteOpener.openWebpage("https://www.jakobsailer.com/");
				} catch (Exception exception) {
					// TODO: error popup
				}
			}
		});
	}
}
