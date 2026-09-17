package nl.unreadable.YPPPP.parser;

import nl.unreadable.YPPPP.model.YPPPPPirate;

public interface PirateFetcher {

	YPPPPPirate fetch(String ocean, String name);
}
