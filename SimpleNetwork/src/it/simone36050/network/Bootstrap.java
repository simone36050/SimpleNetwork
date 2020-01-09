package it.simone36050.network;

import java.io.IOException;
import java.net.Socket;

public class Bootstrap {
	
	private String host = null;
	private int port = -1;
	private Protocol protocol = null;
	private Handler handler = null;
	private boolean daemon = false;
	
	public Bootstrap host(String host) {
		this.host = host;
		return this;
	}
	
	public Bootstrap port(int port) {
		this.port = port;
		return this;
	}
	
	public Bootstrap protocol(Protocol protocol) {
		this.protocol = protocol;
		return this;
	}
	
	public Bootstrap handler(Handler handler) {
		this.handler = handler;
		return this;
	}
	
	public Bootstrap daemon() {
		return daemon(true);
	}
	
	public Bootstrap daemon(boolean daemon) {
		this.daemon = daemon;
		return this;
	}
	
	public Connection connect() {
		// checks
		if(host == null)
			throw new BootstrapException("Host is null");
		if(port == -1)
			throw new BootstrapException("Port is not set");
		if(protocol == null)
			throw new BootstrapException("Protocol is null");
		if(handler == null)
			throw new BootstrapException("Handler is null");
		
		// create socket
		try {
			Socket client = new Socket(host, port);
			Connection conn = new Connection(client, protocol, daemon);
			conn.setHandler(handler);
			conn.startThread();
			return conn;
		} catch (IOException e) {
			handler.onException(new BootstrapException("IO exception during socket creating", e));
		} catch (NetworkException e) {
			handler.onException(e);
		} catch (Exception e) {
			handler.onException(new BootstrapException("Unrecognized exception", e));
		}
		
		return null;
	}
	
	public static class BootstrapException extends NetworkException {

		private static final long serialVersionUID = 5469195062953309513L;
		public BootstrapException() { super(); }
		public BootstrapException(String message) { super(message); }
		public BootstrapException(String message, Throwable cause) { super(message, cause); }
		public BootstrapException(Throwable cause) { super(cause); }
		
	}
	
}
