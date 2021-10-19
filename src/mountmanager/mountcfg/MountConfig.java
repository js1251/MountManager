package mountmanager.mountcfg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MountConfig {
	private ArrayList<MountEntry> entries = new ArrayList<MountEntry>();
	private String mountFilePath;
	private int activeIndex = -1;
	private static String tag = "//Created by MountManager - DO NOT EDIT THIS FILE\n";

	public ArrayList<MountEntry> getEntries() {
		return this.entries;
	}

	public boolean addEntry(MountEntry entry) {
		for (MountEntry existingEntry : entries) {
			if (existingEntry.getName().equals(entry.getName())) {
				return false;
			}
		}

		entries.add(entry);
		setActiveIndex(entries.size() - 1);
		return true;
	}

	public void removeIndex(int index) {
		entries.remove(index);
		if (entries.size() > 0) {
			if (index > 0) {
				setActiveIndex(index > 0 ? index - 1 : index + 1);
			}
		}
	}

	public void setActiveIndex(int index) {
		this.activeIndex = Math.min(Math.max(0, index), entries.size() - 1);
	}

	public int getActiveIndex() {
		return this.activeIndex;
	}

	public MountEntry getActiveEntry() {
		if (entries.size() == 0) {
			return null;
		}
		return entries.get(this.activeIndex);
	}

	public String toString() {
		String combined = "mountcfg\n";
		combined += "{\n";

		// all entries
		for (MountEntry entry : entries) {
			combined += entry.toString();
		}

		combined += "}";

		return combined;
	}

	public void fromFile(String mountFilePath) {
		try {
			this.mountFilePath = mountFilePath;
			File mountFile = new File(mountFilePath);

			if (!mountFile.exists()) {
				throw new Exception("Could.not find " + mountFilePath + "!");
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(mountFile)));
			String line = br.readLine();

			// identify mount.cfg tag
			if (!line.equals(tag.trim())) {
				br.close();
				return;
			} else {
				line = br.readLine();
			}

			// read the whole file
			while (line != null) {
				// skip irrelevant lines
				if (line.isEmpty() || line.contains("mountcfg") || line.contains("{") || line.contains("}")) {
					line = br.readLine();
					continue;
				}

				// a new project always starts with // and does not contain any "
				if (line.startsWith("//") && !line.contains("\"")) {
					// create a new entry and set its name
					MountEntry newEntry = new MountEntry(line.replaceAll("/", "").trim());
					line = br.readLine();

					// read out all entry folders if there are any
					while (line != null && line.contains("\"") && !line.contains("}")) {
						// figure out if the project is enabled by looking at the folders // prefix
						newEntry.setEnabled(!line.startsWith("//"));

						// get the folder path by doing some string manipulation
						newEntry.addFolder(line.replaceAll("//", "").split("\"")[3].trim());

						// read the next line to see if there are any other folders
						line = br.readLine();
					}

					addEntry(newEntry);
				}
			}

			br.close();
		} catch (Exception e) {
			// TODO: error popup
		}
	}

	public void writeToFile() throws Exception {
		File mountFile = new File(mountFilePath);

		if (!mountFile.exists()) {
			throw new Exception("Could.not find " + mountFilePath + "!");
		}

		FileWriter writer = new FileWriter(mountFile);
		// add tag
		writer.write(tag);

		// "mountcfg"
		writer.write("\"mountcfg\"\n{\n");

		// repeate for all entries
		for (MountEntry entry : entries) {
			// write the entry name
			writer.write("//" + entry.getName() + "\n");

			// for all folders of the entry
			for (String folder : entry.getFolders()) {
				// comment out inactive entries
				if (!entry.isEnabled()) {
					writer.write("//");
				}

				// write the foldername and folder
				String folderName = folder.substring(folder.lastIndexOf("\\") + 1);
				writer.write("\"" + folderName + "\"" + " \"" + folder + "\"\n");
			}
		}

		// close the file
		writer.write("}");
		writer.close();
	}
}
