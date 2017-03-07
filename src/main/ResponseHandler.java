package main;

import java.awt.Color;

import config.Logger;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import streaming.JCopernicus;

public class ResponseHandler implements ITableListener {
	
	public ResponseHandler() {
		JCopernicus.table.addTableListener(this);
		
		Logger.Log("Created the response handler");
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
		case "gearIn":
			GearHandler((boolean) value);
			break;
		case "connection":
			ConnectionHandler((boolean) value);
			break;
		}
	}
}
