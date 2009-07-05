package nl.unreadable.YPPPP;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.unreadable.YPPPP.model.YPPPPModel;
import nl.unreadable.YPPPP.model.YPPPPPirate;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

public class YPPPPView extends JFrame{
	public static final long serialVersionUID = 9L;
	// General
	private YPPPPView view;
	private Container content;
	private JPanel allBox;
	private Clipboard systemClipboard;
	private DecimalFormat shipName = new DecimalFormat("##00");
	
	private Hashtable<String,Integer> statToInt;
	private Hashtable<Integer,String> intToStat;
	Pattern statPattern = Pattern.compile("</b>.*/<b>");
	Pattern oceanStatPattern = Pattern.compile("ocean-wide&nbsp;<b>");
	Pattern namePattern = Pattern.compile("<td align=\"center\" height=\"32\"><font size=\"[+]1\"><b>");
	Matcher tempMatch;
	private BufferedReader in;
	private String line;
	
	// YPPPPPanel

	private JPanel yPanel;
	private JCheckBox dcCheck, piCheck, psCheck;
	private JButton exitButton;		
	// DC
	private JPanel dcPanel;
	boolean dc = true;
	private YPPPPModel model;
	private JComboBox myShipChoice, oppShipChoice;
	private JLabel myDamageLab, myMoreInfoLab, oppDamageLab, oppMoreInfoLab;
	private JButton collideButton, undoButton, redoButton, resetButton, dcCopyButton;
	 
	//PI
	private JPanel piPanel;
	boolean pi = true;
	private JTextField nameTxt;
	private JLabel nameLab;
	private JTable pirateTable;
	private Hashtable<String, Integer[]> pirateData;
	private String[] columnNames = {"Name", "Gunning", "Bilge", "Sailing", "Rigging", "Carpentry", "Swordfighting", "Rumble", "DNav", "BNav", "TH", "Forage", "?"};
	private static Vector<String> blacklist,goldlist;
	private JButton piEnterBut, piCopyBut, piDelBut, piClearBut, piGoldBut, piBlackBut;
	private JComboBox oceanChoice;
	private static String ocean = "midnight";	
	private final int listVoid = 20, listGold = 10, listBlack = -1;
	private static boolean preferenceError = false;
	
	//PS
	private JPanel psPanel;
	
	public YPPPPView()
	{
		JFrame.setDefaultLookAndFeelDecorated(true);	
	    try {
	    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	    	
	      } catch (Exception e) {
	        System.out.println("Special look failed to load! No pretties for you :(");
	      }
		this.setTitle("Damage Counter of YPPPP: Yohoho Puzzle Pirate Pillage Program");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(365,680);
		view = this;
		allBox = new JPanel();
		systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		content = this.getContentPane();
		YPPPPPanel();
		dcPanel = dcPanel();
		piPanel = piPanel();
		drawView();
		
		getPreferences();
		if (preferenceError){
			System.out.println("Error reading preference.xml");
			preferenceError = true;
		}
		statToInt = new Hashtable<String,Integer>();
		statToInt.put("Able", 0); statToInt.put("Distinguished", 1); statToInt.put("Respected", 2); statToInt.put("Master", 3); statToInt.put("Renowned", 4); statToInt.put("Grand-Master", 5); statToInt.put("Legendary", 6); statToInt.put("Ultimate", 7);
		intToStat = new Hashtable<Integer,String>();
		intToStat.put(0, "Able"); intToStat.put(1, "Distinguished"); intToStat.put(2, "Respected"); intToStat.put(3, "Master"); intToStat.put(4, "Renowned"); intToStat.put(5, "Grand-Master"); intToStat.put(6, "Legendary"); intToStat.put(7, "Ultimate");
	}
	private void drawView(){
		dc = dcCheck.isSelected();
		pi = piCheck.isSelected();
		content.remove(allBox);
		allBox = new JPanel();
		allBox.setLayout(new BoxLayout(allBox, BoxLayout.PAGE_AXIS));		
		view.setSize(365,50 + (dc ? 177 : 0) + (pi ? 510 : 0));
		allBox.add(YPPPPPanel()); 
		if (dc) {allBox.add(new JSeparator(SwingConstants.HORIZONTAL));allBox.add(dcPanel); }
		if (pi) {allBox.add(new JSeparator(SwingConstants.HORIZONTAL));allBox.add(piPanel);}
		content.add(allBox);		
	}
	
