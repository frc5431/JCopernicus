package components;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import config.Logger;

public class Footer extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int window_width, window_height, footer_height;
	
	
	public Footer() {
		super("Starting up...", SwingConstants.CENTER);
		this.setBackground(Color.YELLOW);
		this.setOpaque(true);
		setParentSize(1000, 1000);
		setHeight(100);
		invalidateFooter();
		
		Logger.Log("Created the dashboard footer");
	}
	
	public void setParentSize(int width, int height) {
		this.window_width = width;
		this.window_height = height;
	}
	
	public void setHeight(int height) {
		this.footer_height = height;
	}
	
	public void invalidateFooter() {
		this.setBounds(0, (window_height - footer_height), window_width, footer_height);
	}
	
	public void setColor(Color color) {
		this.setBackground(color);
	}
}
