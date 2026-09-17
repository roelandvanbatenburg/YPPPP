package nl.unreadable.YPPPP;

import java.util.Set;

public interface ShipListView {

	void Update();

	void reportShipDataError();

	void setShipList(Set<String> ships);
}
