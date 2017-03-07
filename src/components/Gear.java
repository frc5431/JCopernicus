package components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import config.Resources;
import config.StreamConfig;

public class Gear extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int width, height;
	private BufferedImage gear_image;
	
	public Gear(StreamConfig stream_config) {
		super(true);
		this.setFocusable(false);
		this.setBackground(new Color(0,0,0,0));
		this.setBounds(new Rectangle(320, 240));
		
		this.gear_image = Resources.getBufferedResource("gear"); //stream_config.gear_image;
	}
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public void setGearState(boolean state) {
		this.setVisible(state);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		g.drawImage(this.gear_image, 0, 0, this.width, this.height, this);
		g.dispose();
	}
}
