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

import nl.unreadable.YPPPP.model.PirateRank;
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

	// Single source of truth for the pirate table's columns: their order,
	// header icon, and icon label. A column's position here is its position
	// in both the table and PirateRow.getStats() (offset by 1 for "Name").
	private static final Column[] COLUMNS = {
			new Column("Name", "icons/name.png"),
			new Column("Gun", "icons/gun.png"),
			new Column("Bilge", "icons/bilge.png"),
			new Column("Sail", "icons/sail.png"),
			new Column("Rig", "icons/rig.png"),
			new Column("Carp", "icons/carp.png"),
			new Column("Patch", "icons/patch.png"),
			new Column("SF", "icons/sf.png"),
			new Column("Rumble", "icons/rumble.png"),
			new Column("Dnav", "icons/dnav.png"),
			new Column("Bnav", "icons/bnav.png"),
			new Column("TH", "icons/th.png"),
			new Column("For", "icons/forage.png"),
			new Column("?", "icons/list.png"),
	};

	private static final class Column {
		final String iconLabel;
		final String iconPath;

		Column(String iconLabel, String iconPath) {
			this.iconLabel = iconLabel;
			this.iconPath = iconPath;
		}
	}

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
			col.setPreferredWidth(cnt == 0 ? 100 : 10);
			col.setHeaderValue(getIcon(COLUMNS[cnt].iconLabel, COLUMNS[cnt].iconPath));
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
			return COLUMNS.length;
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
				if (val == PirateRoster.LIST_BLACK) {
					cell.setForeground(Color.BLACK);
					cell.setBackground(Color.BLACK);
				} else if (val == PirateRoster.LIST_GOLD) {
					cell.setForeground(Color.YELLOW);
					cell.setBackground(Color.YELLOW);
				} else if (val == PirateRoster.LIST_VOID) {
					cell.setForeground(Color.WHITE);
					cell.setBackground(Color.WHITE);
				} else {
					// val is a PirateRank ordinal, not a raw magic number, so a
					// newly inserted rank can't silently desync this switch the
					// way it did before PirateRank existed.
					switch (PirateRank.values()[val]) {
						case ABLE:
							cell.setBackground(Color.WHITE);
							break;
						case PROFICIENT:
							cell.setBackground(Color.LIGHT_GRAY);
							break;
						case DISTINGUISHED:
							cell.setBackground(Color.GRAY);
							break;
						case RESPECTED:
							cell.setBackground(Color.CYAN);
							break;
						case MASTER:
							cell.setBackground(new Color(0, 0, 255));
							cell.setForeground(Color.WHITE);
							break;
						case RENOWNED:
							cell.setBackground(Color.GREEN);
							break;
						case GRAND_MASTER:
							cell.setBackground(Color.YELLOW);
							break;
						case LEGENDARY:
							cell.setBackground(Color.ORANGE);
							break;
						case ULTIMATE:
							cell.setBackground(Color.RED);
							break;
					}
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
