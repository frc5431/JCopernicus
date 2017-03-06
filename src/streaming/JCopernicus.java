package streaming;

import config.Logger;
import config.StreamConfig;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class JCopernicus {
	public static NetworkTable table;
	public static StreamConfig stream_config;
	
	public static void init(StreamConfig stream_c) {
		
		stream_config = stream_c;
		
		Logger.Log("Attempting to start a NetworkTable client at ip " + stream_config.robot_ip);
		NetworkTable.setClientMode();
		NetworkTable.setIPAddress(stream_config.robot_ip);
		table = NetworkTable.getTable(stream_config.table_name);
		
		Logger.Log("Started the Network Table client!");
	}
	
	public static void putString(String key, String value) {
		table.putString(key, value);
	}
	
	public static String getString(String key, String defaultValue) {
		return table.getString(key, defaultValue);
	}
	
	public static void putNumber(String key, double value) {
		table.putNumber(key, value);
	}
	
	public static double getNumber(String key, double defaultValue) {
		return table.getNumber(key, defaultValue);
	}
	
	public static void putBoolean(String key, boolean value) {
		table.putBoolean(key, value);
	}
	
	public static boolean getBoolean(String key, boolean defaultValue) {
		return table.getBoolean(key, defaultValue);
	}
}
