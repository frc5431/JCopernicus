package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import components.AutoChooser;
import components.CameraStream;
import components.Debugger;
import components.Dialogs;
import components.Footer;
import config.AutoConfig;
import config.Configs;
import config.Logger;
import config.Resources;
import config.StreamConfig;
import config.WindowConfig;
import streaming.JCopernicus;
import streaming.MjpegClient;

public class CopernicusDashboard {
	
	public static final String dashboard_name = "Copernicus";
	public static final String log_location = "logs/";
	public static final String log_file = "copernicus-log.txt";
	public static final String auto_select_tag = "autoSelected";
	
	private static final Executor executor = Executors.newCachedThreadPool();
	
	private static int window_x, window_y;
	
	private static JFrame frame;
	private static JPanel camera_stream;
	private static Footer footer;
	private static Debugger debug;
	private static JButton show_hide;
	
	public static AutoConfig auto_config;
	public static AutoChooser auto_chooser;
	
	/*
	 * Called whenever the window is initialized or resized
	 */
	public static void organizeWindow() {
		window_x = frame.getWidth();
		window_y = frame.getHeight();
		camera_stream.setSize(window_x, window_y);
		
		footer.setParentSize(window_x, window_y);
		footer.setHeight((window_y / 15));
		footer.invalidateFooter();
		
		debug.setSize(window_x, (window_x / 10), window_y);
		debug.invalidateDebugger();
		
		int button_height = window_y / 15;
		
		show_hide.setBounds(window_x - (window_x / 10), (window_y - button_height), (window_x / 10), button_height);
		
		auto_chooser.setPosition((window_x / 6), 25, (window_y / 2) + (window_y / 4));
		auto_chooser.invalidateAutoChooser();
	}
	
	
	public static void main(String[] args) {	
		//Start logger and load the resource images
		Logger.init();		
		Resources.init();
		
		final DateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH-mm");
		final String log_file_path = log_location + date_format.format(new Date()) + CopernicusDashboard.log_file;
		final File file = new File(log_file_path);
		Logger.Log("Attempting to tee output stream to " + log_file_path);
		
		//Create a new log file if it doesn't exist
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException err) {
				err.printStackTrace();
				Logger.Log("Failed creating a new log file");
			}
		}
		
		try {
			FileOutputStream teeOut = new FileOutputStream(file);
			Logger.CopernicusTeePrintStream teePrintStream = new Logger.CopernicusTeePrintStream(teeOut, System.out);
			System.setOut(teePrintStream);
			System.setErr(teePrintStream);
		} catch (IOException err) {
			err.printStackTrace();
			Logger.Log("Failed creating the file tee logger output stream!");
		}
		
		/*
		 * Load from the config files
		 */
		try {
			Configs.init();
		} catch(IOException err) {
			err.printStackTrace();
			Dialogs.ErrorDialog("Copernicus Config Error", 
					"Couldn't load the driverstation configuration. "
					+ "Copernicus requires a config.json");
		}
		
		/*
		 * Change the look and feel
		 */
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException err) {
			err.printStackTrace();
			Dialogs.ErrorDialog("Copernicus Look and Feel Error", 
					"Copernicus couldn't change the Look and Feel of the dashboard");
		}
		
		//Load the JSON config file into the configuration objects
		WindowConfig window_config = Configs.loadWindowConfig();
		StreamConfig stream_config = Configs.loadStreamConfig();
		
		auto_config = Configs.loadAutoConfig();
		
		//Start the JCopernicus network manager
		JCopernicus.init(stream_config);
		
		//Attach a Mjpeg client to the dashboard
		MjpegClient client = new MjpegClient(stream_config.camera_url);
	
		//Create a new camera stream JPanel for the frame
		camera_stream = new CameraStream(client, stream_config);

		//Start the Mjpeg Client thread
		client.start();
		
		//Create the dashboard footer
		footer = new Footer();
		
		//Create the dashboard auto chooser
		auto_chooser = new AutoChooser();
		auto_chooser.setAutoConfigs(auto_config);
		
		//Create the dashboard debugging window
		debug = new Debugger();
		
		show_hide = new JButton("Show");
		show_hide.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(debug.hidden) {
					debug.setVisible(true);
					show_hide.setText("Hide");
					debug.hidden = false;
				} else {
					debug.setVisible(false);
					show_hide.setText("Show");
					debug.hidden = true;
				}
				Logger.Log("Show/Hide button was pressed");
			}
			
		});
		
		/*
		 * Load the dashboard JFrame
		 */
		
		frame = new JFrame(dashboard_name);
		frame.setSize(window_config.width, window_config.height);
		frame.setLocation(window_config.x_location, window_config.x_location);
		frame.setIconImage(Resources.getResource(window_config.icon_name));
		frame.setResizable(window_config.resizable);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setVisible(true);
		
		//Set the dialogs icon resource
		Dialogs.icon_resource = window_config.icon_name;
		
		/*
		 * Load the dashboard on a specific screen
		 */
		
		
		frame.addComponentListener(new ComponentListener() {

			@Override
			public void componentResized(ComponentEvent e) {
				organizeWindow();
				Logger.Log("Dashboard resized " + window_x + "x" + window_y);
			}

			@Override
			public void componentMoved(ComponentEvent e) {}

			@Override
			public void componentShown(ComponentEvent e) {}

			@Override
			public void componentHidden(ComponentEvent e) {}
			
		});
		
		
		auto_chooser.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					JCopernicus.putString(auto_select_tag, auto_chooser.getSelected());
					Logger.Log("Selected a new autonomous " + auto_chooser.getSelected());
				}
			}
		});
		
		executor.execute(new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						sleep(window_config.refresh_millis);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					try {
						frame.repaint();
					} catch(Exception err) {
						Logger.Log("Failed repainting", true);
					}
				}
			}
		});
		
		frame.add(show_hide);
		frame.add(debug);
		frame.add(footer);
		frame.add(auto_chooser);
		frame.add(camera_stream);
		
		JCopernicus.putString(auto_select_tag, auto_chooser.getSelected());
		Logger.Log("Selected a new autonomous " + auto_chooser.getSelected());
		
		organizeWindow();
	}
}
