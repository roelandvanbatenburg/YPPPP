package nl.unreadable.YPPPP;

public class YPPPP {

	public static void main(String[] args) {
		YPPPPView window = new YPPPPView();
		new YPPPPController(window); 
		window.setVisible(true);
	}

}
