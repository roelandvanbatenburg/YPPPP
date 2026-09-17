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
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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

import nl.unreadable.YPPPP.model.PirateRoster;

/**
 * Pure Swing view: renders whatever it is told to render, and reports
 * every user action to its {@link YPPPPViewListener}. Holds no model
 * reference and no business logic.
 */
public class YPPPPView extends JFrame {
	public static final long serialVersionUID = 9L;
	// General
	private YPPPPView view;
	private Container content;
	private JPanel allBox;
	private Clipboard systemClipboard;
	private YPPPPViewListener listener;

	// YPPPPPanel
	private JCheckBox dcCheck, piCheck;
	private JButton exitButton;
	// DC
	private JPanel dcPanel;
	boolean dc = true;
	private JComboBox<String> myShipChoice, oppShipChoice;
	private JLabel myDamageLab, myMoreInfoLab, oppDamageLab, oppMoreInfoLab;
	private JButton collideButton, undoButton, resetButton, dcCopyButton;

	// PI
	private JPanel piPanel;
	boolean pi = true;
	private JTextField nameTxt;
	private JLabel nameLab;
	private JTable pirateTable;
	private List<PirateRow> pirateRows = new ArrayList<PirateRow>();
	private String[] columnNames = { "Name", "Gunning", "Bilge", "Sailing", "Rigging", "Carpentry", "Patching",
			"Swordfighting", "Rumble", "DNav", "BNav", "TH", "Forage", "?" };
	private JButton piEnterBut, piCopyBut, piDelBut, piClearBut, piGoldBut, piBlackBut;
	private JComboBox<String> oceanChoice;

