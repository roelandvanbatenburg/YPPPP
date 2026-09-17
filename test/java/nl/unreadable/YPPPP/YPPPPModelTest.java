package nl.unreadable.YPPPP;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import nl.unreadable.YPPPP.model.YPPPPModel;

public class YPPPPModelTest {

	private FakeShipListView view;
	private YPPPPModel model;

	@Before
	public void setUp() {
		view = new FakeShipListView();
		model = new YPPPPModel(view);
	}

	@Test
	public void loadsShipDataWithoutErrorAndDefaultsBothShipsToSloop() {
		assertFalse(view.shipDataErrorReported);
		assertTrue(model.getShipType(true).endsWith("sloop"));
		assertTrue(model.getShipType(false).endsWith("sloop"));
		assertFalse(model.hasUndo());
	}

	@Test
	public void shootIncreasesDamageByTheOtherShipsCannonballSize() {
		model.shoot(true);
		assertEquals("2.0 / 12.0", model.getDamage(true));
		assertTrue(model.hasUndo());
	}

	@Test
	public void hitRocksIncreasesDamageByRockDamage() {
		model.hitRocks(false);
		assertEquals("1.0 / 12.0", model.getDamage(false));
	}

	@Test
	public void collideAppliesEachShipsRamDamageToTheOther() {
		model.collide();
		assertEquals("1.0 / 12.0", model.getDamage(true));
		assertEquals("1.0 / 12.0", model.getDamage(false));
	}

	@Test
	public void toggleSinkingSwitchesTheFullHealthDenominatorToSinkPoints() {
		model.toggleSinking();
		assertEquals("0.0 / 20.0", model.getDamage(true));
	}

	@Test
	public void getMoreInfoReportsMaxOnceDamageReachesFullHealth() {
		for (int i = 0; i < 6; i++) {
			model.shoot(true); // 6 * 2.0 == sf_hp (12.0)
		}
		assertEquals("Max", model.getMoreInfo(true));
	}

	@Test
	public void toggleLinesSwitchesGetMoreInfoToSixthsFormat() {
		model.shoot(true); // damage = 2.0, sf_hp = 12.0
		model.toggleLines();
		assertEquals("1.0/6.0", model.getMoreInfo(true)); // 6 * 2 / 12 = 1.0
	}

	@Test
	public void changeShipTypeToTheCurrentTypeIsANoop() {
		model.changeShipType(model.getShipType(true), true);
		assertFalse(model.hasUndo());
	}

	@Test
	public void undoRevertsTheLastAction() {
		model.shoot(true);
		assertEquals("2.0 / 12.0", model.getDamage(true));

		model.undo();

		assertEquals("0.0 / 12.0", model.getDamage(true));
		assertFalse(model.hasUndo());
	}

	@Test
	public void resetClearsDamageOnBothShips() {
		model.shoot(true);
		model.shoot(false);

		model.reset();

		assertEquals("0.0 / 12.0", model.getDamage(true));
		assertEquals("0.0 / 12.0", model.getDamage(false));
	}

	@Test
	public void getCopyTextFormatsBothSidesAsPercentages() {
		model.shoot(true); // damage = 2.0, sf_hp = 12.0 -> 16.67%; opponent untouched -> 0.00%
		assertEquals("Damage -> We: 16.67% ~ They: 0.00%", model.getCopyText());
	}
}
