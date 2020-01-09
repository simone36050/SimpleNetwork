package it.simone36050.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class Server {
	
	private static int id = 0;
	private AtomicBoolean open = new AtomicBoolean(true);
	private AtomicBoolean threadStarted = new AtomicBoolean(false);
	private ServerSocket server;
	private Set<Connection> clients = new HashSet<>();
	private Handler handler;
	private Consumer<Connection> initializator;
	private Consumer<NetworkException> errorHandler;
	private Protocol protocol;
	private boolean serverDaemon;
	private boolean clientDaemon;
	
	public Server(ServerSocket server, 
			Protocol protocol, 
			Consumer<Connection> initializator, 
			Consumer<NetworkException> errorHandler, 
			Handler handler,
			boolean serverDaemon,
			boolean clientDaemon) {
		this.server = server;
		this.protocol = protocol;
		this.initializator = initializator;
		this.errorHandler = errorHandler;
		this.handler = handler;
		this.serverDaemon = serverDaemon;
		this.clientDaemon = clientDaemon;
	}
	
	public void shutdown() {
		if(open.compareAndSet(true, false)) {
			clients.forEach(c -> c.shutdown());
			try {
				server.close();
			} catch (IOException e) {
				throw new ServerIOException(e);
			}
		}
	}
	
	public void startThread() {
		if(threadStarted.compareAndSet(false, true))
			new ServerThread().start();
	}
	
	public class ServerThread extends Thread {
		
		public ServerThread() {
			super("Accepter #" + id++);
			setDaemon(serverDaemon);
		}
		
		@Override
		public void run() {
			while(open.get()) {
				
				try {
					Socket client = server.accept();
					
					Connection c = new Connection(client, protocol, clientDaemon);
					if(handler != null)
						c.setHandler(handler);
					if(initializator != null)
						initializator.accept(c);
					c.startThread();
					
				} catch (SocketException e) {
					// closed socket
				} catch (IOException e) {
					if(errorHandler != null)
						errorHandler.accept(new ServerIOException(e));
				} catch (Exception e) {
					if(errorHandler != null)
						errorHandler.accept(new ServerException("Unrecognized exception", e));
				}
				
			}
		}
		
	}
	
	public static class ServerException extends NetworkException {

		private static final long serialVersionUID = -2281851063983880509L;
		public ServerException() { super(); }
		public ServerException(String message) { super(message); }
		public ServerException(String message, Throwable cause) { super(message, cause); }
		public ServerException(Throwable cause) { super(cause); }
		
	}
	
	public static class ServerIOException extends ServerException {

		private static final long serialVersionUID = -8151806984605335744L;
		public ServerIOException() { super(); }
		public ServerIOException(String message) { super(message); }
		public ServerIOException(String message, Throwable cause) { super(message, cause); }
		public ServerIOException(Throwable cause) { super(cause); }
		
	}
	
}
