package nl.unreadable.YPPPP;

public class YPPPP {

	public static void main(String[] args) {
		YPPPPdcView dcView = new YPPPPdcView();
		new YPPPPdcController(dcView); 
		dcView.setVisible(true);
		YPPPPpiView piView = new YPPPPpiView();
		piView.setLocationRelativeTo(dcView);
		piView.setLocation(0,180);
		piView.setVisible(true);
	}

}
