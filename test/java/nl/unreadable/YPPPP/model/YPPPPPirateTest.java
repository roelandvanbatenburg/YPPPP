package nl.unreadable.YPPPP.model;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class YPPPPPirateTest {

	@Test
	public void fullConstructorSetsEveryField() {
		// name, SF, Bilge, Sailing, Rigging, DNav, BNav, Gunning, Carpentry, Patching, Rumble, TH, Forage, List
		YPPPPPirate p = new YPPPPPirate("Btza", 1, 2, 3, 4, 5, 6, 7, 8, 2, 9, 10, 11, 20);

		assertEquals("Btza", p.getName());
		assertEquals(1, p.getSF());
		assertEquals(2, p.getBilge());
		assertEquals(3, p.getSailing());
		assertEquals(4, p.getRigging());
		assertEquals(5, p.getDNav());
		assertEquals(6, p.getBNav());
		assertEquals(7, p.getGunning());
		assertEquals(8, p.getCarpentry());
		assertEquals(2, p.getPatching());
		assertEquals(9, p.getRumble());
		assertEquals(10, p.getTH());
		assertEquals(11, p.getForage());
		assertEquals(20, p.getList());
	}

	@Test
	public void copyConstructorDuplicatesEveryField() {
		YPPPPPirate original = new YPPPPPirate("Btza", 1, 2, 3, 4, 5, 6, 7, 8, 2, 9, 10, 11, 20);
		YPPPPPirate copy = new YPPPPPirate(original);

		assertEquals(original.getName(), copy.getName());
		assertEquals(original.getSF(), copy.getSF());
		assertEquals(original.getBilge(), copy.getBilge());
		assertEquals(original.getSailing(), copy.getSailing());
		assertEquals(original.getRigging(), copy.getRigging());
		assertEquals(original.getDNav(), copy.getDNav());
		assertEquals(original.getBNav(), copy.getBNav());
		assertEquals(original.getGunning(), copy.getGunning());
		assertEquals(original.getCarpentry(), copy.getCarpentry());
		assertEquals(original.getPatching(), copy.getPatching());
		assertEquals(original.getRumble(), copy.getRumble());
		assertEquals(original.getTH(), copy.getTH());
		assertEquals(original.getForage(), copy.getForage());
		assertEquals(original.getList(), copy.getList());
	}

	@Test
	public void defaultConstructorThenSettersRoundtrip() {
		YPPPPPirate p = new YPPPPPirate();
		p.setName("Btza");
		p.setSF(1);
		p.setBilge(2);
		p.setSailing(3);
		p.setRigging(4);
		p.setDNav(5);
		p.setBNav(6);
		p.setGunning(7);
		p.setCarpentry(8);
		p.setPatching(2);
		p.setRumble(9);
		p.setTH(10);
		p.setForage(11);
		p.setList(20);

		assertEquals("Btza", p.getName());
		assertEquals(1, p.getSF());
		assertEquals(2, p.getBilge());
		assertEquals(3, p.getSailing());
		assertEquals(4, p.getRigging());
		assertEquals(5, p.getDNav());
		assertEquals(6, p.getBNav());
		assertEquals(7, p.getGunning());
		assertEquals(8, p.getCarpentry());
		assertEquals(2, p.getPatching());
		assertEquals(9, p.getRumble());
		assertEquals(10, p.getTH());
		assertEquals(11, p.getForage());
		assertEquals(20, p.getList());
	}

	@Test
	public void nameOnlyConstructorLeavesStatsAtZero() {
		YPPPPPirate p = new YPPPPPirate("Btza");
		assertEquals("Btza", p.getName());
		assertEquals(0, p.getSF());
		assertEquals(0, p.getPatching());
	}

	@Test
	public void getAsDataReturnsFieldsInItsOwnDocumentedOrder() {
		// Characterization test: getAsData() has no live callers (verified via a
		// repo-wide search), and its field order does not match the order used by
		// columnNames/addPirate() elsewhere. This test documents current behavior
		// as-is; it does not assert the order is "correct".
		YPPPPPirate p = new YPPPPPirate("Btza", 1, 2, 3, 4, 5, 6, 7, 8, 2, 9, 10, 11, 20);

		ArrayList<Object> expected = new ArrayList<Object>();
		expected.add("Btza");
		expected.add(7); // Gunning
		expected.add(2); // Bilge
		expected.add(3); // Sailing
		expected.add(4); // Rigging
		expected.add(5); // DNav
		expected.add(6); // BNav
		expected.add(8); // Carpentry
		expected.add(2); // Patching
		expected.add(1); // SF
		expected.add(9); // Rumble
		expected.add(10); // TH
		expected.add(11); // Forage
		expected.add(20); // List

		assertEquals(expected, p.getAsData());
	}
}
