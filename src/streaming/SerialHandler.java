package streaming;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import config.Logger;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class SerialHandler {
	public static final int serial_timeout = 2000; //Serial timeout in milliseconds
	
	private Callback serialHandler;
	private boolean initialized = false;
	private CommPort serial_comm;
	private SerialPort serial_port;
	private OutputStream serial_out;
	
	public SerialHandler(String serial_port_id, int serial_baud) {
		Thread connection_thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					CommPortIdentifier portIdentifier;
					
					try {
						portIdentifier = CommPortIdentifier.getPortIdentifier(serial_port_id);
					} catch (NoSuchPortException e) {
						e.printStackTrace();
						Logger.Log("Couldn't find serial port " + serial_port_id, true);
						sleep(500);
						continue;
					}
					
					if(portIdentifier.isCurrentlyOwned()) {
						Logger.Log("The serial port " + serial_port_id + " is currently being used", true);
						sleep(1000);
						continue;
					}
					
					try {
						serial_comm = portIdentifier.open(this.getClass().getName(), serial_timeout);
					} catch (PortInUseException e) {
						Logger.Log("Fatal uncaught usage on port " + serial_port_id, true);
						e.printStackTrace();
						sleep(1000);
						continue;
					}
					
					if(serial_comm instanceof SerialPort) {
						serial_port = (SerialPort) serial_comm;
						try {
							serial_port.setSerialPortParams(serial_baud,
									SerialPort.DATABITS_8, 
									SerialPort.STOPBITS_1, 
									SerialPort.PARITY_NONE);
						} catch (UnsupportedCommOperationException e) {
							Logger.Log("Failed to set serial settings on port " + serial_port_id, true);
							e.printStackTrace();
							sleep(500);
							continue;
						}
						
						InputStream serial_in;
						
						try {
							serial_in = serial_port.getInputStream();
						} catch (IOException e) {
							Logger.Log("Failed to get the serial inputstream on port " + serial_port_id, true);
							e.printStackTrace();
							sleep(1000);
							continue;
						}
						
						try {
							serial_out = serial_port.getOutputStream();
						} catch (IOException e) {
							Logger.Log("Failed to get the serial outputstream on port " + serial_port_id, true);
							e.printStackTrace();
							sleep(1000);
							continue;
						}
						
						//Start the serial reader thread
						(new Thread( new SerialReader(serial_in))).start();
						
						break;
					} else {
						Logger.Log("The current device is not a serial port! " + serial_port_id, true);
						sleep(1000);
					}
					
					Logger.Log("Failed to connect to " + serial_port_id + " Retrying...", true);
					
				}
				
				initialized = true;
				Logger.Log("Succesfully connected to " + serial_port_id);
			}
			
			private void sleep(long sleep_time) {
				try {
					Thread.sleep(sleep_time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		connection_thread.setDaemon(true);
		connection_thread.start();
		
		Logger.Log("Started serial connection thread for " + serial_port);
	}
	
	public interface Callback {
		public void onSerialRead(String serialResponse);
	}
	
	public void attachSerialHandler(Callback callback) {
		this.serialHandler = callback;
	}
	
	public void write(String toWrite) {
		if(!this.initialized) {
			Logger.Log("Couldn't write " + toWrite + " to serial device because it's not connected", true);
			return;
		}
		
		try {
			serial_out.write(toWrite.getBytes());
		} catch (IOException e) {
			Logger.Log("Failed writing to serial port", true);
			e.printStackTrace();
		}
	}
	
	public class SerialReader implements Runnable {
		InputStream serial_in;
		
		public SerialReader(InputStream in) {
			this.serial_in = in;
		}
		
		@Override
		public void run() {
			byte[] buffer = new byte[1024];
			int len = -1;
			try {
				while((len = this.serial_in.read(buffer)) > -1) {
					String readBuffer = new String(buffer, 0, len);
					serialHandler.onSerialRead(readBuffer);
					Logger.Log("Read from serial: " + readBuffer);
				}
			} catch (IOException err) {
				err.printStackTrace();
				Logger.Log("Failed to read from serial!");
			}
		}
	}
}
