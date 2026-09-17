package nl.unreadable.YPPPP;

import static org.junit.Assert.*;

import org.junit.Test;

import nl.unreadable.YPPPP.model.YPPPPModel;

public class BattleIntegrationTest {

	@Test
	public void changeEnemyShipThenFightAFullBattle() {
		FakeShipListView view = new FakeShipListView();
		YPPPPModel model = new YPPPPModel(view);

		assertFalse(view.shipDataErrorReported);
		assertTrue(model.getShipType(true).endsWith("sloop"));
		assertTrue(model.getShipType(false).endsWith("sloop"));

		// 1. Change the enemy ship to war galleon (cb 4.0, ram 5.0, sf 42.0, sink 70.0, rock 3.5).
		String warGalleonKey = findShipKey(view, "war galleon");
		model.changeShipType(warGalleonKey, false);
		assertEquals(warGalleonKey, model.getShipType(false));
		assertTrue(model.hasUndo());

		// 2. We get shot by their (war galleon, cb 4.0) cannon.
		model.shoot(true);
		assertEquals("4.0 / 12.0", model.getDamage(true));

		// 3. They get shot by our (sloop, cb 2.0) cannon.
		model.shoot(false);
		assertEquals("2.0 / 42.0", model.getDamage(false));

		// 4. We hit rocks (sloop rock damage 1.0).
		model.hitRocks(true);
		assertEquals("5.0 / 12.0", model.getDamage(true));

		// 5. The ships collide: each takes the other's ram damage.
		model.collide();
		assertEquals("10.0 / 12.0", model.getDamage(true)); // 5.0 + their ram (5.0)
		assertEquals("3.0 / 42.0", model.getDamage(false)); // 2.0 + our ram (1.0)

		// 6. Switch to sinking mode: the denominator becomes sink points, not sail force.
		model.toggleSinking();
		assertEquals("10.0 / 20.0", model.getDamage(true));
		assertEquals("3.0 / 70.0", model.getDamage(false));

		// 7. Switch to lines mode and check the copy text for both sides.
		model.toggleLines();
		assertEquals("3.0/6.0", model.getMoreInfo(true)); // 6 * 10 / 20
		assertEquals("0.3/6.0", model.getMoreInfo(false)); // 6 * 3 / 70, rounded
		assertEquals("Damage -> We: 3.0/6.0 ~ They: 0.3/6.0", model.getCopyText());

		// 8. Undo the collide: back to post-hitRocks/post-shoot damage.
		model.undo();
		assertEquals("1.5/6.0", model.getMoreInfo(true)); // 6 * 5 / 20
		assertEquals("0.2/6.0", model.getMoreInfo(false)); // 6 * 2 / 70, rounded

		// 9. Reset clears both ships' damage, regardless of sinking/lines mode.
		model.reset();
		assertEquals("0.0 / 20.0", model.getDamage(true));
		assertEquals("0.0 / 70.0", model.getDamage(false));
		assertTrue(model.hasUndo());
	}

	private String findShipKey(FakeShipListView view, String shipName) {
		for (String key : view.shipList) {
			if (key.endsWith(shipName)) {
				return key;
			}
		}
		fail("Expected to find \"" + shipName + "\" in the loaded ship list");
		return null;
	}
}
