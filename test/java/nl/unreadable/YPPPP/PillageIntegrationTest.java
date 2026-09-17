package nl.unreadable.YPPPP;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import nl.unreadable.YPPPP.model.PirateRoster;
import nl.unreadable.YPPPP.model.YPPPPPirate;
import nl.unreadable.YPPPP.parser.FakePirateFetcher;

public class PillageIntegrationTest {

	@Test
	public void evaluateGoldBlackListAndJobCopyAFullCrew() throws IOException {
		FakePirateFetcher fetcher = new FakePirateFetcher();
		fetcher.seed("Btza", loadTestHtmlFile());

		// 1. Evaluate the first jobber via the real fetch -> parse pipeline.
		YPPPPPirate btza = fetcher.fetch("emerald", "Btza");
		assertNotNull("Btza should be found", btza);
		assertEquals("Btza", btza.getName());

		// A pirate who isn't there at all comes back null, same as a real 404.
		assertNull(fetcher.fetch("emerald", "NobodyHome"));

		PirateRoster roster = new PirateRoster();
		roster.add(btza);

		// 2. The rest of the crew joins the roster directly.
		roster.add(new YPPPPPirate("Anne", 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0));
		roster.add(new YPPPPPirate("Jack", 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0));

		assertEquals(3, roster.size());
		assertEquals(java.util.Arrays.asList("Anne", "Btza", "Jack"), roster.namesSorted());

		// Btza's stats made it through the whole pipeline correctly, in
		// PirateRoster's column order: Gunning, Bilge, Sailing, Rigging,
		// Carpentry, Patching, SF, Rumble, DNav, BNav, TH, Forage, listStatus.
		assertArrayEquals(new Integer[] { 5, 7, 8, 7, 8, 2, 6, 4, 3, 8, 4, 7, PirateRoster.LIST_VOID },
				roster.getStats("Btza"));

		// 3. Gold-list one jobber, black-list another.
		roster.toggleGold("Anne");
		roster.toggleBlack("Jack");

		assertTrue(roster.isGold("Anne"));
		assertEquals(PirateRoster.LIST_GOLD, (int) roster.getStats("Anne")[12]);
		assertTrue(roster.isBlack("Jack"));
		assertEquals(PirateRoster.LIST_BLACK, (int) roster.getStats("Jack")[12]);

		// 4. Black-listing overrides an earlier gold-listing for the same jobber.
		roster.toggleGold("Jack");
		assertFalse(roster.isBlack("Jack"));
		assertTrue(roster.isGold("Jack"));

		// 5. Job-copy text for the crew.
		assertEquals("/job Btza", roster.jobCopyText("Btza"));
		assertEquals("/job Anne", roster.jobCopyText("Anne"));

		// 6. One jobber leaves the crew.
		roster.remove("Jack");
		assertEquals(2, roster.size());
		assertNull(roster.getStats("Jack"));
	}

	private String loadTestHtmlFile() throws IOException {
		File testFile = new File("test/resources/test_pirate_page.html");
		assertTrue("Test HTML file should exist", testFile.exists());

		StringBuilder content = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(testFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
		}
		return content.toString();
	}
}