	public YPPPPView() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Special look failed to load! No pretties for you :(");
		}
		this.setTitle("Damage Counter of YPPPP: Yohoho Puzzle Pirate Pillage Program");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(450, 680);
		view = this;
		allBox = new JPanel();
		systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		content = this.getContentPane();
		YPPPPPanel();
		dcPanel = dcPanel();
		piPanel = piPanel();
		drawView();
	}

	public void setListener(YPPPPViewListener l) {
		listener = l;
	}

	private void drawView() {
		dc = dcCheck.isSelected();
		pi = piCheck.isSelected();
		content.remove(allBox);
		allBox = new JPanel();
		allBox.setLayout(new BoxLayout(allBox, BoxLayout.PAGE_AXIS));
		view.setSize(450, 40 + (dc ? 200 : 0) + (pi ? 510 : 0));
		allBox.add(YPPPPPanel());
		if (dc) {
			allBox.add(new JSeparator(SwingConstants.HORIZONTAL));
			allBox.add(dcPanel);
		}
		if (pi) {
			allBox.add(new JSeparator(SwingConstants.HORIZONTAL));
			allBox.add(piPanel);
		}
		content.add(allBox);
	}

	/*
	 * Panels
	 */
	private JPanel YPPPPPanel() {
		JPanel allBox = new JPanel();
		allBox.setLayout(new GridLayout(1, 3));
		dcCheck = new JCheckBox("DC", dc);
		dcCheck.addActionListener(new panelHandler(dcPanel));
		allBox.add(dcCheck);
		piCheck = new JCheckBox("PI", pi);
		piCheck.addActionListener(new panelHandler(piPanel));
		allBox.add(piCheck);
		exitButton = new JButton("Exit");
		exitButton.addActionListener(new ExitHandler());
		allBox.add(exitButton);
		return allBox;
	}

	private JPanel piPanel() {
		// Global box
		JPanel allBox = new JPanel();
		allBox.setLayout(new BoxLayout(allBox, BoxLayout.PAGE_AXIS));

		// Name box where names are entered
		JPanel nameBox = new JPanel();
		nameBox.setLayout(new BoxLayout(nameBox, BoxLayout.LINE_AXIS));
		nameLab = new JLabel("Pirate Name:");
		nameBox.add(nameLab);
		nameTxt = new JTextField("name");
		nameTxt.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				int key = evt.getKeyCode();
				if (key == KeyEvent.VK_ENTER)
					listener.onAddPirate(nameTxt.getText());
			}
		});
		nameBox.add(nameTxt);
		piEnterBut = new JButton("Enter");
		piEnterBut.addActionListener(new EnterHandler());
		nameBox.add(piEnterBut);
		allBox.add(nameBox);

		// Table to show all the data
		pirateTable = new JTable(new HashTableModel());
		pirateTable.setAutoCreateRowSorter(true);
		JScrollPane scrollPane = new JScrollPane(pirateTable);
		pirateTable.setFillsViewportHeight(true);
		TableCellRenderer head = new iconHeaderRenderer();
		TableCellRenderer cell = new statTableCellRenderer();

		int cnt = 0;
		for (Enumeration<TableColumn> e = pirateTable.getTableHeader().getColumnModel().getColumns(); e
				.hasMoreElements();) {
			TableColumn col = e.nextElement();
			col.setHeaderRenderer(head);
			col.setCellRenderer(cell);
			col.setPreferredWidth(10);
			switch (cnt) {
				case 0:
					col.setHeaderValue(getIcon("Name", "icons/name.png"));
					col.setPreferredWidth(100);
					break;
				case 1:
					col.setHeaderValue(getIcon("Gun", "icons/gun.png"));
					break;
				case 2:
					col.setHeaderValue(getIcon("Bilge", "icons/bilge.png"));
					break;
				case 3:
					col.setHeaderValue(getIcon("Sail", "icons/sail.png"));
					break;
				case 4:
					col.setHeaderValue(getIcon("Rig", "icons/rig.png"));
					break;
				case 5:
					col.setHeaderValue(getIcon("Carp", "icons/carp.png"));
					break;
				case 6:
					col.setHeaderValue(getIcon("Patch", "icons/patch.png"));
					break;
				case 7:
					col.setHeaderValue(getIcon("SF", "icons/sf.png"));
					break;
				case 8:
					col.setHeaderValue(getIcon("Rumble", "icons/rumble.png"));
					break;
				case 9:
					col.setHeaderValue(getIcon("Dnav", "icons/dnav.png"));
					break;
				case 10:
					col.setHeaderValue(getIcon("Bnav", "icons/bnav.png"));
					break;
				case 11:
					col.setHeaderValue(getIcon("TH", "icons/th.png"));
					break;
				case 12:
					col.setHeaderValue(getIcon("For", "icons/forage.png"));
					break;
				case 13:
					col.setHeaderValue(getIcon("?", "icons/list.png"));
					break;
			}
			cnt++;
		}
		allBox.add(scrollPane);
		JPanel buttonBox = new JPanel();
		buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.PAGE_AXIS));
		JPanel buttonBoxList = new JPanel();
		buttonBoxList.setLayout(new BoxLayout(buttonBoxList, BoxLayout.LINE_AXIS));
		piDelBut = new JButton("Delete");
		piDelBut.addActionListener(new ClearHandler());
		buttonBoxList.add(piDelBut);
		piClearBut = new JButton("Clear All");
		piClearBut.addActionListener(new ClearAllHandler());
		buttonBoxList.add(piClearBut);
		buttonBox.add(buttonBoxList);

		JPanel buttonBoxXO = new JPanel();
		buttonBoxXO.setLayout(new BoxLayout(buttonBoxXO, BoxLayout.LINE_AXIS));
		piCopyBut = new JButton("Job-Copy");
		piCopyBut.addActionListener(new piCopyHandler());
		buttonBoxXO.add(piCopyBut);
		piBlackBut = new JButton("(Un)Blacklist");
		piBlackBut.addActionListener(new BlackListHandler());
		buttonBoxXO.add(piBlackBut);
		piGoldBut = new JButton("(Un)Goldlist");
		piGoldBut.addActionListener(new GoldListHandler());
		buttonBoxXO.add(piGoldBut);
		String[] oceans = { "cerulean", "emerald", "merideia", "opal", "jade", "crimson", "ice" };
		oceanChoice = new JComboBox<String>(oceans);
		oceanChoice.addActionListener(new OceanChangeHandler(oceanChoice));
		buttonBoxXO.add(oceanChoice);
		buttonBox.add(buttonBoxXO);
		allBox.add(buttonBox);
		return allBox;
	}

	private JPanel dcPanel() {
		JPanel allBox = new JPanel();
		allBox.setLayout(new BoxLayout(allBox, BoxLayout.LINE_AXIS));

		JPanel myShipBox = new JPanel();
		myShipBox.setLayout(new GridLayout(6, 1));
		JLabel myLabel = new JLabel("Your");
		myShipBox.add(myLabel);
		myShipChoice = new JComboBox<String>();
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
		oppShipBox.setLayout(new GridLayout(6, 1));
		JLabel oppLabel = new JLabel("Their");
		oppShipBox.add(oppLabel);
		oppShipChoice = new JComboBox<String>();
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
		buttonBox.setLayout(new GridLayout(6, 1));
		JCheckBox sinkingCheck = new JCheckBox("Sinking");
		sinkingCheck.addActionListener(new SinkingHandler());
		buttonBox.add(sinkingCheck);
		JCheckBox linesCheck = new JCheckBox("Lines");
		linesCheck.addActionListener(new LinesHandler());
		buttonBox.add(linesCheck);
		collideButton = new JButton("Collide");
		collideButton.addActionListener(new CollideHandler());
		buttonBox.add(collideButton);
		undoButton = new JButton("Undo");
		undoButton.addActionListener(new UndoHandler());
		buttonBox.add(undoButton);
		resetButton = new JButton("Reset");
		resetButton.addActionListener(new ResetHandler());
		buttonBox.add(resetButton);
		dcCopyButton = new JButton("Copy");
		dcCopyButton.addActionListener(new dcCopyHandler());
		buttonBox.add(dcCopyButton);

		allBox.add(myShipBox);
		allBox.add(buttonBox);
		allBox.add(oppShipBox);
		return allBox;
	}

	/*
	 * Rendering - called by the controller
	 */
	public void setShipChoices(List<String> shipNames) {
		myShipChoice.removeAllItems();
		oppShipChoice.removeAllItems();
		for (String name : shipNames) {
			myShipChoice.addItem(name);
			oppShipChoice.addItem(name);
		}
		myShipChoice.addActionListener(new ShipChangeHandler(true, myShipChoice));
		oppShipChoice.addActionListener(new ShipChangeHandler(false, oppShipChoice));
	}

	public void setMyShipSelection(String name) {
		myShipChoice.setSelectedItem(name);
	}

	public void setOppShipSelection(String name) {
		oppShipChoice.setSelectedItem(name);
	}

	public void setMyDamage(String text) {
		myDamageLab.setText(text);
	}

	public void setMyMoreInfo(String text) {
		myMoreInfoLab.setText(text);
	}

	public void setOppDamage(String text) {
		oppDamageLab.setText(text);
	}

	public void setOppMoreInfo(String text) {
		oppMoreInfoLab.setText(text);
	}

	public void setUndoEnabled(boolean enabled) {
		undoButton.setEnabled(enabled);
	}

	public void setOceanSelection(String ocean) {
		oceanChoice.setSelectedItem(ocean);
	}

	public void setPirateRows(List<PirateRow> rows) {
		pirateRows = rows;
		((HashTableModel) pirateTable.getModel()).fireTableDataChanged();
	}

	public void copyToClipboard(String text) {
		systemClipboard.setContents(new StringSelection(text), null);
	}

	private String selectedPirateName() {
		int row = pirateTable.getSelectedRow();
		if (row < 0) {
			return null;
		}
		return (String) pirateTable.getValueAt(row, 0);
	}

	/*
	 * Display of text and icons in table cells
	 */
	private TextOrIcon getIcon(String text, String icon) {
		// Try to load from JAR resources first
		URL iconURL = getClass().getResource("/" + icon);
		if (iconURL != null) {
			return new TextOrIcon(text, new ImageIcon(iconURL));
		}
		// Fallback to file system (for development)
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

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
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
				Icon temp = ((TextOrIcon) value).icon;
				setIcon(temp);
				setText(temp != null ? "" : ((TextOrIcon) value).text);
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
	 * Pirate table model - backed by the last snapshot the controller pushed
	 */
	private class HashTableModel extends AbstractTableModel {
		public static final long serialVersionUID = 9L;

		public int getRowCount() {
			return pirateRows.size();
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public Object getValueAt(int row, int column) {
			if (row < 0 || row >= pirateRows.size())
				return new Object();
			PirateRow pirateRow = pirateRows.get(row);
			if (column == 0) // name
				return pirateRow.getName();
			return pirateRow.getStats()[column - 1];
		}
	}

	private class statTableCellRenderer extends DefaultTableCellRenderer {
		public static final long serialVersionUID = 9L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (value instanceof Integer) {
				Integer val = (Integer) value;
				setForeground(Color.BLACK);
				switch (val) {
					case 0:
						cell.setBackground(Color.WHITE);
						break; // Able
					case 1:
						cell.setBackground(Color.LIGHT_GRAY);
						break; // Proficient
					case 2:
						cell.setBackground(Color.GRAY);
						break; // Dis
					case 3:
						cell.setBackground(Color.CYAN);
						break; // Res
					case 4:
						cell.setBackground(new Color(0, 0, 255));
						cell.setForeground(Color.WHITE);
						break; // Mas
					case 5:
						cell.setBackground(Color.GREEN);
						break; // Ren
					case 6:
						cell.setBackground(Color.YELLOW);
						break;// GM
					case 7:
						cell.setBackground(Color.ORANGE);
						break;// Leg
					case 8:
						cell.setBackground(Color.RED);
						break; // Ult
					case PirateRoster.LIST_BLACK:
						cell.setForeground(Color.BLACK);
						cell.setBackground(Color.BLACK);
						break; // Blacklist
					case PirateRoster.LIST_GOLD:
						cell.setForeground(Color.YELLOW);
						cell.setBackground(Color.YELLOW);
						break; // Goldlist
					case PirateRoster.LIST_VOID:
						cell.setForeground(Color.WHITE);
						cell.setBackground(Color.WHITE);
						break; // no list
				}
			} else {
				cell.setForeground(Color.BLACK);
				cell.setBackground(Color.WHITE);
			}
			return cell;
		}
	}

	/*
	 * Handlers (checkbox/button/enter) - forward to the listener, except for
	 * the DC/PI panel toggles, which are pure layout state with no model
	 * involvement.
	 */
	private class panelHandler implements ActionListener {
		private JPanel target;

		panelHandler(JPanel t) {
			target = t;
		}

		public void actionPerformed(ActionEvent e) {
			target.setEnabled(!target.isEnabled());
			drawView();
		}
	}

	private class ExitHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			listener.onExit();
		}
	}

	private class SinkingHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			listener.onSinkingToggled();
		}
	}

	private class LinesHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			listener.onLinesToggled();
		}
	}

	private class CollideHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			listener.onCollide();
		}
	}

	private class UndoHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			listener.onUndo();
		}
	}

	private class ResetHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			listener.onReset();
		}
	}

	private class dcCopyHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			listener.onDcCopyRequested();
		}
	}

	private class piCopyHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String name = selectedPirateName();
			if (name != null) {
				listener.onJobCopyRequested(name);
			}
		}
	}

	private class ShipChangeHandler implements ActionListener {
		private boolean me;
		private JComboBox<String> combobox;

		ShipChangeHandler(boolean m, JComboBox<String> box) {
			me = m;
			combobox = box;
		}

		public void actionPerformed(ActionEvent e) {
			listener.onShipSelected(me, (String) combobox.getSelectedItem());
		}
	}

	private class ShotHandler implements ActionListener {
		private boolean me;

		ShotHandler(boolean m) {
			me = m;
		}

		public void actionPerformed(ActionEvent e) {
			listener.onShot(me);
		}
	}

	private class RockHandler implements ActionListener {
		private boolean me;

		RockHandler(boolean m) {
			me = m;
		}

		public void actionPerformed(ActionEvent e) {
			listener.onHitRocks(me);
		}
	}

	private class EnterHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			listener.onAddPirate(nameTxt.getText());
		}
	}

	private class ClearHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String name = selectedPirateName();
			if (name != null) {
				listener.onDeletePirate(name);
			}
		}
	}

	private class ClearAllHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			listener.onClearAllPirates();
		}
	}

	private class OceanChangeHandler implements ActionListener {
		private JComboBox<String> combobox;

		OceanChangeHandler(JComboBox<String> box) {
			combobox = box;
		}

		public void actionPerformed(ActionEvent e) {
			listener.onOceanChanged((String) combobox.getSelectedItem());
		}
	}

	private class BlackListHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String name = selectedPirateName();
			if (name != null) {
				listener.onToggleBlack(name);
			}
		}
	}

	private class GoldListHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String name = selectedPirateName();
			if (name != null) {
				listener.onToggleGold(name);
			}
		}
	}
}
