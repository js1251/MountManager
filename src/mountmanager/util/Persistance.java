package mountmanager.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import mountmanager.Ui;

public class Persistance {
	private Ui ui;
	
	public Persistance(Ui ui) {
		this.ui = ui;
	}

	public String getMountPath() {
		try {
			String saveFilePath = "./mountmanager.cfg";
			File saveFile = new File(saveFilePath);

			if (saveFile.exists()) {
				return new String(Files.readAllBytes(Paths.get(saveFilePath)));
			}

		} catch (Exception e) {
			// TODO show error message
			e.printStackTrace();
			return null;
		}
		
		return null;
	}

	public void createSaveFile(String saveFilePath) {
		try {
			FileWriter writer = new FileWriter("./mountmanager.cfg");
			writer.write(saveFilePath);
			writer.close();

			ui.load(); // what ??
		} catch (IOException e) {
			// TODO: show error message
		}
	}
}
