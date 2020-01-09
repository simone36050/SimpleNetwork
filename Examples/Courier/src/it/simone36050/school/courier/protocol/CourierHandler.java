package it.simone36050.school.courier.protocol;

import java.net.SocketException;

import it.simone36050.network.Connection;
import it.simone36050.network.Handler;
import it.simone36050.network.NetworkException;

public class CourierHandler extends Handler {
	
	protected Connection connection;
	
	@Override
	public void onConnect(Connection connection) {
		this.connection = connection;
	}
	
	@Override
	public void onException(NetworkException exception) {
		if((exception.getCause() instanceof SocketException) 
				&& exception.getCause().getMessage().equals("Broken pipe (Write failed)")) {
			connection.shutdown();
		} else {
			System.err.println("Exception in connection: ");
			exception.printStackTrace();
		}
	}
	
	public void handleNewPacketRequest(NewPacketRequest packet) {}
	public void handleNewPacketResponse(NewPacketResponse packet) {}
	
	public void handlePassingPacketRequest(PassingPacketRequest packet) {}
	public void handlePassingPacketResponse(PassingPacketResponse packet) {}
	
	public void handlePacketInfoRequest(PacketInfoRequest packet) {}
	public void handlePacketInfoResponse(PacketInfoResponse packet) {}
	
}
