package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Configs {

	public static final String load_config = "config.json";
	private static JSONObject loaded_config = null;
	
	public static void init() throws IOException {
		Logger.Log("Loading config from " + load_config);
		try {
			File config_file = new File(load_config);
			InputStream in = new FileInputStream(config_file);
			loaded_config = new JSONObject(new JSONTokener(in));
		} catch(IOException | JSONException err) {
			err.printStackTrace();
			Logger.Log("Failed loading dashboard configurations!", true);
			throw new IOException("Failed loading the config file!");
		}
		Logger.Log("Finished loading dashboard configurations!");
	}
	
	public static JSONObject getWindowConfig() {
		return loaded_config.getJSONObject("window");
	}
	
	public static JSONObject getStreamConfig() {
		return loaded_config.getJSONObject("streams");
	}
	
	public static JSONObject getDashboardConfig() {
		return loaded_config.getJSONObject("dashboard");
	}
	
	public static JSONObject getAutoConfig() {
		return loaded_config.getJSONObject("autons");
	}
	
	public static JSONArray getBlueAutoConfig(JSONObject autoConfig) {
		return autoConfig.getJSONArray("blue");
	}
	
	public static JSONArray getRedAutoConfig(JSONObject autoConfig) {
		return autoConfig.getJSONArray("red");
	}
	
	public static JSONArray getBothAutoConfig(JSONObject autoConfig) {
		return autoConfig.getJSONArray("both");
	}
	
	public static WindowConfig loadWindowConfig() {
		JSONObject json_config = getWindowConfig();
		
		WindowConfig window_config = new WindowConfig();
		window_config.width = getInt(json_config, "width", 1000);
		window_config.height = getInt(json_config, "height", 1000);
		window_config.icon_name = getString(json_config, "icon_name", "logo");
		window_config.resizable = getBoolean(json_config, "resizable", true);
		window_config.x_location = getInt(json_config, "x_location", 0);
		window_config.y_location = getInt(json_config, "y_location", 0);
		window_config.refresh_millis = getInt(json_config, "refresh_millis", 0);
		return window_config;
	}
	
	public static StreamConfig loadStreamConfig() {
		JSONObject json_config = getStreamConfig();
		
		StreamConfig stream_config = new StreamConfig();
		stream_config.camera_url = getString(json_config, "camera_url", "http://10.54.31.25/mjpg/video.mjpg");
		stream_config.robot_ip = getString(json_config, "robot_ip", "roborio-5431-frc.local");
		stream_config.table_name = getString(json_config, "table_name", "copernicus");
		stream_config.aspect_ratio = getBoolean(json_config, "aspect_ratio", true);
		stream_config.connected_image = Resources.getBufferedResource(getString(json_config, "connected_image", "connected"));
		stream_config.disconnected_image = Resources.getBufferedResource(getString(json_config, "disconnected_image", "disconnected"));
		stream_config.gear_image = Resources.getBufferedResource(getString(json_config, "gear_image", "gear"));
		return stream_config;
	}
	
	public static DashboardConfig.Leds loadDashboardLedConfig() {
		JSONObject json_config = getDashboardConfig().getJSONObject("leds");
		
		DashboardConfig.Leds led_config = new DashboardConfig.Leds();
		led_config.serial_port = getString(json_config, "serial_port", "COM4");
		led_config.serial_baud = getInt(json_config, "serial_baud", 9600);
		
		return led_config;
	}
	
	public static AutoConfig loadAutoConfig() {
		JSONObject json_config = getAutoConfig();
		
		JSONArray blue_autos = getBlueAutoConfig(json_config);
		JSONArray red_autos = getRedAutoConfig(json_config);
		JSONArray both_autos = getBothAutoConfig(json_config);
		
		AutoConfig auto_config = new AutoConfig();
		
		ArrayList<String> 
			blue_names = new ArrayList<String>(), 
			red_names = new ArrayList<String>(), 
			both_names = new ArrayList<String>();
		
		int total_count = 0;
		
		for(int ind = 0; ind < 3; ind++) {
			
			JSONArray current_auto_group = (ind == 0) ? blue_autos : (ind == 1) ? red_autos : both_autos;
			
			for(int j_i = 0; j_i < current_auto_group.length(); j_i++) {
				JSONObject current_auto = current_auto_group.getJSONObject(j_i);
				String auto_name = current_auto.getString("name");
				
				
				if(ind == 0) {
					blue_names.add(auto_name);
				} else if(ind == 1) {
					red_names.add(auto_name);
				} else {
					both_names.add(auto_name);
				}
				
				total_count++;
			}
		}
		
		auto_config.blueAutos = blue_names;
		auto_config.redAutos = red_names;
		auto_config.bothAutos = both_names;
		auto_config.totalAutos = total_count;
		
		try {
			auto_config.defaultAuto = getString(json_config, "default", both_names.get(0));
		} catch(Exception err) {
			err.printStackTrace();
			Logger.Log("Failed setting default autonomous");
			auto_config.defaultAuto = "StandStill";
		}
		
		return auto_config;
	}
	
	public static String getString(JSONObject configs, String key, String defaultValue) {
		if(configs.has(key)) {
			try {
				return configs.getString(key);
			} catch(JSONException err) {
				return defaultValue;
			}
		}
		Logger.Log("Failed loading string key " + key + " from config");
		return defaultValue;
	}

	public static int getInt(JSONObject configs, String key, int defaultValue) {
		if(configs.has(key)) {
			try {
				return configs.getInt(key);
			} catch(JSONException err) {
				return defaultValue;
			}
		}
		Logger.Log("Failed loading integer key " + key + " from config");
		return defaultValue;
	}

	public static double getDouble(JSONObject configs, String key, double defaultValue) {
		if(configs.has(key)) {
			try {
				return configs.getDouble(key);
			} catch(JSONException err) {
				return defaultValue;
			}
		}
		Logger.Log("Failed loading double key " + key + " from config");
		return defaultValue;
	}
	
	public static boolean getBoolean(JSONObject configs, String key, boolean defaultValue) {
		if(configs.has(key)) {
			try {
				return configs.getBoolean(key);
			} catch(JSONException err) {
				return defaultValue;
			}
		}
		Logger.Log("Failed loading boolean key " + key + " from config");
		return defaultValue;
	}
	
}
