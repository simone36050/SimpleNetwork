package it.simone36050.network;

import java.io.IOException;
import java.io.InputStream;

public class NetworkInputStream {
	
	private InputStream input;
	private int length = -1;
	private byte[] data = null;
	
	public NetworkInputStream(InputStream input) {
		this.input = input;
	}
	
	public boolean read() {
		boolean worked = false;
		
		try {			
			// 1. data length
			if(length == -1 && input.available() >= 2) {
				worked = true;
				
				// read length
				byte[] bb = new byte[2];
				input.read(bb);
				length = NetworkUtils.getShort(bb, 0);
				
				// check read value
				if(length < 2)
					throw new WrongPacketSizeException("Length " + length + " is less than 2");
			}
			
			// 2. read data
			if(length != -1 && input.available() >= length) {
				worked = true;
				
				// create and read data
				data = new byte[length];
				input.read(data);
			}
		} catch (IOException e) {
			throw new InputIOException(e);
		} catch (WrongPacketSizeException e) {
			throw (WrongPacketSizeException)e.fillInStackTrace();
		} catch (Exception e) {
			throw new NetworkException("Unrecognized exception", e);
		}
		
		return worked;
	}
	
	public byte[] getData() {
		byte[] d = null; 
		if(data != null) {
			length = -1;
			d = data;
			data = null;
		}
		return d;
	}
	
	public static class NetworkInputStreamException extends NetworkException {

		private static final long serialVersionUID = -8191494279127954196L;
		public NetworkInputStreamException() { super(); }
		public NetworkInputStreamException(String message) { super(message); }
		public NetworkInputStreamException(String message, Throwable cause) { super(message, cause); }
		public NetworkInputStreamException(Throwable cause) { super(cause); }
		
	}
	
	public static class InputIOException extends NetworkInputStreamException {

		private static final long serialVersionUID = 3136966077346175879L;
		public InputIOException() { super(); }
		public InputIOException(String message) { super(message); }
		public InputIOException(String message, Throwable cause) { super(message, cause); }
		public InputIOException(Throwable cause) { super(cause); }
		
	}
	
	public static class WrongPacketSizeException extends NetworkInputStreamException {

		private static final long serialVersionUID = 2686527213099214369L;
		public WrongPacketSizeException() { super(); }
		public WrongPacketSizeException(String message) { super(message); }
		public WrongPacketSizeException(String message, Throwable cause) { super(message, cause); }
		public WrongPacketSizeException(Throwable cause) { super(cause); }
		
	}
	
}
