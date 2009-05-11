package nl.unreadable.YPPPP;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Set;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import nl.unreadable.YPPPP.YPPPPpiView.ExitHandler;
import nl.unreadable.YPPPP.model.YPPPPModel;


public class YPPPPdcView extends JFrame{
	public static final long serialVersionUID = 9L;
	
	private YPPPPModel model;
	private JComboBox myShipChoice, oppShipChoice;
	private JLabel myDamageLab, myMoreInfoLab, oppDamageLab, oppMoreInfoLab;
	
	private Clipboard systemClipboard;
	private DecimalFormat shipName = new DecimalFormat("##00");
	
	public YPPPPdcView()
	{
		JFrame.setDefaultLookAndFeelDecorated(true);	
	    try {
	    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	    	
	      } catch (Exception e) {
	        System.out.println("Special look failed to load! No pretties for you :(");
	      }
		this.setTitle("Damage Counter of YPPPP: Yohoho Puzzle Pirate Pillage Program");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(365,180);
		systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Container content = this.getContentPane();
		
		JPanel allBox = new JPanel();
		allBox.setLayout(new BoxLayout(allBox, BoxLayout.LINE_AXIS));
		
		JPanel myShipBox = new JPanel();
		//myShipBox.setLayout(new BoxLayout(myShipBox, BoxLayout.Y_AXIS));
		myShipBox.setLayout(new GridLayout(6,1));
			JLabel myLabel = new JLabel("Your");
		myShipBox.add(myLabel);
			myShipChoice = new JComboBox();
		myShipBox.add(myShipChoice);
			myDamageLab = new JLabel("damage");
		myShipBox.add(myDamageLab);
			myMoreInfoLab = new JLabel("0/12");
		myShipBox.add(myMoreInfoLab);
			JButton myGetShot = new JButton("Got Shot");
			myGetShot.addActionListener(new ShotHandler(true));
		myShipBox.add(myGetShot);
			JButton myHitRocks = new JButton("Hits Rocks/Edge");
			myHitRocks.addActionListener(new RockHandler(true));
		myShipBox.add(myHitRocks);
		
		JPanel oppShipBox = new JPanel();
		//oppShipBox.setLayout(new BoxLayout(oppShipBox, BoxLayout.Y_AXIS));
		oppShipBox.setLayout(new GridLayout(6,1));
			JLabel oppLabel = new JLabel("Their");
		oppShipBox.add(oppLabel);
			oppShipChoice = new JComboBox();
		oppShipBox.add(oppShipChoice);
			oppDamageLab = new JLabel("damage");
		oppShipBox.add(oppDamageLab);
			oppMoreInfoLab = new JLabel("0/12");
		oppShipBox.add(oppMoreInfoLab);
			JButton oppGetShot = new JButton("Got Shot");
			oppGetShot.setMinimumSize(new Dimension(120, 30));
			oppGetShot.addActionListener(new ShotHandler(false));
		oppShipBox.add(oppGetShot);
			JButton oppHitRocks = new JButton("Hits Rocks/Edge");
			oppHitRocks.addActionListener(new RockHandler(false));
		oppShipBox.add(oppHitRocks);
		
		JPanel buttonBox = new JPanel();
		//buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.PAGE_AXIS));
		buttonBox.setLayout(new GridLayout(7,1));
			JCheckBox sinkingCheck = new JCheckBox("Sinking");
			sinkingCheck.addActionListener(new SinkingHandler());
		buttonBox.add(sinkingCheck);
			JCheckBox linesCheck = new JCheckBox("Lines");
			linesCheck.addActionListener(new LinesHandler());
		buttonBox.add(linesCheck);
			JButton collideButton = new JButton("Collide");
			collideButton.addActionListener(new CollideHandler());
		buttonBox.add(collideButton);
			JButton resetButton = new JButton("Reset");
			resetButton.addActionListener(new ResetHandler());
		buttonBox.add(resetButton);
			JButton copyButton = new JButton("Copy");
			copyButton.addActionListener(new CopyHandler());
		buttonBox.add(copyButton);
			JButton disableButton = new JButton("Disable");
			disableButton.addActionListener(new DisableHandler());
		buttonBox.add(disableButton);
			JButton quitButton = new JButton("Quit");
			quitButton.addActionListener(new ExitHandler());
		buttonBox.add(quitButton);
		
		allBox.add(myShipBox);
		allBox.add(buttonBox);
		allBox.add(oppShipBox);
		content.add(allBox);
	}
	class SinkingHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){model.toggleSinking();}
	}
	class LinesHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){model.toggleLines();}
	}
	class CollideHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){model.collide();}
	}
	class ResetHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){model.reset();}
	}
	class CopyHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){systemClipboard.setContents(new StringSelection(model.getCopyText()), null);}
	}
	class DisableHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){Disable();}
	}
	public void Disable(){
		this.setEnabled(false);
		this.setVisible(false);
	}
	class ShipChangeHandler implements ActionListener{
		private boolean me;
		private JComboBox combobox;
		ShipChangeHandler(boolean m, JComboBox box){me = m; combobox = box; }
		public void actionPerformed(ActionEvent e){
			model.changeShipType(shipName.format(combobox.getSelectedIndex()) + (String) combobox.getSelectedItem(), me);
			}
	}
	class ShotHandler implements ActionListener{
		private boolean me;
		ShotHandler(boolean m){me = m;}
		public void actionPerformed(ActionEvent e){model.shoot(me);}
	}
	class RockHandler implements ActionListener{
		private boolean me;
		RockHandler(boolean m){me = m;}
		public void actionPerformed(ActionEvent e){model.hitRocks(me);}
	}
	public void setModel(YPPPPModel m)
	{
		model = m;
	}
	
	public void reportShipDataError()
	{
		System.out.println("Your \"ships.xml\" is missing or damaged, please replace/repair it");
		System.exit(-1);
	}
	
	public void setShipList(Set<String> s)
	{
		Vector<String> ships = new Vector<String>(s); 
		Collections.sort(ships);
		for (String ship: ships)
		{
			ship = ship.substring(2);
			myShipChoice.addItem(ship);
			oppShipChoice.addItem(ship);
		}
		myShipChoice.addActionListener(new ShipChangeHandler(true, myShipChoice));
		oppShipChoice.addActionListener(new ShipChangeHandler(false, oppShipChoice));
	}
	public void Update()
	{
		myShipChoice.setSelectedItem(model.getShipType(true));
		myDamageLab.setText(model.getDamage(true));
		myMoreInfoLab.setText(model.getMoreInfo(true));
		oppShipChoice.setSelectedItem(model.getShipType(false));
		oppDamageLab.setText(model.getDamage(false));
		oppMoreInfoLab.setText(model.getMoreInfo(false));
	}

}
