package nl.unreadable.YPPPP;

import nl.unreadable.YPPPP.model.YPPPPModel;

public class YPPPPdcController {
	
	public YPPPPdcController(YPPPPdcView view)
	{
		YPPPPModel model = new YPPPPModel(view);
		view.setModel(model);
		view.Update();
	}
}
