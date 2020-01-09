package it.simone36050.network;

public class NetworkException extends RuntimeException {
	
	private static final long serialVersionUID = -7312218914884586645L;
	public NetworkException() { super(); }
	public NetworkException(String message) { super(message); }
	public NetworkException(String message, Throwable cause) { super(message, cause); }
	public NetworkException(Throwable cause) { super(cause); }
	
}
