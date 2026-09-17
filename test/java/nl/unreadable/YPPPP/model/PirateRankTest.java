package nl.unreadable.YPPPP.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class PirateRankTest {

	@Test
	public void toIntMapsEveryLabelToItsOrdinal() {
		assertEquals(0, PirateRank.toInt("Able"));
		assertEquals(1, PirateRank.toInt("Proficient"));
		assertEquals(2, PirateRank.toInt("Distinguished"));
		assertEquals(3, PirateRank.toInt("Respected"));
		assertEquals(4, PirateRank.toInt("Master"));
		assertEquals(5, PirateRank.toInt("Renowned"));
		assertEquals(6, PirateRank.toInt("Grand-Master"));
		assertEquals(7, PirateRank.toInt("Legendary"));
		assertEquals(8, PirateRank.toInt("Ultimate"));
	}

	@Test
	public void toIntReturnsZeroForAnUnknownLabel() {
		assertEquals(0, PirateRank.toInt("NotARealRank"));
	}

	@Test
	public void toLabelMapsEveryOrdinalToItsLabel() {
		assertEquals("Able", PirateRank.toLabel(0));
		assertEquals("Proficient", PirateRank.toLabel(1));
		assertEquals("Distinguished", PirateRank.toLabel(2));
		assertEquals("Respected", PirateRank.toLabel(3));
		assertEquals("Master", PirateRank.toLabel(4));
		assertEquals("Renowned", PirateRank.toLabel(5));
		assertEquals("Grand-Master", PirateRank.toLabel(6));
		assertEquals("Legendary", PirateRank.toLabel(7));
		assertEquals("Ultimate", PirateRank.toLabel(8));
	}

	@Test
	public void toLabelReturnsNullForOutOfRangeValues() {
		assertNull(PirateRank.toLabel(-1));
		assertNull(PirateRank.toLabel(9));
	}
}
