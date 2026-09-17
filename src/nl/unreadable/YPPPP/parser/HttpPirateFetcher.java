package nl.unreadable.YPPPP.parser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import nl.unreadable.YPPPP.model.YPPPPPirate;

public class HttpPirateFetcher implements PirateFetcher {

	public YPPPPPirate fetch(String ocean, String name) {
		try {
			URL url = new URL("http://" + ocean + ".puzzlepirates.com/yoweb/pirate.wm?target=" + name);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			StringBuilder htmlContent = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				htmlContent.append(line).append("\n");
			}
			in.close();

			return PiratePageParser.parseFromHtml(htmlContent.toString());
		} catch (Exception e) {
			return null;
		}
	}
}
