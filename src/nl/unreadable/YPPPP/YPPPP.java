package nl.unreadable.YPPPP;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class YPPPP {

	public static void main(String[] args) {
		// Copy template files if they don't exist
		copyTemplateIfNotExists("preferences.xml");
		copyTemplateIfNotExists("ships.xml");

		YPPPPView view = new YPPPPView();
		new YPPPPController(view);
		view.setVisible(true);
	}

	private static void copyTemplateIfNotExists(String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			try (InputStream in = YPPPP.class.getResourceAsStream("/templates/" + filename);
			     FileOutputStream out = new FileOutputStream(file)) {
				if (in == null) {
					System.err.println("Warning: Template file not found in JAR: " + filename);
					return;
				}
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
				System.out.println("Created " + filename + " from template");
			} catch (IOException e) {
				System.err.println("Error creating " + filename + ": " + e.getMessage());
			}
		}
	}

}
