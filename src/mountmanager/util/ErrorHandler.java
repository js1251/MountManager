package mountmanager.util;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ErrorHandler {

	public static void warningPopup(JFrame relative, String title, String message) {
		JOptionPane.showMessageDialog(relative, message, title, JOptionPane.WARNING_MESSAGE);
	}

	public static void errorPopup(JFrame relative, String title, String message) {
		JOptionPane.showMessageDialog(relative, message, title, JOptionPane.ERROR_MESSAGE);
	}

	public static void infoPopup(JFrame relative, String title, String message) {
		JOptionPane.showMessageDialog(relative, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
}
