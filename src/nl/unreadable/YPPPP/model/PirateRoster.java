package nl.unreadable.YPPPP.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class PirateRoster {

	public static final int LIST_VOID = 20;
	public static final int LIST_GOLD = 10;
	public static final int LIST_BLACK = -1;

	private final Hashtable<String, Integer[]> pirateData = new Hashtable<String, Integer[]>();
	private final Vector<String> goldlist = new Vector<String>();
	private final Vector<String> blacklist = new Vector<String>();

	public void add(YPPPPPirate p) {
		int list = (goldlist.contains(p.getName()) ? LIST_GOLD : (blacklist.contains(p.getName()) ? LIST_BLACK : LIST_VOID));
		Integer[] row = { p.getGunning(), p.getBilge(), p.getSailing(), p.getRigging(), p.getCarpentry(),
				p.getPatching(), p.getSF(), p.getRumble(), p.getDNav(), p.getBNav(), p.getTH(), p.getForage(), list };
		pirateData.put(p.getName(), row);
	}

	public void remove(String name) {
		pirateData.remove(name);
	}

	public void clear() {
		pirateData.clear();
	}

	public int size() {
		return pirateData.size();
	}

	public List<String> namesSorted() {
		Vector<String> names = new Vector<String>(pirateData.keySet());
		Collections.sort(names);
		return names;
	}

	public Integer[] getStats(String name) {
		return pirateData.get(name);
	}

	public boolean isGold(String name) {
		return goldlist.contains(name);
	}

	public boolean isBlack(String name) {
		return blacklist.contains(name);
	}

	public void toggleGold(String name) {
		blacklist.remove(name);
		Integer[] row = pirateData.get(name);
		if (goldlist.contains(name)) {
			goldlist.remove(name);
			row[row.length - 1] = LIST_VOID;
		} else {
			goldlist.add(name);
			row[row.length - 1] = LIST_GOLD;
		}
	}

	public void toggleBlack(String name) {
		goldlist.remove(name);
		Integer[] row = pirateData.get(name);
		if (blacklist.contains(name)) {
			blacklist.remove(name);
			row[row.length - 1] = LIST_VOID;
		} else {
			blacklist.add(name);
			row[row.length - 1] = LIST_BLACK;
		}
	}

	public String jobCopyText(String name) {
		return "/job " + name;
	}

	public void seedGold(String name) {
		goldlist.add(name);
	}

	public void seedBlack(String name) {
		blacklist.add(name);
	}

	public List<String> goldNames() {
		return new ArrayList<String>(goldlist);
	}

	public List<String> blackNames() {
		return new ArrayList<String>(blacklist);
	}
}
