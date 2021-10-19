package mountmanager;
import java.awt.EventQueue;

import javax.swing.UIManager;

public class Main {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					new Ui();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
