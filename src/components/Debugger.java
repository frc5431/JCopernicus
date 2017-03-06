package components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import config.Logger;
import config.StreamConfig;
import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;
import edu.wpi.first.wpilibj.tables.ITable;

public class Debugger extends JScrollPane {
	
	private static final long serialVersionUID = 1L;

	private JList<String> table_debug;
	private ArrayList<String> keys;
	private ArrayList<String> values;
	
	private StreamConfig stream_config;
	
	private int window_width = 1000, width = 100, height = 900;
	
	public boolean hidden = true;
	
	public Debugger(StreamConfig stream_config) {
		super();
		this.setFocusable(false);
		this.setBounds(0, 0, 200, 200);
		
		this.table_debug = new JList<String>(new String[] {"Not connected"});
		this.keys = new ArrayList<String>();
		this.values = new ArrayList<String>();
		
		this.stream_config = stream_config;
		
		//DebugCellRenderer cell_renderer = new DebugCellRenderer();
		//this.table_debug.setCellRenderer(cell_renderer);
		
		this.add(this.table_debug);
		this.add(new JLabel("TEST"));
		
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.setVisible(false);
		
		NetworkTablesJNI.addEntryListener("/" + stream_config.table_name, new NetworkTablesJNI.EntryListenerFunction() {
			@Override
			public void apply(int uid, String key, Object value, int flags) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						getProcessedTree(key, value, flags);
					}
				});
			}},
				ITable.NOTIFY_IMMEDIATE | ITable.NOTIFY_LOCAL | ITable.NOTIFY_NEW | ITable.NOTIFY_DELETE | ITable.NOTIFY_UPDATE /*| ITable.NOTIFY_FLAGS*/ );
		
		
		Logger.Log("Created the debugger window");
	}

	private void getProcessedTree(final String fullKey, Object value, int flags) {
		ArrayList<String> results = new ArrayList<String>();
		
		for(String subtree : fullKey.split("/")) {
			if(subtree.length() > 0) {
				results.add(subtree);
			}
		}
		
		String key = "";
		
		for(int ind = 0; ind < results.size(); ind++) {
			String name = results.get(ind);
			
			key += "/" + name;
			
			if(key.startsWith("/" + this.stream_config.table_name)) {
				key = key.substring(("/" + this.stream_config.table_name).length());
			}
			
			if(!key.startsWith("/")) continue;
			else key = key.substring(1);
			
			if((flags & ITable.NOTIFY_DELETE) != 0) {
				if(this.keys.size() - 1 < ind) {
					break;
				} else if(this.keys.size() -1 == ind){
					this.keys.remove(ind);
					this.values.remove(ind);
					break;
				}
				
				continue;
			}
			
			if(hasCurrentKey(key)) {
				this.values.set(this.getCurrentKeyIndex(key), (String) value);
			} else {
				this.keys.add(key);
				this.values.add((String) value);
			}
		}
		
		DefaultListModel<String> model = new DefaultListModel<String>();
		
		for(int ind = 0; ind < this.keys.size(); ind++) {
			model.addElement(this.keys.get(ind) + " : " + this.values.get(ind));
		}
		
		this.table_debug.setModel(model);
	}
	
	private boolean hasCurrentKey(String name) {
		for(String key : this.keys) {
			if(name.equals(key)) return true;
		}
		return false;
	}
	
	private int getCurrentKeyIndex(String name) {
		for(int ind = 0; ind < this.keys.size(); ind++) {
			if(this.keys.get(ind).equals(name)) return ind;
		}
		return -1;
	}
	
	
	public void setSize(int window_width, int width, int height) {
		this.width = width;
		this.height = height;
		this.window_width = window_width;
	}
	
	public void invalidateDebugger() {
		this.setBounds((this.window_width - this.width), 0, this.width, this.height);
		
		this.table_debug.setBounds(0, 0, this.width, this.height);
		this.table_debug.setPreferredSize(new Dimension(this.width, this.height));
		//this.table_debug.setFixedCellWidth(this.width);
		//this.table_debug.setFixedCellWidth(width);
		
		//int button_height = height / 20;
		
		//this.hide_button.setBounds(0, (this.height - button_height), this.width, button_height);
	}
}

class DebugCellRenderer implements ListCellRenderer<Object> {

    private final JLabel jlblCell = new JLabel(" ", JLabel.LEFT);
    Border lineBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
    Border emptyBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);

    @Override
    public Component getListCellRendererComponent(JList<?> jList, Object value, 
            int index, boolean isSelected, boolean cellHasFocus) {

        jlblCell.setOpaque(true);

        if (isSelected) {
            jlblCell.setForeground(jList.getSelectionForeground());
            jlblCell.setBackground(jList.getSelectionBackground());
            jlblCell.setBorder(new LineBorder(Color.BLUE));
        } else {
            jlblCell.setForeground(jList.getForeground());
            jlblCell.setBackground(jList.getBackground());
        }

        jlblCell.setBorder(cellHasFocus ? lineBorder : emptyBorder);

        return jlblCell;
    }
}


