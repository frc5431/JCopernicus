package components;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import config.Logger;
import config.StreamConfig;
import streaming.MjpegClient;

public class CameraStream extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BufferedImage frame;
	private int frame_width = 320, frame_height = 240,
			//ratio_width = 320, ratio_height = 240,
			new_width = 320, new_height = 320;
	boolean aspect_ratio = true, good_frame = false;
	
	public StreamConfig stream_config;
	
	public CameraStream (MjpegClient client, StreamConfig stream_config) {
		super(true);
		this.setFocusable(false);
		this.setBounds(new Rectangle(320, 240));
		this.stream_config = stream_config;
		this.aspect_ratio = this.stream_config.aspect_ratio;
		
		client.setFrameHandler(new MjpegClient.Callback() {
			
			@Override
			public void onNewFrame(BufferedImage bitmap) {
				//System.out.println("Got a new frame");
				frame = bitmap;
			}

			@Override
			public void onDisconnect() {
				Logger.Log("Camera stream disconnected!");
				frame = stream_config.disconnected_image;
				
			}

			@Override
			public void onConnect() {
				Logger.Log("Camera stream connected!");
				frame = stream_config.connected_image;
			}
		});
	}
	
	public void setSize(int width, int height) {
		this.setBounds(this.getX(), this.getY(), width, height);
		frame_width = width;
		frame_height = height;
		
		if(!aspect_ratio) {
			new_width = frame_width;
			new_height = frame_height;
			return;
		}
		
		/*
		 * FIX THE ASPECT RATIO
		 * 
		 * if(frame == null) return;
		
		ratio_width = frame.getWidth();
		ratio_height = frame.getHeight();
		
		double im_aspect_ratio = ((double) ratio_width) / ratio_height;
		double im_resize_ratio = ((double) frame_width) / frame_height;
		
		if(im_resize_ratio < im_aspect_ratio) {
			new_width = frame_width;
			new_height = (int) (frame_width / im_resize_ratio);
		} else {
			new_width = (int) (frame_height * im_resize_ratio);
			new_height = frame_height;
		}
		
		//new_width = (int) ((double) ratio_height * resize_ratio);
		//new_height = (int) ((double) ratio_width / resize_ratio);
		*/
		good_frame = true;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		//FIX ASPECT RATIO
		//if(!good_frame) setSize(frame_width, frame_height);
		
		g.drawImage(frame, 0, 0, new_width, new_height, this);
		g.dispose();
	}
}
