package it.simone36050.network;

public abstract class Handler {
	
	public void onConnect(Connection connection) {}
	public void onShutdown() {}
	public void onException(NetworkException exception) {}
	
}
