package mountmanager;

import java.util.ArrayList;

public class MountEntry {
	private String name;
	private boolean isEnabled = true;
	private ArrayList<String> folders;

	public MountEntry(String name) {
		this.folders = new ArrayList<String>();
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	public boolean isEnabled() {
		return this.isEnabled;
	}

	public void addFolder(String folder) {
		folders.add(folder);
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

	public String toString() {
		String combined = "//" + name;
		for (String folder : folders) {
			String folderName = folder.substring(folder.lastIndexOf("\\"));
			combined += isEnabled ? "//" : "";
			combined += "\"" + folderName + "\" \"" + folder + "\"\n";
		}

		return combined;
	}
}
