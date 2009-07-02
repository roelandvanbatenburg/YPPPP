package nl.unreadable.YPPPP.model;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilderFactory;

import nl.unreadable.YPPPP.YPPPPView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class YPPPPModel {
	
	private boolean sinking; // true if sinking
	private boolean lines; // true if we want lines as more info
	private YPPPPShip myShip, oppShip;
	private LinkedList<YPPPPShip[]> history;
	boolean hasUndo ;
	
	private Hashtable<String, YPPPPShip> shipList;
	private boolean shipDataError;
	private YPPPPView view;
	
	private DecimalFormat twoDigit = new DecimalFormat("#,##0.00");
	private DecimalFormat oneDigit = new DecimalFormat("#,##0.0");
	private DecimalFormat shipName = new DecimalFormat("##00");
	
	public YPPPPModel(YPPPPView v)
	{
		view = v;
		sinking = false;
		lines = false;
		shipList = new Hashtable<String, YPPPPShip>();
		shipDataError = false;
		getShipData();
		if (shipDataError) v.reportShipDataError();
		view.setShipList(shipList.keySet());
		YPPPPShip firstship = (shipList.elements()).nextElement(); 
		myShip = new YPPPPShip(firstship);
		oppShip = new YPPPPShip(firstship);
		history = new LinkedList<YPPPPShip[]>();
	}
	
	/**
	 * @param me true when the damage from the player, false when from the opponent is requested
	 * @return string containing the damage, (max) when applicable and the max health
	 */
	public String getDamage(boolean me){return oneDigit.format(getSimpleDamage(me)) + (getSimpleDamage(me) >= getFullHealth(me) ? "(max)" : "") + " / " + oneDigit.format((me ? getFullHealth(myShip) : getFullHealth(oppShip)));}
	private double getFullHealth(boolean me){return (me ? getFullHealth(myShip) : getFullHealth(oppShip));}
	private double getFullHealth(YPPPPShip ship){return (sinking ? ship.sink_hp : ship.sf_hp);}
	private double getSimpleDamage(boolean me){return (me ? getSimpleDamage(myShip) : getSimpleDamage(oppShip));}
	private double getSimpleDamage(YPPPPShip ship){return ship.damage;}
	
	/**
	 * @param me true when the damage from the player, false when from the opponent is requested
	 * @return string containing the damage and health in a special format
	 */
	public String getMoreInfo(boolean me){return (me ? getMoreInfo(myShip) : getMoreInfo(oppShip));}
	private String getMoreInfo(YPPPPShip ship){return ((ship.damage >= (sinking ? ship.sink_hp : ship.sf_hp)) ? "Max" : (lines ? oneDigit : twoDigit).format((lines ? 6 : 100) * ship.damage / (sinking ? ship.sink_hp : ship.sf_hp)) + (lines ? "/6.0" : "%"));}
	/**
	 * function to get the text for the clipboard
	 * @return string with both damage counts
	 */
	public String getCopyText() {return "Damage -> We: " + getMoreInfo(true) + " ~ They: " + getMoreInfo(false);}
	/**
	 * method to let one ship hit the other
	 * @param me true when player gets shot, false when opponent
	 */
	public void shoot(boolean me){
		storeState();
		(me ? myShip : oppShip).getShot((me ? oppShip : myShip).cb_damage);
		Update();
	}
	/**
	 * ships collide 
	 */
	public void collide(){
		storeState();
		myShip.ram(oppShip);
		oppShip.ram(myShip);
		Update();
	}
	/**
	 * method to let a ship hit the rocks
	 * @param me true when player, false when opponent
	 */
	public void hitRocks(boolean me){
		storeState();
		(me ? myShip : oppShip).hitRocks();
		Update();
	}
	/**
	 * clear the damage of the ships
	 */
	public void reset(){
		storeState();
		myShip.reset();
		oppShip.reset();
		Update();
	}
	/**
	 * method to change whether we are sinking or not
	 */
	public void toggleSinking(){
		sinking = (sinking ? false : true);
		Update();
	}
	/**
	 * method to change whether we use lines or percentages
	 */
	public void toggleLines(){
		lines = (lines ? false : true);
		Update();
	}
	/**
	 * method to change the ship type
	 * @param type new type
	 * @param me true when player, false when opponent
	 */
	public void changeShipType(String type, boolean me){
		storeState();
		(me ? myShip : oppShip).changeType(shipList.get(type));
		Update();
	}
	/**
	 * function to raise when something changed
	 */
	void Update(){
		view.Update();		
	}
	/**
	 * Read ship data from xml file
	 */
	void getShipData(){
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File("ships.xml"));
			NodeList nodeLst = doc.getElementsByTagName("Ship");
			for(int i = 0; i < nodeLst.getLength(); ++i )
			{
				YPPPPShip ship = readShip(nodeLst, i);
				shipList.put(ship.type, ship);
			}
		} catch(Exception e)
		{
			shipDataError = true;
		}
	}
	/**
	 * method to read a single ship
	 * @param lst contains entry with data of a single ship
	 * @param item to keep track of the order in the xml file
	 * @return a ship
	 */
	private YPPPPShip readShip(NodeList lst, int item){
		YPPPPSize size;
		String s = readXMLItem(lst, item, "size");
		size = YPPPPSize.small;
		if (s == "medium") 
			size = YPPPPSize.medium;
		else if (s == "grand" )
			size = YPPPPSize.grand;
		else if (s == "large")
			size = YPPPPSize.large;
			
		return new YPPPPShip((readXMLItem(lst, item, "name")), new Double(readXMLItem(lst, item, "cb")), 
				new Double(readXMLItem(lst, item, "ram")), new Double(readXMLItem(lst, item, "sf")), new Double(readXMLItem(lst, item, "sink")),
				new Double(readXMLItem(lst, item, "rock")), size);
	}
	/**
	 * method to read a property from the xml-entry
	 * @param lst contains entry with data of a single ship
	 * @param item to keep track of the order in the xml file
	 * @param name of the property
	 * @return value of the property
	 */
	private String readXMLItem(NodeList lst, int item, String name){
		Node node = lst.item(item);
		if(node.getNodeType() == Node.ELEMENT_NODE)
			return ((name.equals("name") ? shipName.format(item) : "") + ((Node) ((Element) ((Element) node).getElementsByTagName(name).item(0)).getChildNodes().item(0)).getNodeValue());
		shipDataError = true;
		return null;
	}
	/**
	 * return ship type
	 * @param me true when player, false when opponent
	 * @return string type
	 */
	public String getShipType(boolean me){return (me ? myShip : oppShip).type;}
	private void storeState()
	{
		YPPPPShip [] tmp = {new YPPPPShip(myShip), new YPPPPShip(oppShip)};
		history.push(tmp);
	}
	public void undo(){
		if (history.size() > 0){   
			YPPPPShip [] tmp = history.pop();
			myShip = new YPPPPShip(tmp[0]);
			oppShip = new YPPPPShip(tmp[1]);
		}
		view.Update();
	}

	public boolean hasUndo(){
		return history.size() != 0; 
	}
	/*
	public void redo(){	
	}	
	public boolean hasRedo(){
		return history.;
	}*/
}