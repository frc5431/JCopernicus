package components;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import config.AutoConfig;
import config.Logger;

public class AutoChooser extends JComboBox<Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int width, height, y_position;
	
	public AutoChooser() {
		super(new String[] {});
		setPosition(100, 100, 100);
		invalidateAutoChooser();
		
		Logger.Log("Created the auto chooser");
	}
	
	public void setAutoConfigs(AutoConfig auto_config) {
		List<String> blue_list = auto_config.blueAutos;
		List<String> red_list = auto_config.redAutos;
		List<String> both_list = auto_config.bothAutos;
		
		List<String> auto_list = new ArrayList<String>();
		auto_list.addAll(both_list);
		auto_list.addAll(blue_list);
		auto_list.addAll(red_list);
		
		Color[] colors = new Color[auto_config.totalAutos];
		String[] autos = new String[auto_config.totalAutos];
		
		for(int ind = 0; ind < auto_config.totalAutos; ind++) {
			String current_auto = auto_list.get(ind).toLowerCase();
			autos[ind] = auto_list.get(ind);
			if(current_auto.contains("blue")) {
				colors[ind] = Color.decode("#1A237E");
			} else if(current_auto.contains("red")) {
				colors[ind] = Color.decode("#B71C1C");
			} else {
				colors[ind] = Color.decode("0x004D40");
			}
		}
		
		ComboBoxRenderer renderer = new ComboBoxRenderer(this);
		
		this.setModel(new DefaultComboBoxModel<Object>(autos));
		
		renderer.setColors(colors);
		renderer.setStrings(autos);
		
		this.setRenderer(renderer);
	}
	
	public void setPosition(int width, int height, int y_position) {
		this.width = width;
		this.height = height;
		this.y_position = y_position;
	}
	
	public void invalidateAutoChooser() {
		this.setBounds(0, this.y_position, width, height);
	}
	
	public void setColor(Color color) {
		this.setBackground(color);
	}
	
	public String getSelected() {
		return (String) this.getSelectedItem();
	}
}

class ComboBoxRenderer extends JPanel implements ListCellRenderer<Object> {

	private static final long serialVersionUID = -1L;
	private Color[] colors;
	private String[] strings;
	
	JPanel textPanel;
	JLabel text;
	
	public ComboBoxRenderer(JComboBox<Object> combo) {
		textPanel = new JPanel();
		textPanel.add(this);
		text = new JLabel();
		text.setOpaque(true);
		text.setFont(combo.getFont());
		textPanel.add(text);
	}
	
	public void setColors(Color[] col) {
		colors = col;
	}
	
	public void setStrings(String[] str) {
		strings = str;
	}
	
	public Color[] getColors() {
		return colors;
	}
	
	public String[] getStrings() {
		return strings;
	}

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value,
    		int index, boolean isSelected, boolean cellHasFocus) {
    	
		if (colors.length != strings.length) {
		    Logger.Log("AutoSelector combo box colors and strings are not matched", true);
		    return this;
		} else if (colors == null) {
		    Logger.Log("AutoChooser colors are not set");
		    return this;
		} else if (strings == null) {
		    Logger.Log("AutoChooser strings are not set");
		    return this;
		}
		
		if (isSelected || index <= -1){
			setBackground(Color.WHITE);
			text.setBackground(Color.BLACK);
		} else if(index > -1) {
			setBackground(colors[index]);
			text.setBackground(colors[index]);
		}
		
		text.setText(value.toString());
		text.setForeground(Color.WHITE); //colors[index]);
		
		return text;
    }
}