	/*
	 * Panels
	 */
	private JPanel YPPPPPanel(){
		JPanel allBox = new JPanel();
		allBox.setLayout(new GridLayout(1,3));
		dcCheck = new JCheckBox("DC",dc); dcCheck.addActionListener(new panelHandler(dcPanel)); allBox.add(dcCheck);
		piCheck = new JCheckBox("PI",pi); piCheck.addActionListener(new panelHandler(piPanel)); allBox.add(piCheck);
		exitButton = new JButton("Exit"); exitButton.addActionListener(new ExitHandler()); allBox.add(exitButton);
		return allBox;
	}
	private JPanel piPanel(){
		// Global box
		JPanel allBox = new JPanel();
		allBox.setLayout(new BoxLayout(allBox, BoxLayout.PAGE_AXIS));
		
		// Name box where names are entered
		JPanel nameBox = new JPanel();
		nameBox.setLayout(new BoxLayout(nameBox, BoxLayout.LINE_AXIS));
		nameLab = new JLabel("Pirate Name:"); nameBox.add(nameLab);
		nameTxt = new JTextField("name"); nameTxt.addKeyListener(new KeyAdapter() {public void keyPressed(KeyEvent evt) {int key = evt.getKeyCode(); if (key == KeyEvent.VK_ENTER) addPirate();}}); nameBox.add(nameTxt);
		piEnterBut = new JButton("Enter"); piEnterBut.addActionListener(new EnterHandler()); nameBox.add(piEnterBut);
		allBox.add(nameBox);
		
		// Table to show all the data
		pirateData = new Hashtable<String,Integer[]>();
		pirateTable = new JTable(new HashTableModel());
		JScrollPane scrollPane = new JScrollPane(pirateTable);
		pirateTable.setFillsViewportHeight(true);
		TableCellRenderer head = new iconHeaderRenderer();
		TableCellRenderer cell = new statTableCellRenderer();
		
		int cnt = 0;
		for (Enumeration<TableColumn> e = pirateTable.getTableHeader().getColumnModel().getColumns(); e.hasMoreElements();){
			TableColumn col = e.nextElement();
			col.setHeaderRenderer(head);
			col.setCellRenderer(cell);
			col.setPreferredWidth(10);
			switch (cnt){
			case 0: col.setHeaderValue(getIcon("Name", "icons/name.png")); col.setPreferredWidth(100); break;
			case 1:	col.setHeaderValue(getIcon("Gun", "icons/gun.png")); break;
			case 2: col.setHeaderValue(getIcon("Bilge", "icons/bilge.png")); break;
			case 3: col.setHeaderValue(getIcon("Sail", "icons/sail.png")); break;
			case 4: col.setHeaderValue(getIcon("Rig", "icons/rig.png")); break;
			case 5: col.setHeaderValue(getIcon("Carp", "icons/carp.png")); break;
			case 6: col.setHeaderValue(getIcon("SF", "icons/sf.png")); break;
			case 7: col.setHeaderValue(getIcon("Rumble", "icons/rumble.png")); break;
			case 8: col.setHeaderValue(getIcon("Dnav", "icons/dnav.png")); break;
			case 9: col.setHeaderValue(getIcon("Bnav", "icons/bnav.png")); break;
			case 10: col.setHeaderValue(getIcon("TH", "icons/th.png")); break;
			case 11: col.setHeaderValue(getIcon("For", "icons/forage.png")); break;
			case 12: col.setHeaderValue(getIcon("?", "icons/list.png")); break;
			}
			cnt++;
		}
		allBox.add(scrollPane);
		JPanel buttonBox = new JPanel();
		buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.PAGE_AXIS));
			JPanel buttonBoxList = new JPanel();
			buttonBoxList.setLayout(new BoxLayout(buttonBoxList, BoxLayout.LINE_AXIS));
			piDelBut = new JButton("Delete"); piDelBut.addActionListener(new ClearHandler()); buttonBoxList.add(piDelBut);
			piClearBut = new JButton("Clear All"); piClearBut.addActionListener(new ClearAllHandler()); buttonBoxList.add(piClearBut);
			buttonBox.add(buttonBoxList);
			
			JPanel buttonBoxXO = new JPanel();
			buttonBoxXO.setLayout(new BoxLayout(buttonBoxXO, BoxLayout.LINE_AXIS));
			piCopyBut = new JButton("Job-Copy"); piCopyBut.addActionListener(new piCopyHandler()); buttonBoxXO.add(piCopyBut);
			piBlackBut = new JButton("(Un)Blacklist"); piBlackBut.addActionListener(new BlackListHandler()); buttonBoxXO.add(piBlackBut);
			piGoldBut = new JButton("(Un)Goldlist"); piGoldBut.addActionListener(new GoldListHandler()); buttonBoxXO.add(piGoldBut);
			String[] oceans = {"midnight","cobalt","viridian","sage","hunter","opal","malachite","jade","crimson","ice"}; oceanChoice = new JComboBox(oceans); oceanChoice.addActionListener(new OceanChangeHandler(oceanChoice)); oceanChoice.setSelectedItem(ocean); buttonBoxXO.add(oceanChoice);
			buttonBox.add(buttonBoxXO);
		allBox.add(buttonBox);
		return allBox;
	}
	private JPanel dcPanel(){
		JPanel allBox = new JPanel();
		allBox.setLayout(new BoxLayout(allBox, BoxLayout.LINE_AXIS));
		
		JPanel myShipBox = new JPanel();
			myShipBox.setLayout(new GridLayout(6,1));
			JLabel myLabel = new JLabel("Your");myShipBox.add(myLabel);
			myShipChoice = new JComboBox();myShipBox.add(myShipChoice);
			myDamageLab = new JLabel("damage");myShipBox.add(myDamageLab);
			myMoreInfoLab = new JLabel("0/12");myShipBox.add(myMoreInfoLab);
			JButton myGetShot = new JButton("Got Shot");myGetShot.addActionListener(new ShotHandler(true));myShipBox.add(myGetShot);
			JButton myHitRocks = new JButton("Hits Rocks/Edge");myHitRocks.addActionListener(new RockHandler(true));myShipBox.add(myHitRocks);
		
		JPanel oppShipBox = new JPanel();
			oppShipBox.setLayout(new GridLayout(6,1));
			JLabel oppLabel = new JLabel("Their");oppShipBox.add(oppLabel);
			oppShipChoice = new JComboBox();oppShipBox.add(oppShipChoice);
			oppDamageLab = new JLabel("damage");oppShipBox.add(oppDamageLab);
			oppMoreInfoLab = new JLabel("0/12");oppShipBox.add(oppMoreInfoLab);
			JButton oppGetShot = new JButton("Got Shot");oppGetShot.setMinimumSize(new Dimension(120, 30));oppGetShot.addActionListener(new ShotHandler(false));oppShipBox.add(oppGetShot);
			JButton oppHitRocks = new JButton("Hits Rocks/Edge");oppHitRocks.addActionListener(new RockHandler(false));oppShipBox.add(oppHitRocks);
		
		JPanel buttonBox = new JPanel();
			buttonBox.setLayout(new GridLayout(7,1));
			JCheckBox sinkingCheck = new JCheckBox("Sinking"); sinkingCheck.addActionListener(new SinkingHandler()); buttonBox.add(sinkingCheck);
			JCheckBox linesCheck = new JCheckBox("Lines"); linesCheck.addActionListener(new LinesHandler()); buttonBox.add(linesCheck);
			collideButton = new JButton("Collide"); collideButton.addActionListener(new CollideHandler());buttonBox.add(collideButton);
			undoButton = new JButton("Undo"); undoButton.addActionListener(new UndoHandler());buttonBox.add(undoButton);
			//redoButton = new JButton("Redo"); redoButton.addActionListener(new RedoHandler());buttonBox.add(redoButton);			
			resetButton = new JButton("Reset");	resetButton.addActionListener(new ResetHandler()); buttonBox.add(resetButton);
			dcCopyButton = new JButton("Copy"); dcCopyButton.addActionListener(new dcCopyHandler());buttonBox.add(dcCopyButton);
		
		allBox.add(myShipBox);
		allBox.add(buttonBox);
		allBox.add(oppShipBox);
		return allBox;
	}
	/*
	 * Ship list stuff
	 */
	public void setModel(YPPPPModel m){	model = m;}
	public void reportShipDataError() {
		System.out.println("Your \"ships.xml\" is missing or damaged, please replace/repair it");
		System.exit(-1);
	}
	public void setShipList(Set<String> s) {
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
	/*
	 * Update
	 */
	public void Update()
	{
		myShipChoice.setSelectedItem((model.getShipType(true)));
		myShipChoice.repaint();
		myDamageLab.setText(model.getDamage(true));
		myMoreInfoLab.setText(model.getMoreInfo(true));
		oppShipChoice.setSelectedItem((model.getShipType(false)));
		oppDamageLab.setText(model.getDamage(false));
		oppMoreInfoLab.setText(model.getMoreInfo(false));
		undoButton.setEnabled(model.hasUndo());
		//redoButton.setEnabled(model.hasRedo());
	}
	/*
	 * XML reading and writing
	 */
	private void getPreferences(){
		try {
			goldlist = new Vector<String>();
			blacklist = new Vector<String>();
			/*
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File("preferences.xml"));
			ocean = doc.getElementsByTagName("ocean").item(0).getChildNodes().item(0).getNodeValue();
			*/
			DOMParser parser = new DOMParser();
			parser.parse("preferences.xml");
			Document doc = parser.getDocument();
			ocean = doc.getElementsByTagName("Ocean").item(0).getAttributes().item(0).getNodeValue();
			int listcnt = Integer.parseInt(doc.getElementsByTagName("ListCnt").item(0).getAttributes().item(0).getNodeValue());
			Node test;
			for (int i = 0; i < listcnt; i++){
				test = doc.getElementsByTagName("List").item(0).getAttributes().item(i);
				if(test.getNodeValue().equals("black"))
					blacklist.add(test.getNodeName());
				if(test.getNodeValue().equals("gold"))
					goldlist.add(test.getNodeName());
			}
		} catch(Exception e){preferenceError = true;}
	}
	private void savePreferences(){
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			Document doc = parser.newDocument();
			Element root = doc.createElement("Preferences");
			doc.appendChild(root);
			// ocean
			Element oceanelm = doc.createElement("Ocean");
			oceanelm.setAttribute("Ocean", ocean);
			root.appendChild(oceanelm);
			// lists
			Element listcntelm = doc.createElement("ListCnt");
			listcntelm.setAttribute("Count", "" + (blacklist.size() + goldlist.size()));
			root.appendChild(listcntelm);
			Element listelm = doc.createElement("List");
			// black
			for (Enumeration<String> e = blacklist.elements(); e.hasMoreElements();){
				listelm.setAttribute(e.nextElement(), "black");
			}
			// gold
			for (Enumeration<String> e = goldlist.elements(); e.hasMoreElements();){
				listelm.setAttribute(e.nextElement(), "gold");
			}
			root.appendChild(listelm);			
			
			// write to file
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(doc), new StreamResult(new BufferedWriter(new FileWriter(new File("preferences.xml")))));
		} catch(Exception e){preferenceError = true; System.out.println(e);}
	}
	/*
	 * Adding pirates to table
	 */
	private void addPirate(){
		YPPPPPirate p = new YPPPPPirate();
		p = getPirateInfo(nameTxt.getText());
		if (p == null){
			System.out.println("Pirate not found (are you on the right ocean?)");
			return;
		}
			
		int list = (goldlist.contains(p.getName()) ? listGold : (blacklist.contains(p.getName()) ? listBlack : listVoid)); 
		Integer[] test = {p.getGunning(), p.getBilge(), p.getSailing(), p.getRigging(), p.getCarpentry(), p.getSF(), p.getRumble(), p.getDNav(), p.getBNav(), p.getTH(), p.getForage(), list};
		pirateData.put(p.getName(), test);
		((HashTableModel) pirateTable.getModel()).fireTableDataChanged();
	}
	private YPPPPPirate getPirateInfo(String name)
	{
		try{
			YPPPPPirate p = new YPPPPPirate(name);
			URL url = new URL("http://" + ocean + ".puzzlepirates.com/yoweb/pirate.wm?target=" + name);
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			line = in.readLine();
			p.setName(readNameLine());
			p.setSF(readStatLine("Swordfighting", "Bilging"));
			p.setBilge(readStatLine("Bilging","Sailing"));
			p.setSailing(readStatLine("Sailing","Rigging"));
			p.setRigging(readStatLine("Rigging","Navigating"));
			p.setDNav(readStatLine("Navigating","Battle Navigation"));
			p.setBNav(readStatLine("Battle Navigation","Gunning"));
			p.setGunning(readStatLine("Gunning","Carpentry"));
			p.setCarpentry(readStatLine("Carpentry","Rumble"));
			p.setRumble(readStatLine("Rumble","Treasure Haul"));
			p.setTH(readStatLine("Treasure Haul","Spades"));
			p.setForage(readStatLine("Foraging",""));
			in.close();
			return p;
		}catch(Exception e){return null;}
	}
	private String readNameLine() throws Exception{
		while (!line.contains("<td align=\"center\" height=\"32\"><font size=\"+1\"><b>") && (line = in.readLine()) != null){}
		tempMatch = namePattern.matcher(line);
		if (!tempMatch.find()) return "";
		return line.substring(tempMatch.end(), line.length()-16);
	}
	private int readStatLine(String stat, String nextStat) throws Exception{
		// read till we are really at stat
		while (!line.contains("alt=\"" + stat + "\"></a></td>") && (line = in.readLine()) != null && !line.contains("alt=\"" + stat + "\"></a></td>")){}
		while((line=in.readLine()) != null && !line.contains("/")){}
		int stand = readStat(line);
		// read till we get ocean-wide or we are at stat
		if (nextStat.equals("")) return stand;
		while ((line = in.readLine()) != null && !line.contains("ocean-wide") && !line.contains("alt=\"" + nextStat + "\"></a></td>")){}
		if (line.contains("ocean-wide")){
			stand = readOceanStat(line);
			in.readLine();
		}
		return stand;
	}
	private int readStat(String line){
		tempMatch = statPattern.matcher(line);
		if (!tempMatch.find()) return 0;
		return statToInt.get(line.substring(tempMatch.end(), line.length()-4));
	}
	private int readOceanStat(String line){
		tempMatch = oceanStatPattern.matcher(line);
		if (!tempMatch.find()) return 0;
		return statToInt.get(line.substring(tempMatch.end(), line.length()-12));
	}
	/*
	 * Managing Lists
	 */
	private void clear(){
		int index = pirateTable.getSelectedRow();
		pirateData.remove(pirateTable.getValueAt(index,0));
		//((HashTableModel) pirateTable.getModel()).fireTableDataChanged();
		((HashTableModel) pirateTable.getModel()).fireTableRowsDeleted(index, index); 
	}
	private void clearAll(){
		pirateData.clear();
		((HashTableModel) pirateTable.getModel()).fireTableDataChanged();
	}
	private void goldlist(){
		int index = pirateTable.getSelectedRow();
		String name = (String) pirateTable.getValueAt(index, 0);
		Integer[] temp = pirateData.get(name);
		blacklist.remove(name);
		if (goldlist.contains(name)){
			goldlist.remove(name);
			temp[temp.length-1] = listVoid;
		} else {
			goldlist.add(name);
			temp[temp.length-1] = listGold;
		}
		((HashTableModel) pirateTable.getModel()).fireTableCellUpdated(index, 12);  
	}
	private void blacklist(){
		int index = pirateTable.getSelectedRow();
		String name = (String) pirateTable.getValueAt(index, 0);
		Integer[] temp = pirateData.get(name);
		goldlist.remove(name);
		if (blacklist.contains(name)){
			blacklist.remove(name);
			temp[temp.length-1] = listVoid;
		} else {
			blacklist.add(name);
			temp[temp.length-1] = listBlack;
		}
		((HashTableModel) pirateTable.getModel()).fireTableCellUpdated(index, 12);
	}
	/*
	 * Display of text and icons in table cells
	 */
	private TextOrIcon getIcon(String text, String icon){
		File f = new File(icon);
		TextOrIcon toi = new TextOrIcon(text, f.exists() ? new ImageIcon(icon) : null);
		return toi;
	}	
	private class TextOrIcon {
    	TextOrIcon(String text, Icon icon) {
        	this.text = text;
            this.icon = icon;
        }
        String text;
        Icon icon;
	}
	private class iconHeaderRenderer extends DefaultTableCellRenderer {
		public static final long serialVersionUID = 9L;
		public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int column) {
            // Inherit the colors and font from the header component
			if (table != null) {
				JTableHeader header = table.getTableHeader();
				if (header != null) {
					setForeground(header.getForeground());
					setBackground(header.getBackground());
					setFont(header.getFont());
				}
			}
			if (value instanceof TextOrIcon) {
				Icon temp = ((TextOrIcon)value).icon;
				setIcon(temp);				
				setText(temp != null ? "" : ((TextOrIcon)value).text);
			} else {
				setText((value == null) ? "" : value.toString());
				setIcon(null);
			}
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			setHorizontalAlignment(JLabel.CENTER);
			return this;
		}
	};
	/*
	 * Hashtable Stuff
	 */
	private class HashTableModel extends AbstractTableModel {
		public static final long serialVersionUID = 9L; 
		public int getRowCount(){return pirateData.size();}
		public int getColumnCount(){return columnNames.length;}
		public Object getValueAt(int row, int column){
			int cnt = 0;
			Vector<String> keys = new Vector<String>(pirateData.keySet());
			Collections.sort(keys);
			for (Enumeration<String> e = keys.elements(); e.hasMoreElements();){
				Object el = e.nextElement();
				if (cnt == row){
					if (column == 0) // name
						return el;
					Integer [] dat = pirateData.get(el);
					return dat[column-1];
				}
				cnt++;
			}
			return new Object();
		}
	}
	private class statTableCellRenderer extends DefaultTableCellRenderer 
	{
		public static final long serialVersionUID = 9L;
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if( value instanceof Integer ){
				Integer val = (Integer) value;
				setForeground(Color.BLACK);
				switch (val){
				case 0: cell.setBackground(Color.WHITE); break; //Able
				case 1: cell.setBackground(Color.GRAY); break;	//Dis
				case 2: cell.setBackground(Color.CYAN); break;	//Res
				case 3: cell.setBackground(new Color(0,0,255));  cell.setForeground(Color.WHITE); break; //Mas
				case 4:	cell.setBackground(Color.GREEN); break;	//Ren
				case 5:	cell.setBackground(Color.YELLOW); break;//GM
				case 6: cell.setBackground(Color.ORANGE); break;//Leg
				case 7: cell.setBackground(Color.RED); break;	//Ult
				case listBlack: cell.setForeground(Color.BLACK); 
					cell.setBackground(Color.BLACK); break;		//Blacklist
				case listGold: cell.setForeground(Color.YELLOW); 
					cell.setBackground(Color.YELLOW); break;	//Goldlist
				case listVoid: cell.setForeground(Color.WHITE); 
					cell.setBackground(Color.WHITE); break;		//no list
				}
			}
			else{
				cell.setForeground(Color.BLACK);
				cell.setBackground(Color.WHITE);
			}
			return cell;
		}
	}	
	/*
	 * Handlers (checkbox/button/enter)
	 */
	private class panelHandler implements ActionListener{
		private JPanel target;
		panelHandler(JPanel t){target = t;}
		public void actionPerformed(ActionEvent e){
			target.setEnabled(!target.isEnabled());
			drawView();
		}
	}
	private class ExitHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){
			savePreferences();
			if (preferenceError){System.out.println("Error writing to preference.xml");	}
			System.exit(0);
		}
	}	
	private class SinkingHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){model.toggleSinking();}
	}
	private class LinesHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){model.toggleLines();}
	}
	private class CollideHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){model.collide();}
	}
	private class UndoHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){model.undo();}
	}
	/*private class RedoHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){model.redo();}
	}*/
	private class ResetHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){model.reset();}
	}
	private class dcCopyHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){systemClipboard.setContents(new StringSelection(model.getCopyText()), null);}
	}
	private class piCopyHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){systemClipboard.setContents(new StringSelection("/job "+pirateTable.getValueAt(pirateTable.getSelectedRow(),0)), null);}
	}
	private class ShipChangeHandler implements ActionListener{
		private boolean me;
		private JComboBox combobox;
		ShipChangeHandler(boolean m, JComboBox box){me = m; combobox = box; }
		public void actionPerformed(ActionEvent e){
			model.changeShipType(shipName.format(combobox.getSelectedIndex()) + (String) combobox.getSelectedItem(), me);
			}
	}
	private class ShotHandler implements ActionListener{
		private boolean me;
		ShotHandler(boolean m){me = m;}
		public void actionPerformed(ActionEvent e){model.shoot(me);}
	}
	private class RockHandler implements ActionListener{
		private boolean me;
		RockHandler(boolean m){me = m;}
		public void actionPerformed(ActionEvent e){model.hitRocks(me);}
	}
	private class EnterHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){addPirate();}
	}
	private class ClearHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){clear();}
	}
	private class ClearAllHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){clearAll();}
	}
	private class OceanChangeHandler implements ActionListener{
		private JComboBox combobox;
		OceanChangeHandler(JComboBox box){combobox = box; }
		public void actionPerformed(ActionEvent e){ocean = (String) combobox.getSelectedItem();}
	}
	private class BlackListHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){blacklist();}
	}
	private class GoldListHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){goldlist();}
	}	
}
