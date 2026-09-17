package nl.unreadable.YPPPP;

import java.util.Set;

public class FakeShipListView implements ShipListView {

	public int updateCount = 0;
	public boolean shipDataErrorReported = false;
	public Set<String> shipList;

	public void Update() {
		updateCount++;
	}

	public void reportShipDataError() {
		shipDataErrorReported = true;
	}

	public void setShipList(Set<String> ships) {
		shipList = ships;
	}
}
