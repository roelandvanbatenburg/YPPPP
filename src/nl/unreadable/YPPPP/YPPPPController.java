package nl.unreadable.YPPPP;

import nl.unreadable.YPPPP.model.YPPPPModel;

public class YPPPPController {
	
	public YPPPPController(YPPPPView view)
	{
		YPPPPModel model = new YPPPPModel(view);
		view.setModel(model);
		view.Update();
	}
}
