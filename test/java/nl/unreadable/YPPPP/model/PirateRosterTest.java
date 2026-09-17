package nl.unreadable.YPPPP.model;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PirateRosterTest {

	private PirateRoster roster;

	@Before
	public void setUp() {
		roster = new PirateRoster();
	}

	private YPPPPPirate pirate(String name) {
		// name, SF, Bilge, Sailing, Rigging, DNav, BNav, Gunning, Carpentry, Patching, Rumble, TH, Forage, List
		return new YPPPPPirate(name, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 0);
	}

	@Test
	public void addStoresStatsInColumnOrderWithVoidListStatusByDefault() {
		roster.add(pirate("Anne"));

		assertEquals(1, roster.size());
		assertArrayEquals(new Integer[] { 7, 2, 3, 4, 8, 1, 1, 2, 5, 6, 3, 4, PirateRoster.LIST_VOID },
				roster.getStats("Anne"));
	}

	@Test
	public void addPicksUpPreExistingGoldMembership() {
		roster.seedGold("Anne");
		roster.add(pirate("Anne"));

		assertEquals(PirateRoster.LIST_GOLD, (int) roster.getStats("Anne")[12]);
		assertTrue(roster.isGold("Anne"));
	}

	@Test
	public void addPicksUpPreExistingBlackMembership() {
		roster.seedBlack("Anne");
		roster.add(pirate("Anne"));

		assertEquals(PirateRoster.LIST_BLACK, (int) roster.getStats("Anne")[12]);
		assertTrue(roster.isBlack("Anne"));
	}

	@Test
	public void toggleGoldTwiceReturnsToVoid() {
		roster.add(pirate("Anne"));

		roster.toggleGold("Anne");
		assertTrue(roster.isGold("Anne"));
		assertEquals(PirateRoster.LIST_GOLD, (int) roster.getStats("Anne")[12]);

		roster.toggleGold("Anne");
		assertFalse(roster.isGold("Anne"));
		assertEquals(PirateRoster.LIST_VOID, (int) roster.getStats("Anne")[12]);
	}

	@Test
	public void toggleBlackTwiceReturnsToVoid() {
		roster.add(pirate("Anne"));

		roster.toggleBlack("Anne");
		assertTrue(roster.isBlack("Anne"));
		assertEquals(PirateRoster.LIST_BLACK, (int) roster.getStats("Anne")[12]);

		roster.toggleBlack("Anne");
		assertFalse(roster.isBlack("Anne"));
		assertEquals(PirateRoster.LIST_VOID, (int) roster.getStats("Anne")[12]);
	}

	@Test
	public void toggleGoldClearsExistingBlacklistMembership() {
		roster.add(pirate("Anne"));
		roster.toggleBlack("Anne");

		roster.toggleGold("Anne");

		assertFalse(roster.isBlack("Anne"));
		assertTrue(roster.isGold("Anne"));
		assertEquals(PirateRoster.LIST_GOLD, (int) roster.getStats("Anne")[12]);
	}

	@Test
	public void toggleBlackClearsExistingGoldMembership() {
		roster.add(pirate("Anne"));
		roster.toggleGold("Anne");

		roster.toggleBlack("Anne");

		assertFalse(roster.isGold("Anne"));
		assertTrue(roster.isBlack("Anne"));
		assertEquals(PirateRoster.LIST_BLACK, (int) roster.getStats("Anne")[12]);
	}

	@Test
	public void removeDropsThePirate() {
		roster.add(pirate("Anne"));
		roster.remove("Anne");

		assertEquals(0, roster.size());
		assertNull(roster.getStats("Anne"));
	}

	@Test
	public void clearDropsEveryone() {
		roster.add(pirate("Anne"));
		roster.add(pirate("Jack"));
		roster.clear();

		assertEquals(0, roster.size());
	}

	@Test
	public void namesSortedIsAlphabeticalRegardlessOfInsertionOrder() {
		roster.add(pirate("Zed"));
		roster.add(pirate("Anne"));

		assertEquals(java.util.Arrays.asList("Anne", "Zed"), roster.namesSorted());
	}

	@Test
	public void jobCopyTextIsSlashJobPlusName() {
		assertEquals("/job Btza", roster.jobCopyText("Btza"));
	}

	@Test
	public void goldAndBlackNamesReflectCurrentMembership() {
		roster.add(pirate("Anne"));
		roster.add(pirate("Jack"));
		roster.toggleGold("Anne");
		roster.toggleBlack("Jack");

		List<String> gold = roster.goldNames();
		List<String> black = roster.blackNames();

		assertEquals(1, gold.size());
		assertTrue(gold.contains("Anne"));
		assertEquals(1, black.size());
		assertTrue(black.contains("Jack"));
	}
}
