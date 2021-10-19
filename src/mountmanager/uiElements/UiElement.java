package mountmanager.uiElements;

import javax.swing.JFrame;

/**
 * Abstract class for any UI element in the MountManager
 * 
 * @author Jakob Sailer
 */
public abstract class UiElement {
	protected JFrame frame;

	public UiElement(JFrame frame) {
		this.frame = frame;
	}

	/**
	 * Gets the parent frame this UI element is added to
	 * 
	 * @return the parent frame
	 */
	public JFrame getParentFrame() {
		return this.frame;
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
