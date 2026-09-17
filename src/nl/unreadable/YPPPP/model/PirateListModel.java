package nl.unreadable.YPPPP.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.unreadable.YPPPP.parser.PirateFetcher;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Owns the pirate roster, the selected ocean, and their persistence to
 * preferences.xml. Has no Swing/AWT dependency.
 */
public class PirateListModel {

	private final PirateRoster roster = new PirateRoster();
	private final PirateFetcher fetcher;
	private String ocean = "emerald";
	private PirateListListener listener;

	public PirateListModel(PirateFetcher fetcher) {
		this.fetcher = fetcher;
		loadPreferences();
	}

	public void setListener(PirateListListener listener) {
		this.listener = listener;
	}

	public String getOcean() {
		return ocean;
	}

	public void setOcean(String ocean) {
		this.ocean = ocean;
	}

	public int size() {
		return roster.size();
	}

	public List<String> namesSorted() {
		return roster.namesSorted();
	}

	public Integer[] getStats(String name) {
		return roster.getStats(name);
	}

	public boolean addPirate(String name) {
		YPPPPPirate p = fetcher.fetch(ocean, name);
		if (p == null) {
			return false;
		}
		roster.add(p);
		notifyChanged();
		return true;
	}

	public void remove(String name) {
		roster.remove(name);
		notifyChanged();
	}

	public void clear() {
		roster.clear();
		notifyChanged();
	}

	public void toggleGold(String name) {
		roster.toggleGold(name);
		notifyChanged();
	}

	public void toggleBlack(String name) {
		roster.toggleBlack(name);
		notifyChanged();
	}

	public String jobCopyText(String name) {
		return roster.jobCopyText(name);
	}

	private void notifyChanged() {
		if (listener != null) {
			listener.onRosterChanged();
		}
	}

	private void loadPreferences() {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File("preferences.xml"));
			ocean = doc.getElementsByTagName("Ocean").item(0).getAttributes().item(0).getNodeValue();
			int listcnt = Integer
					.parseInt(doc.getElementsByTagName("ListCnt").item(0).getAttributes().item(0).getNodeValue());
			for (int i = 0; i < listcnt; i++) {
				Node entry = doc.getElementsByTagName("List").item(0).getAttributes().item(i);
				if (entry.getNodeValue().equals("black")) {
					roster.seedBlack(entry.getNodeName());
				}
				if (entry.getNodeValue().equals("gold")) {
					roster.seedGold(entry.getNodeName());
				}
			}
		} catch (Exception e) {
			System.out.println("Error reading preference.xml");
		}
	}

	public void savePreferences() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			Document doc = parser.newDocument();
			Element root = doc.createElement("Preferences");
			doc.appendChild(root);

			Element oceanElement = doc.createElement("Ocean");
			oceanElement.setAttribute("Ocean", ocean);
			root.appendChild(oceanElement);

			Element listCountElement = doc.createElement("ListCnt");
			listCountElement.setAttribute("Count", "" + (roster.blackNames().size() + roster.goldNames().size()));
			root.appendChild(listCountElement);

			Element listElement = doc.createElement("List");
			for (String name : roster.blackNames()) {
				listElement.setAttribute(name, "black");
			}
			for (String name : roster.goldNames()) {
				listElement.setAttribute(name, "gold");
			}
			root.appendChild(listElement);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(doc),
					new StreamResult(new BufferedWriter(new FileWriter(new File("preferences.xml")))));
		} catch (Exception e) {
			System.out.println("Error writing to preference.xml");
		}
	}
}
