package mountmanager.mountcfg;

import java.util.ArrayList;

public class MountEntry {
	private String name;
	private boolean isEnabled = false;
	private ArrayList<String> folders;

	public MountEntry(String name) {
		this.folders = new ArrayList<String>();
		this.name = name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean isEnabled() {
		return this.isEnabled && this.folders.size() > 0;
	}

	public void addFolder(String folder) {
		folders.add(folder);
	}

	public void addFolderAt(String folder, int index) {
		folders.set(index, folder);
	}

	public String getFolder(int index) {
		return folders.get(index);
	}

	public void removeFolder(int index) {
		folders.remove(index);
	}

	public ArrayList<String> getFolders() {
		return this.folders;
	}
}
