package it.simone36050.network;

public abstract class DefinedPacket<T extends Handler> implements Packet {

	@SuppressWarnings("unchecked")
	@Override
	public void handle(Handler handler) {
		try {
			handle0((T) handler);
		} catch (ClassCastException e) {
			throw new DefinedPacketException("Wrong handler", e);
		}
	}
	
	public abstract void handle0(T handler);
	
	public static class DefinedPacketException extends NetworkException {

		private static final long serialVersionUID = -2887838733659101202L;
		public DefinedPacketException() { super(); }
		public DefinedPacketException(String message) { super(message); }
		public DefinedPacketException(String message, Throwable cause) { super(message, cause); }
		public DefinedPacketException(Throwable cause) { super(cause); }
		
	}
	
}
