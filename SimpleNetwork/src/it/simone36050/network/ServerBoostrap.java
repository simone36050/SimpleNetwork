package it.simone36050.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.function.Consumer;

public class ServerBoostrap {
	
	private int port = -1;
	private Protocol protocol = null;
	private Handler handler = null;
	private Consumer<Connection> initializator = null;
	private Consumer<NetworkException> errorHandler = null;
	private boolean serverDaemon = false;
	private boolean clientDaemon = true;
	
	public ServerBoostrap port(int port) {
		this.port = port;
		return this;
	}
	
	public ServerBoostrap protocol(Protocol protocol) {
		this.protocol = protocol;
		return this;
	}
	
	public ServerBoostrap handler(Handler handler) {
		this.handler = handler;
		return this;
	}
	
	public ServerBoostrap initializator(Consumer<Connection> initializator) {
		this.initializator = initializator;
		return this;
	}
	
	public ServerBoostrap errorHandler(Consumer<NetworkException> errorHandler) {
		this.errorHandler = errorHandler;
		return this;
	}
	
	public ServerBoostrap serverDaemon() {
		return serverDaemon(true);
	}
	
	public ServerBoostrap serverDaemon(boolean serverDaemon) {
		this.serverDaemon = serverDaemon;
		return this;
	}
	
	public ServerBoostrap clientDaemon() {
		return clientDaemon(true);
	}
	
	public ServerBoostrap clientDaemon(boolean clientDaemon) {
		this.clientDaemon = clientDaemon;
		return this;
	}
	
	public Server bind() {
		// checks
		if(port == -1)
			throw new ServerBootstrapException("Port is not set");
		if(protocol == null)
			throw new ServerBootstrapException("Protocol is null");
		
		// bind
		try {
			ServerSocket sock = new ServerSocket(port);
			Server server = new Server(sock, protocol, initializator, errorHandler, handler,
										serverDaemon, clientDaemon);
			server.startThread();
			return server;
		} catch (IOException e) {
			if(errorHandler != null)
				errorHandler.accept(new ServerBootstrapException("IO exception in boostrap", e));
		} catch (NetworkException e) {
			if(errorHandler != null)
				errorHandler.accept(e);
		} catch (Exception e) {
			if(errorHandler != null)
				errorHandler.accept(new ServerBootstrapException("Unrecognized exception", e));
		}
		
		return null;
	}
	
	public static class ServerBootstrapException extends NetworkException {

		private static final long serialVersionUID = 8848351829075488050L;
		public ServerBootstrapException() { super(); }
		public ServerBootstrapException(String message) { super(message); }
		public ServerBootstrapException(String message, Throwable cause) { super(message, cause); }
		public ServerBootstrapException(Throwable cause) { super(cause); }
		
	}
	
}
