package components;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import config.Logger;

public class Debugger extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private JList<Object> table_debug;
	//private JButton hide_button;
	
	private int window_width = 1000, width = 100, height = 900;
	
	public boolean hidden = true;
	
	public Debugger() {
		super();
		this.setFocusable(false);
		this.setBounds(0, 0, 200, 200);
		
		this.table_debug = new JList<Object>(new String[] {"okay", "two"});
		
		DebugCellRenderer cell_renderer = new DebugCellRenderer();
		this.table_debug.setCellRenderer(cell_renderer);
		
		/*this.hide_button = new JButton("Hide >>>");
		this.hide_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//setVisible(false);
				Logger.Log("Hide button pressed");
			}
			
		});*/
		
		this.add(this.table_debug);
		//this.add(this.hide_button);
		this.setVisible(false);
		
		Logger.Log("Created the debugger window");
	}
	
	public void setSize(int window_width, int width, int height) {
		this.width = width;
		this.height = height;
		this.window_width = window_width;
	}
	
	public void invalidateDebugger() {
		this.setBounds((this.window_width - this.width), 0, this.width, this.height);
		
		this.table_debug.setBounds(0, 0, this.width, (this.height / 2));
		this.table_debug.setFixedCellWidth(this.width);
		
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


