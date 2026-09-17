package nl.unreadable.YPPPP.parser;

import java.util.HashMap;
import java.util.Map;

import nl.unreadable.YPPPP.model.YPPPPPirate;

public class FakePirateFetcher implements PirateFetcher {

	private final Map<String, String> htmlByName = new HashMap<String, String>();

	public void seed(String name, String html) {
		htmlByName.put(name, html);
	}

	public YPPPPPirate fetch(String ocean, String name) {
		String html = htmlByName.get(name);
		if (html == null) {
			return null;
		}
		try {
			return PiratePageParser.parseFromHtml(html);
		} catch (Exception e) {
			return null;
		}
	}
}
