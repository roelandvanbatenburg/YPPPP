package nl.unreadable.YPPPP;

/**
 * User intents raised by the view. Implemented by the controller, which
 * translates each one into calls on the models.
 */
public interface YPPPPViewListener {

	void onExit();

	void onShipSelected(boolean me, String shipName);

	void onShot(boolean me);

	void onHitRocks(boolean me);

	void onSinkingToggled();

	void onLinesToggled();

	void onCollide();

	void onUndo();

	void onReset();

	void onDcCopyRequested();

	void onOceanChanged(String ocean);

	void onAddPirate(String name);

	void onDeletePirate(String name);

	void onClearAllPirates();

	void onToggleGold(String name);

	void onToggleBlack(String name);

	void onJobCopyRequested(String name);
}
