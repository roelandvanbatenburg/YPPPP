package nl.unreadable.YPPPP;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.unreadable.YPPPP.model.YPPPPPirate;

import org.w3c.dom.Document;


public class YPPPPpiView extends JFrame{
	public static final long serialVersionUID = 9L;
	
	private JTextField nameTxt;
	private JLabel nameLab;
	private JTable pirateTable;
	private Hashtable<String, Integer[]> pirateData;
	private String[] columnNames = {"Name", "Gunning", "Bilge", "Sailing", "Rigging", "Carpentry", "Swordfighting", "Rumble", "DNav", "BNav", "TH", "?"};
	private Vector<String> blacklist,goldlist;
	private JButton piEnterBut, piCopyBut, piDelBut, piClearBut, piQuitBut, piDisableBut, piGoldBut, piBlackBut;
	private JComboBox oceanChoice;
	
	private Clipboard systemClipboard;
	
	private Hashtable<String,Integer> statToInt;
	private Hashtable<Integer,String> intToStat;
	Pattern statPattern = Pattern.compile("</b>.*/<b>");
	Pattern oceanStatPattern = Pattern.compile("ocean-wide&nbsp;<b>");
	Matcher tempMatch;
	private BufferedReader in;
	private String line;
	
	private static String ocean = "midnight";
	private static boolean preferenceError = false;
	
