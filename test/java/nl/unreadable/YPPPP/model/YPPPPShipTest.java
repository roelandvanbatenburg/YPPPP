package nl.unreadable.YPPPP.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class YPPPPShipTest {

	private YPPPPShip ship(String type, double cb, double ram, double sf, double sink, double rock) {
		return new YPPPPShip(type, cb, ram, sf, sink, rock, YPPPPSize.small);
	}

	@Test
	public void newShipStartsWithZeroDamage() {
		YPPPPShip s = ship("sloop", 2.0, 1.0, 12.0, 20.0, 1.0);
		assertEquals(0.0, s.damage, 0.0001);
	}

	@Test
	public void getShotAccumulatesDamage() {
		YPPPPShip s = ship("sloop", 2.0, 1.0, 12.0, 20.0, 1.0);
		s.getShot(2.0);
		s.getShot(3.0);
		assertEquals(5.0, s.damage, 0.0001);
	}

	@Test
	public void hitRocksAddsRockDamage() {
		YPPPPShip s = ship("sloop", 2.0, 1.0, 12.0, 20.0, 1.5);
		s.hitRocks();
		s.hitRocks();
		assertEquals(3.0, s.damage, 0.0001);
	}

	@Test
	public void ramAddsTheOtherShipsRamDamage() {
		YPPPPShip a = ship("sloop", 2.0, 1.0, 12.0, 20.0, 1.0);
		YPPPPShip b = ship("war galleon", 4.0, 5.0, 42.0, 70.0, 3.5);

		a.ram(b);
		b.ram(a);

		assertEquals(5.0, a.damage, 0.0001);
		assertEquals(1.0, b.damage, 0.0001);
	}

	@Test
	public void resetClearsAccumulatedDamage() {
		YPPPPShip s = ship("sloop", 2.0, 1.0, 12.0, 20.0, 1.0);
		s.getShot(10.0);
		s.reset();
		assertEquals(0.0, s.damage, 0.0001);
	}

	@Test
	public void copyConstructorDuplicatesAllFieldsIncludingDamage() {
		YPPPPShip original = ship("sloop", 2.0, 1.0, 12.0, 20.0, 1.0);
		original.getShot(4.0);

		YPPPPShip copy = new YPPPPShip(original);

		assertEquals(original.type, copy.type);
		assertEquals(original.cb_damage, copy.cb_damage, 0.0001);
		assertEquals(original.ram_damage, copy.ram_damage, 0.0001);
		assertEquals(original.sf_hp, copy.sf_hp, 0.0001);
		assertEquals(original.sink_hp, copy.sink_hp, 0.0001);
		assertEquals(original.rock_damage, copy.rock_damage, 0.0001);
		assertEquals(original.damage, copy.damage, 0.0001);
	}

	@Test
	public void changeTypeReplacesStatsButKeepsCurrentDamage() {
		YPPPPShip s = ship("sloop", 2.0, 1.0, 12.0, 20.0, 1.0);
		s.getShot(4.0);

		YPPPPShip warGalleon = ship("war galleon", 4.0, 5.0, 42.0, 70.0, 3.5);
		s.changeType(warGalleon);

		assertEquals("war galleon", s.type);
		assertEquals(4.0, s.cb_damage, 0.0001);
		assertEquals(4.0, s.damage, 0.0001); // damage is NOT reset by a type change
	}
}
