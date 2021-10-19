package mountmanager;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

// https://stackoverflow.com/questions/10967451/open-a-link-in-browser-with-java-button
public class WebsiteOpener {
	public static void openWebpage(String urlString) throws IOException, URISyntaxException {
		URL url = new URL(urlString);
		openWebpage(url.toURI());
	}

	private static void openWebpage(URI uri) throws IOException {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			desktop.browse(uri);
		}
	}
}
