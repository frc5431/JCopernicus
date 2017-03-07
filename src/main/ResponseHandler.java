package main;

import java.awt.Color;

import config.Logger;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import streaming.JCopernicus;

public class ResponseHandler implements ITableListener {
	
	private static final String
		connection_tag = "connection",
		gear_in_tag = "gearIn";
	
	public ResponseHandler() {
		JCopernicus.table.addTableListener(this);
		
		Logger.Log("Created the response handler");
	}
	
	public void loadDefaults() {
		boolean connectionState = JCopernicus.getBoolean(connection_tag, false);
		boolean gearState = JCopernicus.getBoolean(gear_in_tag, false);
	
		ConnectionHandler(connectionState);
		GearHandler(gearState);
	}
	
	private void GearHandler(boolean state) {
		CopernicusDashboard.serial_handler.write("S:G:E");
		CopernicusDashboard.gear.setGearState(state);
		
		Logger.Log("Got a new gear state: " + String.valueOf(state));
	}
	
	private void ConnectionHandler(boolean state) {
		CopernicusDashboard.footer.setText((state) ? "Connected" : "Disconnected");
		CopernicusDashboard.footer.setBackground((state) ? Color.GREEN : Color.RED);
		
		Logger.Log("Got a new connection state: " + String.valueOf(state));
	}

	@Override
	public void valueChanged(ITable itable, String key, Object value, boolean newvalue) {
		switch(key) {
		case connection_tag:
			ConnectionHandler((boolean) value);
			break;
		case gear_in_tag:
			GearHandler((boolean) value);
			break;
		}
	}
}
