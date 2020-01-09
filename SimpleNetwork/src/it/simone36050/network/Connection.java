package it.simone36050.network;

import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Connection {
	
	private static int id = 0;
	private AtomicBoolean open = new AtomicBoolean(true);
	private AtomicBoolean threadStarted = new AtomicBoolean(false);
	private Socket socket;
	private Queue<SendingPacket> queue;
	private NetworkInputStream input;
	private NetworkOutputStream output;
	private Handler handler;
	private Protocol protocol;
	private boolean daemon;
	
	public Connection(Socket socket, Protocol protocol, boolean daemon) throws IOException {
		this.socket = socket;
		this.protocol = protocol;
		this.daemon = daemon;
		
		input = new NetworkInputStream(socket.getInputStream());
		output = new NetworkOutputStream(socket.getOutputStream());
		
		queue = new ConcurrentLinkedQueue<>();
	}
	
	public void send(Packet packet) {
		queue.add(new SendingPacket(false, packet));
	}
	
	public void sendAndFlush(Packet packet) {
		queue.add(new SendingPacket(true, packet));
	}
	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
	public void shutdown() {
		if(open.compareAndSet(true, false))
			try {
				socket.close();
			} catch (IOException e) {
				throw new ConnectionIOException(e);
			}
	}
	
	public void startThread() {
		if(threadStarted.compareAndSet(false, true))
			new ConnectionThread().start();
	}
	
	public class SendingPacket {
		public boolean flush = false;
		public Packet packet = null;
		
		public SendingPacket(boolean flush, Packet packet) {
			this.flush = flush;
			this.packet = packet;
		}
	}
	
	public class ConnectionThread extends Thread {
		
		private long lastReceived;
		private long lastSent;
		
		public ConnectionThread() {
			super("Connection #" + id++);
			setDaemon(daemon);
		}
		
		@Override
		public void run() {
			if(handler != null)
				handler.onConnect(Connection.this);
			
			lastReceived = lastSent = System.currentTimeMillis();
			
			while(open.get()) {
				
				boolean sleep = true;
				
				try {
					
					// 1. read
					if(input.read())
						sleep = false;
					byte[] data = input.getData();
					if(data != null) {
						lastReceived = System.currentTimeMillis();
						Packet p = protocol.dataToPacket(data);
						if(handler != null)
							p.handle(handler);
					}
					
					// 2. write
					SendingPacket pk = queue.poll();
					if(pk != null) {
						lastSent = System.currentTimeMillis();
						data = protocol.packetToData(pk.packet);
						output.write(data);
						if(pk.flush)
							output.flush();
					}
					
					// 2. keep alive
					long act = System.currentTimeMillis();
					if(act - lastReceived >= 10_000)
						shutdown();
					if(act - lastSent >= 5_000) 
						sendAndFlush(KeepAlive.INSTANCE);
					
				} catch (NetworkException e) {
					if(handler != null)
						handler.onException(e);
				} catch (Exception e) {
					if(handler != null)
						handler.onException(new NetworkException("Unrecognized exception", e));
				}
				
				if(sleep)
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// nothing...
					}
			}
			
			if(handler != null)
				handler.onShutdown();
		}
		
	}
	
	public static class ConnectionException extends NetworkException {

		private static final long serialVersionUID = -6356086916189725051L;
		public ConnectionException() { super(); }
		public ConnectionException(String message) { super(message); }
		public ConnectionException(String message, Throwable cause) { super(message, cause); }
		public ConnectionException(Throwable cause) { super(cause); }
		
	}
	
	public static class ConnectionIOException extends ConnectionException {

		private static final long serialVersionUID = -6256092612798516128L;
		public ConnectionIOException() { super(); }
		public ConnectionIOException(String message) { super(message); }
		public ConnectionIOException(String message, Throwable cause) { super(message, cause); }
		public ConnectionIOException(Throwable cause) { super(cause); }
		
	}
	
}
