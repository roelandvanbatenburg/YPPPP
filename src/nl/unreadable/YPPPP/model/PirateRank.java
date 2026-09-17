package nl.unreadable.YPPPP.model;

public enum PirateRank {
	ABLE("Able"),
	PROFICIENT("Proficient"),
	DISTINGUISHED("Distinguished"),
	RESPECTED("Respected"),
	MASTER("Master"),
	RENOWNED("Renowned"),
	GRAND_MASTER("Grand-Master"),
	LEGENDARY("Legendary"),
	ULTIMATE("Ultimate");

	private final String label;

	PirateRank(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public static int toInt(String label) {
		for (PirateRank rank : values()) {
			if (rank.label.equals(label)) {
				return rank.ordinal();
			}
		}
		return 0;
	}

	public static String toLabel(int value) {
		PirateRank[] ranks = values();
		if (value < 0 || value >= ranks.length) {
			return null;
		}
		return ranks[value].label;
	}
}
