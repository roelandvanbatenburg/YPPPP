package nl.unreadable.YPPPP;

/**
 * A single pirate row as displayed in the table: a name plus its stat
 * values (ending with the gold/black/void list status).
 */
public class PirateRow {

	private final String name;
	private final Integer[] stats;

	public PirateRow(String name, Integer[] stats) {
		this.name = name;
		this.stats = stats;
	}

	public String getName() {
		return name;
	}

	public Integer[] getStats() {
		return stats;
	}
}
