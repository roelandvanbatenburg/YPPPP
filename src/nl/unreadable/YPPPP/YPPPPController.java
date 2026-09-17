package nl.unreadable.YPPPP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.unreadable.YPPPP.model.PirateListListener;
import nl.unreadable.YPPPP.model.PirateListModel;
import nl.unreadable.YPPPP.model.YPPPPModel;
import nl.unreadable.YPPPP.parser.HttpPirateFetcher;

/**
 * Mediates between the view (raw UI events) and the two models (ship
 * battle state, pirate roster state). The view never talks to a model
 * directly, and a model never talks to the view directly.
 */
public class YPPPPController implements ShipListView, PirateListListener, YPPPPViewListener {

	private final YPPPPView view;
	private final YPPPPModel shipModel;
	private final PirateListModel pirateModel;
	private final Map<String, String> shipKeysByName = new HashMap<String, String>();

	public YPPPPController(YPPPPView view) {
		this.view = view;
		view.setListener(this);

		this.shipModel = new YPPPPModel(this);
		this.pirateModel = new PirateListModel(new HttpPirateFetcher());
		pirateModel.setListener(this);

		view.setOceanSelection(pirateModel.getOcean());
		refreshPirateTable();
		Update();
	}

	/*
	 * ShipListView - callbacks from the ship battle model
	 */
	public void Update() {
		view.setMyShipSelection(displayName(shipModel.getShipType(true)));
		view.setMyDamage(shipModel.getDamage(true));
		view.setMyMoreInfo(shipModel.getMoreInfo(true));
		view.setOppShipSelection(displayName(shipModel.getShipType(false)));
		view.setOppDamage(shipModel.getDamage(false));
		view.setOppMoreInfo(shipModel.getMoreInfo(false));
		view.setUndoEnabled(shipModel.hasUndo());
	}

	public void reportShipDataError() {
		System.out.println("Your \"ships.xml\" is missing or damaged, please replace/repair it");
		System.exit(-1);
	}

	public void setShipList(Set<String> ships) {
		List<String> sortedKeys = new ArrayList<String>(ships);
		Collections.sort(sortedKeys);
		List<String> displayNames = new ArrayList<String>();
		shipKeysByName.clear();
		for (String key : sortedKeys) {
			String name = displayName(key);
			shipKeysByName.put(name, key);
			displayNames.add(name);
		}
		view.setShipChoices(displayNames);
	}

	private String displayName(String key) {
		return key.substring(2);
	}

	/*
	 * PirateListListener - callback from the pirate roster model
	 */
	public void onRosterChanged() {
		refreshPirateTable();
	}

	private void refreshPirateTable() {
		List<PirateRow> rows = new ArrayList<PirateRow>();
		for (String name : pirateModel.namesSorted()) {
			rows.add(new PirateRow(name, pirateModel.getStats(name)));
		}
		view.setPirateRows(rows);
	}

	/*
	 * YPPPPViewListener - user actions raised by the view
	 */
	public void onExit() {
		pirateModel.savePreferences();
		System.exit(0);
	}

	public void onShipSelected(boolean me, String shipName) {
		String key = shipKeysByName.get(shipName);
		if (key != null) {
			shipModel.changeShipType(key, me);
		}
	}

	public void onShot(boolean me) {
		shipModel.shoot(me);
	}

	public void onHitRocks(boolean me) {
		shipModel.hitRocks(me);
	}

	public void onSinkingToggled() {
		shipModel.toggleSinking();
	}

	public void onLinesToggled() {
		shipModel.toggleLines();
	}

	public void onCollide() {
		shipModel.collide();
	}

	public void onUndo() {
		shipModel.undo();
	}

	public void onReset() {
		shipModel.reset();
	}

	public void onDcCopyRequested() {
		view.copyToClipboard(shipModel.getCopyText());
	}

	public void onOceanChanged(String ocean) {
		pirateModel.setOcean(ocean);
	}

	public void onAddPirate(String name) {
		if (!pirateModel.addPirate(name)) {
			System.out.println("Pirate not found (are you on the right ocean?)");
		}
	}

	public void onDeletePirate(String name) {
		pirateModel.remove(name);
	}

	public void onClearAllPirates() {
		pirateModel.clear();
	}

	public void onToggleGold(String name) {
		pirateModel.toggleGold(name);
	}

	public void onToggleBlack(String name) {
		pirateModel.toggleBlack(name);
	}

	public void onJobCopyRequested(String name) {
		view.copyToClipboard(pirateModel.jobCopyText(name));
	}
}
