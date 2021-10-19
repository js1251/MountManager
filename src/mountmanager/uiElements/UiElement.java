package mountmanager.uiElements;

import javax.swing.JPanel;

import mountmanager.Ui;

/**
 * Abstract class for any UI element in the MountManager
 * 
 * @author Jakob Sailer
 */
public abstract class UiElement {
	protected JPanel mainPanel;
	protected Ui ui;
	
	public UiElement(Ui ui) {
		this.ui = ui;
		initialize();
		setupComponents();
		setupListeners();
	}
	
	public JPanel create() {
		return this.mainPanel;
	}

	/**
	 * Initializes the UI element
	 */
	protected abstract void initialize();

	/**
	 * Sets up all components responsible for this element
	 */
	protected abstract void setupComponents();

	/**
	 * Sets up all listeners responsible for the features of this UI element
	 */
	protected abstract void setupListeners();
}