	public YPPPPpiView()
	{
		JFrame.setDefaultLookAndFeelDecorated(true);	
	    try {
	        UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
	      } catch (Exception e) {
	        System.out.println("Special look failed to load! No pretties for you :(");
	      }
		this.setTitle("Pirate Informer of YPPPP: Yohoho Puzzle Pirate Pillage Program");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(365,400);
		systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		
		getPreferences();
		if (preferenceError){
			System.out.println("Error reading preference.xml");
			preferenceError = true;
		}
		
		// Global box
		Container content = this.getContentPane();
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
		//TableCellRenderer renderer = new CustomTableCellRenderer();
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
			case 11: col.setHeaderValue(getIcon("?", "icons/list.png")); break;
			}
			cnt++;
		}		
		

		allBox.add(scrollPane);
		
		// Button box
		JPanel buttonBox = new JPanel();
		buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.PAGE_AXIS));
			JPanel buttonBoxList = new JPanel();
			buttonBoxList.setLayout(new BoxLayout(buttonBoxList, BoxLayout.LINE_AXIS));
			piDelBut = new JButton("Delete"); piDelBut.addActionListener(new ClearHandler()); buttonBoxList.add(piDelBut);
			piClearBut = new JButton("Clear All"); piClearBut.addActionListener(new ClearAllHandler()); buttonBoxList.add(piClearBut);
			piDisableBut = new JButton("Disable"); piDisableBut.addActionListener(new DisableHandler()); buttonBoxList.add(piDisableBut);
			piQuitBut = new JButton("Exit"); piQuitBut.addActionListener(new ExitHandler()); buttonBoxList.add(piQuitBut);
			buttonBox.add(buttonBoxList);
			
			JPanel buttonBoxXO = new JPanel();
			buttonBoxXO.setLayout(new BoxLayout(buttonBoxXO, BoxLayout.LINE_AXIS));
			piCopyBut = new JButton("Job-Copy"); piCopyBut.addActionListener(new CopyHandler()); buttonBoxXO.add(piCopyBut);
			piBlackBut = new JButton("Blacklist"); piBlackBut.addActionListener(new BlackListHandler()); buttonBoxXO.add(piBlackBut);
			piGoldBut = new JButton("Goldlist"); piGoldBut.addActionListener(new GoldListHandler()); buttonBoxXO.add(piGoldBut);
			String[] oceans = {"midnight","cobalt","viridian","sage","hunter","opal","malachite","ice"}; oceanChoice = new JComboBox(oceans); oceanChoice.addActionListener(new OceanChangeHandler(oceanChoice)); oceanChoice.setSelectedItem(ocean); buttonBoxXO.add(oceanChoice);
			buttonBox.add(buttonBoxXO);
		allBox.add(buttonBox);
		
		content.add(allBox);
		statToInt = new Hashtable<String,Integer>();
		statToInt.put("Able", 0); statToInt.put("Distinguished", 1); statToInt.put("Respected", 2); statToInt.put("Master", 3); statToInt.put("Renowned", 4); statToInt.put("Grand-Master", 5); statToInt.put("Legendary", 6); statToInt.put("Ultimate", 7);
		intToStat = new Hashtable<Integer,String>();
		intToStat.put(0, "Able"); intToStat.put(1, "Distinguished"); intToStat.put(2, "Respected"); intToStat.put(3, "Master"); intToStat.put(4, "Renowned"); intToStat.put(5, "Grand-Master"); intToStat.put(6, "Legendary"); intToStat.put(7, "Ultimate");
		

	}
	
	private TextOrIcon getIcon(String text, String icon){
		File f = new File(icon);
		TextOrIcon toi = new TextOrIcon(text, f.exists() ? new ImageIcon(icon) : null);
		return toi;
		
	}
	
	/*
	 * Adding pirates to table
	 */
	private void addPirate(){
		YPPPPPirate p = new YPPPPPirate();
		p = getPirateInfo(nameTxt.getText());
		Integer[] test = {p.getGunning(), p.getBilge(), p.getSailing(), p.getRigging(), p.getCarpentry(), p.getSF(), p.getRumble(), p.getDNav(), p.getBNav(), p.getTH(), 20};
		pirateData.put(nameTxt.getText(), test);
		((HashTableModel) pirateTable.getModel()).fireTableDataChanged();
	}
	
	private YPPPPPirate getPirateInfo(String name)
	{
		try{
			YPPPPPirate p = new YPPPPPirate(name);
			URL url = new URL("http://" + ocean + ".puzzlepirates.com/yoweb/pirate.wm?target=" + name);
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			//in = new BufferedReader(new FileReader("btza.txt"));
			line = in.readLine();
			p.setSF(readStatLine("Swordfighting", "Bilging",p));
			p.setBilge(readStatLine("Bilging","Sailing",p));
			p.setSailing(readStatLine("Sailing","Rigging",p));
			p.setRigging(readStatLine("Rigging","Navigating",p));
			p.setDNav(readStatLine("Navigating","Battle Navigation",p));
			p.setBNav(readStatLine("Battle Navigation","Gunning",p));
			p.setGunning(readStatLine("Gunning","Carpentry",p));
			p.setCarpentry(readStatLine("Carpentry","Rumble",p));
			p.setRumble(readStatLine("Rumble","Treasure Haul", p));
			p.setTH(readStatLine("Treasure Haul","Spades",p));
			in.close();
			return p;
			
		}catch(Exception e){
			return new YPPPPPirate();
		}
	}
	int readStatLine(String stat, String nextStat, YPPPPPirate p) throws Exception{
		// read till we are really at stat
		while (!line.contains("alt=\"" + stat + "\"></a></td>") && (line = in.readLine()) != null && !line.contains("alt=\"" + stat + "\"></a></td>")){}
		while((line=in.readLine()) != null && !line.contains("/")){}
		int stand = readStat(line);
		// read till we get ocean-wide or we are at stat
		while ((line = in.readLine()) != null && !line.contains("ocean-wide") && !line.contains("alt=\"" + nextStat + "\"></a></td>")){}
		return stand;
	}
	int readStat(String line){
		tempMatch = statPattern.matcher(line);
		if (!tempMatch.find()) return 0;
		return statToInt.get(line.substring(tempMatch.end(), line.length()-4));
	}
	int readOceanStat(String line){
		tempMatch = oceanStatPattern.matcher(line);
		if (!tempMatch.find()) return 0;
		return statToInt.get(line.substring(tempMatch.end(), line.length()-12));
	}

	/*
	 * XML reading and writing
	 */
	void getPreferences(){
		try {
			goldlist = new Vector<String>();
			blacklist = new Vector<String>();
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File("preferences.xml"));
			ocean = doc.getElementsByTagName("ocean").item(0).getChildNodes().item(0).getNodeValue();
		} catch(Exception e){preferenceError = true;}
	}
	static void savePreferences(){
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File("preferences.xml"));
			doc.getElementsByTagName("ocean").item(0).getFirstChild().setNodeValue(ocean);
			TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult("preferences.xml"));
		} catch(Exception e){preferenceError = true;}
	}
	/*
	 * Button handlers
	 */
	class CopyHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){systemClipboard.setContents(new StringSelection("/job "+pirateTable.getValueAt(pirateTable.getSelectedRow(),0)), null);}
	}
	public static class ExitHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){
			savePreferences();
			if (preferenceError){
				System.out.println("Error writing to preference.xml");
			}
			System.exit(0);
		}
	}
	class EnterHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){addPirate();}
	}
	class ClearHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){clear();}
	}
	class ClearAllHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){clearAll();}
	}
	class DisableHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){Disable();}
	}
	class OceanChangeHandler implements ActionListener{
		private JComboBox combobox;
		OceanChangeHandler(JComboBox box){combobox = box; }
		public void actionPerformed(ActionEvent e){ocean = (String) combobox.getSelectedItem();}
	}
	class BlackListHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){blacklist();}
	}
	class GoldListHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){goldlist();}
	}
	public void Disable(){
		this.setEnabled(false);
		this.setVisible(false);
	}
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
		goldlist.add(name);
		Integer[] temp = pirateData.get(name);
		temp[temp.length-1] = 10;
		((HashTableModel) pirateTable.getModel()).fireTableCellUpdated(index, 11);  
	}
	private void blacklist(){
		int index = pirateTable.getSelectedRow();
		String name = (String) pirateTable.getValueAt(index, 0);
		blacklist.add(name);
		Integer[] temp = pirateData.get(name);
		temp[temp.length-1] = -1;
		((HashTableModel) pirateTable.getModel()).fireTableCellUpdated(index, 11);
	}
	/**
	 * Hashtable Stuff
	 */
	class HashTableModel extends AbstractTableModel {
		public static final long serialVersionUID = 9L;
		//private Hashtable<String, String[]> data; 
		
		public int getRowCount(){
			return pirateData.size();
		}
		public int getColumnCount(){
			return columnNames.length;
		}
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
			// empty object	
			return new Object();
		}
	}
	public class statTableCellRenderer extends DefaultTableCellRenderer 
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
				case 3:	cell.setBackground(Color.BLUE); break;	//Mas
				case 4:	cell.setBackground(Color.GREEN); break;	//Ren
				case 5:	cell.setBackground(Color.YELLOW); break;//GM
				case 6: cell.setBackground(Color.ORANGE); break;//Leg
				case 7: cell.setBackground(Color.RED); break;	//Ult
				case -1: cell.setForeground(Color.BLACK); 
					cell.setBackground(Color.BLACK); break;		//Blacklist
				case 10: cell.setForeground(Color.ORANGE); 
					cell.setBackground(Color.YELLOW); break;	//Goldlist
				case 20: cell.setForeground(Color.WHITE); 
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
    // This class is used to hold the text and icon values
    // used by the renderer that renders both text and icons
	class TextOrIcon {
    	TextOrIcon(String text, Icon icon) {
        	this.text = text;
            this.icon = icon;
        }
        String text;
        Icon icon;
	}
	
    // This customized renderer can render objects of the type TextandIcon
	public class iconHeaderRenderer extends DefaultTableCellRenderer {
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
	
	
}

