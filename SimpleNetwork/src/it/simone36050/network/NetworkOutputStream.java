package it.simone36050.network;

import java.io.IOException;
import java.io.OutputStream;

public class NetworkOutputStream {
	
	private OutputStream output;
	
	public NetworkOutputStream(OutputStream output) {
		this.output = output;
	}
	
	public void write(byte[] data) {
		try {
			// checks
			if(data.length > Short.MAX_VALUE) 
				throw new WrongDataSizeException("Length " + data.length + " is bigger than " + data.length);
			
			// write length
			byte[] length = new byte[2];
			NetworkUtils.setShort((short)data.length, length, 0);
			output.write(length);
			
			// write data
			output.write(data);
		} catch (IOException e) {
			throw new OutputIOException(e);
		} catch (WrongDataSizeException e) {
			throw (WrongDataSizeException)e.fillInStackTrace();
		} catch (Exception e) {
			throw new NetworkOutputStreamException("Unrecognized exception", e);
		}
	}
	
	public void flush() {
		try {
			// flush
			output.flush();
		} catch (IOException e) {
			throw new OutputIOException(e);
		} catch (Exception e) {
			throw new NetworkOutputStreamException("Unrecognized exception", e);
		}
	}
	
	public static class NetworkOutputStreamException extends NetworkException {

		private static final long serialVersionUID = 2028496086830322207L;
		public NetworkOutputStreamException() { super(); }
		public NetworkOutputStreamException(String message) { super(message); }
		public NetworkOutputStreamException(String message, Throwable cause) { super(message, cause); }
		public NetworkOutputStreamException(Throwable cause) { super(cause); }
		
	}
	
	public static class OutputIOException extends NetworkOutputStreamException {

		private static final long serialVersionUID = -595988820597985292L;
		public OutputIOException() { super(); }
		public OutputIOException(String message) { super(message); }
		public OutputIOException(String message, Throwable cause) { super(message, cause); }
		public OutputIOException(Throwable cause) { super(cause); }
		
	}
	
	public static class WrongDataSizeException extends NetworkOutputStreamException {

		private static final long serialVersionUID = 1952143065240267599L;
		public WrongDataSizeException() { super(); }
		public WrongDataSizeException(String message) { super(message); }
		public WrongDataSizeException(String message, Throwable cause) { super(message, cause); }
		public WrongDataSizeException(Throwable cause) { super(cause); }
		
	}
	
}
