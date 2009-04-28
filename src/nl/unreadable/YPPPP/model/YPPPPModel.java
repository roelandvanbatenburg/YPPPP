package nl.unreadable.YPPPP.model;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Hashtable;

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
		if (shipDataError)
		{
			v.reportShipDataError();
		}
		view.setShipList(shipList.keySet());
		YPPPPShip firstship = (shipList.elements()).nextElement(); 
		myShip = new YPPPPShip(firstship);
		oppShip = new YPPPPShip(firstship);
	}
	
	/**
	 * 
	 * @param me true when the damage from the player, false when from the opponent is requested
	 * @return string containing the damage, (max) when applicable and the max health
	 */
	public String getDamage(boolean me){
		return oneDigit.format(getSimpleDamage(me)) + (getSimpleDamage(me) >= getFullHealth(me) ? "(max)" : "") + " / " + oneDigit.format((me ? getFullHealth(myShip) : getFullHealth(oppShip)));}
	private double getFullHealth(boolean me){
		return (me ? getFullHealth(myShip) : getFullHealth(oppShip));}
	private double getFullHealth(YPPPPShip ship){
		return (sinking ? ship.sink_hp : ship.sf_hp);	}
	private double getSimpleDamage(boolean me){
		return (me ? getSimpleDamage(myShip) : getSimpleDamage(oppShip));}
	private double getSimpleDamage(YPPPPShip ship){
		return ship.damage;}
	
	/**
	 * 
	 * @param me true when the damage from the player, false when from the opponent is requested
	 * @return string containing the damage and health in a special format
	 */
	public String getMoreInfo(boolean me){
		return (me ? getMoreInfo(myShip) : getMoreInfo(oppShip));}
	private String getMoreInfo(YPPPPShip ship){
		return ((ship.damage >= (sinking ? ship.sink_hp : ship.sf_hp)) ? "Max" : (lines ? oneDigit : twoDigit).format((lines ? 6 : 100) * ship.damage / (sinking ? ship.sink_hp : ship.sf_hp)) + (lines ? "/6.0" : "%"));}
	
	public String getCopyText() {
		return "Damage -> We: " + getMoreInfo(true) + " ~ They: " + getMoreInfo(false);
	}
	
	public void shoot(boolean me)
	{
		(me ? myShip : oppShip).getShot((me ? oppShip : myShip).cb_damage);
		Update();
	}
	public void collide()
	{
		myShip.ram(oppShip);
		oppShip.ram(myShip);
		Update();
	}
	public void hitRocks(boolean me)
	{
		(me ? myShip : oppShip).hitRocks();
		Update();
	}
	public void reset()
	{
		myShip.reset();
		oppShip.reset();
		Update();
	}
	public void toggleSinking()
	{
		sinking = (sinking ? false : true);
		Update();
	}
	public void toggleLines()
	{
		lines = (lines ? false : true);
		Update();
	}
	public void changeShipType(String type, boolean me)
	{
		(me ? myShip : oppShip).changeType(shipList.get(type));
		Update();
	}
	void Update()
	{
		// raise event?
		view.Update();
	}
	void getShipData()
	{
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
	private YPPPPShip readShip(NodeList lst, int item)
	{
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
	private String readXMLItem(NodeList lst, int item, String name)
	{
		Node node = lst.item(item);
		if(node.getNodeType() == Node.ELEMENT_NODE)
			return ((name.equals("name") ? shipName.format(item) : "") + ((Node) ((Element) ((Element) node).getElementsByTagName(name).item(0)).getChildNodes().item(0)).getNodeValue());
		shipDataError = true;
		return null;
	}
	
	public String getShipType(boolean me)
	{
		return (me ? myShip : oppShip).type;
	}
}